package com.proyectox.item;

import com.proyectox.entity.SawDisc;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.world.item.ItemStack;

/** PX-20 SIERRA — lanzadiscos: discos de 10 de daño que atraviesan enemigos y rebotan 4 veces. */
public class SierraItem extends GunItem {
    public static final float DAMAGE = 10.0F;

    public SierraItem(Properties properties) {
        super("sierra", "PX-20", 0x2EE6A0, Fx.SIERRA, properties);
    }

    @Override
    public int maxCharge() {
        return 20;
    }

    @Override
    public int regenTicks() {
        return 10;
    }

    @Override
    public int interval() {
        return 8;
    }

    @Override
    public double hipSpread() {
        return 0.5;
    }

    @Override
    public float zoom() {
        return 0.2F;
    }

    @Override
    public float kick() {
        return 0.5F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        shot.level().addFreshEntity(SawDisc.create(shot.level(), shot.player(), shot.muzzle(), shot.aimDir(96.0).scale(2.6), DAMAGE));
        FX.sound(shot.level(), shot.player(), PXSounds.SIERRA_LANZAR.get(), 1.0F, 1.0F + shot.level().random.nextFloat() * 0.1F);
    }
}
