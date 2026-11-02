package com.proyectox.item;

import com.proyectox.entity.Orb;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

/**
 * PX-27 SOLAR — cañón de fusión. Mantén el gatillo para encender el reactor (2 s) y suéltalo: un sol en
 * miniatura que estalla al chocar. Sin cargar hace 20 de daño en radio 3; con la carga completa, 60 en radio 6.
 */
public class SolarItem extends GunItem {
    public static final int FULL_CHARGE = 40;
    public static final int COOLDOWN = 20;

    public SolarItem(Properties properties) {
        super("solar", "PX-27", 0xFFE45A, Fx.SOLAR, properties);
    }

    @Override
    public int maxCharge() {
        return 8;
    }

    @Override
    public int regenTicks() {
        return 20;
    }

    @Override
    public int regenDelay() {
        return 10;
    }

    @Override
    public int interval() {
        return COOLDOWN;
    }

    @Override
    public int chargeTicks() {
        return FULL_CHARGE;
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
        return 0.3F;
    }

    @Override
    public float kick() {
        return 1.0F;
    }

    public static float chargeFraction(int ticks) {
        return Mth.clamp(ticks / (float) FULL_CHARGE, 0.0F, 1.0F);
    }

    @Override
    public void triggerTick(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming) {
        if (heldTicks == 0 && ready(player, now) && charge(stack, now) > 0) FX.sound(level, player, PXSounds.SOLAR_CARGA.get(), 1.0F, 1.0F);
        if (heldTicks == FULL_CHARGE) FX.sound(level, player, PXSounds.LISTO.get(), 0.8F, 1.3F);
    }

    @Override
    public void triggerRelease(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming) {
        if (player.isSpectator() || !ready(player, now)) return;
        if (!spend(player, stack, level, now, 1)) return;
        delay(player, now, COOLDOWN);
        Shot shot = Shot.of(player, level, aiming, aiming ? aimSpread() : hipSpread(), muzzleForward());
        fire(shot, stack, heldTicks);
        Fx.muzzle(level, player, style(), shot.muzzle(), shot.look());
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        float p = chargeFraction(heldTicks);
        shot.level().addFreshEntity(Orb.create(shot.level(), shot.player(), Orb.SOLAR, shot.muzzle(), shot.aimDir(180.0).scale(1.8), 0.0F, 100, p));
        FX.sound(shot.level(), shot.player(), PXSounds.SOLAR_DISPARO.get(), 1.2F + p, 1.0F - 0.3F * p);
    }
}
