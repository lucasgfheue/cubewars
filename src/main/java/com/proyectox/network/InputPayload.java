package com.proyectox.network;

import com.proyectox.ProyectoX;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Estado de los botones del arma (cliente → servidor): gatillo (clic izquierdo) y mira (clic derecho).
 * Mientras alguno está pulsado el cliente lo reenvía cada 2 ticks.
 */
public record InputPayload(boolean fire, boolean aim) implements CustomPacketPayload {
    public static final Type<InputPayload> TYPE = new Type<>(ProyectoX.id("entrada"));
    public static final StreamCodec<ByteBuf, InputPayload> CODEC = ByteBufCodecs.BYTE.map(
            b -> new InputPayload((b & 1) != 0, (b & 2) != 0),
            p -> (byte) ((p.fire() ? 1 : 0) | (p.aim() ? 2 : 0)));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
