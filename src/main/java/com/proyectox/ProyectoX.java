package com.proyectox;

import com.mojang.logging.LogUtils;
import com.proyectox.network.PXNetwork;
import com.proyectox.registry.PXComponents;
import com.proyectox.registry.PXEffects;
import com.proyectox.registry.PXEntities;
import com.proyectox.registry.PXItems;
import com.proyectox.registry.PXSounds;
import com.proyectox.registry.PXTabs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

/**
 * Proyecto X — arsenal experimental de la Fundición Ferrum.
 * Ninguna arma usa munición: cada una se sostiene con su propio sistema de energía.
 */
@Mod(ProyectoX.MODID)
public final class ProyectoX {
    public static final String MODID = "proyectox";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ProyectoX(IEventBus modBus, ModContainer container) {
        PXComponents.REGISTRAR.register(modBus);
        PXSounds.SOUNDS.register(modBus);
        PXEffects.EFFECTS.register(modBus);
        PXItems.ITEMS.register(modBus);
        PXEntities.ENTITIES.register(modBus);
        PXTabs.TABS.register(modBus);
        modBus.addListener(PXNetwork::register);
        LOGGER.info("Proyecto X: arsenal en línea");
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
