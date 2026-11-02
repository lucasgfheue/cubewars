package com.proyectox.item;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import com.proyectox.util.Hits;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * PX-15 SOPLETE — lanzallamas de plasma: chorro continuo de 20 bloques que hace 3 de daño muy seguido
 * (10 veces por segundo) y deja ardiendo 6 s. El plasma también quema a las criaturas inmunes al fuego.
 */
public class SopleteItem extends GunItem {
    public static final double RANGE = 20.0;
    public static final float DAMAGE = 3.0F;
    public static final double HALF_ANGLE = 10.0;

    public SopleteItem(Properties properties) {
        super("soplete", "PX-15", 0xFF5E1A, Fx.SOPLETE, properties);
    }

    @Override
    public int maxCharge() {
        return 300;
    }

    @Override
    public int regenTicks() {
        return 1;
    }

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public int interval() {
        return 2;
    }

    @Override
    public float zoom() {
        return 0.12F;
    }

    @Override
    public float kick() {
        return 0.12F;
    }

    @Override
    protected boolean muzzleFlash() {
        return false;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        ServerLevel level = shot.level();
        Vec3 eye = shot.eye();
        Vec3 dir = shot.look();
        DamageSource source = PXDamage.source(level, PXDamage.PLASMA, shot.player(), shot.player());
        for (LivingEntity e : Aim.cone(level, shot.player(), eye, dir, RANGE, HALF_ANGLE)) {
            Vec3 c = e.getBoundingBox().getCenter();
            if (level.clip(new ClipContext(eye, c, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shot.player())).getType() != HitResult.Type.MISS) continue;
            Hits.hurt(e, source, DAMAGE, shot.player());
            e.igniteForSeconds(6.0F);
        }
        Vec3 end = Aim.lookPoint(shot.player(), RANGE);
        Fx.line(level, Fx.FLAME, Fx.SOPLETE, shot.muzzle(), end, (float) RANGE, shot.player().getId());
        if ((heldTicks / 2) % 3 == 0) FX.sound(level, shot.player(), PXSounds.SOPLETE_CHORRO.get(), 1.0F, 0.9F + level.random.nextFloat() * 0.2F);
    }
}
