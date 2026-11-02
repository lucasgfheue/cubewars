package com.proyectox.util;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;

/** A quién persiguen solas las armas de la serie v7 (proyectiles que buscan, descargas en cadena, larvas). */
public final class Targets {
    private Targets() {}

    /**
     * Monstruos, quien pelea con el dueño y criaturas salvajes; nunca jugadores (salvo que peleen con él), mascotas con
     * dueño, aldeanos ni gólems.
     */
    public static boolean fair(@Nullable Entity owner, LivingEntity e) {
        if (Aim.threat(owner, e)) return true;
        if (e instanceof Player || !(e instanceof Mob)) return false;
        if (e instanceof TamableAnimal t && t.isTame()) return false;
        return !(e instanceof AbstractVillager) && !(e instanceof AbstractGolem);
    }
}
