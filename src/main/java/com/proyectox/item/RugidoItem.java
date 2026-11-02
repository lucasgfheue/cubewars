package com.proyectox.item;

import com.proyectox.entity.Orb;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.world.item.ItemStack;

/**
 * PX-42 RUGIDO — lanzacohetes de hombro. Cada cohete vuela recto 216 bloques y estalla al chocar: 40 de daño en
 * radio 4,5 (el objetivo directo recibe el daño completo). Un cohete cada 1,5 s.
 */
public class RugidoItem extends GunItem {
    public static final double SPEED = 2.4;
    public static final int LIFE = 90;

    public RugidoItem(Properties properties) {
        super("rugido", "PX-42", 0xFF6A1A, Fx.RUGIDO, properties);
    }

    @Override
    public int maxCharge() {
        return 6;
    }

    @Override
    public int regenTicks() {
        return 30;
    }

    @Override
    public int interval() {
        return 30;
    }

    @Override
    public double hipSpread() {
        return 0.8;
    }

    @Override
    public double aimSpread() {
        return 0.0;
    }

    @Override
    public float zoom() {
        return 0.3F;
    }

    @Override
    public float kick() {
        return 1.0F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        shot.level().addFreshEntity(Orb.create(shot.level(), shot.player(), Orb.ROCKET, shot.muzzle(), shot.aimDir(SPEED * LIFE).scale(SPEED),
                Orb.ROCKET_DAMAGE, LIFE, 0.0F));
        FX.sound(shot.level(), shot.player(), PXSounds.RUGIDO_DISPARO.get(), 1.8F, 0.85F + shot.level().random.nextFloat() * 0.1F);
    }
}
