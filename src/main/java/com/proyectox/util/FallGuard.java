package com.proyectox.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.Entity;

/** Protección temporal contra el daño por caída (ancla del VÓRTICE, retroceso del REMACHE, embestida del TALADRO). */
public final class FallGuard {
    private FallGuard() {}

    private static final Map<UUID, Long> UNTIL = new ConcurrentHashMap<>();

    public static void protect(Entity entity, int ticks) {
        long until = entity.level().getGameTime() + ticks;
        UNTIL.merge(entity.getUUID(), until, Math::max);
    }

    public static boolean active(Entity entity) {
        Long until = UNTIL.get(entity.getUUID());
        if (until == null) return false;
        if (entity.level().getGameTime() > until) {
            UNTIL.remove(entity.getUUID());
            return false;
        }
        return true;
    }

    public static void forget(Entity entity) {
        UNTIL.remove(entity.getUUID());
    }
}
