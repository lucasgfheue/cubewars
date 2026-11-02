package com.proyectox.item;

import com.proyectox.entity.Singularity;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.world.item.ItemStack;

/**
 * PX-05 VÓRTICE — proyector gravitacional: dispara una singularidad que vuela hasta 260 bloques.
 * Al tocar a una criatura estalla (20 de daño en área); contra un bloque abre un vórtice que atrae a
 * las criaturas y colapsa (20 de daño).
 */
public class VorticeItem extends GunItem {
    public VorticeItem(Properties properties) {
        super("vortice", "PX-05", 0xAA5AFF, Fx.VORTICE, properties);
    }

    @Override
    public int maxCharge() {
        return 10;
    }

    @Override
    public int regenTicks() {
        return 20;
    }

    @Override
    public int interval() {
        return 12;
    }

    @Override
    public float zoom() {
        return 0.25F;
    }

    @Override
    public float kick() {
        return 0.55F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        shot.level().addFreshEntity(Singularity.create(shot.level(), shot.player(), shot.muzzle(), shot.aimDir(260.0).scale(2.6)));
        FX.sound(shot.level(), shot.player(), PXSounds.VORTICE_DISPARO.get(), 1.2F, 0.9F);
    }
}
