package com.proyectox.network;

import com.proyectox.client.ClientOnly;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** Red: botones del arma (cliente → servidor) y efectos visuales (servidor → clientes). */
public final class PXNetwork {
    private PXNetwork() {}

    /** Último estado de los botones de cada jugador y momento en que llegó. */
    private record Input(boolean fire, boolean aim, long stamp) {}

    private static final Map<UUID, Input> INPUT = new ConcurrentHashMap<>();
    /** Momento en que se pulsó el gatillo por última vez (se consume en el tick del jugador). */
    private static final Map<UUID, Long> PRESSED = new ConcurrentHashMap<>();

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("3");
        registrar.playToServer(InputPayload.TYPE, InputPayload.CODEC, PXNetwork::onInput);
        registrar.playToClient(FxPayload.TYPE, FxPayload.CODEC, PXNetwork::onFx);
    }

    private static void onInput(InputPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Input before = INPUT.get(player.getUUID());
            if (payload.fire() && (before == null || !before.fire())) PRESSED.put(player.getUUID(), player.level().getGameTime());
            if (!payload.fire() && !payload.aim()) {
                INPUT.remove(player.getUUID());
            } else {
                INPUT.put(player.getUUID(), new Input(payload.fire(), payload.aim(), player.level().getGameTime()));
            }
        });
    }

    private static void onFx(FxPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientOnly.fx(payload));
    }

    /** El cliente reenvía el estado cada 2 ticks; si deja de llegar, los botones se consideran sueltos. */
    public static boolean firing(Player player, long now) {
        Input in = INPUT.get(player.getUUID());
        return in != null && in.fire() && now - in.stamp() <= 4L;
    }

    /** Si el gatillo se pulsó hace poco y aún no se ha tenido en cuenta (lo consume). */
    public static boolean pressed(Player player, long now) {
        Long t = PRESSED.remove(player.getUUID());
        return t != null && now - t <= 4L;
    }

    public static boolean aiming(Player player, long now) {
        Input in = INPUT.get(player.getUUID());
        return in != null && in.aim() && now - in.stamp() <= 4L;
    }

    public static void forget(Player player) {
        INPUT.remove(player.getUUID());
        PRESSED.remove(player.getUUID());
    }
}
