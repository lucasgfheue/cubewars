package com.proyectox.entity;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXEntities;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Pulso de plasma del AVISPÓN: rápido, recto y de largo alcance; caliente, además incendia. */
public class PlasmaBolt extends PXProjectile {
    private static final EntityDataAccessor<Boolean> HOT = SynchedEntityData.defineId(PlasmaBolt.class, EntityDataSerializers.BOOLEAN);

    public PlasmaBolt(EntityType<? extends PlasmaBolt> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public static PlasmaBolt create(ServerLevel level, LivingEntity owner, Vec3 pos, Vec3 velocity, float damage, int life, boolean hot) {
        PlasmaBolt bolt = new PlasmaBolt(PXEntities.PLASMA.get(), level);
        bolt.entityData.set(HOT, hot);
        bolt.launch(owner, pos, velocity);
        bolt.damage = damage;
        bolt.maxLife = life;
        return bolt;
    }

    public boolean isHot() {
        return this.entityData.get(HOT);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(HOT, false);
    }

    private int fxStyle() {
        return isHot() ? Fx.AVISPON_HOT : Fx.AVISPON;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level() instanceof ServerLevel level) {
            strike(level, result.getEntity(), PXDamage.PLASMA, this.damage);
            if (isHot()) result.getEntity().igniteForSeconds(4.0F);
            Fx.impact(level, fxStyle(), result.getLocation(), this.heading().reverse());
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.level() instanceof ServerLevel level) Fx.impact(level, fxStyle(), result.getLocation(), normal(result));
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) this.discard();
    }
}
