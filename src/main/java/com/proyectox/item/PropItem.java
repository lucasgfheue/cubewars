package com.proyectox.item;

import net.minecraft.world.item.Item;

/**
 * Modelo 3D de una pieza que se ve en el mundo (el cohete del RUGIDO, el meteorito, el chakram, la torreta): no se
 * fabrica ni aparece en la pestaña del arsenal; solo existe para que el juego cargue su modelo.
 */
public class PropItem extends Item {
    public PropItem(Properties properties) {
        super(properties);
    }
}
