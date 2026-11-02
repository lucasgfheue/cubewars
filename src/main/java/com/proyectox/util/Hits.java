package com.proyectox.util;

import com.proyectox.fx.Fx;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * Daño de las armas: sin pausa de invulnerabilidad entre impactos, con marcador de acierto para el
 * tirador y <b>sin empujar</b> nunca al objetivo (ninguna arma de Proyecto X lo desplaza).
 */
public final class Hits {
    private Hits() {}

    /** Profundidad de llamadas a {@link #hurt} en curso en este hilo: mientras sea &gt; 0 no hay retroceso. */
    private static final ThreadLocal<int[]> DEPTH = ThreadLocal.withInitial(() -> new int[1]);

    /** Objetivos golpeados cuerpo a cuerpo con un arma de Proyecto X en el tick indicado. */
    private static final Map<Integer, Long> MELEE = new ConcurrentHashMap<>();

    public static boolean hurt(Entity target, DamageSource source, float amount, @Nullable Entity shooter) {
        target.invulnerableTime = 0;
        int[] depth = DEPTH.get();
        depth[0]++;
        boolean hurt;
        try {
            hurt = target.hurt(source, amount);
        } finally {
            depth[0]--;
        }
        if (hurt && target instanceof LivingEntity) Fx.hitmark(shooter, !target.isAlive());
        return hurt;
    }

    /** Un golpe cuerpo a cuerpo con un arma de Proyecto X está a punto de producirse. */
    public static void meleeHit(Entity target) {
        MELEE.put(target.getId(), target.level().getGameTime());
        if (MELEE.size() > 512) MELEE.clear();
    }

    /** Si el retroceso que va a recibir esta criatura viene de un arma de Proyecto X (y debe anularse). */
    public static boolean suppressKnockback(LivingEntity entity) {
        if (DEPTH.get()[0] > 0) return true;
        Long tick = MELEE.get(entity.getId());
        return tick != null && tick == entity.level().getGameTime();
    }
}
