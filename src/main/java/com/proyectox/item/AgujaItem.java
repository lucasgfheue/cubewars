package com.proyectox.item;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import com.proyectox.util.Hits;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * PX-02 AGUJA — fusil láser de riel. Mantén el gatillo para cargar los condensadores (1,5 s) y suéltalo:
 * un láser celeste instantáneo de 256 bloques que atraviesa paredes y a todos los enemigos que cruza.
 * 20 de daño sin cargar, 50 con la carga completa. Apuntando se mira por el visor.
 */
public class AgujaItem extends GunItem {
    public static final int FULL_CHARGE = 30;
    public static final double RANGE = 256.0;
    public static final float MIN_DAMAGE = 20.0F;
    public static final float MAX_DAMAGE = 50.0F;
    public static final int COOLDOWN = 16;

    public AgujaItem(Properties properties) {
        super("aguja", "PX-02", 0x3CE6FF, Fx.AGUJA, properties);
    }

    @Override
    public int maxCharge() {
        return 12;
    }

    @Override
    public int regenTicks() {
        return 15;
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
        return 0.4;
    }

    @Override
    public double aimSpread() {
        return 0.0;
    }

    @Override
    public float zoom() {
        return 0.75F;
    }

    @Override
    public boolean scoped() {
        return true;
    }

    @Override
    public float kick() {
        return 1.0F;
    }

    /** Fracción de carga tras mantener el gatillo {@code ticks}. */
    public static float chargeFraction(int ticks) {
        return Mth.clamp(ticks / (float) FULL_CHARGE, 0.0F, 1.0F);
    }

    /** Mientras se mantiene el gatillo los condensadores se cargan (solo sonido: el disparo es al soltar). */
    @Override
    public void triggerTick(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming) {
        if (heldTicks == 0 && ready(player, now) && charge(stack, now) > 0) {
            FX.sound(level, player, PXSounds.AGUJA_CARGA.get(), 0.7F, 1.0F);
        }
        if (heldTicks == FULL_CHARGE) FX.sound(level, player, PXSounds.LISTO.get(), 0.8F, 1.6F);
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
        float f = chargeFraction(heldTicks);
        float damage = MIN_DAMAGE + (MAX_DAMAGE - MIN_DAMAGE) * f;
        Vec3 dir = shot.eyeDir();
        Vec3 end = shot.eye().add(dir.scale(RANGE));
        // el rayo no se detiene en los bloques: atraviesa paredes y a todas las criaturas que cruza
        DamageSource source = PXDamage.source(shot.level(), PXDamage.RIEL, shot.player(), shot.player());
        for (LivingEntity e : Aim.segment(shot.level(), shot.player(), shot.eye(), end, 0.25)) {
            Hits.hurt(e, source, damage, shot.player());
            Fx.impact(shot.level(), Fx.AGUJA, e.getBoundingBox().getCenter(), dir.reverse());
        }
        Fx.line(shot.level(), Fx.BEAM, Fx.AGUJA, shot.muzzle(), end, f, shot.player().getId());
        FX.sound(shot.level(), shot.player(), PXSounds.AGUJA_DISPARO.get(), 1.4F + f, 1.15F - 0.3F * f);
        if (f >= 0.999F) FX.sound(shot.level(), shot.player(), PXSounds.AGUJA_RIEL.get(), 1.6F, 1.0F);
    }
}
