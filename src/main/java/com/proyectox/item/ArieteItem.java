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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * PX-50 ARIETE — guantelete de impacto. Cada puñetazo suelta una onda de choque en un cono de 60° y 5 bloques que
 * hace 12 de daño a todas las criaturas que alcanza. Manteniendo el clic, un puñetazo cada 0,75 s.
 */
public class ArieteItem extends MeleeItem implements TriggerWeapon {
    public static final double RANGE = 5.0;
    public static final double HALF_ANGLE = 30.0;
    public static final float DAMAGE = 12.0F;
    public static final int INTERVAL = 15;
    /** Próximo tick en que cada jugador puede volver a golpear (también haciendo clic muy rápido). */
    private static final Map<UUID, Long> NEXT = new ConcurrentHashMap<>();

    public ArieteItem(Properties properties) {
        super("ariete", "PX-50", 0x5AE0FF, Fx.ARIETE, properties);
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
        DamageSource ds = PXDamage.source(level, PXDamage.ARIETE, player, player);
        for (LivingEntity e : Aim.cone(level, player, eye, look, RANGE, HALF_ANGLE)) {
            Vec3 c = e.getBoundingBox().getCenter();
            if (level.clip(new ClipContext(eye, c, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)).getType() != HitResult.Type.MISS) continue;
            Hits.hurt(e, ds, DAMAGE, player);
            Fx.point(level, Fx.MELEE, Fx.ARIETE, c, look, 1.0F, player.getId());
        }
        // la onda se detiene en la pared
        BlockHitResult wall = level.clip(new ClipContext(eye, eye.add(look.scale(RANGE)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        Vec3 end = wall.getType() == HitResult.Type.MISS ? eye.add(look.scale(RANGE)) : wall.getLocation();
        player.swing(InteractionHand.MAIN_HAND, true);
        Fx.line(level, Fx.PUNCH, Fx.ARIETE, eye, end, wall.getType() == HitResult.Type.MISS ? 0.0F : 1.0F, player.getId());
        FX.sound(level, player, PXSounds.ARIETE_GOLPE.get(), 1.2F, 0.9F + level.random.nextFloat() * 0.15F);
        FX.sound(level, end, PXSounds.ARIETE_ONDA.get(), 1.0F, 1.1F);
    }
}
