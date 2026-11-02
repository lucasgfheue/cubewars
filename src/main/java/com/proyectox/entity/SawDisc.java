package com.proyectox.entity;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXEntities;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Disco de sierra de la SIERRA: atraviesa a todos los enemigos (cada uno recibe un corte cada
 * 10 ticks) y rebota hasta 4 veces en los bloques. Nunca corta a su tirador.
 */
public class SawDisc extends PXProjectile {
    private static final int MAX_BOUNCES = 4;

    private final Map<Integer, Integer> lastHit = new HashMap<>();
    private int bounces;

    public SawDisc(EntityType<? extends SawDisc> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public static SawDisc create(ServerLevel level, LivingEntity owner, Vec3 pos, Vec3 velocity, float damage) {
        SawDisc d = new SawDisc(PXEntities.DISCO.get(), level);
        d.launch(owner, pos, velocity);
        d.damage = damage;
        d.maxLife = 100;
        return d;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel level && this.isAlive()) {
            // el filo es ancho: además del rayo de movimiento, corta lo que roce
            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.35),
                    x -> x != this.getOwner() && x.isAlive() && !x.isSpectator())) {
                cut(level, e);
            }
        }
    }

    @Override
    protected void expire() {
        if (this.level() instanceof ServerLevel level) Fx.impact(level, Fx.SIERRA, this.position(), this.heading().reverse());
        this.discard();
    }

    private void cut(ServerLevel level, Entity target) {
        Integer last = this.lastHit.get(target.getId());
        if (last != null && this.tickCount - last < 10) return;
        this.lastHit.put(target.getId(), this.tickCount);
        strike(level, target, PXDamage.DISCO, this.damage);
        Fx.impact(level, Fx.SIERRA, target.getBoundingBox().getCenter(), this.heading().reverse());
        FX.sound(level, this.position(), PXSounds.SIERRA_CORTE.get(), 0.9F, 1.0F + this.random.nextFloat() * 0.3F);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level() instanceof ServerLevel level) cut(level, result.getEntity());
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!(this.level() instanceof ServerLevel level)) return;
        Fx.impact(level, Fx.SIERRA, result.getLocation(), normal(result));
        if (this.bounces >= MAX_BOUNCES) {
            this.discard();
            return;
        }
        this.bounces++;
        Vec3 v = this.getDeltaMovement();
        Vec3 n = normal(result);
        this.setDeltaMovement(v.subtract(n.scale(2.0 * v.dot(n))));
        Vec3 p = result.getLocation().add(n.scale(0.2));
        this.setPos(p.x, p.y, p.z);
        face(this, this.getDeltaMovement());
        FX.sound(level, p, PXSounds.SIERRA_REBOTE.get(), 0.8F, 1.6F + this.random.nextFloat() * 0.2F);
    }
}
