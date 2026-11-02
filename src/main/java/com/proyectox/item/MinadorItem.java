package com.proyectox.item;

import com.proyectox.entity.Sticky;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.world.item.ItemStack;

/**
 * PX-31 MINADOR — lanzaminas de proximidad. Las minas se pegan a cualquier superficie, se arman en 0,6 s y
 * estallan cuando una criatura se acerca: 30 de daño en radio 4 (si tocan a una criatura estallan en el acto).
 * Como mucho 8 minas a la vez: al poner la novena, la más vieja estalla.
 */
public class MinadorItem extends GunItem {
    public MinadorItem(Properties properties) {
        super("minador", "PX-31", 0xFF3A3A, Fx.MINADOR, properties);
    }

    @Override
    public int maxCharge() {
        return 8;
    }

    @Override
    public int regenTicks() {
        return 20;
    }

    @Override
    public int interval() {
        return 10;
    }

    @Override
    public double hipSpread() {
        return 0.5;
    }

    @Override
    public float zoom() {
        return 0.25F;
    }

    @Override
    public float kick() {
        return 0.5F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        shot.level().addFreshEntity(Sticky.create(shot.level(), shot.player(), Sticky.MINE, shot.muzzle(), shot.aimDir(48.0).scale(1.6).add(0.0, 0.1, 0.0), 100));
        FX.sound(shot.level(), shot.player(), PXSounds.MINADOR_DISPARO.get(), 1.0F, 0.8F);
    }
}
