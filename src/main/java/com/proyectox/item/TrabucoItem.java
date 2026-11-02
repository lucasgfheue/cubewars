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
 * PX-38 TRABUCO — escopeta de corredera: cada disparo suelta 8 perdigones de 3,5 de daño (28 si aciertan todos,
 * a quemarropa) con 64 bloques de alcance; un disparo cada 0,75 s.
 */
public class TrabucoItem extends BulletGun {
    public static final float PELLET_DAMAGE = 3.5F;
    public static final int PELLETS = 8;

    public TrabucoItem(Properties properties) {
        super("trabuco", "PX-38", 0xE83A2A, Fx.TRABUCO, properties);
    }

    @Override
    public float damage() {
        return PELLET_DAMAGE;
    }

    @Override
    public int pellets() {
        return PELLETS;
    }

    @Override
    public double pelletSpread() {
        return 5.0;
    }

    @Override
    public int maxCharge() {
        return 16;
    }

    @Override
    public int regenTicks() {
        return 10;
    }

    @Override
    public int regenDelay() {
        return 10;
    }

    @Override
    public int interval() {
        return 15;
    }

    @Override
    public double range() {
        return 64.0;
    }

    @Override
    public double hipSpread() {
        return 1.0;
    }

    @Override
    public double aimSpread() {
        return 0.3;
    }

    @Override
    public float zoom() {
        return 0.2F;
    }

    @Override
    public float kick() {
        return 1.0F;
    }

    @Override
    public ResourceKey<DamageType> damageType() {
        return PXDamage.PERDIGON;
    }

    @Override
    protected SoundEvent shotSound() {
        return PXSounds.TRABUCO_DISPARO.get();
    }

    @Override
    protected float shotVolume() {
        return 3.5F;
    }

    /** La corredera se acciona un poco después de cada disparo. */
    @Override
    protected void afterShot(Shot shot, ItemStack stack) {
        ServerPlayer player = shot.player();
        Scheduler.schedule(shot.level(), 7, () -> {
            if (player.isAlive()) FX.sound(player.level(), player, PXSounds.TRABUCO_CORREDERA.get(), 0.9F, 1.0F);
        });
    }
}
