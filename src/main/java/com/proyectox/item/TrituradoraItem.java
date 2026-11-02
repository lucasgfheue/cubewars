package com.proyectox.item;

import com.proyectox.entity.Grenade;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.world.item.ItemStack;

/** PX-11 TRITURADORA — lanzagranadas de racimo: granadas de 30 de daño (radio 4) que sueltan 4 bombetas de 10. */
public class TrituradoraItem extends GunItem {
    public TrituradoraItem(Properties properties) {
        super("trituradora", "PX-11", 0xB7C34A, Fx.TRITURADORA, properties);
    }

    @Override
    public int maxCharge() {
        return 12;
    }

    @Override
    public int regenTicks() {
        return 16;
    }

    @Override
    public int interval() {
        return 14;
    }

    @Override
    public double hipSpread() {
        return 0.6;
    }

    @Override
    public float zoom() {
        return 0.22F;
    }

    @Override
    public float kick() {
        return 0.75F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        shot.level().addFreshEntity(Grenade.create(shot.level(), shot.player(), Grenade.GRENADE, shot.muzzle(),
                shot.aimDir(80.0).scale(2.7).add(0.0, 0.06, 0.0), 60));
        FX.sound(shot.level(), shot.player(), PXSounds.TRITURADORA_LANZAR.get(), 1.2F, 0.9F);
    }
}
