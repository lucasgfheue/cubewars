package com.proyectox.item;

import com.proyectox.entity.SonicWave;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.world.item.ItemStack;

/**
 * PX-17 ECO — cañón sónico: dispara una onda expansiva que avanza 64 bloques atravesando paredes y
 * criaturas mientras se ensancha; hace 6 de daño en área a todo lo que alcanza.
 */
public class EcoItem extends GunItem {
    public EcoItem(Properties properties) {
        super("eco", "PX-17", 0xE6BEFF, Fx.ECO, properties);
    }

    @Override
    public int maxCharge() {
        return 12;
    }

    @Override
    public int regenTicks() {
        return 12;
    }

    @Override
    public int interval() {
        return 14;
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
        shot.level().addFreshEntity(SonicWave.create(shot.level(), shot.player(), shot.muzzle(), shot.look()));
        FX.sound(shot.level(), shot.player(), PXSounds.ECO_ONDA.get(), 2.5F, 0.9F);
    }
}
