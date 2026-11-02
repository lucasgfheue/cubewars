package com.proyectox.item;

import com.proyectox.entity.Field;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * PX-28 TEMPESTAD — cañón de tormenta. Forma una nube eléctrica sobre el punto al que apuntas (hasta 96
 * bloques) que durante 4 s lanza rayos de 4 de daño a las criaturas que tiene debajo.
 */
public class TempestadItem extends GunItem {
    public static final double RANGE = 96.0;
    public static final double HEIGHT = 7.0;

    public TempestadItem(Properties properties) {
        super("tempestad", "PX-28", 0x8AA8FF, Fx.TEMPESTAD, properties);
    }

    @Override
    public int maxCharge() {
        return 6;
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
        return 0.3F;
    }

    @Override
    public float kick() {
        return 0.7F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        ServerLevel level = shot.level();
        Aim.Ray ray = Aim.pierce(level, shot.player(), shot.eye(), shot.look(), RANGE, 1);
        Vec3 target = ray.hits().isEmpty() ? ray.end() : ray.hits().get(0).position();
        // la nube se forma encima del objetivo, por debajo del techo si lo hay
        Vec3 cloud = target.add(0.0, HEIGHT, 0.0);
        BlockHitResult roof = level.clip(new ClipContext(target.add(0.0, 0.5, 0.0), cloud, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shot.player()));
        if (roof.getType() != HitResult.Type.MISS) cloud = new Vec3(cloud.x, Math.max(target.y + 2.0, roof.getLocation().y - 1.0), cloud.z);
        level.addFreshEntity(Field.create(level, shot.player(), Field.STORM, cloud, Vec3.ZERO));
        Fx.line(level, Fx.LIGHTNING, Fx.TEMPESTAD, shot.muzzle(), cloud, 0.6F, shot.player().getId());
        Fx.point(level, Fx.STORM, Fx.TEMPESTAD, cloud, new Vec3(0, 1, 0), (float) Field.STORM_RADIUS * 0.6F, shot.player().getId());
        FX.sound(level, shot.player(), PXSounds.TEMPESTAD_DISPARO.get(), 1.0F, 1.2F);
        FX.sound(level, cloud, PXSounds.TEMPESTAD_RAYO.get(), 1.4F, 0.7F);
    }
}
