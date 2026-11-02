package com.proyectox.util;

import com.proyectox.registry.PXDamage;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Daño en área de las armas: el objetivo directo recibe el daño completo y los demás algo menos según la
 * distancia. Nunca rompe bloques, nunca empuja y nunca daña al tirador.
 */
public final class Blast {
    private Blast() {}

    public static void damage(ServerLevel level, Entity source, @Nullable Entity owner, Vec3 center, double radius, float amount,
                              @Nullable Entity direct, ResourceKey<DamageType> type, float minFactor) {
        DamageSource ds = PXDamage.source(level, type, source, owner);
        if (direct != null && direct.isAlive() && direct != owner) Hits.hurt(direct, ds, amount, owner);
        for (LivingEntity e : Aim.sphere(level, owner != null ? owner : source, center, radius)) {
            if (e == direct || e == owner) continue;
            double d = e.getBoundingBox().getCenter().distanceTo(center);
            float f = (float) Math.max(minFactor, 1.0 - d / (radius + 1.5));
            Hits.hurt(e, ds, amount * f, owner);
        }
    }

    /** Suelo bajo un punto (hasta {@code depth} bloques), o null si no hay. */
    @Nullable
    public static Vec3 ground(ServerLevel level, Entity source, Vec3 at, double depth) {
        Vec3 from = at.add(0.0, 0.3, 0.0);
        BlockHitResult hit = level.clip(new ClipContext(from, at.subtract(0.0, depth, 0.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, source));
        return hit.getType() == HitResult.Type.MISS ? null : hit.getLocation().add(0.0, 0.02, 0.0);
    }
}
