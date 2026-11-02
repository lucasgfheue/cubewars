package com.proyectox.item;

import com.proyectox.entity.EnergyBolt;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.world.item.ItemStack;

/** PX-12 ROTOR — ametralladora rotativa de energía: 20 trazadoras por segundo de 2 de daño, sin arranque. */
public class RotorItem extends GunItem {
    public static final float DAMAGE = 2.0F;

    public RotorItem(Properties properties) {
        super("rotor", "PX-12", 0xB4BCC6, Fx.ROTOR, properties);
    }

    @Override
    public int maxCharge() {
        return 500;
    }

    @Override
    public int regenTicks() {
        return 1;
    }

    @Override
    public int interval() {
        return 1;
    }

    @Override
    public double hipSpread() {
        return 1.6;
    }

    @Override
    public double aimSpread() {
        return 0.5;
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
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        shot.level().addFreshEntity(EnergyBolt.create(shot.level(), shot.player(), EnergyBolt.TRACER, shot.muzzle(), shot.aimDir(160.0).scale(7.0), DAMAGE, 40));
        if (shot.level().getGameTime() % 2L == 0L) {
            FX.sound(shot.level(), shot.player(), PXSounds.ROTOR_DISPARO.get(), 0.8F, 0.95F + shot.level().random.nextFloat() * 0.1F);
        }
    }
}
