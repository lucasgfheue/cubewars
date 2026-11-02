package com.proyectox.item;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import com.proyectox.util.Hits;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * PX-24 PRISMA — cañón prismático. Un haz de luz blanca que, a 2,5 bloques, el cristal divide en cinco rayos
 * de colores en abanico (instantáneos, 64 bloques, 8 de daño cada uno; cada rayo atraviesa a 2 criaturas).
 * De cerca los cinco rayos se concentran en el mismo enemigo: 24 de daño. Una criatura nunca recibe más de 24
 * de un mismo disparo.
 */
public class PrismaItem extends GunItem {
    public static final double SPLIT = 2.5;
    public static final double RANGE = 64.0;
    public static final float DAMAGE = 8.0F;
    /** Daño a quemarropa (los cinco rayos en el mismo objetivo) y tope por criatura en cada disparo. */
    public static final float CLOSE_DAMAGE = 24.0F;
    public static final double[] FAN = {-6.0, -3.0, 0.0, 3.0, 6.0};
    private static final Vec3 UP = new Vec3(0, 1, 0);

    public PrismaItem(Properties properties) {
        super("prisma", "PX-24", 0xF4F6FF, Fx.PRISMA, properties);
    }

    @Override
    public int maxCharge() {
        return 30;
    }

    @Override
    public int regenTicks() {
        return 5;
    }

    @Override
    public int interval() {
        return 12;
    }

    @Override
    public double hipSpread() {
        return 0.4;
    }

    @Override
    public double aimSpread() {
        return 0.0;
    }

    @Override
    public float zoom() {
        return 0.35F;
    }

    @Override
    public float kick() {
        return 0.55F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        ServerLevel level = shot.level();
        Vec3 eye = shot.eye();
        Vec3 dir = shot.eyeDir();
        DamageSource ds = PXDamage.source(level, PXDamage.PRISMA, shot.player(), shot.player());
        // haz blanco hasta el punto de división (o la pared, o la primera criatura)
        Vec3 split = eye.add(dir.scale(SPLIT));
        BlockHitResult wall = level.clip(new ClipContext(eye, split, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shot.player()));
        if (wall.getType() != HitResult.Type.MISS) split = wall.getLocation().subtract(dir.scale(0.05));
        Aim.Ray trunk = Aim.pierce(level, shot.player(), eye, dir, eye.distanceTo(split), 1);
        Entity first = trunk.hits().isEmpty() ? null : trunk.hits().get(0);
        // daño ya hecho a cada criatura en este disparo (tope: CLOSE_DAMAGE)
        Map<Entity, Float> dealt = new IdentityHashMap<>();
        if (first != null) {
            // a quemarropa los cinco rayos se concentran en el mismo objetivo
            Hits.hurt(first, ds, CLOSE_DAMAGE, shot.player());
            dealt.put(first, CLOSE_DAMAGE);
            split = first.getBoundingBox().getCenter();
        }
        Fx.line(level, Fx.PRISM, Fx.PRISMA, shot.muzzle(), split, -1.0F, shot.player().getId());
        Vec3 right = dir.cross(UP);
        right = right.lengthSqr() < 1.0E-6 ? new Vec3(1, 0, 0) : right.normalize();
        for (int i = 0; i < FAN.length; i++) {
            double a = Math.toRadians(FAN[i]);
            Vec3 d = dir.scale(Math.cos(a)).add(right.scale(Math.sin(a))).normalize();
            Aim.Ray ray = Aim.pierce(level, shot.player(), split, d, RANGE, first != null ? 3 : 2);
            for (Entity e : ray.hits()) {
                float done = dealt.getOrDefault(e, 0.0F);
                float amount = Math.min(DAMAGE, CLOSE_DAMAGE - done);
                if (amount <= 0.0F) continue;
                Hits.hurt(e, ds, amount, shot.player());
                dealt.put(e, done + amount);
            }
            Fx.line(level, Fx.PRISM, Fx.PRISMA, split, ray.end(), i, shot.player().getId());
        }
        FX.sound(level, shot.player(), PXSounds.PRISMA_DISPARO.get(), 1.2F, 1.5F);
        FX.sound(level, split, PXSounds.PRISMA_DIVISION.get(), 1.0F, 1.2F + level.random.nextFloat() * 0.3F);
    }
}
