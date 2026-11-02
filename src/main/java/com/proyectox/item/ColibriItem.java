package com.proyectox.item;

import com.proyectox.entity.EnergyBolt;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.world.item.ItemStack;

/** PX-19 COLIBRÍ — subfusil de agujas de cristal: 20 agujas por segundo de 6 de daño. */
public class ColibriItem extends GunItem {
    public static final float DAMAGE = 6.0F;

    public ColibriItem(Properties properties) {
        super("colibri", "PX-19", 0xFF8FD0, Fx.COLIBRI, properties);
    }

    @Override
    public int maxCharge() {
        return 240;
    }

    @Override
    public int regenTicks() {
        return 1;
    }

    @Override
    public int interval() {
        return 1;
    }

    @Override
    public double hipSpread() {
        return 1.2;
    }

    @Override
    public double aimSpread() {
        return 0.25;
    }

    @Override
    public float zoom() {
        return 0.18F;
    }

    @Override
    public float kick() {
        return 0.18F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        shot.level().addFreshEntity(EnergyBolt.create(shot.level(), shot.player(), EnergyBolt.NEEDLE, shot.muzzle(), shot.aimDir(160.0).scale(6.0), DAMAGE, 40));
        if (shot.level().getGameTime() % 2L == 0L) {
            FX.sound(shot.level(), shot.player(), PXSounds.COLIBRI_DISPARO.get(), 0.6F, 1.1F + shot.level().random.nextFloat() * 0.2F);
        }
    }
}
