package com.proyectox.item;

import com.proyectox.entity.Field;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.Blast;
import com.proyectox.util.FX;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * PX-46 CENTINELA — torreta desplegable. Coloca una torreta automática donde apuntas (hasta 12 bloques) que durante
 * 20 s dispara 4 balas por segundo de 5 de daño a los enemigos que ve en 24 bloques. Hasta 2 a la vez: una tercera
 * releva a la más antigua.
 */
public class CentinelaItem extends GunItem {
    public static final double PLACE_RANGE = 12.0;

    public CentinelaItem(Properties properties) {
        super("centinela", "PX-46", 0x34F5C5, Fx.CENTINELA, properties);
    }

    @Override
    public int maxCharge() {
        return 3;
    }

    @Override
    public int regenTicks() {
        return 100;
    }

    @Override
    public int interval() {
        return 20;
    }

    @Override
    public float zoom() {
        return 0.15F;
    }

    @Override
    public float kick() {
        return 0.3F;
    }

    @Override
    protected boolean muzzleFlash() {
        return false;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        ServerLevel level = shot.level();
        ServerPlayer player = shot.player();
        BlockHitResult hit = Aim.lookBlock(player, PLACE_RANGE);
        Vec3 spot;
        if (hit.getType() != HitResult.Type.MISS && hit.getDirection() == Direction.UP) {
            spot = hit.getLocation().add(0.0, 0.02, 0.0);
        } else {
            Vec3 p = hit.getType() == HitResult.Type.MISS ? player.getEyePosition().add(player.getLookAngle().scale(PLACE_RANGE))
                    : hit.getLocation().add(Vec3.atLowerCornerOf(hit.getDirection().getNormal()).scale(0.4));
            spot = Blast.ground(level, player, p, 24.0);
            if (spot == null) spot = player.position();
        }
        List<Field> mine = new ArrayList<>(level.getEntitiesOfClass(Field.class, player.getBoundingBox().inflate(128.0),
                f -> f.isAlive() && f.type() == Field.TURRET && f.ownedBy(player)));
        mine.sort(Comparator.comparingInt(Field::remaining));
        while (mine.size() >= Field.MAX_TURRETS) mine.remove(0).retire(level);
        Vec3 flat = new Vec3(shot.look().x, 0.0, shot.look().z);
        Field turret = Field.create(level, player, Field.TURRET, spot, flat);
        level.addFreshEntity(turret);
        Fx.point(level, Fx.DEPLOY, Fx.CENTINELA, spot, new Vec3(0.0, 1.0, 0.0), 1.0F, turret.getId());
        FX.sound(level, spot, PXSounds.CENTINELA_DESPLEGAR.get(), 1.2F, 1.0F);
    }
}
