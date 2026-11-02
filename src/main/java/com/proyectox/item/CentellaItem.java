package com.proyectox.item;

import com.proyectox.entity.EnergyBolt;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * PX-21 CENTELLA — revólver de plasma. Cada clic dispara una bala de plasma pesada de 10 de daño que vuela
 * 170 bloques (hasta 5 disparos por segundo si haces clic rápido; manteniendo el gatillo, 2,5 por segundo).
 */
public class CentellaItem extends GunItem {
    public static final float DAMAGE = 10.0F;
    public static final int HOLD_INTERVAL = 8;

    public CentellaItem(Properties properties) {
        super("centella", "PX-21", 0xFFC83A, Fx.CENTELLA, properties);
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
        return 4;
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
        return 0.25F;
    }

    @Override
    public float kick() {
        return 0.7F;
    }

    /** Semiautomático: dispara al pulsar y, si se mantiene, cada 0,4 s. */
    @Override
    public void triggerTick(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming) {
        if (heldTicks == 0 || heldTicks % HOLD_INTERVAL == 0) super.triggerTick(player, stack, level, now, heldTicks, aiming);
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        shot.level().addFreshEntity(EnergyBolt.create(shot.level(), shot.player(), EnergyBolt.SLUG, shot.muzzle(), shot.aimDir(170.0).scale(4.2), DAMAGE, 40));
        FX.sound(shot.level(), shot.player(), PXSounds.CENTELLA_DISPARO.get(), 1.3F, 0.95F + shot.level().random.nextFloat() * 0.1F);
        FX.sound(shot.level(), shot.player(), PXSounds.CENTELLA_TAMBOR.get(), 0.5F, 1.5F);
    }
}
