package com.proyectox.network;

import com.proyectox.ProyectoX;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

/**
 * Efecto visual (servidor → clientes cercanos). Cada cliente lo compone con sus propias partículas.
 *
 * @param kind   tipo de efecto ({@link com.proyectox.fx.Fx})
 * @param style  arma o variante que lo produce
 * @param x      origen (o punto del efecto)
 * @param x2     destino de un haz, o dirección/normal en los efectos puntuales
 * @param value  parámetro libre (radio, longitud, retardo...)
 * @param entity entidad asociada (el tirador), -1 si no hay
 * @param other  segunda entidad (el objetivo de una cuerda o de una fijación), -1 si no hay
 */
public record FxPayload(int kind, int style, double x, double y, double z, double x2, double y2, double z2, float value, int entity, int other)
        implements CustomPacketPayload {
    public static final Type<FxPayload> TYPE = new Type<>(ProyectoX.id("efecto"));
    public static final StreamCodec<FriendlyByteBuf, FxPayload> CODEC = CustomPacketPayload.codec(FxPayload::write, FxPayload::read);

    private void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.kind);
        buf.writeVarInt(this.style);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeDouble(this.x2);
        buf.writeDouble(this.y2);
        buf.writeDouble(this.z2);
        buf.writeFloat(this.value);
        buf.writeVarInt(this.entity);
        buf.writeVarInt(this.other);
    }

    private static FxPayload read(FriendlyByteBuf buf) {
        int kind = buf.readVarInt();
        int style = buf.readVarInt();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        double x2 = buf.readDouble();
        double y2 = buf.readDouble();
        double z2 = buf.readDouble();
        float value = buf.readFloat();
        int entity = buf.readVarInt();
        int other = buf.readVarInt();
        return new FxPayload(kind, style, x, y, z, x2, y2, z2, value, entity, other);
    }

    public Vec3 from() {
        return new Vec3(this.x, this.y, this.z);
    }

    public Vec3 to() {
        return new Vec3(this.x2, this.y2, this.z2);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
