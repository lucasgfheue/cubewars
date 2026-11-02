package com.proyectox.item;

import com.proyectox.entity.Orb;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.world.item.ItemStack;

/**
 * PX-25 ENJAMBRE — lanzador de nanoenjambre. Dispara una cápsula que, al chocar, libera un enjambre de
 * nanobots que caza a la criatura más cercana durante 5 s (1 de daño cada 0,25 s a lo que alcanza).
 */
public class EnjambreItem extends GunItem {
    public EnjambreItem(Properties properties) {
        super("enjambre", "PX-25", 0xFFB800, Fx.ENJAMBRE, properties);
    }

    @Override
    public int maxCharge() {
        return 6;
    }

    @Override
    public int regenTicks() {
        return 30;
    }

    @Override
    public int interval() {
        return 20;
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
        return 0.6F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        shot.level().addFreshEntity(Orb.create(shot.level(), shot.player(), Orb.POD, shot.muzzle(), shot.aimDir(90.0).scale(2.4).add(0.0, 0.05, 0.0),
                0.0F, 40, 0.0F));
        FX.sound(shot.level(), shot.player(), PXSounds.ENJAMBRE_DISPARO.get(), 1.2F, 0.7F);
    }
}
