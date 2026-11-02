package com.proyectox.event;

import com.proyectox.ProyectoX;
import com.proyectox.entity.Sticky;
import com.proyectox.item.ArieteItem;
import com.proyectox.item.BastionItem;
import com.proyectox.item.ColmenaItem;
import com.proyectox.item.GuadanaItem;
import com.proyectox.item.BulletGun;
import com.proyectox.item.GunItem;
import com.proyectox.item.MangualItem;
import com.proyectox.item.MartineteItem;
import com.proyectox.item.MeleeItem;
import com.proyectox.item.PicaItem;
import com.proyectox.item.SerpentinaItem;
import com.proyectox.item.SpecGun;
import com.proyectox.item.SpecMelee;
import com.proyectox.item.TaladroItem;
import com.proyectox.item.TriggerWeapon;
import com.proyectox.network.PXNetwork;
import com.proyectox.registry.PXEffects;
import com.proyectox.util.FallGuard;
import com.proyectox.util.Hits;
import com.proyectox.util.Scheduler;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Eventos del juego (bus principal de NeoForge). */
@EventBusSubscriber(modid = ProyectoX.MODID)
public final class PXEvents {
    private PXEvents() {}

    /** Estado del gatillo de cada jugador en el servidor: con qué arma, si está pulsado y desde cuándo. */
    private static final class Trigger {
        Item item;
        boolean down;
        long since;
    }

    private static final Map<UUID, Trigger> TRIGGERS = new ConcurrentHashMap<>();

    /**
     * Gatillo: el cliente avisa de si mantiene el clic izquierdo; aquí se convierte en pulsación,
     * mantenimiento y suelta para el arma que el jugador tiene en la mano.
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !(player.level() instanceof ServerLevel level)) return;
        long now = level.getGameTime();
        ItemStack stack = player.getMainHandItem();
        Item item = stack.getItem();
        // un clic muy corto (pulsar y soltar entre dos ticks) también cuenta como un disparo
        boolean pressed = PXNetwork.pressed(player, now);
        boolean firing = player.isAlive() && (PXNetwork.firing(player, now) || pressed);
        boolean aiming = PXNetwork.aiming(player, now);
        Trigger t = TRIGGERS.get(player.getUUID());
        if (t == null) {
            if (!firing) return;
            t = new Trigger();
            TRIGGERS.put(player.getUUID(), t);
        }
        // al cambiar de arma con el gatillo pulsado, el arma anterior lo "suelta"
        if (t.down && t.item != item) {
            release(player, t, level, now, aiming);
        }
        t.item = item;
        if (firing) {
            if (!t.down) {
                t.down = true;
                t.since = now;
            }
            if (item instanceof TriggerWeapon weapon) weapon.triggerTick(player, stack, level, now, (int) (now - t.since), aiming);
        } else if (t.down) {
            release(player, t, level, now, aiming);
            TRIGGERS.remove(player.getUUID());
        } else {
            TRIGGERS.remove(player.getUUID());
        }
    }

    private static void release(ServerPlayer player, Trigger t, ServerLevel level, long now, boolean aiming) {
        t.down = false;
        if (!(t.item instanceof TriggerWeapon weapon)) return;
        // se busca la pila del arma que soltó el gatillo (puede que ya no esté en la mano)
        ItemStack stack = player.getMainHandItem().getItem() == t.item ? player.getMainHandItem() : null;
        if (stack == null) {
            for (ItemStack s : player.getInventory().items) {
                if (s.getItem() == t.item) {
                    stack = s;
                    break;
                }
            }
        }
        if (stack != null) weapon.triggerRelease(player, stack, level, now, (int) (now - t.since), aiming);
    }

    /** Ningún arma de Proyecto X empuja: se anula el retroceso de sus impactos y golpes. */
    @SubscribeEvent
    public static void onKnockBack(LivingKnockBackEvent event) {
        if (!event.getEntity().level().isClientSide && Hits.suppressKnockback(event.getEntity())) event.setCanceled(true);
    }

    /** Golpe cuerpo a cuerpo con un arma de Proyecto X: sin retroceso. */
    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        if (event.getEntity().getMainHandItem().getItem() instanceof MeleeItem) Hits.meleeHit(event.getTarget());
    }

    /**
     * Daño entrante: una criatura paralizada no puede hacer daño; SERPENTINA: el primer golpe al enemigo que acaba de
     * arrastrar hace el triple de daño; MARTINETE: el golpe en caída hace siempre 25 (con o sin golpe crítico).
     */
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        if (source.getEntity() instanceof LivingEntity from && from.hasEffect(PXEffects.PARALISIS)) {
            event.setCanceled(true);
            return;
        }
        if (source.getEntity() instanceof Player attacker && source.getDirectEntity() == attacker) {
            Item held = attacker.getMainHandItem().getItem();
            if (held instanceof SerpentinaItem) {
                event.setAmount(SerpentinaItem.empower(event.getEntity(), attacker, event.getAmount()));
            } else if (held instanceof MartineteItem && source.is(DamageTypes.PLAYER_ATTACK) && MartineteItem.slamming(attacker)) {
                event.setAmount(MartineteItem.SLAM_DAMAGE);
            }
        }
    }

    /** Sin daño por caída mientras dura una protección temporal. */
    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        if (FallGuard.active(event.getEntity())) {
            event.setDamageMultiplier(0.0F);
            event.setDistance(0.0F);
        }
    }

    /** BASTIÓN: con el escudo alzado se bloquea el daño de cualquier dirección, y el escudo no se desgasta. */
    @SubscribeEvent
    public static void onShieldBlock(LivingShieldBlockEvent event) {
        LivingEntity holder = event.getEntity();
        if (holder.level().isClientSide) return;
        ItemStack using = holder.getUseItem();
        if (!(using.getItem() instanceof BastionItem bastion) || !holder.isBlocking()) return;
        DamageSource source = event.getDamageSource();
        if (source.is(DamageTypeTags.BYPASSES_SHIELD)) return;
        event.setBlocked(true);
        event.setShieldDamage(0.0F);
        Vec3 from = source.getSourcePosition();
        bastion.blocked(holder, from != null ? from : holder.getEyePosition().add(holder.getLookAngle()));
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) Scheduler.tick(level);
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        Scheduler.clear();
        TRIGGERS.clear();
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        TRIGGERS.remove(player.getUUID());
        PXNetwork.forget(player);
        GunItem.forget(player);
        BulletGun.forget(player);
        ColmenaItem.forget(player);
        TaladroItem.forget(player);
        Sticky.forget(player);
        GuadanaItem.forget(player);
        PicaItem.forget(player);
        MangualItem.forget(player);
        ArieteItem.forget(player);
        SpecGun.forget(player);
        SpecMelee.forget(player);
        FallGuard.forget(player);
    }
}
