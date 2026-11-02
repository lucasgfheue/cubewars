package com.proyectox.entity;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXEntities;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Proyectiles que atraviesan criaturas: el anillo desintegrador de la CENIZA (lento, de casi un bloque de
 * radio; 7 de daño a cada criatura que cruza, sin que la frenen escudos ni armaduras), la flecha de luz de la SAETA
 * (atraviesa hasta 3 criaturas, de 10 a 25 de daño según lo tensada) y el chakram del RETORNO (11 a cada criatura que cruza a la ida y otra
 * vez a la vuelta). Los dos primeros se detienen al chocar con un bloque; el chakram vuelve a la mano.
 */
public class Piercer extends PXProjectile {
    public static final int RING = 0;
    public static final int ARROW = 1;
    public static final int CHAKRAM = 2;

    public static final float RING_DAMAGE = 7.0F;
    public static final double RING_RADIUS = 0.9;
    public static final int ARROW_PIERCE = 3;
    public static final float CHAKRAM_DAMAGE = 11.0F;
    public static final double CHAKRAM_RADIUS = 0.7;
    /** Ticks de ida antes de volver (si no choca antes con un bloque). */
    public static final int CHAKRAM_OUT = 16;
    public static final double CHAKRAM_RETURN_SPEED = 2.2;

