package com.proyectox.entity;

import com.proyectox.client.ClientOnly;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXEntities;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.Blast;
import com.proyectox.util.FX;
import com.proyectox.util.Hits;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Zonas de efecto que dejan algunas armas: el charco de la CÁUSTICA (2 de daño cada 0,5 s), la nube de la
 * TEMPESTAD (lanza un rayo de 4 cada 0,3 s a las criaturas que tiene debajo), el tornado del CICLÓN (avanza
 * 3 s, atrae y hace 2 cada 0,25 s), el enjambre del ENJAMBRE (caza a la criatura más cercana durante 5 s,
 * 1 cada 0,25 s), la grieta de la FALLA (recorre el suelo y hace 22 una vez a cada criatura que alcanza) y la
 * torreta CENTINELA (20 s disparando 4 balas por segundo de 5 a los enemigos que ve). Nunca dañan ni atraen a su
 * tirador.
 */
public class Field extends Entity {
    public static final int ACID_POOL = 0;
    public static final int STORM = 1;
    public static final int TORNADO = 2;
    public static final int SWARM = 3;
    public static final int FISSURE = 4;
    public static final int TURRET = 5;

    public static final float POOL_RADIUS = 1.6F;
    public static final float POOL_MAX_RADIUS = 2.6F;
    public static final int POOL_LIFE = 80;
    public static final float POOL_DAMAGE = 2.0F;

    public static final int STORM_LIFE = 80;
    public static final double STORM_RADIUS = 6.0;
    public static final float STORM_DAMAGE = 4.0F;
    public static final int STORM_EVERY = 6;

    public static final int TORNADO_LIFE = 60;
    public static final double TORNADO_SPEED = 0.35;
    public static final double TORNADO_PULL = 5.5;
    public static final double TORNADO_CORE = 2.4;
    public static final double TORNADO_HEIGHT = 5.0;
    public static final float TORNADO_DAMAGE = 2.0F;

    public static final int SWARM_LIFE = 100;
    public static final double SWARM_SPEED = 0.55;
    public static final double SWARM_SEEK = 24.0;
    public static final double SWARM_RADIUS = 1.8;
    public static final float SWARM_DAMAGE = 1.0F;

    public static final int FISSURE_LIFE = 24;
    public static final double FISSURE_STEP = 1.2;
    public static final double FISSURE_RADIUS = 1.6;
    public static final float FISSURE_DAMAGE = 22.0F;

