package com.proyectox.entity;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXEffects;
import com.proyectox.registry.PXEntities;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.Blast;
import com.proyectox.util.FX;
import com.proyectox.util.Hits;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Proyectiles que actúan al chocar: el obús del MORTERO (parábola alta hasta el punto de mira, 35 de daño en
 * radio 5), el sol del SOLAR (de 20 a 60 de daño en radio 3–6 según la carga), la cápsula del ENJAMBRE (suelta el
 * enjambre cazador), el glóbulo de la CÁUSTICA (8 de daño, salpica y deja un charco), el cristal de la ESQUIRLA
 * (10 de daño y se parte en 7 esquirlas de 8), el cohete del RUGIDO (40 en radio 4,5), el meteorito del METEORO
 * (45 en radio 6), la esfera del PULSAR (atraviesa criaturas: a las que toca o alcanza con sus rayos les hace 6 y
 * las paraliza 1 s; al estallar hace 10 en radio 4 y paraliza 3 s a todo lo de alrededor) y la granada de 40 mm del
 * VOLCÁN (cae en parábola y estalla al chocar: 30 de daño en radio 4). Nunca dañan a su tirador.
 */
public class Orb extends PXProjectile {
    public static final int SHELL = 0;
    public static final int SOLAR = 1;
    public static final int POD = 2;
    public static final int ACID = 3;
    public static final int CRYSTAL = 4;
    public static final int SHARD = 5;
    public static final int ROCKET = 6;
    public static final int METEOR = 7;
    public static final int PULSAR = 8;
    public static final int GRENADE40 = 9;

    public static final float SHELL_DAMAGE = 35.0F;
    public static final double SHELL_RADIUS = 5.0;
    /** Gravedad del obús por tick (sin rozamiento: ver {@link #tick()}). */
    public static final double SHELL_GRAVITY = 0.08;
    public static final float SOLAR_MIN = 20.0F;
    public static final float SOLAR_MAX = 60.0F;
    public static final float ACID_DAMAGE = 4.0F;
    public static final double ACID_SPLASH = 1.8;
    public static final float CRYSTAL_DAMAGE = 10.0F;
    public static final float SHARD_DAMAGE = 8.0F;
    public static final int SHARDS = 7;
    public static final float ROCKET_DAMAGE = 40.0F;
    public static final double ROCKET_RADIUS = 4.5;
    public static final float METEOR_DAMAGE = 45.0F;
    public static final double METEOR_RADIUS = 6.0;
    public static final float PULSAR_DAMAGE = 10.0F;
    public static final double PULSAR_RADIUS = 4.0;
    public static final float PULSAR_ZAP = 6.0F;
    public static final double PULSAR_REACH = 6.0;
    public static final int PULSAR_TARGETS = 3;
    public static final int PULSAR_EVERY = 10;
    /** Parálisis de la esfera del PULSAR al tocar (o al alcanzar con un rayo) y al estallar. */
    public static final int PULSAR_STUN = 20;
    public static final int PULSAR_BLAST_STUN = 60;
    /** Radio de contacto de la esfera (más grande que su caja de choque, como se dibuja). */
    public static final double PULSAR_TOUCH = 0.6;
    public static final float GRENADE_DAMAGE = 30.0F;
    public static final double GRENADE_RADIUS = 4.0;
    /** Gravedad de la granada de 40 mm por tick: vuela en parábola, como una granada de verdad. */
    public static final double GRENADE_GRAVITY = 0.03;

    private static final EntityDataAccessor<Integer> KIND = SynchedEntityData.defineId(Orb.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> POWER = SynchedEntityData.defineId(Orb.class, EntityDataSerializers.FLOAT);

    /** Criatura que una esquirla no vuelve a herir (la que partió el cristal). */
    private int ignore = -1;
    /** Criaturas que la esfera del PULSAR ya ha tocado (solo les hace daño la primera vez). */
    private final java.util.Set<Integer> touched = new java.util.HashSet<>();

    public Orb(EntityType<? extends Orb> type, Level level) {
        super(type, level);
    }

    public static Orb create(ServerLevel level, @Nullable Entity owner, int kind, Vec3 pos, Vec3 velocity, float damage, int life, float power) {
        Orb o = new Orb(PXEntities.ORBE.get(), level);
        o.entityData.set(KIND, kind);
        o.entityData.set(POWER, power);
        o.setNoGravity(gravity(kind) <= 0.0);
        o.launch(owner, pos, velocity);
        o.damage = damage;
        o.maxLife = life;
        return o;
    }

    public int kind() {
        return this.entityData.get(KIND);
    }

    /** Carga del sol (0–1). */
    public float power() {
        return this.entityData.get(POWER);
    }

    /** Gravedad efectiva por tick de cada tipo. */
    public static double gravity(int kind) {
        return switch (kind) {
            case SHELL -> SHELL_GRAVITY;
            case ACID -> 0.035;
            case POD -> 0.025;
            case GRENADE40 -> GRENADE_GRAVITY;
            default -> 0.0;   // cohete, meteorito y esfera del PULSAR vuelan rectos
        };
    }

    /** Radio de la explosión del sol según su carga. */
    public static double solarRadius(float power) {
        return 3.0 + 3.0 * power;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(KIND, SHELL);
        builder.define(POWER, 0.0F);
    }

    @Override
    protected double getDefaultGravity() {
        return gravity(kind());
    }

    @Override
    public void tick() {
        super.tick();
        // el obús vuela sin rozamiento para caer justo donde se apuntó
        if (this.isAlive() && kind() == SHELL && !this.isInWater()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(1.0 / 0.99));
        }
        if (this.isAlive() && kind() == PULSAR && this.level() instanceof ServerLevel level) {
            touch(level);
            if (this.tickCount % PULSAR_EVERY == 0) zap(level);
        }
    }

