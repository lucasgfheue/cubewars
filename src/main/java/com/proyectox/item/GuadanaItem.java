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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * PX-34 GUADAÑA — guadaña de energía. Con el clic izquierdo da un barrido de 150° y 4,5 bloques de radio que
 * alcanza a todas las criaturas delante de ti (9 de daño). Manteniendo el clic, un barrido cada 0,6 s.
 */
public class GuadanaItem extends MeleeItem implements TriggerWeapon {
    public static final double RADIUS = 4.5;
    public static final double HALF_ANGLE = 75.0;
    public static final float DAMAGE = 9.0F;
    /** Próximo tick en que cada jugador puede volver a golpear (también haciendo clic muy rápido). */
    private static final Map<UUID, Long> NEXT = new ConcurrentHashMap<>();

    public static final int INTERVAL = 12;

    public GuadanaItem(Properties properties) {
        super("guadana", "PX-34", 0xE01E3C, Fx.GUADANA, properties);
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
        Vec3 flat = new Vec3(look.x, 0.0, look.z);
        flat = flat.lengthSqr() < 1.0E-4 ? Vec3.directionFromRotation(0.0F, player.getYRot()) : flat.normalize();
        Vec3 center = player.position().add(0.0, player.getBbHeight() * 0.55, 0.0);
        DamageSource ds = PXDamage.source(level, PXDamage.GUADANA, player, player);
        AABB box = new AABB(center, center).inflate(RADIUS + 1.0, 2.5, RADIUS + 1.0);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, x -> Aim.targetable(player, x))) {
            Vec3 c = e.getBoundingBox().getCenter();
            Vec3 to = new Vec3(c.x - center.x, 0.0, c.z - center.z);
            double dist = to.length();
            if (dist > RADIUS + e.getBbWidth() * 0.5 || Math.abs(c.y - center.y) > 2.2 + e.getBbHeight() * 0.5) continue;
            if (dist > 0.6 && Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, to.normalize().dot(flat))))) > HALF_ANGLE) continue;
            // sin atravesar paredes
            if (level.clip(new ClipContext(eye, c, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)).getType() != HitResult.Type.MISS) continue;
            Hits.hurt(e, ds, DAMAGE, player);
            Fx.point(level, Fx.MELEE, Fx.GUADANA, c, to.lengthSqr() < 1.0E-6 ? flat : to.normalize(), 1.0F, player.getId());
        }
        player.swing(InteractionHand.MAIN_HAND, true);
        Fx.point(level, Fx.SWEEP, Fx.GUADANA, center, flat, (float) RADIUS, player.getId());
        FX.sound(level, player, PXSounds.GUADANA_TAJO.get(), 1.2F, 0.75F + level.random.nextFloat() * 0.15F);
    }
}
