package com.proyectox.registry;

import com.mojang.serialization.Codec;
import com.proyectox.ProyectoX;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Estado de las armas guardado en el propio ItemStack. */
public final class PXComponents {
    private PXComponents() {}

    public static final DeferredRegister.DataComponents REGISTRAR =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ProyectoX.MODID);

    /** Carga de energía actual (disparos, pulsos, cohetes, singularidades o cargas de presión). */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CHARGE =
            REGISTRAR.registerComponentType("carga", b -> b.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    /** Momento (tiempo de juego) en que se fijó la carga; sirve para calcular la regeneración. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> STAMP =
            REGISTRAR.registerComponentType("marca", b -> b.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));

    /** Calor acumulado (0-100). */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> HEAT =
            REGISTRAR.registerComponentType("calor", b -> b.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    /** Momento del último cambio de calor. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> HEAT_STAMP =
            REGISTRAR.registerComponentType("marca_calor", b -> b.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));

    /** Id de la entidad vinculada al arma (el dron desplegado del HALCÓN). */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LINK =
            REGISTRAR.registerComponentType("vinculo", b -> b.networkSynchronized(ByteBufCodecs.VAR_INT));

    /** Tiempo de juego a partir del cual la habilidad secundaria vuelve a estar lista. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> READY_AT =
            REGISTRAR.registerComponentType("lista_en", b -> b.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));
}
