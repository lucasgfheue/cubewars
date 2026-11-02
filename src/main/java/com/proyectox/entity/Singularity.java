package com.proyectox.entity;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXEntities;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import com.proyectox.util.Hits;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Singularidad del VÓRTICE: vuela hasta 260 bloques. Si toca a una criatura estalla en el acto
 * (20 de daño en radio 4); si choca con un bloque o agota su vuelo abre un vórtice de 2 s que atrae a
 * las criaturas en radio 8 y después colapsa (20 de daño en radio 4,5). Nunca empuja hacia fuera, y
 * el tirador nunca es atraído ni dañado.
 */
public class Singularity extends PXProjectile {
    private static final EntityDataAccessor<Boolean> OPEN = SynchedEntityData.defineId(Singularity.class, EntityDataSerializers.BOOLEAN);
    public static final int FLIGHT_TICKS = 100;
    public static final int VORTEX_TICKS = 40;
    public static final double PULL_RADIUS = 8.0;
    public static final double COLLAPSE_RADIUS = 4.5;
    public static final float DAMAGE = 20.0F;
    public static final double BLAST_RADIUS = 4.0;

    private int openTicks;

    public Singularity(EntityType<? extends Singularity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public static Singularity create(ServerLevel level, LivingEntity owner, Vec3 pos, Vec3 velocity) {
        Singularity s = new Singularity(PXEntities.SINGULARIDAD.get(), level);
        s.launch(owner, pos, velocity);
        s.maxLife = FLIGHT_TICKS + VORTEX_TICKS + 20;
        return s;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OPEN, false);
    }

    public boolean isOpen() {
        return this.entityData.get(OPEN);
    }

    /** Ticks desde que se abrió el vórtice (el cliente lleva su propia cuenta para animar). */
    public int openTicks() {
        return this.openTicks;
    }

    @Override
    public void tick() {
        if (isOpen()) {
            this.baseTick();
            this.setDeltaMovement(Vec3.ZERO);
            this.openTicks++;
            if (this.level() instanceof ServerLevel level) {
                vortex(level);
                if (this.openTicks >= VORTEX_TICKS) collapse(level);
            } else {
                com.proyectox.client.ClientOnly.projectileTick(this);
            }
            return;
        }
        super.tick();
        if (!this.level().isClientSide && this.isAlive() && this.tickCount >= FLIGHT_TICKS) open(this.position());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level() instanceof ServerLevel level && !isOpen()) blast(level, result.getEntity());
    }

    /** Al tocar a una criatura la singularidad estalla en el acto. */
    private void blast(ServerLevel level, Entity direct) {
        Vec3 c = this.position();
        Entity owner = this.getOwner();
        DamageSource source = PXDamage.source(level, PXDamage.COLAPSO, this, owner);
        Hits.hurt(direct, source, DAMAGE, owner);
        for (LivingEntity e : Aim.sphere(level, this, c, BLAST_RADIUS)) {
            if (e == direct || !splashable(e)) continue;
            Hits.hurt(e, source, DAMAGE, owner);
        }
        Fx.explosion(level, Fx.VORTICE, c, (float) BLAST_RADIUS);
        FX.sound(level, c, PXSounds.VORTICE_COLAPSO.get(), 1.8F, 1.1F);
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        // se separa un poco de la cara golpeada para no quedar dentro del bloque
        if (!this.level().isClientSide) open(result.getLocation().add(normal(result).scale(0.8)));
    }

    @Override
    protected void onHit(HitResult result) {
        if (isOpen()) return;
        super.onHit(result);
    }

    private void open(Vec3 at) {
        if (isOpen()) return;
        this.setPos(at.x, at.y, at.z);
        this.setDeltaMovement(Vec3.ZERO);
        this.noPhysics = true;
        this.entityData.set(OPEN, true);
        this.openTicks = 0;
        if (this.level() instanceof ServerLevel level) Fx.point(level, Fx.VORTEX, Fx.VORTICE, at, new Vec3(0, 1, 0), (float) PULL_RADIUS, this.getId());
        FX.sound(this.level(), at, PXSounds.VORTICE_ABRIR.get(), 1.4F, 0.8F);
    }

    private void vortex(ServerLevel level) {
        Vec3 c = this.position();
        Entity owner = this.getOwner();
        AABB box = new AABB(c, c).inflate(PULL_RADIUS);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, x -> x.isAlive() && !x.isSpectator() && x != owner)) {
            if (e instanceof Player p && (p.isCreative() || p.getAbilities().flying)) continue;
            Vec3 to = c.subtract(e.position().add(0.0, e.getBbHeight() * 0.5, 0.0));
            double d = to.length();
            if (d > PULL_RADIUS || d < 0.4) continue;
            double strength = 0.09 + 0.08 * (1.0 - d / PULL_RADIUS);
            e.setDeltaMovement(e.getDeltaMovement().scale(0.85).add(to.normalize().scale(strength)));
            e.resetFallDistance();
            e.hurtMarked = true;
        }
    }

    private void collapse(ServerLevel level) {
        Vec3 c = this.position();
        Entity owner = this.getOwner();
        DamageSource source = PXDamage.source(level, PXDamage.COLAPSO, this, owner);
        for (LivingEntity e : Aim.sphere(level, this, c, COLLAPSE_RADIUS)) {
            if (!splashable(e)) continue;
            Hits.hurt(e, source, DAMAGE, owner);
        }
        Fx.explosion(level, Fx.VORTICE, c, (float) COLLAPSE_RADIUS);
        FX.sound(level, c, PXSounds.VORTICE_COLAPSO.get(), 1.8F, 0.9F);
        this.discard();
    }

    @Override
    protected void expire() {
        if (this.level() instanceof ServerLevel level) collapse(level);
    }
}