    /** El meteorito y la esfera del PULSAR se dibujan mucho más grandes que su caja de choque. */
    @Override
    public AABB getBoundingBoxForCulling() {
        int k = kind();
        return k == METEOR ? this.getBoundingBox().inflate(3.0) : k == PULSAR || k == SOLAR ? this.getBoundingBox().inflate(1.5) : this.getBoundingBox();
    }

    /** La esfera del PULSAR atraviesa a las criaturas (solo choca con los bloques). */
    @Override
    protected boolean canHitEntity(Entity target) {
        return kind() != PULSAR && target.getId() != this.ignore && super.canHitEntity(target);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!(this.level() instanceof ServerLevel level) || !this.isAlive()) return;
        Entity target = result.getEntity();
        Vec3 at = contact(target);
        switch (kind()) {
            case SHELL -> shellBlast(level, at, target);
            case SOLAR -> sunBlast(level, at, target);
            case POD -> releaseSwarm(level, at);
            case ACID -> {
                strike(level, target, PXDamage.ACIDO, this.damage);
                Vec3 ground = Blast.ground(level, this, target.position().add(0.0, 0.5, 0.0), 6.0);
                splash(level, at, target, ground);
            }
            case CRYSTAL -> {
                strike(level, target, PXDamage.ESQUIRLA, this.damage);
                shatter(level, at, this.heading(), null, target.getId());
            }
            case ROCKET -> blast(level, at, target, ROCKET_RADIUS, PXDamage.COHETE, Fx.RUGIDO, PXSounds.RUGIDO_EXPLOSION.get(), 2.6F);
            case METEOR -> blast(level, at, target, METEOR_RADIUS, PXDamage.METEORITO, Fx.METEORO, PXSounds.METEORO_IMPACTO.get(), 4.0F);
            case GRENADE40 -> blast(level, at, target, GRENADE_RADIUS, PXDamage.GRANADA, Fx.VOLCAN, PXSounds.VOLCAN_EXPLOSION.get(), 5.0F);
            case PULSAR -> {}
            default -> {
                strike(level, target, PXDamage.ESQUIRLA, this.damage);
                Fx.impact(level, Fx.ESQUIRLA, at, this.heading().reverse());
                this.discard();
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!(this.level() instanceof ServerLevel level) || !this.isAlive()) return;
        Vec3 n = normal(result);
        Vec3 at = result.getLocation().add(n.scale(0.2));
        switch (kind()) {
            case SHELL -> shellBlast(level, at, null);
            case SOLAR -> sunBlast(level, at, null);
            case POD -> releaseSwarm(level, result.getLocation().add(n.scale(0.6)));
            case ACID -> splash(level, at, null, n.y > 0.5 ? result.getLocation().add(0.0, 0.02, 0.0) : Blast.ground(level, this, at, 6.0));
            case CRYSTAL -> shatter(level, result.getLocation().add(n.scale(0.15)), this.heading(), n, -1);
            case ROCKET -> blast(level, at, null, ROCKET_RADIUS, PXDamage.COHETE, Fx.RUGIDO, PXSounds.RUGIDO_EXPLOSION.get(), 2.6F);
            case METEOR -> blast(level, at, null, METEOR_RADIUS, PXDamage.METEORITO, Fx.METEORO, PXSounds.METEORO_IMPACTO.get(), 4.0F);
            case PULSAR -> pulsarBlast(level, at);
            case GRENADE40 -> blast(level, at, null, GRENADE_RADIUS, PXDamage.GRANADA, Fx.VOLCAN, PXSounds.VOLCAN_EXPLOSION.get(), 5.0F);
            default -> {
                Fx.impact(level, Fx.ESQUIRLA, result.getLocation(), n);
                this.discard();
            }
        }
    }