    public static final int TURRET_LIFE = 400;
    public static final double TURRET_RANGE = 24.0;
    public static final float TURRET_DAMAGE = 5.0F;
    public static final int TURRET_EVERY = 5;
    public static final int MAX_TURRETS = 2;
    /** Escala del modelo de la torreta y, en bloques, altura del eje de la cabeza y largo del cañón. */
    public static final float TURRET_SCALE = 1.6F;
    public static final double TURRET_HEAD = 0.7 * TURRET_SCALE;
    public static final double TURRET_BARREL = 0.46 * TURRET_SCALE;

    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(Field.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(Field.class, EntityDataSerializers.FLOAT);
    /** Tick (de su propia cuenta) en que la zona se acaba. */
    private static final EntityDataAccessor<Integer> END = SynchedEntityData.defineId(Field.class, EntityDataSerializers.INT);
    /** Criatura a la que apunta la torreta (-1 = ninguna): el cliente gira la cabeza hacia ella. */
    private static final EntityDataAccessor<Integer> TARGET = SynchedEntityData.defineId(Field.class, EntityDataSerializers.INT);

    @Nullable
    private UUID ownerId;
    private Vec3 heading = Vec3.ZERO;
    private Vec3 home = Vec3.ZERO;
    private int targetId = -1;
    /** Criaturas ya alcanzadas por la grieta (solo una vez cada una). */
    private final Set<Integer> struck = new HashSet<>();
    /** Cliente: orientación de la cabeza de la torreta (grados; yaw = atan2(x, z), pitch positivo hacia arriba). */
    private float headYaw, headPitch, headYawO, headPitchO;

    public Field(EntityType<? extends Field> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public static Field create(ServerLevel level, @Nullable Entity owner, int type, Vec3 pos, Vec3 heading) {
        Field f = new Field(PXEntities.CAMPO.get(), level);
        f.setPos(pos.x, pos.y, pos.z);
        f.entityData.set(TYPE, type);
        f.ownerId = owner != null ? owner.getUUID() : null;
        f.heading = new Vec3(heading.x, 0.0, heading.z);
        if (f.heading.lengthSqr() > 1.0E-6) f.heading = f.heading.normalize();
        f.home = pos;
        f.entityData.set(END, switch (type) {
            case ACID_POOL -> POOL_LIFE;
            case STORM -> STORM_LIFE;
            case TORNADO -> TORNADO_LIFE;
            case FISSURE -> FISSURE_LIFE;
            case TURRET -> TURRET_LIFE;
            default -> SWARM_LIFE;
        });
        if (type == TURRET && f.heading.lengthSqr() > 1.0E-6) f.setYRot((float) Math.toDegrees(Math.atan2(f.heading.x, f.heading.z)));
        f.entityData.set(SIZE, type == ACID_POOL ? POOL_RADIUS : 1.0F);
        return f;
    }

    public int type() {
        return this.entityData.get(TYPE);
    }

    /** Radio del charco (crece si le caen más glóbulos). */
    public float size() {
        return this.entityData.get(SIZE);
    }

    /** Tick en que se acaba la zona (el cliente lo usa para el fundido). */
    public int end() {
        return this.entityData.get(END);
    }

    /** Ticks que le quedan. */
    public int remaining() {
        return end() - this.tickCount;
    }

    /** Criatura a la que apunta la torreta, o -1. */
    public int target() {
        return this.entityData.get(TARGET);
    }

    public boolean ownedBy(Entity e) {
        return this.ownerId != null && this.ownerId.equals(e.getUUID());
    }

    /** Punto de giro de la cabeza de la torreta. */
    public Vec3 headPos() {
        return this.position().add(0.0, TURRET_HEAD, 0.0);
    }

    /** Cliente: la cabeza de la torreta gira suavemente hacia su objetivo (o vigila a un lado y a otro). */
    private void aimHead() {
        this.headYawO = this.headYaw;
        this.headPitchO = this.headPitch;
        float yaw;
        float pitch;
        int id = target();
        Entity t = id >= 0 ? this.level().getEntity(id) : null;
        if (t != null) {
            Vec3 d = t.getBoundingBox().getCenter().subtract(headPos());
            yaw = (float) Math.toDegrees(Math.atan2(d.x, d.z));
            pitch = (float) Math.toDegrees(Math.atan2(d.y, Math.sqrt(d.x * d.x + d.z * d.z)));
        } else {
            yaw = this.getYRot() + 40.0F * Mth.sin(this.tickCount * 0.04F);
            pitch = 0.0F;
        }
        if (this.tickCount <= 1) {
            this.headYaw = this.headYawO = yaw;
            this.headPitch = this.headPitchO = pitch;
            return;
        }
        this.headYaw += Mth.wrapDegrees(yaw - this.headYaw) * 0.4F;
        this.headPitch += (pitch - this.headPitch) * 0.4F;
    }

    public float headYaw(float partialTick) {
        return Mth.rotLerp(partialTick, this.headYawO, this.headYaw);
    }

    public float headPitch(float partialTick) {
        return Mth.lerp(partialTick, this.headPitchO, this.headPitch);
    }

    /** La torreta se pliega y desaparece. */
    public void retire(ServerLevel level) {
        if (!this.isAlive()) return;
        if (type() == TURRET) {
            Fx.point(level, Fx.DEPLOY, Fx.CENTINELA, this.position(), new Vec3(0.0, 1.0, 0.0), 0.0F, this.getId());
            FX.sound(level, this.position(), PXSounds.CENTINELA_RETIRAR.get(), 1.0F, 1.0F);
        }
        this.discard();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(TYPE, ACID_POOL);
        builder.define(SIZE, 1.0F);
        builder.define(END, 60);
        builder.define(TARGET, -1);
    }

    /** Un glóbulo nuevo cae sobre el charco: dura más y se ensancha un poco. */
    public void refresh() {
        this.entityData.set(END, this.tickCount + POOL_LIFE);
        this.entityData.set(SIZE, Math.min(POOL_MAX_RADIUS, size() + 0.12F));
    }

    @Nullable
    private Entity owner(ServerLevel level) {
        return this.ownerId == null ? null : level.getEntity(this.ownerId);
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level)) {
            if (type() == TURRET) aimHead();
            ClientOnly.fieldTick(this);
            return;
        }
        if (this.tickCount > end()) {
            if (type() == TURRET) retire(level);
            else this.discard();
            return;
        }
        Entity owner = owner(level);
        Entity filter = owner != null ? owner : this;
        switch (type()) {
            case ACID_POOL -> pool(level, owner, filter);
            case STORM -> storm(level, owner, filter);
            case TORNADO -> tornado(level, owner, filter);
            case FISSURE -> fissure(level, owner, filter);
            case TURRET -> turret(level, owner);
            default -> swarm(level, owner, filter);
        }
    }

