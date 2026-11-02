package com.proyectox.entity;

import com.proyectox.client.ClientOnly;
import com.proyectox.util.Hits;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import com.proyectox.registry.PXDamage;

/**
 * Base de los proyectiles de Proyecto X: velocidad constante (sin rozamiento ni frenado en el agua),
 * vida limitada, nunca alcanzan a su tirador y guardan en el cliente las últimas posiciones para
 * dibujar su estela.
 */
public abstract class PXProjectile extends ThrowableProjectile {
    public static final int TRAIL = 12;

    protected float damage = 1.0F;
    protected int maxLife = 60;
    /** Módulo de la velocidad que se mantiene cada tick (se toma del lanzamiento). */
    protected double speed;
    /** Si se mantiene constante el módulo de la velocidad (proyectiles sin gravedad). */
    protected boolean keepSpeed = true;

    private final Vec3[] trail = new Vec3[TRAIL];
    private int trailHead;
    private int trailSize;

    protected PXProjectile(EntityType<? extends PXProjectile> type, Level level) {
        super(type, level);
    }

    /** Coloca y lanza el proyectil. */
    protected void launch(@Nullable Entity owner, Vec3 pos, Vec3 velocity) {
        if (owner != null) this.setOwner(owner);
        this.setPos(pos.x, pos.y, pos.z);
        this.setDeltaMovement(velocity);
        if (this.isNoGravity()) this.speed = velocity.length();
        face(this, velocity);
    }

    public float damage() {
        return this.damage;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isAlive()) return;
        if (this.keepSpeed && this.isNoGravity()) {
            Vec3 v = this.getDeltaMovement();
            if (this.speed <= 0.0) this.speed = v.length();
            if (v.lengthSqr() > 1.0E-8 && this.speed > 0.0) this.setDeltaMovement(v.normalize().scale(this.speed));
        }
        if (this.level().isClientSide) {
            pushTrail(this.position());
            ClientOnly.projectileTick(this);
        } else if (this.tickCount > this.maxLife) {
            expire();
        }
    }

    /** Se agota la vida del proyectil (servidor). */
    protected void expire() {
        this.discard();
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return target != this.getOwner() && !(target instanceof PXProjectile) && super.canHitEntity(target);
    }

    protected boolean strike(ServerLevel level, Entity target, ResourceKey<DamageType> type, float amount) {
        return Hits.hurt(target, PXDamage.source(level, type, this, this.getOwner()), amount, this.getOwner());
    }

    /** Punto de contacto con una criatura a lo largo del vuelo de este tick (o su centro). */
    protected Vec3 contact(Entity target) {
        Vec3 from = this.position();
        Vec3 to = from.add(this.getDeltaMovement());
        return target.getBoundingBox().inflate(0.3).clip(from, to).orElse(target.getBoundingBox().getCenter());
    }

    protected static Vec3 normal(BlockHitResult result) {
        return Vec3.atLowerCornerOf(result.getDirection().getNormal());
    }

    /** Dirección de vuelo (o, si está parado, la de su orientación: los proyectiles usan yaw = atan2(x, z)). */
    public Vec3 heading() {
        Vec3 v = this.getDeltaMovement();
        if (v.lengthSqr() >= 1.0E-8) return v.normalize();
        float yaw = (float) Math.toRadians(this.getYRot());
        float pitch = (float) Math.toRadians(this.getXRot());
        return new Vec3(Mth.sin(yaw) * Mth.cos(pitch), Mth.sin(pitch), Mth.cos(yaw) * Mth.cos(pitch));
    }

    // ------------------------------------------------------------------ estela (cliente)

    private void pushTrail(Vec3 p) {
        this.trail[this.trailHead] = p;
        this.trailHead = (this.trailHead + 1) % TRAIL;
        this.trailSize = Math.min(TRAIL, this.trailSize + 1);
    }

    /** Número de posiciones guardadas. */
    public int trailSize() {
        return this.trailSize;
    }

    /** Posición guardada {@code i} ticks atrás (0 = la del último tick). */
    public Vec3 trailPoint(int i) {
        int idx = Math.floorMod(this.trailHead - 1 - i, TRAIL);
        Vec3 p = this.trail[idx];
        return p == null ? this.position() : p;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 256.0 * 256.0;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    /** Orienta el proyectil según su velocidad (para que se dibuje bien desde el primer tick). */
    public static void face(Entity entity, Vec3 velocity) {
        double horizontal = velocity.horizontalDistance();
        entity.setYRot((float) (Mth.atan2(velocity.x, velocity.z) * (180.0 / Math.PI)));
        entity.setXRot((float) (Mth.atan2(velocity.y, horizontal) * (180.0 / Math.PI)));
        entity.yRotO = entity.getYRot();
        entity.xRotO = entity.getXRot();
    }

    /** Criaturas que pueden recibir daño de área de este proyectil (nunca su tirador). */
    protected boolean splashable(LivingEntity e) {
        return e != this.getOwner();
    }
}
