package com.proyectox.item;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import net.minecraft.sounds.SoundEvent;

/**
 * PX-39 COYOTE — revólver de seis recámaras: balas de 7 de daño con 220 bloques de alcance y precisión total al
 * apuntar (hasta 4 disparos por segundo haciendo clic; manteniendo el gatillo, casi 3 por segundo).
 */
public class CoyoteItem extends BulletGun {
    public static final float DAMAGE = 7.0F;

    public CoyoteItem(Properties properties) {
        super("coyote", "PX-39", 0xE8B070, Fx.COYOTE, properties);
    }

    @Override
    public float damage() {
        return DAMAGE;
    }

    @Override
    public int maxCharge() {
        return 36;
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
        return 5;
    }

    @Override
    public int holdInterval() {
        return 7;
    }

    @Override
    public double range() {
        return 220.0;
    }

    @Override
    public double hipSpread() {
        return 0.4;
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
        return 0.75F;
    }

    @Override
    protected SoundEvent shotSound() {
        return PXSounds.COYOTE_DISPARO.get();
    }

    @Override
    protected float shotVolume() {
        return 3.0F;
    }
}
