package com.proyectox.item;

import com.proyectox.entity.Drone;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * PX-13 HALCÓN — lanzador de drones de combate: cada disparo despliega un dron que caza enemigos en
 * 32 bloques durante 40 s (pulsos de 12 de daño). Hasta 3 drones a la vez; un cuarto releva al más antiguo.
 */
public class HalconItem extends GunItem {
    public static final int MAX_DRONES = 3;
    public static final int DRONE_LIFE = 800;

    public HalconItem(Properties properties) {
        super("halcon", "PX-13", 0xFFB13B, Fx.HALCON, properties);
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
        return 0.2F;
    }

    @Override
    public float kick() {
        return 0.3F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        ServerLevel level = shot.level();
        ServerPlayer player = shot.player();
        List<Drone> mine = new ArrayList<>(level.getEntitiesOfClass(Drone.class, player.getBoundingBox().inflate(128.0), d -> d.isAlive() && d.ownedBy(player)));
        mine.sort(Comparator.comparingInt(Drone::remainingTicks));
        while (mine.size() >= MAX_DRONES) mine.remove(0).retire();
        boolean[] used = new boolean[MAX_DRONES];
        for (Drone d : mine) used[Math.floorMod(d.slot(), MAX_DRONES)] = true;
        int slot = 0;
        while (slot < MAX_DRONES - 1 && used[slot]) slot++;
        Vec3 pos = shot.muzzle().add(shot.look().scale(0.6)).add(0.0, 0.25, 0.0);
        Drone drone = Drone.create(level, player, pos, DRONE_LIFE, slot);
        level.addFreshEntity(drone);
        Fx.point(level, Fx.DRONE, Fx.HALCON, pos, shot.look(), 1.0F, drone.getId());
        FX.sound(level, player, PXSounds.HALCON_DESPLIEGUE.get(), 1.0F, 1.0F);
    }
}
