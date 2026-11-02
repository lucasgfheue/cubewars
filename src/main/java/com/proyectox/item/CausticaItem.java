package com.proyectox.item;

import com.proyectox.entity.Orb;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.world.item.ItemStack;

/**
 * PX-26 CÁUSTICA — proyector de ácido automático. Glóbulos de ácido (casi 7 por segundo): 4 de daño, la mitad
 * a los que estén al lado, y dejan en el suelo un charco que hace 2 de daño cada 0,5 s durante 4 s.
 */
public class CausticaItem extends GunItem {
    public CausticaItem(Properties properties) {
        super("caustica", "PX-26", 0xB4FF1E, Fx.CAUSTICA, properties);
    }

    @Override
    public int maxCharge() {
        return 120;
    }

    @Override
    public int regenTicks() {
        return 2;
    }

    @Override
    public int regenDelay() {
        return 10;
    }

    @Override
    public int interval() {
        return 3;
    }

    @Override
    public double hipSpread() {
        return 1.6;
    }

    @Override
    public double aimSpread() {
        return 0.6;
    }

    @Override
    public float zoom() {
        return 0.2F;
    }

    @Override
    public float kick() {
        return 0.18F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        shot.level().addFreshEntity(Orb.create(shot.level(), shot.player(), Orb.ACID, shot.muzzle(), shot.aimDir(60.0).scale(2.1).add(0.0, 0.04, 0.0),
                Orb.ACID_DAMAGE, 60, 0.0F));
        if (heldTicks % 6 == 0) FX.sound(shot.level(), shot.player(), PXSounds.CAUSTICA_DISPARO.get(), 0.8F, 1.2F + shot.level().random.nextFloat() * 0.2F);
    }
}
