package com.proyectox.item;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import com.proyectox.util.Hits;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

/**
 * PX-14 TRUENO — cañón láser. Mientras se mantiene el gatillo dispara un láser continuo de 64 bloques
 * que atraviesa hasta 3 criaturas, cinco veces por segundo. Cuanto más tiempo sigue encendido más se calienta
 * y más daño hace, por etapas: 1 el primer segundo (celeste), 5 desde el segundo 1, 20 desde el segundo 2 y,
 * a los 3 s, rojo: 75.
 */
public class TruenoItem extends GunItem {
    public static final double RANGE = 64.0;
    public static final int RAMP_TICKS = 60;
    /** Daño de cada etapa (una por segundo encendido; la última, al rojo, a los 3 s). */
    public static final float[] STAGE_DAMAGE = {1.0F, 5.0F, 20.0F, 75.0F};
    public static final int STAGE_TICKS = 20;

    public TruenoItem(Properties properties) {
        super("trueno", "PX-14", 0xD6E4FF, Fx.TRUENO, properties);
    }

    @Override
    public int maxCharge() {
        return 400;
    }

    @Override
    public int regenTicks() {
        return 1;
    }

    @Override
    public int regenDelay() {
        return 12;
    }

    @Override
    public int interval() {
        return 2;
    }

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public float zoom() {
        return 0.4F;
    }

    @Override
    public float kick() {
        return 0.06F;
    }

    @Override
    protected boolean muzzleFlash() {
        return false;
    }

    /** Calentamiento del láser (0 = celeste, 1 = rojo) tras {@code ticks} encendido. */
    public static float heat(int ticks) {
        return Mth.clamp(ticks / (float) RAMP_TICKS, 0.0F, 1.0F);
    }

    /** Daño de cada impacto tras {@code ticks} encendido: 1, 5, 20 y 75 (al rojo). */
    public static float damage(int ticks) {
        return STAGE_DAMAGE[Mth.clamp(ticks / STAGE_TICKS, 0, STAGE_DAMAGE.length - 1)];
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        ServerLevel level = shot.level();
        float heat = heat(heldTicks);
        Aim.Ray ray = Aim.pierce(level, shot.player(), shot.eye(), shot.look(), RANGE, 3);
        if ((heldTicks / 2) % 2 == 0) {
            DamageSource source = PXDamage.source(level, PXDamage.LASER, shot.player(), shot.player());
            float damage = damage(heldTicks);
            for (Entity e : ray.hits()) Hits.hurt(e, source, damage, shot.player());
        }
        Fx.line(level, Fx.LASER, Fx.TRUENO, shot.muzzle(), ray.end(), heat, shot.player().getId());
        if ((heldTicks / 2) % 4 == 0) {
            FX.sound(level, shot.player(), PXSounds.TRUENO_MARCAR.get(), 0.8F, 1.3F + heat * 0.6F);
        }
    }
}
