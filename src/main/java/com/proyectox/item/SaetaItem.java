package com.proyectox.item;

import com.proyectox.entity.Piercer;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

/**
 * PX-33 SAETA — arco de energía. Mantén el gatillo para tensar la cuerda de luz (1 s) y suéltalo: una flecha
 * de luz que atraviesa hasta 3 criaturas. De 10 de daño sin tensar a 25 con la tensión completa.
 */
public class SaetaItem extends GunItem {
    public static final int FULL_DRAW = 20;
    public static final float MIN_DAMAGE = 10.0F;
    public static final float MAX_DAMAGE = 25.0F;
    public static final int COOLDOWN = 6;

    public SaetaItem(Properties properties) {
        super("saeta", "PX-33", 0xFFE9A0, Fx.SAETA, properties);
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
        return COOLDOWN;
    }

    @Override
    public int chargeTicks() {
        return FULL_DRAW;
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
        return 0.3F;
    }

    @Override
    public float kick() {
        return 0.4F;
    }

    public static float drawFraction(int ticks) {
        return Mth.clamp(ticks / (float) FULL_DRAW, 0.0F, 1.0F);
    }

    @Override
    public void triggerTick(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming) {
        if (heldTicks == 0 && ready(player, now) && charge(stack, now) > 0) FX.sound(level, player, PXSounds.SAETA_TENSAR.get(), 0.9F, 1.2F);
        if (heldTicks == FULL_DRAW) FX.sound(level, player, PXSounds.LISTO.get(), 0.7F, 1.8F);
    }

    @Override
    public void triggerRelease(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming) {
        if (player.isSpectator() || !ready(player, now)) return;
        if (!spend(player, stack, level, now, 1)) return;
        delay(player, now, COOLDOWN);
        float p = drawFraction(heldTicks);
        double spread = (aiming ? aimSpread() : hipSpread()) * (1.0 - 0.7 * p);
        Shot shot = Shot.of(player, level, aiming, spread, muzzleForward());
        fire(shot, stack, heldTicks);
        Fx.muzzle(level, player, style(), shot.muzzle(), shot.look());
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        float p = drawFraction(heldTicks);
        float damage = MIN_DAMAGE + (MAX_DAMAGE - MIN_DAMAGE) * p;
        shot.level().addFreshEntity(Piercer.create(shot.level(), shot.player(), Piercer.ARROW, shot.muzzle(), shot.aimDir(200.0).scale(3.0 + 2.0 * p),
                damage, 60, p));
        FX.sound(shot.level(), shot.player(), PXSounds.SAETA_DISPARO.get(), 1.0F + 0.4F * p, 1.3F - 0.3F * p);
    }
}
