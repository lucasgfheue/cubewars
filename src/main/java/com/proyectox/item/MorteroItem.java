package com.proyectox.item;

import com.proyectox.entity.Orb;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * PX-23 MORTERO — mortero de mano. Lanza un obús en parábola alta que cae exactamente en el punto que marca
 * la mira (hasta 120 bloques): 35 de daño en radio 5.
 */
public class MorteroItem extends GunItem {
    public static final double RANGE = 120.0;

    public MorteroItem(Properties properties) {
        super("mortero", "PX-23", 0xFF7A2A, Fx.MORTERO, properties);
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
    public int interval() {
        return 24;
    }

    @Override
    public double hipSpread() {
        return 1.2;
    }

    @Override
    public double aimSpread() {
        return 0.2;
    }

    @Override
    public float zoom() {
        return 0.3F;
    }

    @Override
    public float kick() {
        return 0.95F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        Aim.Ray ray = Aim.pierce(shot.level(), shot.player(), shot.eye(), shot.look(), RANGE, 1);
        Vec3 target = ray.hits().isEmpty() ? ray.end() : ray.hits().get(0).position();
        // imprecisión: un pequeño desvío alrededor del punto (menor apuntando)
        double off = shot.spread() * 0.6 * shot.level().random.nextDouble();
        double ang = shot.level().random.nextDouble() * Math.PI * 2.0;
        target = target.add(Math.cos(ang) * off, 0.0, Math.sin(ang) * off);
        Vec3 from = shot.muzzle();
        Vec3 d = target.subtract(from);
        double horizontal = Math.sqrt(d.x * d.x + d.z * d.z);
        int ticks = Mth.clamp((int) Math.round(18.0 + horizontal * 0.45), 20, 60);
        // trayectoria sin rozamiento: p(n) = p0 + n·v0 - g·n(n-1)/2
        double g = Orb.SHELL_GRAVITY / 0.99;
        Vec3 v0 = d.scale(1.0 / ticks).add(0.0, g * (ticks - 1) / 2.0, 0.0);
        Entity owner = shot.player();
        shot.level().addFreshEntity(Orb.create(shot.level(), owner, Orb.SHELL, from, v0, Orb.SHELL_DAMAGE, ticks + 200, 0.0F));
        FX.sound(shot.level(), shot.player(), PXSounds.MORTERO_DISPARO.get(), 1.6F, 0.6F + shot.level().random.nextFloat() * 0.1F);
    }
}
