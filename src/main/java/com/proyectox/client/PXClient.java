package com.proyectox.client;

import com.proyectox.ProyectoX;
import com.proyectox.client.fx.FxEntityRenderer;
import com.proyectox.client.fx.Looks;
import com.proyectox.client.fx.Looks7;
import com.proyectox.client.fx.Muzzle;
import com.proyectox.item.GunItem;
import com.proyectox.item.PXWeapon;
import com.proyectox.registry.PXEntities;
import com.proyectox.registry.PXItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.CalculatePlayerTurnEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

/** Parte exclusiva del cliente: renderizadores, controles, apuntado, retícula y efectos. */
@Mod(value = ProyectoX.MODID, dist = Dist.CLIENT)
public final class PXClient {
    public PXClient(IEventBus modBus, ModContainer container) {
        modBus.addListener(PXClient::registerRenderers);
        modBus.addListener(PXClient::registerExtensions);
        NeoForge.EVENT_BUS.addListener(PXClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(GunInput::onClick);
        NeoForge.EVENT_BUS.addListener(PXClient::onFov);
        NeoForge.EVENT_BUS.addListener(PXClient::onTurn);
        NeoForge.EVENT_BUS.addListener(PXClient::onGuiLayer);
        NeoForge.EVENT_BUS.addListener(PXClient::onRenderHand);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(PXEntities.PLASMA.get(), FxEntityRenderer.of(Looks::plasma));
        event.registerEntityRenderer(PXEntities.MICROCOHETE.get(), FxEntityRenderer.of(Looks::rocket));
        event.registerEntityRenderer(PXEntities.SINGULARIDAD.get(), FxEntityRenderer.of(Looks::singularity));
        event.registerEntityRenderer(PXEntities.BOLT.get(), FxEntityRenderer.of(Looks::bolt));
        event.registerEntityRenderer(PXEntities.GRANADA.get(), FxEntityRenderer.of(Looks::grenade));
        event.registerEntityRenderer(PXEntities.DISCO.get(), FxEntityRenderer.of(Looks::disc));
        event.registerEntityRenderer(PXEntities.TAJO.get(), FxEntityRenderer.of(Looks::slash));
        event.registerEntityRenderer(PXEntities.ONDA.get(), FxEntityRenderer.of(Looks::wave));
        event.registerEntityRenderer(PXEntities.DRON.get(), DroneRenderer::new);
        // el cohete, el meteorito, el chakram y la torreta se dibujan además con su modelo 3D
        event.registerEntityRenderer(PXEntities.ORBE.get(), PropRenderer.of(Looks::orb));
        event.registerEntityRenderer(PXEntities.ANCLADO.get(), FxEntityRenderer.of(Looks::sticky));
        event.registerEntityRenderer(PXEntities.PERFORANTE.get(), PropRenderer.of(Looks::piercer));
        event.registerEntityRenderer(PXEntities.CAMPO.get(), PropRenderer.of(Looks::field));
        // v7: proyectiles y zonas de las series NEÓN, CHATARRA y XENO
        event.registerEntityRenderer(PXEntities.PROYECTIL.get(), Prop7Renderer.of(Looks7::bolt));
        event.registerEntityRenderer(PXEntities.ZONA.get(), Prop7Renderer.of(Looks7::field));
    }

    /** Cómo se sostiene cada arma (todas las armas de Proyecto X). */
    private static void registerExtensions(RegisterClientExtensionsEvent event) {
        for (var entry : PXItems.ITEMS.getEntries()) {
            Item item = entry.get();
            if (item instanceof PXWeapon weapon) event.registerItem(new GunHands(weapon), item);
        }
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        GunInput.tick();
    }

    /** Apuntar estrecha el campo de visión (la AGUJA, hasta un visor de francotirador). */
    private static void onFov(ViewportEvent.ComputeFov event) {
        if (!event.usedConfiguredFov()) return;
        GunItem gun = heldGun();
        if (gun == null || !gun.aims()) return;
        float aim = Muzzle.aim((float) event.getPartialTick());
        if (aim > 0.0F) event.setFOV(event.getFOV() * (1.0 - gun.zoom() * aim));
    }

    /** Con zoom, el ratón se mueve más despacio para poder afinar. */
    private static void onTurn(CalculatePlayerTurnEvent event) {
        GunItem gun = heldGun();
        if (gun == null || !gun.aims()) return;
        float aim = Muzzle.aim(1.0F);
        if (aim > 0.0F) event.setMouseSensitivity(event.getMouseSensitivity() * (1.0 - gun.zoom() * aim * 0.75));
    }

    /** Retícula propia de las armas de fuego en lugar de la cruz normal. */
    private static void onGuiLayer(RenderGuiLayerEvent.Pre event) {
        if (!event.getName().equals(VanillaGuiLayers.CROSSHAIR) || !Reticle.active()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;
        event.setCanceled(true);
        Reticle.render(event.getGuiGraphics(), event.getPartialTick().getGameTimeDeltaPartialTick(true));
    }

    /** Mirando por el visor no se ven las manos. */
    private static void onRenderHand(RenderHandEvent event) {
        GunItem gun = heldGun();
        if (gun != null && gun.scoped() && Muzzle.aim(event.getPartialTick()) > 0.85F) event.setCanceled(true);
    }

    private static GunItem heldGun() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && player.getMainHandItem().getItem() instanceof GunItem gun ? gun : null;
    }
}
