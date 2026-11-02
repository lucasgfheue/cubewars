package com.proyectox.item;

import com.proyectox.entity.Orb;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.world.item.ItemStack;

/**
 * PX-44 PULSAR — proyector de plasma. Lanza una esfera lenta que atraviesa a las criaturas: a las que toca les hace
 * 6 de daño y las paraliza 1 s, y cada 0,5 s descarga un rayo con el mismo efecto sobre los 3 enemigos más cercanos
 * (6 bloques). Al chocar o apagarse estalla: 10 de daño en radio 4 y parálisis de 3 s a todo lo de alrededor.
 */
public class PulsarItem extends GunItem {
    public static final double SPEED = 0.6;
    public static final int LIFE = 100;

    public PulsarItem(Properties properties) {
        super("pulsar", "PX-44", 0xFF40E0, Fx.PULSAR, properties);
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
        return 24;
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
        return 0.65F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        shot.level().addFreshEntity(Orb.create(shot.level(), shot.player(), Orb.PULSAR, shot.muzzle().add(shot.look().scale(0.4)),
                shot.aimDir(SPEED * LIFE).scale(SPEED), Orb.PULSAR_DAMAGE, LIFE, 0.0F));
        FX.sound(shot.level(), shot.player(), PXSounds.PULSAR_DISPARO.get(), 1.4F, 0.9F + shot.level().random.nextFloat() * 0.1F);
    }
}
