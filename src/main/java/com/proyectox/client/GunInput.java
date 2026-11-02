package com.proyectox.client;

import com.proyectox.client.fx.Muzzle;
import com.proyectox.item.GunItem;
import com.proyectox.item.TaladroItem;
import com.proyectox.item.TriggerWeapon;
import com.proyectox.network.InputPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Controles de las armas: el clic izquierdo es el gatillo y el derecho apunta. El estado se envía al
 * servidor cuando cambia y, mientras algún botón está pulsado, cada 2 ticks.
 */
public final class GunInput {
    private GunInput() {}

    private static boolean lastFire;
    private static boolean lastAim;
    private static int resend;
    private static int fireTicks;

    /** Ticks que lleva pulsado el gatillo (para la carga de la AGUJA y la fijación de la COLMENA). */
    public static int fireTicks() {
        return fireTicks;
    }

    /**
     * Clics del jugador: con un arma de gatillo en la mano (armas de fuego, GUADAÑA, PICA, el TALADRO sobre
     * criaturas) el clic izquierdo no golpea ni pica; con un arma de fuego el derecho apunta.
     */
    public static void onClick(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        Item item = player.getMainHandItem().getItem();
        if (event.isAttack()) {
            boolean block = mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK;
            if (item instanceof TriggerWeapon && !(item instanceof TaladroItem && block)) {
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        } else if (event.isUseItem() && event.getHand() == InteractionHand.MAIN_HAND) {
            if (item instanceof GunItem gun && gun.aims()) {
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        }
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        Reticle.tick();
        if (player == null) {
            lastFire = lastAim = false;
            fireTicks = 0;
            Muzzle.reset();
            return;
        }
        Item item = player.getMainHandItem().getItem();
        boolean ok = mc.screen == null && !player.isSpectator() && player.isAlive();
        boolean fire = ok && item instanceof TriggerWeapon && mc.options.keyAttack.isDown();
        boolean aim = ok && item instanceof GunItem gun && gun.aims() && mc.options.keyUse.isDown();
        if (aim && player.isSprinting()) player.setSprinting(false);
        fireTicks = fire ? fireTicks + 1 : 0;
        if (mc.getConnection() != null && (fire != lastFire || aim != lastAim || ((fire || aim) && ++resend % 2 == 0))) {
            PacketDistributor.sendToServer(new InputPayload(fire, aim));
        }
        lastFire = fire;
        lastAim = aim;
        Muzzle.tick(aim, fire && item instanceof TaladroItem);
    }
}
