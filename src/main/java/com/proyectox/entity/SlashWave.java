package com.proyectox.entity;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXEntities;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/** Tajo de media luna de la CIZALLA: atraviesa a todos los enemigos que encuentra. */
public class SlashWave extends PXProjectile {
    private final Set<Integer> hit = new HashSet<>();

    public SlashWave(EntityType<? extends SlashWave> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public static SlashWave create(ServerLevel level, LivingEntity owner, Vec3 pos, Vec3 velocity, float damage) {
        SlashWave w = new SlashWave(PXEntities.TAJO.get(), level);
        w.launch(owner, pos, velocity);
        w.damage = damage;
        w.maxLife = 26;
        return w;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel level && this.isAlive()) {
            // el filo es ancho: además del rayo de movimiento, barre una franja alrededor
            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.2, 0.6, 1.2),
                    x -> x != this.getOwner() && x.isAlive() && !x.isSpectator())) {
                slash(level, e);
            }
        }
    }

    private void slash(ServerLevel level, Entity target) {
        if (!this.hit.add(target.getId())) return;
        strike(level, target, PXDamage.CORTE, this.damage);
        Fx.impact(level, Fx.CIZALLA, target.getBoundingBox().getCenter(), this.heading().reverse());
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && !this.hit.contains(target.getId());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level() instanceof ServerLevel level) slash(level, result.getEntity());
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.level() instanceof ServerLevel level) {
            Fx.impact(level, Fx.CIZALLA, result.getLocation(), normal(result));
            this.discard();
        }
    }
}
