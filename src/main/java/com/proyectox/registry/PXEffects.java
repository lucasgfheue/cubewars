package com.proyectox.registry;

import com.proyectox.ProyectoX;
import com.proyectox.effect.StunEffect;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Efectos de estado propios. */
public final class PXEffects {
    private PXEffects() {}

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ProyectoX.MODID);

    /** Parálisis: no puede moverse, saltar ni atacar. */
    public static final DeferredHolder<MobEffect, StunEffect> PARALISIS = EFFECTS.register("paralisis", StunEffect::new);

    /** Paraliza a una criatura durante {@code ticks} (si ya lo estaba, se queda con la duración más larga). */
    public static void stun(LivingEntity target, int ticks, @Nullable Entity source) {
        if (!target.isAlive()) return;
        target.addEffect(new MobEffectInstance(PARALISIS, ticks, 0, false, true, true), source);
    }
}
