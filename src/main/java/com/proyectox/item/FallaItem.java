package com.proyectox.item;

import com.proyectox.entity.Field;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Blast;
import com.proyectox.util.FX;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * PX-45 FALLA — martillo sísmico. Golpea el suelo y abre una grieta que avanza 29 bloques en la dirección en que
 * miras siguiendo el terreno; los picos de roca hacen 22 de daño a cada criatura que alcanzan.
 */
public class FallaItem extends GunItem {
    public FallaItem(Properties properties) {
        super("falla", "PX-45", 0xE8A23C, Fx.FALLA, properties);
    }

    @Override
    public int maxCharge() {
        return 6;
    }

    @Override
    public int regenTicks() {
        return 30;
    }

    @Override
    public int interval() {
        return 24;
    }

    @Override
    public float zoom() {
        return 0.15F;
    }

    @Override
    public float kick() {
        return 0.9F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        ServerLevel level = shot.level();
        Vec3 look = shot.look();
        Vec3 flat = new Vec3(look.x, 0.0, look.z);
        flat = flat.lengthSqr() < 1.0E-4 ? Vec3.directionFromRotation(0.0F, shot.player().getYRot()) : flat.normalize();
        Vec3 feet = shot.player().position();
        Vec3 start = Blast.ground(level, shot.player(), feet.add(flat.scale(1.2)).add(0.0, 1.0, 0.0), 4.0);
        if (start == null) start = feet;
        level.addFreshEntity(Field.create(level, shot.player(), Field.FISSURE, start, flat));
        // golpe contra el suelo: un estallido de roca mayor que los de la grieta
        Fx.point(level, Fx.ERUPT, Fx.FALLA, start, flat, 2.6F, -1);
        FX.sound(level, start, PXSounds.FALLA_GOLPE.get(), 1.4F, 0.85F + level.random.nextFloat() * 0.1F);
    }
}
