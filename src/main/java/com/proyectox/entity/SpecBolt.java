package com.proyectox.entity;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXEffects;
import com.proyectox.registry.PXEntities;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.Blast;
import com.proyectox.util.FX;
import com.proyectox.util.FallGuard;
import com.proyectox.util.Hits;
import com.proyectox.util.Targets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Proyectiles de la serie v7 (PX-66…PX-101): láseres, discos, granadas EMP, drones, clavos, sierras, varillas, cócteles
 * molotov, bidones, chatarra que rebota, garfios, esporas, larvas, orbes y flechas de luz. Su aspecto y su comportamiento
 * dependen del arma que los dispara ({@link #style()} y {@link BoltSpec}). Nunca dañan a su tirador.
 */
public class SpecBolt extends PXProjectile {
    private static final EntityDataAccessor<Integer> STYLE = SynchedEntityData.defineId(SpecBolt.class, EntityDataSerializers.INT);
    private static final Vec3 UP = new Vec3(0.0, 1.0, 0.0);

    /** Criaturas ya alcanzadas (no vuelve a golpearlas). */
    private final Set<Integer> struck = new HashSet<>();
    /** Discos: último tick en que cortó a cada criatura. */
    private final Map<Integer, Integer> cuts = new HashMap<>();
    private int bounces;
    private int pierced;
    private int hops;
    private int targetId = -1;
    @Nullable
    private BoltSpec cached;
    private int cachedStyle = -1;

    public SpecBolt(EntityType<? extends SpecBolt> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public static SpecBolt create(ServerLevel level, @Nullable LivingEntity owner, int style, Vec3 pos, Vec3 velocity, float damage) {
        SpecBolt b = new SpecBolt(PXEntities.PROYECTIL.get(), level);
        b.entityData.set(STYLE, style);
        BoltSpec s = b.spec();
        b.keepSpeed = s.gravity <= 0.0;
        b.launch(owner, pos, velocity);
        b.damage = damage;
        b.maxLife = s.life;
        return b;
    }

    public int style() {
        return this.entityData.get(STYLE);
    }

    public BoltSpec spec() {
        int st = style();
        if (this.cached == null || st != this.cachedStyle) {
            this.cached = BoltSpec.of(st);
            this.cachedStyle = st;
        }
        return this.cached;
    }

    /** Rebotes que lleva (el cliente no lo sabe: solo para el servidor). */
    public int bounces() {
        return this.bounces;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(STYLE, Fx.PIXEL);
    }

    // ------------------------------------------------------------------ vuelo

    @Override
    public void tick() {
        BoltSpec s = spec();
        // la caída se aplica a mano (igual en el servidor y en el cliente) para que cada arma tenga la suya
        this.keepSpeed = s.gravity <= 0.0;
        if (s.gravity > 0.0) this.setDeltaMovement(this.getDeltaMovement().add(0.0, -s.gravity, 0.0));
        if (!this.level().isClientSide && s.homing > 0.0 && this.tickCount >= s.homingDelay) home(s);
        super.tick();
        if (!(this.level() instanceof ServerLevel level) || !this.isAlive()) return;
        if (s.grind) grind(level, s);
        if (s.aura > 0.0 && this.tickCount % 5 == 0) aura(level, s);
        if (s.grapple && (this.getOwner() == null || !this.getOwner().isAlive())) this.discard();
    }

    /** Gira poco a poco hacia la criatura que persigue (manteniendo la velocidad). */
    private void home(BoltSpec s) {
        Entity t = this.targetId >= 0 ? this.level().getEntity(this.targetId) : null;
        if (!(t instanceof LivingEntity l) || !l.isAlive() || this.tickCount % 10 == 0) t = seek(s, 80.0);
        if (t == null) return;
        Vec3 to = t.getBoundingBox().getCenter().subtract(this.position());
        Vec3 v = this.getDeltaMovement();
        double sp = v.length();
        if (to.lengthSqr() < 1.0E-4 || sp < 1.0E-4) return;
        Vec3 dir = v.scale(1.0 / sp);
        Vec3 nd = dir.add(to.normalize().subtract(dir).scale(s.homing)).normalize();
        this.setDeltaMovement(nd.scale(sp));
    }

    /** La criatura más prometedora por delante (las que pelean con el dueño, primero). */
    @Nullable
    private LivingEntity seek(BoltSpec s, double maxAngle) {
        Entity owner = this.getOwner();
        Entity filter = owner != null ? owner : this;
        Vec3 p = this.position();
        Vec3 fwd = this.heading();
        LivingEntity best = null;
        double bestScore = Double.MAX_VALUE;
        for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, new AABB(p, p).inflate(s.seek),
                x -> Aim.targetable(filter, x) && Targets.fair(owner, x) && !this.struck.contains(x.getId()))) {
            Vec3 c = e.getBoundingBox().getCenter();
            double dist = c.distanceTo(p);
            if (dist > s.seek) continue;
            double ang = Aim.angleTo(p, fwd, c);
            if (ang > maxAngle) continue;
            double score = dist * (1.0 + ang / 60.0) * (Aim.threat(owner, e) ? 0.6 : 1.0);
            if (score < bestScore) {
                bestScore = score;
                best = e;
            }
        }
        this.targetId = best == null ? -1 : best.getId();
        return best;
    }

    // ------------------------------------------------------------------ impactos

    @Override
    protected boolean canHitEntity(Entity target) {
        if (spec().grind) return false;
        return super.canHitEntity(target) && !this.struck.contains(target.getId());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!(this.level() instanceof ServerLevel level) || !this.isAlive()) return;
        Entity target = result.getEntity();
        if (!this.struck.add(target.getId())) return;
        BoltSpec s = spec();
        Vec3 at = contact(target);
        if (s.explode > 0.0F) {
            explode(level, s, at, target);
            return;
        }
        strike(level, target, s.type, this.damage);
        effects(s, target);
        if (s.field >= 0) {
            burst(level, s, at);
            return;
        }
        if (s.grapple) {
            yank(level, target);
            Fx.impact(level, style(), at, this.heading().reverse());
            this.discard();
            return;
        }
        if (s.chain > 0) chain(level, s, target);
        Fx.impact(level, style(), at, this.heading().reverse());
        if (s.bounceEntities && this.bounces < s.bounces) {
            // la chatarra rebota en la criatura y sigue volando hacia atrás y hacia arriba
            this.bounces++;
            Vec3 v = this.getDeltaMovement();
            Vec3 r = new Vec3(-v.x * 0.6, Math.abs(v.y) * 0.4 + 0.28, -v.z * 0.6);
            this.setDeltaMovement(r);
            face(this, r);
            FX.sound(level, at, PXSounds.CACHARRO_REBOTE.get(), 1.0F, 1.2F + this.random.nextFloat() * 0.3F);
            return;
        }
        if (s.pierce < 0 || this.pierced < s.pierce) {
            this.pierced++;
            return;
        }
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!(this.level() instanceof ServerLevel level) || !this.isAlive()) return;
        BoltSpec s = spec();
        Vec3 n = normal(result);
        Vec3 at = result.getLocation();
        if (s.hop && this.hops < s.maxHops) {
            if (n.y > 0.6) hop(level, s, at);
            else bounce(level, s, at, n, 0.5);
            this.hops++;
            return;
        }
        boolean bursts = s.explode > 0.0F && !s.fuse;
        if (this.bounces < s.bounces && !bursts) {
            this.bounces++;
            bounce(level, s, at, n, s.bounceKeep);
            return;
        }
        if (bursts) {
            explode(level, s, at.add(n.scale(0.15)), null);
            return;
        }
        if (s.fuse) {
            // sin rebotes: se queda quieta hasta que se acaba la mecha
            Vec3 p = at.add(n.scale(0.15));
            this.setPos(p.x, p.y, p.z);
            this.setDeltaMovement(Vec3.ZERO);
            return;
        }
        if (s.field >= 0) {
            burst(level, s, at.add(n.scale(0.1)));
            return;
        }
        if (s.grapple) {
            grapple(level, at);
            return;
        }
        Fx.impact(level, style(), at, n);
        this.discard();
    }

    @Override
    protected void expire() {
        if (this.level() instanceof ServerLevel level) {
            BoltSpec s = spec();
            if (s.fuse && s.explode > 0.0F) {
                explode(level, s, this.position(), null);
                return;
            }
            if (s.field >= 0) {
                burst(level, s, this.position());
                return;
            }
            Fx.impact(level, style(), this.position(), this.heading().reverse());
        }
        this.discard();
    }

    private void bounce(ServerLevel level, BoltSpec s, Vec3 at, Vec3 n, double keep) {
        Vec3 v = this.getDeltaMovement();
        Vec3 r = v.subtract(n.scale(2.0 * v.dot(n))).scale(keep);
        this.setDeltaMovement(r);
        Vec3 p = at.add(n.scale(0.15));
        this.setPos(p.x, p.y, p.z);
        face(this, r);
        if (s.gravity <= 0.0) this.speed = r.length();
        // los botecitos de una granada que ya casi rueda no hacen chispas ni ruido
        if (r.length() < 0.08) return;
        Fx.impact(level, style(), at, n);
        SoundEvent sound = switch (style()) {
            case Fx.VINILO -> PXSounds.VINILO_REBOTE.get();
            case Fx.SERRUCHO -> PXSounds.SIERRA_REBOTE.get();
            case Fx.CACHARRO -> PXSounds.CACHARRO_REBOTE.get();
            case Fx.LARVA -> PXSounds.LARVA_SALTO.get();
            default -> PXSounds.TRITURADORA_REBOTE.get();
        };
        float vol = (float) Math.min(1.0, 0.3 + r.length());
        FX.sound(level, p, sound, vol, 1.1F + this.random.nextFloat() * 0.3F);
    }

    /** LARVA: salta desde el suelo hacia su presa (o sigue hacia delante dando saltitos). */
    private void hop(ServerLevel level, BoltSpec s, Vec3 at) {
        LivingEntity t = seek(s, 180.0);
        Vec3 flat;
        double dist = 2.0;
        if (t != null) {
            Vec3 d = t.position().subtract(at);
            flat = new Vec3(d.x, 0.0, d.z);
            dist = flat.length();
        } else {
            Vec3 v = this.getDeltaMovement();
            flat = new Vec3(v.x, 0.0, v.z);
        }
        flat = flat.lengthSqr() < 1.0E-4 ? Vec3.ZERO : flat.normalize();
        double vh = Math.min(0.55, 0.16 + dist * 0.07);
        Vec3 v = flat.scale(vh).add(0.0, 0.42 + this.random.nextDouble() * 0.08, 0.0);
        Vec3 p = at.add(0.0, 0.12, 0.0);
        this.setPos(p.x, p.y, p.z);
        this.setDeltaMovement(v);
        face(this, v);
        if (this.hops % 2 == 0) FX.sound(level, p, PXSounds.LARVA_SALTO.get(), 0.6F, 1.3F + this.random.nextFloat() * 0.4F);
    }

    private void explode(ServerLevel level, BoltSpec s, Vec3 at, @Nullable Entity direct) {
        Entity owner = this.getOwner();
        Blast.damage(level, this, owner, at, s.explode, s.explodeDamage, direct, s.explodeType, 0.35F);
        int st = style();
        if (s.stun > 0) {
            for (LivingEntity e : Aim.sphere(level, owner != null ? owner : this, at, s.explode)) PXEffects.stun(e, s.stun, owner);
            if (direct instanceof LivingEntity l && l.isAlive()) PXEffects.stun(l, s.stun, owner);
        }
        if (st == Fx.FUSIBLE) {
            Fx.point(level, Fx.PULSE7, st, at, UP, s.explode, -1);
            FX.sound(level, at, PXSounds.FUSIBLE_PULSO.get(), 2.0F, 0.9F + this.random.nextFloat() * 0.2F);
        } else {
            Fx.explosion(level, st, at, s.explode);
            SoundEvent sound = st == Fx.BIDON ? PXSounds.VOLCAN_EXPLOSION.get() : PXSounds.NEXO_EXPLOSION.get();
            FX.sound(level, at, sound, st == Fx.BIDON ? 4.0F : 1.2F, 0.9F + this.random.nextFloat() * 0.2F);
        }
        this.discard();
    }

    /** MOLOTOV, ABISMO y NÉBULA: el proyectil se rompe y deja su zona. */
    private void burst(ServerLevel level, BoltSpec s, Vec3 at) {
        Entity owner = this.getOwner();
        int st = style();
        Vec3 pos = at;
        if (s.field == SpecField.FIRE || s.field == SpecField.CLOUD) {
            Vec3 g = Blast.ground(level, this, at.add(0.0, 0.4, 0.0), 5.0);
            if (g != null) pos = g;
        }
        switch (s.field) {
            case SpecField.FIRE -> {
                for (LivingEntity e : Aim.sphere(level, owner != null ? owner : this, at, 2.2)) e.igniteForSeconds(s.ignite);
                FX.sound(level, at, PXSounds.MOLOTOV_ROMPER.get(), 1.3F, 0.85F + this.random.nextFloat() * 0.2F);
                Fx.point(level, Fx.PULSE7, st, pos, UP, (float) SpecField.FIRE_RADIUS, -1);
            }
            case SpecField.WELL -> {
                FX.sound(level, at, PXSounds.ABISMO_POZO.get(), 1.5F, 0.7F);
                Fx.point(level, Fx.PULSE7, st, pos, UP, (float) SpecField.WELL_RADIUS, -1);
            }
            default -> {
                FX.sound(level, at, PXSounds.NEBULA_ESTALLIDO.get(), 1.3F, 0.7F + this.random.nextFloat() * 0.2F);
                Fx.point(level, Fx.PULSE7, st, pos, UP, (float) SpecField.CLOUD_RADIUS, -1);
            }
        }
        level.addFreshEntity(SpecField.create(level, owner, s.field, pos, this.heading()));
        this.discard();
    }

    private void effects(BoltSpec s, Entity target) {
        if (!(target instanceof LivingEntity l) || !l.isAlive()) return;
        Entity owner = this.getOwner();
        if (s.stun > 0 && s.explode <= 0.0F) PXEffects.stun(l, s.stun, owner);
        if (s.poison > 0) l.addEffect(new MobEffectInstance(MobEffects.POISON, s.poison, s.poisonAmp, false, true), owner);
        if (s.slow > 0) l.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, s.slow, s.slowAmp, false, true), owner);
        if (s.ignite > 0) l.igniteForSeconds(s.ignite);
    }

    /** VOLTIO: la descarga salta a las criaturas cercanas. */
    private void chain(ServerLevel level, BoltSpec s, Entity first) {
        Entity owner = this.getOwner();
        Entity filter = owner != null ? owner : this;
        Set<Integer> done = new HashSet<>();
        done.add(first.getId());
        Entity from = first;
        DamageSource ds = PXDamage.source(level, PXDamage.ELECTROCUCION, this, owner);
        for (int i = 0; i < s.chain; i++) {
            Vec3 c = from.getBoundingBox().getCenter();
            LivingEntity next = null;
            double best = s.chainRange * s.chainRange;
            for (LivingEntity e : Aim.sphere(level, filter, c, s.chainRange)) {
                if (done.contains(e.getId()) || !Targets.fair(owner, e)) continue;
                double d = e.getBoundingBox().getCenter().distanceToSqr(c);
                if (d < best) {
                    best = d;
                    next = e;
                }
            }
            if (next == null) break;
            done.add(next.getId());
            Hits.hurt(next, ds, s.chainDamage, owner);
            Fx.line(level, Fx.ARC, style(), c, next.getBoundingBox().getCenter(), 0.7F, -1);
            from = next;
        }
    }

    /** Discos: cortan todo lo que rozan (cada criatura, como mucho cada 10 ticks). */
    private void grind(ServerLevel level, BoltSpec s) {
        Entity owner = this.getOwner();
        Entity filter = owner != null ? owner : this;
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.45), x -> Aim.targetable(filter, x))) {
            Integer last = this.cuts.get(e.getId());
            if (last != null && this.tickCount - last < 10) continue;
            this.cuts.put(e.getId(), this.tickCount);
            strike(level, e, s.type, this.damage);
            effects(s, e);
            Fx.impact(level, style(), e.getBoundingBox().getCenter(), this.heading().reverse());
            SoundEvent sound = style() == Fx.SERRUCHO ? PXSounds.SIERRA_CORTE.get() : PXSounds.VINILO_REBOTE.get();
            FX.sound(level, this.position(), sound, 0.9F, 1.0F + this.random.nextFloat() * 0.3F);
        }
    }

    /** NÉBULA: daña y frena todo lo que pasa cerca mientras vuela. */
    private void aura(ServerLevel level, BoltSpec s) {
        Entity owner = this.getOwner();
        DamageSource ds = PXDamage.source(level, s.type, this, owner);
        for (LivingEntity e : Aim.sphere(level, owner != null ? owner : this, this.position(), s.aura)) {
            Hits.hurt(e, ds, s.auraDamage, owner);
            if (s.slow > 0) e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, s.slow, s.slowAmp, false, true), owner);
        }
    }

    /** GARFIO contra una criatura: la arrastra hacia el tirador. */
    private void yank(ServerLevel level, Entity target) {
        Entity owner = this.getOwner();
        if (owner == null || !target.isAlive()) return;
        Vec3 d = owner.position().subtract(target.position());
        double len = d.length();
        if (len < 1.5) return;
        double sp = Math.min(1.9, 0.45 + len * 0.12);
        Vec3 v = d.scale(sp / len).add(0.0, 0.32, 0.0);
        target.setDeltaMovement(v);
        target.hurtMarked = true;
        FX.sound(level, target.position(), PXSounds.GARFIO_ENGANCHAR.get(), 1.0F, 1.0F);
        FX.sound(level, owner, PXSounds.GARFIO_TIRAR.get(), 1.0F, 1.0F);
    }

    /** GARFIO contra un bloque: el tirador sale volando hacia el gancho (sin daño por caída al llegar). */
    private void grapple(ServerLevel level, Vec3 at) {
        Entity owner = this.getOwner();
        if (owner instanceof LivingEntity o && o.isAlive() && o.level() == level) {
            Vec3 d = at.subtract(o.getEyePosition());
            double len = d.length();
            if (len > 1.2) {
                double sp = Math.min(2.4, 0.5 + len * 0.13);
                Vec3 v = d.scale(sp / len).add(0.0, 0.32, 0.0);
                o.setDeltaMovement(v);
                o.hurtMarked = true;
                o.resetFallDistance();
                FallGuard.protect(o, 70);
            }
            FX.sound(level, o, PXSounds.GARFIO_TIRAR.get(), 1.0F, 1.0F);
        }
        Fx.impact(level, style(), at, this.heading().reverse());
        FX.sound(level, at, PXSounds.GARFIO_ENGANCHAR.get(), 1.0F, 1.0F);
        this.discard();
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 160.0 * 160.0;
    }
}
