package com.proyectox.entity;

import com.proyectox.client.ClientOnly;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXEntities;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import com.proyectox.util.Hits;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Zonas de la serie v7: el fuego del MOLOTOV (3 de daño cada 0,5 s y deja ardiendo), el pozo de gravedad del ABISMO
 * (atrae y hace 2 cada 0,25 s), la nebulosa de la NÉBULA (2 cada 0,5 s y frena), la grieta del UMBRAL (atrae desde lejos y
 * hace 3 cada 0,25 s) y el ojo vigilante del OBSERVADOR (flota 15 s y dispara un rayo de 5 cada 0,5 s a los enemigos que
 * ve). Nunca dañan ni atraen a su tirador.
 */
public class SpecField extends Entity {
    public static final int FIRE = 0;
    public static final int WELL = 1;
    public static final int CLOUD = 2;
    public static final int RIFT = 3;
    public static final int EYE = 4;

    public static final double FIRE_RADIUS = 2.5;
    public static final int FIRE_LIFE = 80;
    public static final float FIRE_DAMAGE = 3.0F;

    public static final double WELL_RADIUS = 5.0;
    public static final int WELL_LIFE = 60;
    public static final float WELL_DAMAGE = 2.0F;

    public static final double CLOUD_RADIUS = 3.0;
    public static final int CLOUD_LIFE = 80;
    public static final float CLOUD_DAMAGE = 2.0F;

    public static final double RIFT_RADIUS = 3.0;
    public static final double RIFT_PULL = 6.0;
    public static final int RIFT_LIFE = 60;
    public static final float RIFT_DAMAGE = 3.0F;

    public static final int EYE_LIFE = 300;
    public static final double EYE_RANGE = 20.0;
    public static final float EYE_DAMAGE = 5.0F;
    public static final int EYE_EVERY = 10;
    public static final int MAX_EYES = 2;

    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(SpecField.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> END = SynchedEntityData.defineId(SpecField.class, EntityDataSerializers.INT);
    /** Criatura a la que mira el ojo (-1 = ninguna). */
    private static final EntityDataAccessor<Integer> TARGET = SynchedEntityData.defineId(SpecField.class, EntityDataSerializers.INT);

    @Nullable
    private UUID ownerId;
    private Vec3 home = Vec3.ZERO;
    private int targetId = -1;
    /** Cliente: hacia dónde mira el ojo (vector unitario suavizado). */
    private Vec3 gaze = new Vec3(0.0, 0.0, 1.0);
    private Vec3 gazeO = new Vec3(0.0, 0.0, 1.0);

    public SpecField(EntityType<? extends SpecField> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public static SpecField create(ServerLevel level, @Nullable Entity owner, int type, Vec3 pos, Vec3 heading) {
        SpecField f = new SpecField(PXEntities.ZONA.get(), level);
        f.setPos(pos.x, pos.y, pos.z);
        f.home = pos;
        f.ownerId = owner != null ? owner.getUUID() : null;
        f.entityData.set(TYPE, type);
        f.entityData.set(END, switch (type) {
            case FIRE -> FIRE_LIFE;
            case WELL -> WELL_LIFE;
            case CLOUD -> CLOUD_LIFE;
            case RIFT -> RIFT_LIFE;
            default -> EYE_LIFE;
        });
        Vec3 flat = new Vec3(heading.x, 0.0, heading.z);
        if (flat.lengthSqr() > 1.0E-6) f.setYRot((float) Math.toDegrees(Math.atan2(flat.x, flat.z)));
        return f;
    }

    /** OBSERVADOR: como mucho {@link #MAX_EYES} ojos por jugador; al invocar otro, se cierra el más viejo. */
    public static void limitEyes(ServerLevel level, Entity owner) {
        List<SpecField> eyes = new ArrayList<>(level.getEntitiesOfClass(SpecField.class, owner.getBoundingBox().inflate(160.0),
                f -> f.type() == EYE && f.ownedBy(owner) && f.isAlive()));
        // del más joven al más viejo: se quedan los más jóvenes y se cierran los viejos (deja sitio para el nuevo)
        eyes.sort((a, b) -> Integer.compare(a.tickCount, b.tickCount));
        for (int i = MAX_EYES - 1; i < eyes.size(); i++) eyes.get(i).close(level);
    }

    public int type() {
        return this.entityData.get(TYPE);
    }

    public int end() {
        return this.entityData.get(END);
    }

    public int remaining() {
        return end() - this.tickCount;
    }

    public int target() {
        return this.entityData.get(TARGET);
    }

    public boolean ownedBy(Entity e) {
        return this.ownerId != null && this.ownerId.equals(e.getUUID());
    }

    /** Estilo de efectos del arma que creó la zona. */
    public int style() {
        return switch (type()) {
            case FIRE -> Fx.MOLOTOV;
            case WELL -> Fx.ABISMO;
            case CLOUD -> Fx.NEBULA;
            case RIFT -> Fx.UMBRAL;
            default -> Fx.OBSERVADOR;
        };
    }

    /** Radio de la zona (para dibujarla). */
    public float radius() {
        return switch (type()) {
            case FIRE -> (float) FIRE_RADIUS;
            case WELL -> (float) WELL_RADIUS;
            case CLOUD -> (float) CLOUD_RADIUS;
            case RIFT -> (float) RIFT_RADIUS;
            default -> 0.6F;
        };
    }

    /** Cliente: dirección en la que mira el ojo. */
    public Vec3 gaze(float pt) {
        Vec3 g = this.gazeO.add(this.gaze.subtract(this.gazeO).scale(pt));
        return g.lengthSqr() < 1.0E-6 ? new Vec3(0.0, 0.0, 1.0) : g.normalize();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(TYPE, FIRE);
        builder.define(END, 60);
        builder.define(TARGET, -1);
    }

    @Nullable
    private Entity owner(ServerLevel level) {
        return this.ownerId == null ? null : level.getEntity(this.ownerId);
    }

    /** El ojo se cierra (o la zona se apaga) antes de tiempo. */
    public void close(ServerLevel level) {
        if (!this.isAlive()) return;
        if (type() == EYE) Fx.point(level, Fx.PULSE7, Fx.OBSERVADOR, this.position(), new Vec3(0.0, 1.0, 0.0), 0.8F, this.getId());
        this.discard();
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level)) {
            if (type() == EYE) look();
            ClientOnly.specFieldTick(this);
            return;
        }
        if (this.tickCount > end()) {
            close(level);
            return;
        }
        Entity owner = owner(level);
        Entity filter = owner != null ? owner : this;
        switch (type()) {
            case FIRE -> fire(level, owner, filter);
            case WELL -> pull(level, owner, filter, WELL_RADIUS, WELL_RADIUS, WELL_DAMAGE, 0.07, 0.11, PXDamage.GRAVEDAD);
            case CLOUD -> cloud(level, owner, filter);
            case RIFT -> pull(level, owner, filter, RIFT_PULL, RIFT_RADIUS, RIFT_DAMAGE, 0.09, 0.16, PXDamage.GRAVEDAD);
            default -> eye(level, owner);
        }
    }