    @Override
    protected void expire() {
        if (!(this.level() instanceof ServerLevel level)) return;
        Vec3 p = this.position();
        switch (kind()) {
            case SHELL -> shellBlast(level, p, null);
            case SOLAR -> sunBlast(level, p, null);
            case POD -> releaseSwarm(level, p);
            case ACID -> splash(level, p, null, null);
            case CRYSTAL -> shatter(level, p, this.heading(), null, -1);
            case ROCKET -> blast(level, p, null, ROCKET_RADIUS, PXDamage.COHETE, Fx.RUGIDO, PXSounds.RUGIDO_EXPLOSION.get(), 2.6F);
            case METEOR -> blast(level, p, null, METEOR_RADIUS, PXDamage.METEORITO, Fx.METEORO, PXSounds.METEORO_IMPACTO.get(), 4.0F);
            case PULSAR -> pulsarBlast(level, p);
            case GRENADE40 -> blast(level, p, null, GRENADE_RADIUS, PXDamage.GRANADA, Fx.VOLCAN, PXSounds.VOLCAN_EXPLOSION.get(), 5.0F);
            default -> this.discard();
        }
    }

    // ------------------------------------------------------------------ efectos

    private void shellBlast(ServerLevel level, Vec3 at, @Nullable Entity direct) {
        Blast.damage(level, this, this.getOwner(), at, SHELL_RADIUS, this.damage, direct, PXDamage.MORTERO, 0.5F);
        Fx.explosion(level, Fx.MORTERO, at, (float) SHELL_RADIUS);
        FX.sound(level, at, PXSounds.MORTERO_EXPLOSION.get(), 2.4F, 0.8F + this.random.nextFloat() * 0.1F);
        this.discard();
    }

    /** Explosión genérica (cohete, meteorito, esfera del PULSAR): daño completo al objetivo directo, menor alrededor. */
    private void blast(ServerLevel level, Vec3 at, @Nullable Entity direct, double radius, ResourceKey<DamageType> type, int style, SoundEvent sound,
                       float volume) {
        Blast.damage(level, this, this.getOwner(), at, radius, this.damage, direct, type, 0.5F);
        Fx.explosion(level, style, at, (float) radius);
        FX.sound(level, at, sound, volume, 0.85F + this.random.nextFloat() * 0.15F);
        this.discard();
    }

    /** PULSAR: la esfera atraviesa a las criaturas; a cada una que toca le hace 6 de daño y la paraliza 1 s (una vez). */
    private void touch(ServerLevel level) {
        Entity owner = this.getOwner();
        Vec3 from = new Vec3(this.xo, this.yo, this.zo).add(0.0, this.getBbHeight() * 0.5, 0.0);
        Vec3 to = this.position().add(0.0, this.getBbHeight() * 0.5, 0.0);
        DamageSource ds = null;
        for (LivingEntity e : Aim.segment(level, owner != null ? owner : this, from, to, PULSAR_TOUCH)) {
            if (e == owner || !this.touched.add(e.getId())) continue;
            if (ds == null) ds = PXDamage.source(level, PXDamage.ELECTROCUCION, this, owner);
            Hits.hurt(e, ds, PULSAR_ZAP, owner);
            PXEffects.stun(e, PULSAR_STUN, owner);
            Fx.impact(level, Fx.PULSAR, e.getBoundingBox().getCenter(), this.heading().reverse());
        }
    }

    /** Estallido de la esfera del PULSAR: 10 de daño en radio 4 y parálisis de 3 s a todo lo de alrededor. */
    private void pulsarBlast(ServerLevel level, Vec3 at) {
        Entity owner = this.getOwner();
        Blast.damage(level, this, owner, at, PULSAR_RADIUS, this.damage, null, PXDamage.ELECTROCUCION, 0.5F);
        for (LivingEntity e : Aim.sphere(level, owner != null ? owner : this, at, PULSAR_RADIUS)) {
            if (e != owner) PXEffects.stun(e, PULSAR_BLAST_STUN, owner);
        }
        Fx.explosion(level, Fx.PULSAR, at, (float) PULSAR_RADIUS);
        FX.sound(level, at, PXSounds.PULSAR_EXPLOSION.get(), 2.0F, 0.85F + this.random.nextFloat() * 0.15F);
        this.discard();
    }

