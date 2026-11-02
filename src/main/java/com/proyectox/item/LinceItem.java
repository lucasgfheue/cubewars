package com.proyectox.item;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import com.proyectox.util.Scheduler;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.ItemStack;

/**
 * PX-41 LINCE — fusil de francotirador de cerrojo con visor: balas de 20 de daño con 320 bloques de alcance y
 * precisión total al apuntar; un disparo cada 1,2 s.
 */
public class LinceItem extends BulletGun {
    public static final float DAMAGE = 20.0F;

    public LinceItem(Properties properties) {
        super("lince", "PX-41", 0x40FFD0, Fx.LINCE, properties);
    }

    @Override
    public float damage() {
        return DAMAGE;
    }

    @Override
    public int maxCharge() {
        return 12;
    }

    @Override
    public int regenTicks() {
        return 15;
    }

    @Override
    public int regenDelay() {
        return 10;
    }

    @Override
    public int interval() {
        return 24;
    }

    @Override
    public double range() {
        return 320.0;
    }

    @Override
    public double hipSpread() {
        return 0.3;
    }

    @Override
    public double aimSpread() {
        return 0.0;
    }

    @Override
    public float zoom() {
        return 0.82F;
    }

    @Override
    public float kick() {
        return 1.0F;
    }

    @Override
    public boolean scoped() {
        return true;
    }

    @Override
    public ResourceKey<DamageType> damageType() {
        return PXDamage.FRANCOTIRADOR;
    }

    @Override
    protected SoundEvent shotSound() {
        return PXSounds.LINCE_DISPARO.get();
    }

    @Override
    protected float shotVolume() {
        return 5.0F;
    }

    /** Se acciona el cerrojo medio segundo después de cada disparo. */
    @Override
    protected void afterShot(Shot shot, ItemStack stack) {
        ServerPlayer player = shot.player();
        Scheduler.schedule(shot.level(), 10, () -> {
            if (player.isAlive()) FX.sound(player.level(), player, PXSounds.LINCE_CERROJO.get(), 0.8F, 1.0F);
        });
    }
}
