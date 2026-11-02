package com.proyectox.entity;

import com.proyectox.registry.PXEntities;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Dron HALCÓN: sobrevuela al operador, busca amenazas en 32 bloques y dispara pulsos de energía
 * (12 de daño, unos 3 por segundo). Cada operador puede tener 3 drones a la vez; duran 40 s.
 */
public class Drone extends Entity {
    private UUID ownerId;
    private int life;
    private int maxLife;
    private int targetId = -1;
    private int slot;
    private int cooldown;

    public Drone(EntityType<? extends Drone> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public static final int RADIUS = 32;
    public static final float BOLT_DAMAGE = 12.0F;

    public static Drone create(ServerLevel level, Player owner, Vec3 pos, int lifeTicks, int slot) {
        Drone d = new Drone(PXEntities.DRON.get(), level);
        d.setPos(pos.x, pos.y, pos.z);
        d.ownerId = owner.getUUID();
        d.life = lifeTicks;
        d.maxLife = lifeTicks;
        d.slot = slot;
        d.setYRot(owner.getYRot());
        return d;
    }

    public boolean ownedBy(Player player) {
        return player.getUUID().equals(this.ownerId);
    }

    /** Retirada del dron (al acabarse la batería o al desplegar un cuarto). */
    public void retire() {
        if (this.level() instanceof ServerLevel level) {
            com.proyectox.fx.Fx.point(level, com.proyectox.fx.Fx.DRONE, com.proyectox.fx.Fx.HALCON, this.position(), Vec3.ZERO, 0.0F, this.getId());
            FX.sound(level, this.position(), PXSounds.HALCON_RETIRADA.get(), 0.8F, 1.2F);
        }
        this.discard();
    }

    @Nullable
    public Player owner() {
        return this.ownerId == null ? null : this.level().getPlayerByUUID(this.ownerId);
    }

    public int remainingTicks() {
        return Math.max(0, this.life);
    }

    /** Hueco de la formación (0–2). */
    public int slot() {
        return this.slot;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            com.proyectox.client.ClientOnly.droneTick(this);
            return;
        }
        ServerLevel level = (ServerLevel) this.level();
        Player owner = owner();
        if (owner == null || !owner.isAlive() || owner.level() != level || --this.life <= 0 || this.distanceToSqr(owner.position()) > 96 * 96) {
            retire();
            return;
        }
        LivingEntity target = target(level, owner);
        // movimiento: sobre el objetivo o detrás y por encima del hombro del operador
        Vec3 anchor;
        if (target != null) {
            double a = this.tickCount * 0.05;
            anchor = target.position().add(Math.cos(a) * 3.5, target.getBbHeight() + 2.6, Math.sin(a) * 3.5);
        } else {
            // formación sobre el operador: cada dron ocupa su hueco
            Vec3 look = owner.getLookAngle();
            Vec3 side = new Vec3(-look.z, 0, look.x);
            side = side.lengthSqr() < 1.0E-4 ? new Vec3(1, 0, 0) : side.normalize();
            double offset = (this.slot - 1) * 1.6;
            anchor = owner.position().add(side.scale(offset)).add(look.x * -1.0, 2.6 + Math.sin(this.tickCount * 0.1 + this.slot) * 0.15, look.z * -1.0);
        }
        Vec3 to = anchor.subtract(this.position());
        Vec3 v = to.scale(0.12);
        if (v.length() > 0.9) v = v.normalize().scale(0.9);
        this.setDeltaMovement(v);
        this.setPos(this.getX() + v.x, this.getY() + v.y, this.getZ() + v.z);
        // orientación y disparo
        Vec3 face = target != null ? target.getBoundingBox().getCenter().subtract(this.position()) : owner.getLookAngle();
        this.setYRot((float) (Mth.atan2(-face.x, face.z) * (180.0 / Math.PI)));
        if (target != null && --this.cooldown <= 0 && canSee(level, target)) {
            this.cooldown = 6;
            Vec3 muzzle = this.position().add(0, -0.15, 0);
            Vec3 dir = target.getBoundingBox().getCenter().subtract(muzzle).normalize();
            level.addFreshEntity(EnergyBolt.create(level, owner, EnergyBolt.DRONE, muzzle, Aim.spread(dir, this.random, 0.8).scale(4.0), BOLT_DAMAGE, 30));
            com.proyectox.fx.Fx.muzzle(level, this, com.proyectox.fx.Fx.HALCON, muzzle, dir);
            FX.sound(level, muzzle, PXSounds.HALCON_DISPARO.get(), 0.7F, 1.6F + this.random.nextFloat() * 0.2F);
        }
    }

    private boolean canSee(ServerLevel level, Entity target) {
        Vec3 from = this.position();
        Vec3 to = target.getBoundingBox().getCenter();
        return level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    @Nullable
    private LivingEntity target(ServerLevel level, Player owner) {
        Entity cur = this.targetId >= 0 ? level.getEntity(this.targetId) : null;
        if (cur instanceof LivingEntity l && l.isAlive() && l.distanceToSqr(owner.position()) < (RADIUS + 4) * (RADIUS + 4) && this.tickCount % 20 != 0) return l;
        // busca la amenaza más cercana al operador (o quien le acaba de herir)
        LivingEntity attacker = owner.getLastHurtByMob();
        if (attacker != null && attacker.isAlive() && attacker != owner && attacker.distanceToSqr(owner.position()) < RADIUS * RADIUS) {
            this.targetId = attacker.getId();
            return attacker;
        }
        LivingEntity best = null;
        double bestD = Double.MAX_VALUE;
        AABB box = owner.getBoundingBox().inflate(RADIUS);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, x -> x instanceof Enemy && Aim.targetable(owner, x))) {
            double d = e.distanceToSqr(owner.position());
            if (d < bestD && canSee(level, e)) {
                bestD = d;
                best = e;
            }
        }
        this.targetId = best == null ? -1 : best.getId();
        return best;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
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
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 96.0 * 96.0;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}
}
