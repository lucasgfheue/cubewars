package com.proyectox.util;

import com.proyectox.client.ClientOnly;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * Reloj de juego compartido. La energía de las armas se calcula a partir de marcas de tiempo,
 * así que el cliente puede dibujar la barra y el tooltip sin que el servidor envíe nada.
 */
public final class Times {
    private Times() {}

    public static long now(@Nullable Level level) {
        return level != null ? level.getGameTime() : client();
    }

    /** Tiempo del mundo que ve el cliente; 0 en un servidor dedicado. */
    public static long client() {
        return FMLEnvironment.dist.isClient() ? ClientOnly.gameTime() : 0L;
    }

    public static boolean shiftDown() {
        return FMLEnvironment.dist.isClient() && ClientOnly.shiftDown();
    }
}
