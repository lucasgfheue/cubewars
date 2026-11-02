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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * PX-49 MANGUAL — mangual de energía. Con el clic izquierdo haces girar la bola a tu alrededor: 8 de daño a todas
 * las criaturas a menos de 3,6 bloques en los 360°. Manteniendo el clic, un giro cada 0,3 s.
 */
public class MangualItem extends MeleeItem implements TriggerWeapon {
    public static final double RADIUS = 3.6;
    public static final float DAMAGE = 8.0F;
    public static final int INTERVAL = 6;
    /** Próximo tick en que cada jugador puede volver a girar (también haciendo clic muy rápido). */
    private static final Map<UUID, Long> NEXT = new ConcurrentHashMap<>();

    public MangualItem(Properties properties) {
        super("mangual", "PX-49", 0xB04CFF, Fx.MANGUAL, properties);
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
        Vec3 center = player.position().add(0.0, player.getBbHeight() * 0.55, 0.0);
        DamageSource ds = PXDamage.source(level, PXDamage.MANGUAL, player, player);
        AABB box = new AABB(center, center).inflate(RADIUS + 1.0, 2.5, RADIUS + 1.0);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, x -> Aim.targetable(player, x))) {
            Vec3 c = e.getBoundingBox().getCenter();
            double dx = c.x - center.x;
            double dz = c.z - center.z;
            if (Math.sqrt(dx * dx + dz * dz) > RADIUS + e.getBbWidth() * 0.5 || Math.abs(c.y - center.y) > 2.0 + e.getBbHeight() * 0.5) continue;
            // sin atravesar paredes
            if (level.clip(new ClipContext(eye, c, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)).getType() != HitResult.Type.MISS) continue;
            Hits.hurt(e, ds, DAMAGE, player);
            Vec3 out = new Vec3(dx, 0.0, dz);
            Fx.point(level, Fx.MELEE, Fx.MANGUAL, c, out.lengthSqr() < 1.0E-6 ? player.getLookAngle() : out.normalize(), 1.0F, player.getId());
        }
        player.swing(InteractionHand.MAIN_HAND, true);
        Fx.point(level, Fx.SPIN, Fx.MANGUAL, center, player.getLookAngle(), (float) RADIUS, player.getId());
        FX.sound(level, player, PXSounds.MANGUAL_GIRO.get(), 1.0F, 0.85F + level.random.nextFloat() * 0.2F);
    }
}
