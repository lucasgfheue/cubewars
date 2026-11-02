package com.proyectox.client;

import com.proyectox.client.fx.FxEffects;
import com.proyectox.client.fx.Fx7;
import com.proyectox.entity.Drone;
import com.proyectox.entity.Field;
import com.proyectox.entity.PXProjectile;
import com.proyectox.entity.SpecField;
import com.proyectox.network.FxPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;

/** Accesos al cliente. Solo se llama desde código que se ejecuta en el cliente. */
public final class ClientOnly {
    private ClientOnly() {}

    public static long gameTime() {
        ClientLevel level = Minecraft.getInstance().level;
        return level == null ? 0L : level.getGameTime();
    }

    public static boolean shiftDown() {
        return Screen.hasShiftDown();
    }

    /** Efecto visual recibido del servidor. */
    public static void fx(FxPayload payload) {
        FxEffects.play(payload);
    }

    /** Estela de un proyectil (cada tick en el cliente). */
    public static void projectileTick(PXProjectile projectile) {
        FxEffects.projectileTick(projectile);
    }

    public static void droneTick(Drone drone) {
        FxEffects.droneTick(drone);
    }

    /** Partículas de ambiente de una zona de efecto (charco, nube, tornado, enjambre). */
    public static void fieldTick(Field field) {
        FxEffects.fieldTick(field);
    }

    /** Ambiente de las zonas de la serie v7 (fuego, pozo, nebulosa, grieta, ojo). */
    public static void specFieldTick(SpecField field) {
        Fx7.fieldTick(field);
    }
}
