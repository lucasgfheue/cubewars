package com.proyectox.item;

import com.proyectox.entity.Orb;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.world.item.ItemStack;

/**
 * PX-29 ESQUIRLA — fusil de fragmentación cristalina. El cristal hace 10 de daño y se parte en 7 esquirlas
 * de 8: si atraviesa a una criatura siguen hacia delante en abanico; si choca con una pared rebotan.
 */
public class EsquirlaItem extends GunItem {
    public EsquirlaItem(Properties properties) {
        super("esquirla", "PX-29", 0xC07CFF, Fx.ESQUIRLA, properties);
    }

    @Override
    public int maxCharge() {
        return 24;
    }

    @Override
    public int regenTicks() {
        return 8;
    }

    @Override
    public int interval() {
        return 8;
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
        return 0.45F;
    }

    @Override
    public float kick() {
        return 0.6F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        shot.level().addFreshEntity(Orb.create(shot.level(), shot.player(), Orb.CRYSTAL, shot.muzzle(), shot.aimDir(180.0).scale(3.6), Orb.CRYSTAL_DAMAGE, 50, 0.0F));
        FX.sound(shot.level(), shot.player(), PXSounds.ESQUIRLA_DISPARO.get(), 1.1F, 1.3F + shot.level().random.nextFloat() * 0.2F);
    }
}
