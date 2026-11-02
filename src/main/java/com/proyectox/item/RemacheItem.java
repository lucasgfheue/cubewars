package com.proyectox.item;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import com.proyectox.util.Hits;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * PX-10 REMACHE — escopeta de remaches magnéticos: 9 remaches que, hasta 7 bloques, suman 28 de daño si aciertan
 * todos. Más lejos cada remache pierde fuerza poco a poco, hasta quedarse en la cuarta parte a 32 bloques o más
 * (alcance de 96 bloques).
 */
public class RemacheItem extends GunItem {
    public static final int PELLETS = 9;
    public static final double RANGE = 96.0;
    /** Daño de los 9 remaches juntos hasta {@link #FULL_RANGE} bloques. */
    public static final float FULL_DAMAGE = 28.0F;
    public static final double FULL_RANGE = 7.0;
    /** A partir de esta distancia cada remache hace {@link #FAR_FACTOR} de su daño. */
    public static final double FAR_RANGE = 32.0;
    public static final float FAR_FACTOR = 0.25F;

    public RemacheItem(Properties properties) {
        super("remache", "PX-10", 0xC88B4A, Fx.REMACHE, properties);
    }

    @Override
    public int maxCharge() {
        return 24;
    }

    @Override
    public int regenTicks() {
        return 10;
    }

    @Override
    public int interval() {
        return 12;
    }

    @Override
    public double hipSpread() {
        return 2.5;
    }

    @Override
    public double aimSpread() {
        return 1.2;
    }

    /** Fracción del daño de un remache según la distancia (1 hasta 7 bloques, 1/4 a partir de 32). */
    public static float falloff(double distance) {
        if (distance <= FULL_RANGE) return 1.0F;
        if (distance >= FAR_RANGE) return FAR_FACTOR;
        return (float) (1.0 - (1.0 - FAR_FACTOR) * (distance - FULL_RANGE) / (FAR_RANGE - FULL_RANGE));
    }

    @Override
    public float zoom() {
        return 0.18F;
    }

    @Override
    public float kick() {
        return 0.9F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        ServerLevel level = shot.level();
        DamageSource source = PXDamage.source(level, PXDamage.REMACHE, shot.player(), shot.player());
        for (int i = 0; i < PELLETS; i++) {
            Vec3 dir = shot.eyeDir();
            Aim.Ray ray = Aim.pierce(level, shot.player(), shot.eye(), dir, RANGE, 1);
            for (Entity e : ray.hits()) {
                double d = e.getBoundingBox().getCenter().distanceTo(shot.eye());
                Hits.hurt(e, source, FULL_DAMAGE / PELLETS * falloff(d), shot.player());
            }
            Fx.line(level, Fx.TRACER, Fx.REMACHE, shot.muzzle(), ray.end(), 1.0F, shot.player().getId());
            if (ray.hitBlock() || !ray.hits().isEmpty()) Fx.impact(level, Fx.REMACHE, ray.end(), ray.normal());
        }
        FX.sound(level, shot.player(), PXSounds.REMACHE_DISPARO.get(), 1.6F, 0.85F + level.random.nextFloat() * 0.1F);
    }
}
