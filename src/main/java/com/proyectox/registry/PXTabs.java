package com.proyectox.registry;

import com.proyectox.ProyectoX;
import com.proyectox.item.PropItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PXTabs {
    private PXTabs() {}

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ProyectoX.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ARSENAL = TABS.register("arsenal",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.proyectox.arsenal"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> new ItemStack(PXItems.AGUJA.get()))
                    .displayItems((parameters, output) -> PXItems.ITEMS.getEntries().forEach(item -> {
                        // las piezas del mundo (cohete, torreta...) no se muestran
                        if (!(item.get() instanceof PropItem)) output.accept(item.get());
                    }))
                    .build());
}
