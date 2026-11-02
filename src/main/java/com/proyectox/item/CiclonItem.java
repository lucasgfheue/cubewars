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
 * PX-30 CICLÓN — cañón de torbellino. Lanza un tornado que avanza por el suelo durante 3 s (unos 21 bloques):
 * atrae a las criaturas de alrededor y tritura a las que atrapa (2 de daño cada 0,25 s).
 */
public class CiclonItem extends GunItem {
    public CiclonItem(Properties properties) {
        super("ciclon", "PX-30", 0x7FE8D8, Fx.CICLON, properties);
    }

    @Override
    public int maxCharge() {
        return 5;
    }

    @Override
    public int regenTicks() {
        return 40;
    }

    @Override
    public int interval() {
        return 30;
    }

    @Override
    public float zoom() {
        return 0.2F;
    }

    @Override
    public float kick() {
        return 0.8F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        ServerLevel level = shot.level();
        Vec3 look = shot.look();
        Vec3 heading = new Vec3(look.x, 0.0, look.z);
        if (heading.lengthSqr() < 1.0E-4) heading = Vec3.directionFromRotation(0.0F, shot.player().getYRot());
        heading = heading.normalize();
        Vec3 start = shot.player().position().add(heading.scale(2.0));
        Vec3 ground = Blast.ground(level, shot.player(), start.add(0.0, 1.5, 0.0), 6.0);
        if (ground != null) start = ground;
        level.addFreshEntity(Field.create(level, shot.player(), Field.TORNADO, start, heading));
        FX.sound(level, shot.player(), PXSounds.CICLON_DISPARO.get(), 1.3F, 0.8F);
    }
}