    /** PULSAR: rayos a los enemigos más cercanos que la esfera ve (6 de daño y 1 s de parálisis). */
    private void zap(ServerLevel level) {
        Entity owner = this.getOwner();
        Vec3 p = this.position().add(0.0, this.getBbHeight() * 0.5, 0.0);
        List<LivingEntity> near = new ArrayList<>(Aim.sphere(level, owner != null ? owner : this, p, PULSAR_REACH));
        near.removeIf(e -> !Aim.threat(owner, e));
        near.sort(Comparator.comparingDouble(e -> e.getBoundingBox().getCenter().distanceToSqr(p)));
        DamageSource ds = PXDamage.source(level, PXDamage.ELECTROCUCION, this, owner);
        int n = 0;
        for (LivingEntity e : near) {
            if (n >= PULSAR_TARGETS) break;
            Vec3 c = e.getBoundingBox().getCenter();
            if (level.clip(new ClipContext(p, c, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS) continue;
            Hits.hurt(e, ds, PULSAR_ZAP, owner);
            PXEffects.stun(e, PULSAR_STUN, owner);
            Fx.line(level, Fx.ARC, Fx.PULSAR, p, c, 1.2F, -1);
            n++;
        }
        if (n > 0) FX.sound(level, p, PXSounds.PULSAR_DESCARGA.get(), 1.0F, 1.2F + this.random.nextFloat() * 0.4F);
    }

    private void sunBlast(ServerLevel level, Vec3 at, @Nullable Entity direct) {
        float p = power();
        double radius = solarRadius(p);
        float amount = SOLAR_MIN + (SOLAR_MAX - SOLAR_MIN) * p;
        Blast.damage(level, this, this.getOwner(), at, radius, amount, direct, PXDamage.FUSION, 0.55F);
        Fx.explosion(level, Fx.SOLAR, at, (float) radius);
        FX.sound(level, at, PXSounds.SOLAR_EXPLOSION.get(), 1.6F + p, 1.0F - 0.3F * p);
        this.discard();
    }

    private void releaseSwarm(ServerLevel level, Vec3 at) {
        level.addFreshEntity(Field.create(level, this.getOwner(), Field.SWARM, at, Vec3.ZERO));
        Fx.explosion(level, Fx.ENJAMBRE, at, 1.5F);
        FX.sound(level, at, PXSounds.ENJAMBRE_ZUMBIDO.get(), 1.2F, 1.2F);
        this.discard();
    }

    /** Glóbulo de ácido: salpica alrededor (la mitad del daño) y deja un charco o refuerza el que ya hay. */
    private void splash(ServerLevel level, Vec3 at, @Nullable Entity direct, @Nullable Vec3 ground) {
        Entity owner = this.getOwner();
        DamageSource ds = PXDamage.source(level, PXDamage.ACIDO, this, owner);
        for (LivingEntity e : Aim.sphere(level, owner != null ? owner : this, at, ACID_SPLASH)) {
            if (e != direct && e != owner) Hits.hurt(e, ds, this.damage * 0.5F, owner);
        }
        if (ground != null) {
            Field pool = null;
            for (Field f : level.getEntitiesOfClass(Field.class, new AABB(ground, ground).inflate(1.4), f -> f.type() == Field.ACID_POOL)) {
                pool = f;
                break;
            }
            if (pool != null) pool.refresh();
            else level.addFreshEntity(Field.create(level, owner, Field.ACID_POOL, ground, Vec3.ZERO));
        }
        Fx.explosion(level, Fx.CAUSTICA, at, (float) ACID_SPLASH);
        if (this.random.nextInt(3) == 0) FX.sound(level, at, PXSounds.CAUSTICA_SALPICAR.get(), 0.8F, 1.3F + this.random.nextFloat() * 0.4F);
        this.discard();
    }

    /**
     * El cristal se parte en esquirlas: si atravesó a una criatura siguen hacia delante en abanico; si chocó
     * contra un bloque rebotan hacia fuera de la pared.
     */
    private void shatter(ServerLevel level, Vec3 at, Vec3 dir, @Nullable Vec3 normal, int ignoreId) {
        Entity owner = this.getOwner();
        for (int i = 0; i < SHARDS; i++) {
            Vec3 d;
            if (normal == null) {
                d = Aim.spread(dir, this.random, 16.0);
            } else {
                Vec3 refl = dir.subtract(normal.scale(2.0 * dir.dot(normal)));
                d = Aim.spread(refl.add(normal.scale(0.6)).normalize(), this.random, 32.0);
                if (d.dot(normal) < 0.15) d = d.add(normal.scale(0.4)).normalize();
            }
            Orb shard = create(level, owner, SHARD, at.add(d.scale(0.25)), d.scale(2.8), SHARD_DAMAGE, 16, 0.0F);
            shard.ignore = ignoreId;
            level.addFreshEntity(shard);
        }
        Fx.explosion(level, Fx.ESQUIRLA, at, 1.2F);
        FX.sound(level, at, PXSounds.ESQUIRLA_ESTALLIDO.get(), 1.2F, 1.1F + this.random.nextFloat() * 0.3F);
        this.discard();
    }
}
