package com.proyectox.item;

import com.proyectox.entity.Sticky;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.world.item.ItemStack;

/**
 * PX-22 ARPÓN — fusil de arpones de fusión. El arpón se clava en la criatura o en la pared (18 de daño) y
 * un segundo después su carga de fusión estalla: 10 de daño en radio 3,5.
 */
public class ArponItem extends GunItem {
    public ArponItem(Properties properties) {
        super("arpon", "PX-22", 0x39D6FF, Fx.ARPON, properties);
    }

    @Override
    public int maxCharge() {
        return 10;
    }

    @Override
    public int regenTicks() {
        return 16;
    }

    @Override
    public int interval() {
        return 16;
    }

    @Override
    public double hipSpread() {
        return 0.3;
    }

    @Override
    public double aimSpread() {
        return 0.0;
    }

    @Override
    public float zoom() {
        return 0.3F;
    }

    @Override
    public float kick() {
        return 0.8F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        shot.level().addFreshEntity(Sticky.create(shot.level(), shot.player(), Sticky.HARPOON, shot.muzzle(), shot.aimDir(170.0).scale(3.8), 45));
        FX.sound(shot.level(), shot.player(), PXSounds.ARPON_DISPARO.get(), 1.3F, 0.75F);
    }
}
