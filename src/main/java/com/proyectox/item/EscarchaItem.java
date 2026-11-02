package com.proyectox.item;

import com.proyectox.entity.EnergyBolt;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.world.item.ItemStack;

/**
 * PX-16 ESCARCHA — proyector criogénico automático: dispara pequeños láseres de hielo (10 por segundo,
 * 4 de daño). Cada impacto ralentiza un poco más al enemigo hasta dejarlo inmóvil.
 */
public class EscarchaItem extends GunItem {
    public EscarchaItem(Properties properties) {
        super("escarcha", "PX-16", 0xA8F0FF, Fx.ESCARCHA, properties);
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
    public int regenDelay() {
        return 10;
    }

    @Override
    public int interval() {
        return 2;
    }

    @Override
    public double hipSpread() {
        return 0.9;
    }

    @Override
    public double aimSpread() {
        return 0.2;
    }

    @Override
    public float zoom() {
        return 0.22F;
    }

    @Override
    public float kick() {
        return 0.16F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        shot.level().addFreshEntity(EnergyBolt.create(shot.level(), shot.player(), EnergyBolt.FROST, shot.muzzle(), shot.aimDir(140.0).scale(4.5), 4.0F, 32));
        if ((heldTicks / 2) % 2 == 0) {
            FX.sound(shot.level(), shot.player(), PXSounds.ESCARCHA_RAYO.get(), 0.6F, 1.5F + shot.level().random.nextFloat() * 0.3F);
        }
    }
}
