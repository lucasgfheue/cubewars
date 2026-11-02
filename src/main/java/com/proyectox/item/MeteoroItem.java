package com.proyectox.item;

import com.proyectox.entity.Orb;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.Blast;
import com.proyectox.util.FX;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * PX-43 METEORO — designador de meteoritos. Marca el punto de la mira (hasta 128 bloques) y un segundo después cae
 * un meteorito justo ahí: 45 de daño en radio 6. Bajo techo, el meteorito aparece por debajo del techo.
 */
public class MeteoroItem extends GunItem {
    public static final double RANGE = 128.0;
    /** Ticks desde la marca hasta el impacto. */
    public static final int FALL_TICKS = 20;
    public static final double HEIGHT = 36.0;
    public static final double MIN_HEIGHT = 6.0;

    public MeteoroItem(Properties properties) {
        super("meteoro", "PX-43", 0xFF3A1A, Fx.METEORO, properties);
    }

    @Override
    public int maxCharge() {
        return 4;
    }

    @Override
    public int regenTicks() {
        return 50;
    }

    @Override
    public int interval() {
        return 40;
    }

    @Override
    public float zoom() {
        return 0.4F;
    }

    @Override
    public float kick() {
        return 0.4F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        ServerLevel level = shot.level();
        Aim.Ray ray = Aim.pierce(level, shot.player(), shot.eye(), shot.look(), RANGE, 1);
        Vec3 target = ray.hits().isEmpty() ? ray.end() : ray.hits().get(0).position();
        // la marca se dibuja en el suelo bajo el punto (si lo hay cerca)
        Vec3 ground = Blast.ground(level, shot.player(), target.add(0.0, 0.5, 0.0), 8.0);
        Vec3 mark = ground != null ? ground : target;
        // cae en diagonal desde detrás del punto, por debajo del techo si lo hay
        Vec3 flat = new Vec3(shot.look().x, 0.0, shot.look().z);
        flat = flat.lengthSqr() < 1.0E-4 ? new Vec3(0.0, 0.0, 1.0) : flat.normalize();
        Vec3 sky = target.add(0.0, HEIGHT, 0.0).subtract(flat.scale(HEIGHT / 3.0));
        Entity owner = shot.player();
        BlockHitResult roof = level.clip(new ClipContext(target.add(0.0, 0.6, 0.0), sky, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner));
        Vec3 start = sky;
        if (roof.getType() != HitResult.Type.MISS) {
            Vec3 up = sky.subtract(target);
            double reach = Math.max(MIN_HEIGHT, roof.getLocation().distanceTo(target) - 1.0);
            start = target.add(up.normalize().scale(Math.min(reach, up.length())));
        }
        Vec3 velocity = target.subtract(start).scale(1.0 / FALL_TICKS);
        level.addFreshEntity(Orb.create(level, owner, Orb.METEOR, start, velocity, Orb.METEOR_DAMAGE, FALL_TICKS + 20, 0.0F));
        Fx.point(level, Fx.MARK, Fx.METEORO, mark, new Vec3(0.0, 1.0, 0.0), FALL_TICKS, shot.player().getId());
        FX.sound(level, shot.player(), PXSounds.METEORO_MARCAR.get(), 1.0F, 1.0F);
        FX.sound(level, start, PXSounds.METEORO_CAIDA.get(), 4.0F, 0.9F + level.random.nextFloat() * 0.1F);
    }
}