    private static final EntityDataAccessor<Integer> KIND = SynchedEntityData.defineId(Piercer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> POWER = SynchedEntityData.defineId(Piercer.class, EntityDataSerializers.FLOAT);
    /** El chakram vuelve hacia su dueño. */
    private static final EntityDataAccessor<Boolean> BACK = SynchedEntityData.defineId(Piercer.class, EntityDataSerializers.BOOLEAN);

    private final Set<Integer> hit = new HashSet<>();

    public Piercer(EntityType<? extends Piercer> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public static Piercer create(ServerLevel level, @Nullable Entity owner, int kind, Vec3 pos, Vec3 velocity, float damage, int life, float power) {
        Piercer p = new Piercer(PXEntities.PERFORANTE.get(), level);
        p.entityData.set(KIND, kind);
        p.entityData.set(POWER, power);
        p.launch(owner, pos, velocity);
        p.damage = damage;
        p.maxLife = life;
        return p;
    }

    public int kind() {
        return this.entityData.get(KIND);
    }

    /** Tensión con la que se disparó la flecha (0–1). */
    public float power() {
        return this.entityData.get(POWER);
    }

    /** Si el chakram ya está volviendo. */
    public boolean returning() {
        return this.entityData.get(BACK);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(KIND, RING);
        builder.define(POWER, 0.0F);
        builder.define(BACK, false);
    }

    /** Las criaturas se alcanzan con un barrido propio (ver {@link #tick()}), no con el choque normal. */
    @Override
    protected boolean canHitEntity(Entity target) {
        return false;
    }

    @Override
    public void tick() {
        if (this.level() instanceof ServerLevel level && this.isAlive()) {
            if (kind() == CHAKRAM && !steerChakram(level)) return;
            Vec3 from = this.position();
            Vec3 to = from.add(this.getDeltaMovement());
            if (returning()) {
                // a la vuelta atraviesa los bloques
                sweep(level, from, to);
            } else {
                BlockHitResult block = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                sweep(level, from, block.getType() == HitResult.Type.MISS ? to : block.getLocation());
            }
            if (!this.isAlive()) return;
        }
        super.tick();
    }

    /**
     * Chakram: tras la ida da la vuelta y persigue a su dueño hasta volver a su mano.
     * @return false si ha desaparecido
     */
    private boolean steerChakram(ServerLevel level) {
        Entity owner = this.getOwner();
        if (owner == null || !owner.isAlive() || owner.level() != level) {
            this.discard();
            return false;
        }
        if (!returning() && this.tickCount >= CHAKRAM_OUT) turnBack(level);
        if (returning()) {
            Vec3 to = owner.getBoundingBox().getCenter().subtract(this.position());
            double dist = to.length();
            if (dist < 1.5) {
                FX.sound(level, owner.position(), PXSounds.RETORNO_ATRAPAR.get(), 1.0F, 1.0F + this.random.nextFloat() * 0.2F);
                this.discard();
                return false;
            }
            this.speed = CHAKRAM_RETURN_SPEED;
            this.setDeltaMovement(to.scale(CHAKRAM_RETURN_SPEED / dist));
        }
        return true;
    }

    private void turnBack(ServerLevel level) {
        if (returning()) return;
        this.entityData.set(BACK, true);
        this.hit.clear();
        FX.sound(level, this.position(), PXSounds.RETORNO_VOLVER.get(), 1.0F, 1.0F + this.random.nextFloat() * 0.2F);
    }

    private void sweep(ServerLevel level, Vec3 from, Vec3 to) {
        Entity owner = this.getOwner();
        boolean ring = kind() == RING;
        boolean chakram = kind() == CHAKRAM;
        List<LivingEntity> found = Aim.segment(level, owner != null ? owner : this, from, to, ring ? RING_RADIUS : chakram ? CHAKRAM_RADIUS : 0.3);
        found.sort(Comparator.comparingDouble(e -> e.getBoundingBox().getCenter().distanceToSqr(from)));
        for (LivingEntity e : found) {
            if (!this.hit.add(e.getId())) continue;
            if (chakram) {
                strike(level, e, PXDamage.CHAKRAM, this.damage);
                Vec3 at = e.getBoundingBox().inflate(0.3).clip(from, to).orElse(e.getBoundingBox().getCenter());
                Fx.impact(level, Fx.RETORNO, at, this.heading().reverse());
                FX.sound(level, at, PXSounds.RETORNO_CORTE.get(), 0.9F, 1.4F + this.random.nextFloat() * 0.3F);
            } else if (ring) {
                strike(level, e, PXDamage.DESINTEGRACION, this.damage);
                Fx.point(level, Fx.DISINTEGRATE, Fx.CENIZA, e.getBoundingBox().getCenter(), this.heading(), e.getBbHeight(), e.getId());
                FX.sound(level, e.position(), PXSounds.CENIZA_DESINTEGRAR.get(), 1.0F, 0.8F + this.random.nextFloat() * 0.3F);
            } else {
                strike(level, e, PXDamage.FLECHA_LUZ, this.damage);
                Vec3 at = e.getBoundingBox().inflate(0.3).clip(from, to).orElse(e.getBoundingBox().getCenter());
                Fx.impact(level, Fx.SAETA, at, this.heading().reverse());
                FX.sound(level, at, PXSounds.SAETA_IMPACTO.get(), 0.9F, 1.3F + this.random.nextFloat() * 0.3F);
                if (this.hit.size() >= ARROW_PIERCE) {
                    this.discard();
                    return;
                }
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (kind() == CHAKRAM) {
            // el chakram rebota en la pared y vuelve (a la vuelta atraviesa los bloques)
            if (!returning() && this.level() instanceof ServerLevel level) {
                Fx.impact(level, Fx.RETORNO, result.getLocation(), normal(result));
                this.setPos(result.getLocation().add(normal(result).scale(0.3)));
                this.setDeltaMovement(Vec3.ZERO);
                turnBack(level);
            }
            return;
        }
        super.onHitBlock(result);
        if (this.level() instanceof ServerLevel level) {
            Fx.impact(level, kind() == RING ? Fx.CENIZA : Fx.SAETA, result.getLocation(), normal(result));
            this.discard();
        }
    }

    @Override
    protected void expire() {
        if (this.level() instanceof ServerLevel level && kind() == RING) {
            Fx.impact(level, Fx.CENIZA, this.position(), this.heading().reverse());
        }
        this.discard();
    }
}
