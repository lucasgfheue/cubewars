package com.proyectox.item;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import net.minecraft.sounds.SoundEvent;

/**
 * PX-36 YUNQUE — pistola pesada semiautomática de gran calibre. Cada clic dispara una bala de 8 de daño que llega a
 * 200 bloques (hasta 5 disparos por segundo haciendo clic; manteniendo el gatillo, 3,3 por segundo).
 */
public class YunqueItem extends BulletGun {
    public static final float DAMAGE = 8.0F;

    public YunqueItem(Properties properties) {
        super("yunque", "PX-36", 0xFF8C3A, Fx.YUNQUE, properties);
    }

    @Override
    public float damage() {
        return DAMAGE;
    }

    @Override
    public int maxCharge() {
        return 28;
    }

    @Override
    public int regenTicks() {
        return 5;
    }

    @Override
    public int regenDelay() {
        return 8;
    }

    @Override
    public int interval() {
        return 4;
    }

    @Override
    public int holdInterval() {
        return 6;
    }

    @Override
    public double range() {
        return 200.0;
    }

    @Override
    public double hipSpread() {
        return 0.6;
    }

    @Override
    public double aimSpread() {
        return 0.0;
    }

    @Override
    public float zoom() {
        return 0.25F;
    }

    @Override
    public float kick() {
        return 0.85F;
    }

    @Override
    protected SoundEvent shotSound() {
        return PXSounds.YUNQUE_DISPARO.get();
    }

    @Override
    protected float shotVolume() {
        return 3.0F;
    }
}
