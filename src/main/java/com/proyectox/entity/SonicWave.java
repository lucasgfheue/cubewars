package com.proyectox.entity;

import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXEntities;
import com.proyectox.util.Aim;
import com.proyectox.util.Hits;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Onda expansiva del ECO: avanza en línea recta atravesando bloques y criaturas mientras se ensancha
 * (de 1,5 a 5 bloques de radio). Cada criatura que alcanza recibe 6 de daño una sola vez.
 */
public class SonicWave extends Entity {
    public static final int LIFE = 36;
    public static final double SPEED = 1.8;
    public static final float START_RADIUS = 1.5F;
    public static final float END_RADIUS = 5.0F;
    public static final float DAMAGE = 6.0F;

    private static final EntityDataAccessor<Float> DIR_X = SynchedEntityData.defineId(SonicWave.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DIR_Y = SynchedEntityData.defineId(SonicWave.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DIR_Z = SynchedEntityData.defineId(SonicWave.class, EntityDataSerializers.FLOAT);

    @Nullable
    private UUID ownerId;
    private final Set<Integer> hit = new HashSet<>();

    public SonicWave(EntityType<? extends SonicWave> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public static SonicWave create(ServerLevel level, Player owner, Vec3 pos, Vec3 dir) {
        SonicWave w = new SonicWave(PXEntities.ONDA.get(), level);
        w.setPos(pos.x, pos.y, pos.z);
        w.ownerId = owner.getUUID();
        Vec3 n = dir.normalize();
        w.entityData.set(DIR_X, (float) n.x);
        w.entityData.set(DIR_Y, (float) n.y);
        w.entityData.set(DIR_Z, (float) n.z);
        PXProjectile.face(w, n);
        return w;
    }

    public Vec3 direction() {
        return new Vec3(this.entityData.get(DIR_X), this.entityData.get(DIR_Y), this.entityData.get(DIR_Z));
    }

    /** Radio de la onda en un momento de su vida (también lo usa el cliente para dibujarla). */
    public static float radius(float age) {
        float t = Math.min(1.0F, age / LIFE);
        return START_RADIUS + (END_RADIUS - START_RADIUS) * (float) Math.sqrt(t);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DIR_X, 0.0F);
        builder.define(DIR_Y, 0.0F);
        builder.define(DIR_Z, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 d = direction().scale(SPEED);
        this.setPos(this.getX() + d.x, this.getY() + d.y, this.getZ() + d.z);
        if (!(this.level() instanceof ServerLevel level)) return;
        if (this.tickCount > LIFE) {
            this.discard();
            return;
        }
        Player owner = this.ownerId == null ? null : level.getPlayerByUUID(this.ownerId);
        DamageSource source = PXDamage.source(level, PXDamage.SONICO, this, owner);
        Vec3 c = this.position();
        double r = radius(this.tickCount);
        // la onda es un disco grueso: alcanza a lo que esté a menos de r del eje y cerca del frente
        for (LivingEntity e : Aim.sphere(level, owner != null ? owner : this, c, r + 1.0)) {
            if (e == owner || !this.hit.add(e.getId())) continue;
            Hits.hurt(e, source, DAMAGE, owner);
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 160.0 * 160.0;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}
}
