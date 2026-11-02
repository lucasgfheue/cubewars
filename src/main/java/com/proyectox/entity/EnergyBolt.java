package com.proyectox.entity;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXEntities;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Proyectil de energía: trazadora (ROTOR), aguja de cristal (COLIBRÍ), pulso del dron (HALCÓN),
 * láser criogénico (ESCARCHA: cada impacto ralentiza más hasta inmovilizar), bala cinética (BASTIÓN) y
 * bala de plasma pesada (CENTELLA).
 */
public class EnergyBolt extends PXProjectile {
    public static final int TRACER = 0;
    public static final int NEEDLE = 1;
    public static final int DRONE = 2;
    public static final int FROST = 3;
    public static final int BULLET = 4;
    public static final int SLUG = 5;
    /** Nivel máximo de Lentitud que acumula la ESCARCHA (VII = inmóvil). */
    public static final int MAX_SLOW = 6;

    private static final EntityDataAccessor<Integer> STYLE = SynchedEntityData.defineId(EnergyBolt.class, EntityDataSerializers.INT);

    public EnergyBolt(EntityType<? extends EnergyBolt> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public static EnergyBolt create(ServerLevel level, LivingEntity owner, int style, Vec3 pos, Vec3 velocity, float damage, int life) {
        EnergyBolt bolt = new EnergyBolt(PXEntities.BOLT.get(), level);
        bolt.entityData.set(STYLE, style);
        bolt.launch(owner, pos, velocity);
        bolt.damage = damage;
        bolt.maxLife = life;
        return bolt;
    }

    public int style() {
        return this.entityData.get(STYLE);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(STYLE, TRACER);
    }

    private ResourceKey<DamageType> damageType() {
        return switch (style()) {
            case NEEDLE -> PXDamage.AGUJA_TOXICA;
            case DRONE -> PXDamage.DRON;
            case FROST -> PXDamage.FRIO;
            case BULLET -> PXDamage.CINETICO;
            case SLUG -> PXDamage.FULGOR;
            default -> PXDamage.TRAZADORA;
        };
    }

    private int fxStyle() {
        return switch (style()) {
            case NEEDLE -> Fx.COLIBRI;
            case DRONE -> Fx.HALCON;
            case FROST -> Fx.ESCARCHA;
            case BULLET -> Fx.BASTION;
            case SLUG -> Fx.CENTELLA;
            default -> Fx.ROTOR;
        };
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level() instanceof ServerLevel level) {
            strike(level, result.getEntity(), damageType(), this.damage);
            if (style() == FROST && result.getEntity() instanceof LivingEntity living && living.isAlive()) chill(living);
            Fx.impact(level, fxStyle(), contact(result.getEntity()), this.heading().reverse());
        }
    }

    /** ESCARCHA: cada impacto suma un nivel de Lentitud (hasta quedar inmóvil) y escarcha al objetivo. */
    private void chill(LivingEntity living) {
        MobEffectInstance current = living.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
        int amplifier = current == null ? 0 : Math.min(MAX_SLOW, current.getAmplifier() + 1);
        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, amplifier, false, true), this.getOwner());
        living.setTicksFrozen(Math.min(living.getTicksRequiredToFreeze() + 20, living.getTicksFrozen() + 12));
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
