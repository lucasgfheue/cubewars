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
 * PX-48 MAREA — cañón hidráulico: chorro continuo de agua a presión de 26 bloques que hace 2 de daño 10 veces por
 * segundo a todas las criaturas que toca.
 */
public class MareaItem extends GunItem {
    public static final double RANGE = 26.0;
    public static final float DAMAGE = 2.0F;
    public static final double HALF_ANGLE = 6.0;

    public MareaItem(Properties properties) {
        super("marea", "PX-48", 0x3AB4FF, Fx.MAREA, properties);
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
        return 0.15F;
    }

    @Override
    public float kick() {
        return 0.08F;
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
        DamageSource source = PXDamage.source(level, PXDamage.HIDRO, shot.player(), shot.player());
        for (LivingEntity e : Aim.cone(level, shot.player(), eye, dir, RANGE, HALF_ANGLE)) {
            Vec3 c = e.getBoundingBox().getCenter();
            if (level.clip(new ClipContext(eye, c, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shot.player())).getType() != HitResult.Type.MISS) continue;
            Hits.hurt(e, source, DAMAGE, shot.player());
        }
        Vec3 end = Aim.lookPoint(shot.player(), RANGE);
        Fx.line(level, Fx.WATER, Fx.MAREA, shot.muzzle(), end, (float) RANGE, shot.player().getId());
        if ((heldTicks / 2) % 3 == 0) FX.sound(level, shot.player(), PXSounds.MAREA_CHORRO.get(), 0.9F, 0.9F + level.random.nextFloat() * 0.2F);
        if ((heldTicks / 2) % 4 == 1 && end.distanceTo(eye) < RANGE - 0.5) FX.sound(level, end, PXSounds.MAREA_SALPICAR.get(), 0.7F, 1.0F + level.random.nextFloat() * 0.3F);
    }
}
