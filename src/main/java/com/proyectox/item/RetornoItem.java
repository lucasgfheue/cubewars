package com.proyectox.item;

import com.proyectox.entity.Piercer;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.world.item.ItemStack;

/**
 * PX-47 RETORNO — lanzador de chakram. El chakram vuela 32 bloques (o hasta chocar con un bloque) cortando a todas
 * las criaturas que cruza (11 de daño) y vuelve a tu mano atravesando lo que encuentre, cortando otra vez (11).
 */
public class RetornoItem extends GunItem {
    public static final double SPEED = 2.0;

    public RetornoItem(Properties properties) {
        super("retorno", "PX-47", 0xFFD84A, Fx.RETORNO, properties);
    }

    @Override
    public int maxCharge() {
        return 6;
    }

    @Override
    public int regenTicks() {
        return 15;
    }

    @Override
    public int interval() {
        return 12;
    }

    @Override
    public double hipSpread() {
        return 0.5;
    }

    @Override
    public double aimSpread() {
        return 0.0;
    }

    @Override
    public float zoom() {
        return 0.2F;
    }

    @Override
    public float kick() {
        return 0.45F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        shot.level().addFreshEntity(Piercer.create(shot.level(), shot.player(), Piercer.CHAKRAM, shot.muzzle(),
                shot.aimDir(SPEED * Piercer.CHAKRAM_OUT).scale(SPEED), Piercer.CHAKRAM_DAMAGE, 120, 0.0F));
        FX.sound(shot.level(), shot.player(), PXSounds.RETORNO_LANZAR.get(), 1.2F, 0.9F + shot.level().random.nextFloat() * 0.2F);
    }
}
