package com.proyectox.item;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import com.proyectox.util.Hits;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * PX-35 PICA — lanza de plasma. Con el clic izquierdo da una estocada de plasma de 7 bloques que atraviesa a
 * todas las criaturas en línea (10 de daño). Manteniendo el clic, una estocada cada 0,5 s.
 */
public class PicaItem extends MeleeItem implements TriggerWeapon {
    public static final double REACH = 7.0;
    public static final float DAMAGE = 10.0F;
    /** Próximo tick en que cada jugador puede volver a golpear (también haciendo clic muy rápido). */
    private static final Map<UUID, Long> NEXT = new ConcurrentHashMap<>();

    public static final int INTERVAL = 10;

    public PicaItem(Properties properties) {
        super("pica", "PX-35", 0x4FA8FF, Fx.PICA, properties);
    }

    public static void forget(Player player) {
        NEXT.remove(player.getUUID());
    }

    public static ItemAttributeModifiers attributes() {
        return MeleeItem.attributes(DAMAGE, 20.0 / INTERVAL, 0.0);
    }

    @Override
    public void triggerTick(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming) {
        if (player.isSpectator()) return;
        Long next = NEXT.get(player.getUUID());
        if (next != null && now < next) return;
        NEXT.put(player.getUUID(), now + INTERVAL);
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 far = eye.add(look.scale(REACH));
        BlockHitResult wall = level.clip(new ClipContext(eye, far, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        Vec3 end = wall.getType() == HitResult.Type.MISS ? far : wall.getLocation();
        DamageSource ds = PXDamage.source(level, PXDamage.PICA, player, player);
        for (LivingEntity e : Aim.segment(level, player, eye, end, 0.6)) {
            Hits.hurt(e, ds, DAMAGE, player);
            Fx.point(level, Fx.MELEE, Fx.PICA, e.getBoundingBox().getCenter(), look, 1.0F, player.getId());
        }
        player.swing(InteractionHand.MAIN_HAND, true);
        Fx.line(level, Fx.THRUST, Fx.PICA, eye, end, wall.getType() == HitResult.Type.MISS ? 0.0F : 1.0F, player.getId());
        FX.sound(level, player, PXSounds.PICA_ESTOCADA.get(), 1.1F, 1.3F + level.random.nextFloat() * 0.15F);
    }
}