    // ------------------------------------------------------------------ MOLOTOV

    private void fire(ServerLevel level, @Nullable Entity owner, Entity filter) {
        if (this.tickCount % 20 == 1) FX.sound(level, this.position(), PXSounds.MOLOTOV_FUEGO.get(), 1.0F, 0.8F + this.random.nextFloat() * 0.3F);
        if (this.tickCount % 10 != 0) return;
        Vec3 c = this.position();
        DamageSource ds = PXDamage.source(level, PXDamage.LLAMA, this, owner);
        AABB box = new AABB(c.x - FIRE_RADIUS - 1.0, c.y - 0.6, c.z - FIRE_RADIUS - 1.0, c.x + FIRE_RADIUS + 1.0, c.y + 2.0, c.z + FIRE_RADIUS + 1.0);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, x -> Aim.targetable(filter, x))) {
            double dx = e.getX() - c.x;
            double dz = e.getZ() - c.z;
            if (Math.sqrt(dx * dx + dz * dz) > FIRE_RADIUS + e.getBbWidth() * 0.5) continue;
            Hits.hurt(e, ds, FIRE_DAMAGE, owner);
            e.igniteForSeconds(4.0F);
        }
    }

    // ------------------------------------------------------------------ NÉBULA

    private void cloud(ServerLevel level, @Nullable Entity owner, Entity filter) {
        if (this.tickCount % 10 != 0) return;
        DamageSource ds = PXDamage.source(level, PXDamage.XENO, this, owner);
        for (LivingEntity e : Aim.sphere(level, filter, this.position().add(0.0, 0.8, 0.0), CLOUD_RADIUS)) {
            Hits.hurt(e, ds, CLOUD_DAMAGE, owner);
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1, false, true), owner);
        }
    }

    // ------------------------------------------------------------------ ABISMO y UMBRAL

    /** Atrae hacia el centro a todo lo que está a menos de {@code pullRadius} y daña cada 0,25 s a lo que está muy cerca. */
    private void pull(ServerLevel level, @Nullable Entity owner, Entity filter, double pullRadius, double hurtRadius, float damage,
                      double base, double extra, net.minecraft.resources.ResourceKey<net.minecraft.world.damagesource.DamageType> type) {
        Vec3 c = this.position();
        boolean hitTick = this.tickCount % 5 == 0;
        DamageSource ds = PXDamage.source(level, type, this, owner);
        if (type() == RIFT && this.tickCount % 15 == 1) FX.sound(level, c, PXSounds.UMBRAL_GRIETA.get(), 1.2F, 0.8F + this.random.nextFloat() * 0.3F);
        if (type() == WELL && this.tickCount % 20 == 1) FX.sound(level, c, PXSounds.ABISMO_POZO.get(), 1.0F, 0.6F + this.random.nextFloat() * 0.2F);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new AABB(c, c).inflate(pullRadius + 1.0), x -> Aim.targetable(filter, x))) {
            if (e instanceof Player pl && pl.getAbilities().flying) continue;
            Vec3 to = c.subtract(e.getBoundingBox().getCenter());
            double d = to.length();
            if (d > pullRadius + e.getBbWidth() * 0.5) continue;
            if (d > 0.4) {
                double k = base + extra * (1.0 - Mth.clamp(d / pullRadius, 0.0, 1.0));
                e.setDeltaMovement(e.getDeltaMovement().scale(0.7).add(to.scale(k / d)));
                e.resetFallDistance();
                e.hurtMarked = true;
            }
            if (hitTick && d <= hurtRadius + e.getBbWidth() * 0.5) Hits.hurt(e, ds, damage, owner);
        }
    }

    // ------------------------------------------------------------------ OBSERVADOR

    private void eye(ServerLevel level, @Nullable Entity owner) {
        if (owner == null || !owner.isAlive() || owner.level() != level) {
            close(level);
            return;
        }
        // flota sobre el sitio donde se invocó, meciéndose un poco
        Vec3 p = this.home.add(0.0, 0.18 * Math.sin(this.tickCount * 0.09), 0.0);
        this.setPos(p.x, p.y, p.z);
        LivingEntity target = eyeTarget(level, owner, p);
        int id = target == null ? -1 : target.getId();
        if (this.entityData.get(TARGET) != id) this.entityData.set(TARGET, id);
        if (target == null || this.tickCount < 10 || this.tickCount % EYE_EVERY != 0) return;
        Vec3 to = target.getBoundingBox().getCenter();
        Vec3 from = p.add(to.subtract(p).normalize().scale(0.45));
        Hits.hurt(target, PXDamage.source(level, PXDamage.XENO, this, owner), EYE_DAMAGE, owner);
        Fx.line(level, Fx.BEAM7, Fx.OBSERVADOR, from, to, 0.7F, -1);
        FX.sound(level, p, PXSounds.OBSERVADOR_RAYO.get(), 0.9F, 1.4F + this.random.nextFloat() * 0.3F);
    }

    /** El enemigo visible más cercano (se mantiene el actual mientras siga a tiro). */
    @Nullable
    private LivingEntity eyeTarget(ServerLevel level, Entity owner, Vec3 at) {
        double range2 = EYE_RANGE * EYE_RANGE;
        Entity cur = this.targetId >= 0 ? level.getEntity(this.targetId) : null;
        if (cur instanceof LivingEntity l && l.isAlive() && this.tickCount % 10 != 5
                && l.getBoundingBox().getCenter().distanceToSqr(at) < range2 && sees(level, at, l)) return l;
        LivingEntity best = null;
        double bestD = range2;
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(EYE_RANGE),
                x -> Aim.targetable(owner, x) && Aim.threat(owner, x))) {
            double d = e.getBoundingBox().getCenter().distanceToSqr(at);
            if (d < bestD && sees(level, at, e)) {
                bestD = d;
                best = e;
            }
        }
        this.targetId = best == null ? -1 : best.getId();
        return best;
    }

    private boolean sees(ServerLevel level, Vec3 from, Entity target) {
        return level.clip(new ClipContext(from, target.getBoundingBox().getCenter(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this))
                .getType() == HitResult.Type.MISS;
    }

    /** Cliente: el ojo gira suavemente hacia su objetivo (o mira alrededor). */
    private void look() {
        this.gazeO = this.gaze;
        int id = target();
        Entity t = id >= 0 ? this.level().getEntity(id) : null;
        Vec3 want;
        if (t != null) {
            want = t.getBoundingBox().getCenter().subtract(this.position());
        } else {
            float a = (float) Math.toRadians(this.getYRot()) + 0.9F * Mth.sin(this.tickCount * 0.035F);
            want = new Vec3(Mth.sin(a), -0.15 + 0.1 * Mth.sin(this.tickCount * 0.05F), Mth.cos(a));
        }
        if (want.lengthSqr() < 1.0E-6) return;
        Vec3 w = want.normalize();
        if (this.tickCount <= 1) {
            this.gaze = this.gazeO = w;
            return;
        }
        Vec3 g = this.gaze.add(w.subtract(this.gaze).scale(0.35));
        this.gaze = g.lengthSqr() < 1.0E-6 ? w : g.normalize();
    }

    // ------------------------------------------------------------------ entidad

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean isAttackable() {
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
    public AABB getBoundingBoxForCulling() {
        double r = type() == EYE ? 1.0 : radius() + 2.0;
        return this.getBoundingBox().inflate(r, r, r);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}
}