    // ------------------------------------------------------------------ CÁUSTICA

    private void pool(ServerLevel level, @Nullable Entity owner, Entity filter) {
        if (this.tickCount % 10 != 0) return;
        Vec3 c = this.position();
        double r = size();
        DamageSource ds = PXDamage.source(level, PXDamage.ACIDO, this, owner);
        AABB box = new AABB(c.x - r - 1, c.y - 0.5, c.z - r - 1, c.x + r + 1, c.y + 1.4, c.z + r + 1);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, x -> Aim.targetable(filter, x))) {
            double dx = e.getX() - c.x;
            double dz = e.getZ() - c.z;
            if (Math.sqrt(dx * dx + dz * dz) <= r + e.getBbWidth() * 0.5) Hits.hurt(e, ds, POOL_DAMAGE, owner);
        }
    }

    // ------------------------------------------------------------------ TEMPESTAD

    private void storm(ServerLevel level, @Nullable Entity owner, Entity filter) {
        if (this.tickCount < 8 || this.tickCount % STORM_EVERY != 0) return;
        Vec3 c = this.position();
        AABB box = new AABB(c.x - STORM_RADIUS, c.y - 18.0, c.z - STORM_RADIUS, c.x + STORM_RADIUS, c.y + 1.0, c.z + STORM_RADIUS);
        List<LivingEntity> under = level.getEntitiesOfClass(LivingEntity.class, box, x -> Aim.targetable(filter, x));
        LivingEntity victim = under.isEmpty() ? null : under.get(this.random.nextInt(under.size()));
        Vec3 target;
        if (victim != null) {
            target = victim.getBoundingBox().getCenter();
        } else {
            double a = this.random.nextDouble() * Math.PI * 2.0;
            double d = this.random.nextDouble() * 4.5;
            target = Blast.ground(level, this, c.add(Math.cos(a) * d, -0.5, Math.sin(a) * d), 18.0);
            if (target == null) return;
        }
        Vec3 from = c.add((this.random.nextDouble() - 0.5) * 3.0, -0.6, (this.random.nextDouble() - 0.5) * 3.0);
        DamageSource ds = PXDamage.source(level, PXDamage.TORMENTA, this, owner);
        if (victim != null) Hits.hurt(victim, ds, STORM_DAMAGE, owner);
        for (LivingEntity e : Aim.sphere(level, filter, target, 1.6)) {
            if (e != victim) Hits.hurt(e, ds, STORM_DAMAGE, owner);
        }
        Fx.line(level, Fx.LIGHTNING, Fx.TEMPESTAD, from, target, 1.0F, -1);
        FX.sound(level, target, PXSounds.TEMPESTAD_RAYO.get(), 1.6F, 0.85F + this.random.nextFloat() * 0.35F);
    }

    // ------------------------------------------------------------------ CICLÓN

    private void tornado(ServerLevel level, @Nullable Entity owner, Entity filter) {
        Vec3 p = this.position();
        if (this.heading.lengthSqr() > 1.0E-6) {
            Vec3 next = p.add(this.heading.scale(TORNADO_SPEED));
            BlockHitResult g = level.clip(new ClipContext(next.add(0.0, 2.5, 0.0), next.subtract(0.0, 4.0, 0.0),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (g.getType() != HitResult.Type.MISS) next = new Vec3(next.x, g.getLocation().y, next.z);
            BlockPos wall = BlockPos.containing(next.x, next.y + 0.6, next.z);
            if (level.getBlockState(wall).getCollisionShape(level, wall).isEmpty()) {
                this.setPos(next.x, next.y, next.z);
                p = next;
            } else {
                this.heading = Vec3.ZERO;
            }
        }
        if (this.tickCount % 20 == 1) FX.sound(level, p, PXSounds.CICLON_VIENTO.get(), 1.2F, 0.7F + this.random.nextFloat() * 0.2F);
        DamageSource ds = PXDamage.source(level, PXDamage.TORBELLINO, this, owner);
        boolean hitTick = this.tickCount % 5 == 0;
        AABB box = new AABB(p.x - TORNADO_PULL, p.y - 1.0, p.z - TORNADO_PULL, p.x + TORNADO_PULL, p.y + TORNADO_HEIGHT + 1.0, p.z + TORNADO_PULL);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, x -> Aim.targetable(filter, x))) {
            if (e instanceof Player pl && pl.getAbilities().flying) continue;
            double dx = p.x - e.getX();
            double dz = p.z - e.getZ();
            double dh = Math.sqrt(dx * dx + dz * dz);
            if (dh > TORNADO_PULL) continue;
            double rel = e.getY() - p.y;
            // atrae hacia el eje y hace girar; dentro del núcleo lo levanta un poco
            Vec3 inward = dh < 1.0E-3 ? Vec3.ZERO : new Vec3(dx / dh, 0.0, dz / dh);
            Vec3 swirl = new Vec3(-inward.z, 0.0, inward.x);
            double pull = 0.06 + 0.12 * (1.0 - dh / TORNADO_PULL);
            double lift = dh < TORNADO_CORE && rel < 3.2 ? 0.1 : 0.0;
            e.setDeltaMovement(e.getDeltaMovement().scale(0.6).add(inward.scale(pull)).add(swirl.scale(0.16)).add(0.0, lift, 0.0));
            e.resetFallDistance();
            e.hurtMarked = true;
            if (hitTick && dh <= TORNADO_CORE + e.getBbWidth() * 0.5 && rel > -1.0 && rel < TORNADO_HEIGHT) Hits.hurt(e, ds, TORNADO_DAMAGE, owner);
        }
    }

    // ------------------------------------------------------------------ ENJAMBRE

    private void swarm(ServerLevel level, @Nullable Entity owner, Entity filter) {
        Vec3 p = this.position();
        Entity target = this.targetId >= 0 ? level.getEntity(this.targetId) : null;
        if (target == null || !target.isAlive() || this.tickCount % 10 == 0) {
            LivingEntity best = null;
            double bestD = SWARM_SEEK * SWARM_SEEK;
            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new AABB(p, p).inflate(SWARM_SEEK), x -> Aim.targetable(filter, x))) {
                double d = e.distanceToSqr(p);
                if (d < bestD) {
                    bestD = d;
                    best = e;
                }
            }
            target = best;
            this.targetId = best != null ? best.getId() : -1;
        }
        Vec3 goal;
        if (target != null) {
            goal = target.getBoundingBox().getCenter();
        } else {
            float t = this.tickCount * 0.12F;
            goal = this.home.add(Mth.cos(t) * 1.5, 1.4 + Mth.sin(t * 0.7F) * 0.4, Mth.sin(t) * 1.5);
        }
        Vec3 d = goal.subtract(p);
        double len = d.length();
        if (len > 1.0E-3) {
            Vec3 step = d.scale(Math.min(len, SWARM_SPEED) / len);
            this.setPos(p.x + step.x, p.y + step.y, p.z + step.z);
        }
        if (this.tickCount % 5 == 0) {
            DamageSource ds = PXDamage.source(level, PXDamage.ENJAMBRE, this, owner);
            for (LivingEntity e : Aim.sphere(level, filter, this.position(), SWARM_RADIUS)) Hits.hurt(e, ds, SWARM_DAMAGE, owner);
        }
        if (this.tickCount % 16 == 2) FX.sound(level, this.position(), PXSounds.ENJAMBRE_ZUMBIDO.get(), 0.9F, 1.4F + this.random.nextFloat() * 0.3F);
    }

    // ------------------------------------------------------------------ FALLA

    /** La grieta avanza por el suelo (sube escalones de hasta 1,5 bloques) y hace brotar picos de roca. */
    private void fissure(ServerLevel level, @Nullable Entity owner, Entity filter) {
        Vec3 p = this.position();
        if (this.tickCount > 1) {
            Vec3 next = p.add(this.heading.scale(FISSURE_STEP));
            BlockHitResult g = level.clip(new ClipContext(next.add(0.0, 1.6, 0.0), next.subtract(0.0, 3.0, 0.0),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (g.getType() == HitResult.Type.MISS || g.isInside() || this.heading.lengthSqr() < 1.0E-6) {
                // un muro o un precipicio: la grieta se detiene
                this.discard();
                return;
            }
            next = new Vec3(next.x, g.getLocation().y + 0.02, next.z);
            this.setPos(next.x, next.y, next.z);
            p = next;
        }
        Fx.point(level, Fx.ERUPT, Fx.FALLA, p, this.heading, (float) FISSURE_RADIUS, -1);
        if (this.tickCount % 3 == 1) FX.sound(level, p, PXSounds.FALLA_GRIETA.get(), 1.1F, 0.75F + this.random.nextFloat() * 0.3F);
        DamageSource ds = PXDamage.source(level, PXDamage.SISMO, this, owner);
        double r = FISSURE_RADIUS;
        AABB box = new AABB(p.x - r - 1.0, p.y - 1.0, p.z - r - 1.0, p.x + r + 1.0, p.y + 3.0, p.z + r + 1.0);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, x -> Aim.targetable(filter, x))) {
            double dx = e.getX() - p.x;
            double dz = e.getZ() - p.z;
            if (Math.sqrt(dx * dx + dz * dz) > r + e.getBbWidth() * 0.5) continue;
            if (!this.struck.add(e.getId())) continue;
            Hits.hurt(e, ds, FISSURE_DAMAGE, owner);
        }
    }

    // ------------------------------------------------------------------ CENTINELA

    private void turret(ServerLevel level, @Nullable Entity owner) {
        if (owner == null || !owner.isAlive() || owner.level() != level) {
            retire(level);
            return;
        }
        Vec3 head = headPos();
        LivingEntity target = turretTarget(level, owner, head);
        int id = target == null ? -1 : target.getId();
        if (this.entityData.get(TARGET) != id) this.entityData.set(TARGET, id);
        if (target == null || this.tickCount < 12 || this.tickCount % TURRET_EVERY != 0) return;
        Vec3 dir = target.getBoundingBox().getCenter().subtract(head).normalize();
        Vec3 muzzle = head.add(dir.scale(TURRET_BARREL));
        Aim.Ray ray = Aim.pierce(level, this, muzzle, Aim.spread(dir, this.random, 0.6), TURRET_RANGE + 6.0, 1, owner);
        DamageSource ds = PXDamage.source(level, PXDamage.TORRETA, this, owner);
        for (Entity e : ray.hits()) Hits.hurt(e, ds, TURRET_DAMAGE, owner);
        float what = !ray.hits().isEmpty() ? 2.0F : ray.hitBlock() ? 1.0F : 0.0F;
        Fx.line(level, Fx.BULLET, Fx.CENTINELA, muzzle, ray.end(), what, this.getId());
        FX.sound(level, muzzle, PXSounds.CENTINELA_DISPARO.get(), 1.0F, 1.0F + this.random.nextFloat() * 0.15F);
    }

    /** El enemigo visible más cercano (se mantiene el actual mientras siga a tiro). */
    @Nullable
    private LivingEntity turretTarget(ServerLevel level, Entity owner, Vec3 head) {
        double range2 = TURRET_RANGE * TURRET_RANGE;
        Entity cur = this.targetId >= 0 ? level.getEntity(this.targetId) : null;
        if (cur instanceof LivingEntity l && l.isAlive() && this.tickCount % 10 != 0
                && l.getBoundingBox().getCenter().distanceToSqr(head) < range2 && sees(level, head, l)) return l;
        LivingEntity best = null;
        double bestD = range2;
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new AABB(head, head).inflate(TURRET_RANGE),
                x -> Aim.targetable(owner, x) && Aim.threat(owner, x))) {
            double d = e.getBoundingBox().getCenter().distanceToSqr(head);
            if (d < bestD && sees(level, head, e)) {
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
        return distance < 192.0 * 192.0;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(type() == STORM ? 7.0 : 4.0, type() == TORNADO ? 6.0 : 3.0, type() == STORM ? 7.0 : 4.0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}
}
