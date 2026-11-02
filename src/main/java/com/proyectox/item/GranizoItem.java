package com.proyectox.item;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import net.minecraft.sounds.SoundEvent;

/**
 * PX-40 GRANIZO — ametralladora pesada: 20 balas por segundo de 2 de daño con 200 bloques de alcance. Reserva de
 * 400 balas que se recarga sola en cuanto dejas de disparar.
 */
public class GranizoItem extends BulletGun {
    public static final float DAMAGE = 2.0F;

    public GranizoItem(Properties properties) {
        super("granizo", "PX-40", 0xB8E0FF, Fx.GRANIZO, properties);
    }

    @Override
    public float damage() {
        return DAMAGE;
    }

    @Override
    public int maxCharge() {
        return 400;
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
        return 1;
    }

    @Override
    public double range() {
        return 200.0;
    }

    @Override
    public double hipSpread() {
        return 2.2;
    }

    @Override
    public double aimSpread() {
        return 0.8;
    }

    @Override
    public float zoom() {
        return 0.2F;
    }

    @Override
    public float kick() {
        return 0.22F;
    }

    @Override
    protected SoundEvent shotSound() {
        return PXSounds.GRANIZO_DISPARO.get();
    }

    @Override
    protected float shotVolume() {
        return 3.5F;
    }
}
