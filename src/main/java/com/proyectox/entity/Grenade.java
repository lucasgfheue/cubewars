package com.proyectox.entity;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXEntities;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import com.proyectox.util.Hits;
import javax.annotation.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Granada de racimo de la TRITURADORA (rebota dos veces y estalla al tocar una criatura o al agotarse
 * la espoleta: 30 de daño en radio 4) y sus cuatro bombetas (10 de daño en radio 2,5).
 * Nunca dañan ni empujan a nadie, tampoco a su tirador.
 */
public class Grenade extends PXProjectile {
    public static final int GRENADE = 0;
    public static final int BOMBLET = 1;

    private static final EntityDataAccessor<Integer> KIND = SynchedEntityData.defineId(Grenade.class, EntityDataSerializers.INT);

    private int bounces;

    public Grenade(EntityType<? extends Grenade> type, Level level) {
        super(type, level);
    }

    public static Grenade create(ServerLevel level, @Nullable Entity owner, int kind, Vec3 pos, Vec3 velocity, int fuse) {
        Grenade g = new Grenade(PXEntities.GRANADA.get(), level);
        g.entityData.set(KIND, kind);
        g.launch(owner, pos, velocity);
        g.maxLife = fuse;
        return g;
    }

    public int kind() {
        return this.entityData.get(KIND);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(KIND, GRENADE);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.045;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) explode(result.getEntity());
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.level().isClientSide) return;
        if (kind() == GRENADE && this.bounces < 2) {
            // rebote amortiguado sobre la cara golpeada
            this.bounces++;
            Vec3 v = this.getDeltaMovement();
            Vec3 n = normal(result);
            Vec3 r = v.subtract(n.scale(2.0 * v.dot(n))).scale(0.5);
            this.setDeltaMovement(r);
            Vec3 p = result.getLocation().add(n.scale(0.15));
            this.setPos(p.x, p.y, p.z);
            FX.sound(this.level(), p, PXSounds.TRITURADORA_REBOTE.get(), 0.7F, 1.2F);
            return;
        }
        explode(null);
    }

    @Override
    protected void expire() {
        explode(null);
    }

    private void explode(@Nullable Entity direct) {
        if (!(this.level() instanceof ServerLevel level) || !this.isAlive()) return;
        Vec3 p = this.position();
        boolean big = kind() == GRENADE;
        double radius = big ? 4.0 : 2.5;
        float dmg = big ? 30.0F : 10.0F;
        Entity owner = this.getOwner();
        DamageSource source = PXDamage.source(level, PXDamage.METRALLA, this, owner);
        for (LivingEntity e : Aim.sphere(level, this, p, radius)) {
            if (!splashable(e)) continue;
            double d = e.getBoundingBox().getCenter().distanceTo(p);
            float f = e == direct ? 1.0F : (float) Math.max(0.45, 1.0 - d / (radius + 1.5));
            Hits.hurt(e, source, dmg * f, owner);
        }
        Fx.explosion(level, big ? Fx.TRITURADORA : Fx.BOMBLET, p, (float) radius);
        FX.sound(level, p, PXSounds.TRITURADORA_EXPLOSION.get(), big ? 1.8F : 0.9F, big ? 0.85F : 1.35F);
        if (big) {
            // racimo: cuatro bombetas que estallan medio segundo después
            for (int i = 0; i < 4; i++) {
                double a = Math.PI / 2 * i + this.random.nextDouble() * 0.6;
                Vec3 v = new Vec3(Math.cos(a) * 0.36, 0.5, Math.sin(a) * 0.36);
                level.addFreshEntity(create(level, owner, BOMBLET, p.add(0, 0.4, 0), v, 10 + this.random.nextInt(6)));
            }
        }
        this.discard();
    }
}
