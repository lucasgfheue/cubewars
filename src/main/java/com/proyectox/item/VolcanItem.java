package com.proyectox.item;

import com.proyectox.entity.Orb;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import com.proyectox.util.Scheduler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * PX-64 VOLCÁN — lanzagranadas de tambor de 40 mm. Cada granada vuela en parábola y estalla al chocar: 30 de daño en
 * radio 4 (el objetivo directo recibe el daño completo). Seis granadas en el tambor; cada clic dispara (hasta 2 por
 * segundo) y manteniendo el gatillo, una cada 0,7 s. Nunca te daña a ti ni rompe bloques.
 */
public class VolcanItem extends GunItem {
    public static final double SPEED = 2.2;
    public static final int LIFE = 140;
    /** Ticks entre disparos manteniendo el gatillo. */
    public static final int HOLD = 14;

    public VolcanItem(Properties properties) {
        super("volcan", "PX-64", 0xE8823A, Fx.VOLCAN, properties);
    }

    @Override
    public int maxCharge() {
        return 12;
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
        return 10;
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
        return 0.25F;
    }

    @Override
    public float kick() {
        return 0.9F;
    }

    @Override
    public void triggerTick(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming) {
        if (heldTicks != 0 && heldTicks % HOLD != 0) return;
        super.triggerTick(player, stack, level, now, heldTicks, aiming);
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        ServerLevel level = shot.level();
        ServerPlayer player = shot.player();
        level.addFreshEntity(Orb.create(level, player, Orb.GRENADE40, shot.muzzle(), shot.aimDir(60.0).scale(SPEED), Orb.GRENADE_DAMAGE, LIFE, 0.0F));
        FX.sound(level, player, PXSounds.VOLCAN_DISPARO.get(), 3.0F, 0.95F + level.random.nextFloat() * 0.1F);
        // el tambor gira hasta la siguiente recámara
        Scheduler.schedule(level, 5, () -> {
            if (player.isAlive()) FX.sound(player.level(), player, PXSounds.VOLCAN_TAMBOR.get(), 0.8F, 1.0F);
        });
    }
}
