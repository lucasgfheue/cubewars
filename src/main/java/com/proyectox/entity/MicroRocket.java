package com.proyectox.entity;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXEntities;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import com.proyectox.util.Hits;
import javax.annotation.Nullable;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import com.proyectox.item.ColmenaItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Microcohete dirigible de la COLMENA: acelera, persigue al objetivo fijado o, si no tiene, sigue el
 * punto al que apunta su tirador mientras sostenga la COLMENA; estalla con 20 de daño (también en un
 * radio de 2,5 bloques, menos cuanto más lejos). Nunca daña ni empuja a su tirador.
 */
public class MicroRocket extends PXProjectile {
    public static final float DAMAGE = 20.0F;
    private static final double SPLASH_RADIUS = 2.5;
    private static final double MAX_SPEED = 3.8;

    private int targetId = -1;
    @Nullable
    private Vec3 aimPoint;
    private boolean exploded;

    public MicroRocket(EntityType<? extends MicroRocket> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.keepSpeed = false;
    }

    public static MicroRocket create(ServerLevel level, LivingEntity owner, Vec3 pos, Vec3 velocity, @Nullable LivingEntity target, Vec3 aimPoint) {
        MicroRocket rocket = new MicroRocket(PXEntities.MICROCOHETE.get(), level);
        rocket.launch(owner, pos, velocity);
        rocket.targetId = target == null ? -1 : target.getId();
        rocket.aimPoint = aimPoint;
        rocket.maxLife = 140;
        return rocket;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        if (!this.level().isClientSide) steer();
        super.tick();
    }

    private void steer() {
        Vec3 v = this.getDeltaMovement();
        double speed = Math.min(MAX_SPEED, Math.max(1.1, v.length()) + 0.16);
        Vec3 dir = v.lengthSqr() < 1.0E-6 ? new Vec3(0, 0, 1) : v.normalize();
        Vec3 want = null;
        Entity target = this.targetId >= 0 ? this.level().getEntity(this.targetId) : null;
        if (!(target instanceof LivingEntity l && l.isAlive()) && this.getOwner() instanceof Player owner && owner.isAlive()
                && owner.getMainHandItem().getItem() instanceof ColmenaItem) {
            // cohete dirigible: sigue el punto de mira actual de su tirador
            this.aimPoint = Aim.lookPoint(owner, ColmenaItem.RANGE);
        }
        if (target instanceof LivingEntity living && living.isAlive()) {
            want = living.getBoundingBox().getCenter().subtract(this.position());
        } else if (this.aimPoint != null) {
            want = this.aimPoint.subtract(this.position());
            if (want.lengthSqr() < 4.0) this.aimPoint = null;
        }
        if (want != null && want.lengthSqr() > 1.0E-6 && this.tickCount > 1) {
            double turn = Math.min(0.55, 0.18 + this.tickCount * 0.03);
            dir = dir.scale(1.0 - turn).add(want.normalize().scale(turn)).normalize();
        }
        this.setDeltaMovement(dir.scale(speed));
        face(this, this.getDeltaMovement());
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && !(target instanceof MicroRocket);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level().isClientSide || this.exploded) return;
        detonate(result instanceof EntityHitResult ehr ? ehr.getEntity() : null);
    }

    @Override
    protected void expire() {
        detonate(null);
    }

    private void detonate(@Nullable Entity direct) {
        if (this.exploded || !(this.level() instanceof ServerLevel level)) return;
        this.exploded = true;
        Vec3 p = this.position();
        Entity owner = this.getOwner();
        DamageSource source = PXDamage.source(level, PXDamage.MICROCOHETE, this, owner);
        if (direct != null) Hits.hurt(direct, source, DAMAGE, owner);
        for (LivingEntity e : Aim.sphere(level, this, p, SPLASH_RADIUS)) {
            if (e == direct || !splashable(e)) continue;
            double d = e.getBoundingBox().getCenter().distanceTo(p);
            Hits.hurt(e, source, (float) (DAMAGE * Math.max(0.5, 1.0 - d / (SPLASH_RADIUS * 2.0))), owner);
        }
        Fx.explosion(level, Fx.COLMENA, p, (float) SPLASH_RADIUS);
        FX.sound(level, p, PXSounds.COLMENA_EXPLOSION.get(), 1.6F, 0.95F + this.random.nextFloat() * 0.2F);
        this.discard();
    }
}
