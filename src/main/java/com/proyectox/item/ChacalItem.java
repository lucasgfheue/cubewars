package com.proyectox.item;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import net.minecraft.sounds.SoundEvent;

/**
 * PX-37 CHACAL — fusil de asalto automático: 10 balas por segundo de 7 de daño con 200 bloques de alcance.
 * Reserva de 120 balas que se recarga sola en cuanto dejas de disparar.
 */
public class ChacalItem extends BulletGun {
    public static final float DAMAGE = 7.0F;

    public ChacalItem(Properties properties) {
        super("chacal", "PX-37", 0xE8D06A, Fx.CHACAL, properties);
    }

    @Override
    public float damage() {
        return DAMAGE;
    }

    @Override
    public int maxCharge() {
        return 120;
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
    public double range() {
        return 200.0;
    }

    @Override
    public double hipSpread() {
        return 1.4;
    }

    @Override
    public double aimSpread() {
        return 0.35;
    }

    @Override
    public float zoom() {
        return 0.3F;
    }

    @Override
    public float kick() {
        return 0.35F;
    }

    @Override
    protected SoundEvent shotSound() {
        return PXSounds.CHACAL_DISPARO.get();
    }

    @Override
    protected float shotVolume() {
        return 3.5F;
    }
}
