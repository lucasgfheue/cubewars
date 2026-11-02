package com.proyectox.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Arma que se acciona con el clic izquierdo (gatillo). El cliente envía el estado del botón y el
 * servidor llama a estos métodos desde el tick del jugador.
 */
public interface TriggerWeapon {
    /**
     * Cada tick mientras se mantiene el gatillo.
     * @param heldTicks ticks que lleva pulsado (0 en el primer tick)
     */
    void triggerTick(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming);

    /** Al soltar el gatillo (o al cambiar de arma con él pulsado). */
    default void triggerRelease(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming) {}
}
