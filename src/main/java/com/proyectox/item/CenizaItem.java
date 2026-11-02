package com.proyectox.item;

import com.proyectox.entity.Piercer;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.world.item.ItemStack;

/**
 * PX-32 CENIZA — desintegrador molecular. Proyecta un anillo verde lento (70 bloques) que atraviesa a todas
 * las criaturas que encuentra y les hace 7 de daño a cada una, sin que la detengan escudos ni armaduras.
 */
public class CenizaItem extends GunItem {
    public CenizaItem(Properties properties) {
        super("ceniza", "PX-32", 0x3CFF9E, Fx.CENIZA, properties);
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
    public float zoom() {
        return 0.3F;
    }

    @Override
    public float kick() {
        return 0.75F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        shot.level().addFreshEntity(Piercer.create(shot.level(), shot.player(), Piercer.RING, shot.muzzle(), shot.aimDir(70.0).scale(1.0),
                Piercer.RING_DAMAGE, 70, 0.0F));
        FX.sound(shot.level(), shot.player(), PXSounds.CENIZA_DISPARO.get(), 1.2F, 0.9F);
    }
}
