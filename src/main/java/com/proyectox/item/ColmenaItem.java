package com.proyectox.item;

import com.proyectox.entity.MicroRocket;
import com.proyectox.fx.Fx;
import com.proyectox.network.PXNetwork;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import com.proyectox.util.Scheduler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * PX-04 COLMENA — lanzador de microcohetes dirigibles. Mantén el gatillo para fijar hasta 6 objetivos
 * cerca de la mira y suéltalo para lanzar la salva: cada cohete persigue a su objetivo; los que no
 * tienen objetivo siguen el punto al que apuntas. 20 de daño por cohete.
 */
public class ColmenaItem extends GunItem {
    public static final int SALVO = 6;
    public static final double RANGE = 160.0;
    public static final double LOCK_CONE = 14.0;
    public static final int LOCK_TICKS = 5;
    public static final int COOLDOWN = 24;

    /** Objetivos fijados por cada jugador durante la pulsación actual. */
    private static final Map<UUID, List<Integer>> LOCKS = new ConcurrentHashMap<>();

    public ColmenaItem(Properties properties) {
        super("colmena", "PX-04", 0xFF3C3C, Fx.COLMENA, properties);
    }

    @Override
    public int maxCharge() {
        return 36;
    }

    @Override
    public int regenTicks() {
        return 4;
    }

    @Override
    public int regenDelay() {
        return 20;
    }

    @Override
    public int interval() {
        return COOLDOWN;
    }

    @Override
    public int chargeTicks() {
        return LOCK_TICKS * SALVO;
    }

    @Override
    public float zoom() {
        return 0.3F;
    }

    @Override
    public float kick() {
        return 0.45F;
    }

    public static void forget(Player player) {
        LOCKS.remove(player.getUUID());
    }

    /** Mientras se mantiene el gatillo se fija un objetivo nuevo cada 0,25 s. */
    @Override
    public void triggerTick(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming) {
        if (heldTicks == 0) LOCKS.remove(player.getUUID());
        if (!ready(player, now) || heldTicks == 0 || heldTicks % LOCK_TICKS != 0) return;
        List<Integer> locks = LOCKS.computeIfAbsent(player.getUUID(), k -> new ArrayList<>());
        if (locks.size() >= SALVO || locks.size() >= charge(stack, now)) return;
        LivingEntity target = nextTarget(level, player, locks);
        if (target == null) return;
        locks.add(target.getId());
        Fx.only(player, Fx.LOCK, Fx.COLMENA, target.getBoundingBox().getCenter(), locks.size(), target.getId());
        FX.sound(level, player, PXSounds.COLMENA_FIJAR.get(), 0.8F, 1.0F + locks.size() * 0.08F);
    }

    @Override
    public void triggerRelease(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming) {
        List<Integer> locks = LOCKS.remove(player.getUUID());
        if (player.isSpectator() || !ready(player, now)) return;
        int rockets = Math.min(SALVO, charge(stack, now));
        if (rockets <= 0 || !spend(player, stack, level, now, rockets)) return;
        delay(player, now, COOLDOWN);
        List<Integer> targets = locks == null ? List.of() : locks;
        Vec3 aimPoint = Aim.lookPoint(player, RANGE);
        for (int i = 0; i < rockets; i++) {
            final int n = i;
            final int count = rockets;
            final int targetId = targets.isEmpty() ? -1 : targets.get(i % targets.size());
            Scheduler.schedule(level, i * 2, () -> {
                if (!player.isAlive() || player.level() != level) return;
                Shot shot = Shot.of(player, level, PXNetwork.aiming(player, level.getGameTime()), 0.0, muzzleForward());
                Entity target = targetId >= 0 ? level.getEntity(targetId) : null;
                launch(level, player, shot, n, count, target instanceof LivingEntity l && l.isAlive() ? l : null, aimPoint);
                Fx.muzzle(level, player, Fx.COLMENA, shot.muzzle(), shot.look());
            });
        }
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        // la COLMENA dispara al soltar el gatillo (triggerRelease)
    }

    /** Cada cohete sale en abanico y después corrige el rumbo hacia su objetivo o hacia la mira. */
    private static void launch(ServerLevel level, ServerPlayer player, Shot shot, int n, int count, @Nullable LivingEntity target, Vec3 aimPoint) {
        Vec3 look = shot.look();
        Vec3 right = look.cross(new Vec3(0, 1, 0));
        right = right.lengthSqr() < 1.0E-6 ? new Vec3(1, 0, 0) : right.normalize();
        Vec3 up = right.cross(look).normalize();
        double a = Math.PI * 2.0 * n / Math.max(1, count);
        Vec3 dir = look.add(right.scale(Math.cos(a) * 0.2)).add(up.scale(Math.sin(a) * 0.2 + 0.05)).normalize();
        level.addFreshEntity(MicroRocket.create(level, player, shot.muzzle(), dir.scale(1.3), target, aimPoint));
        FX.sound(level, player, PXSounds.COLMENA_LANZAR.get(), 0.9F, 0.9F + n * 0.06F);
    }

    /** El enemigo visible más centrado en la mira que aún no está fijado (o el mejor ya fijado si no hay más). */
    @Nullable
    private static LivingEntity nextTarget(ServerLevel level, ServerPlayer player, List<Integer> locks) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        AABB box = new AABB(eye, eye.add(look.scale(RANGE))).inflate(RANGE * 0.3);
        LivingEntity best = null;
        LivingEntity bestLocked = null;
        double bestScore = Double.MAX_VALUE;
        double bestLockedScore = Double.MAX_VALUE;
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, x -> Aim.targetable(player, x))) {
            Vec3 c = e.getBoundingBox().getCenter();
            double angle = Aim.angleTo(eye, look, c);
            if (c.distanceTo(eye) > RANGE || angle > LOCK_CONE) continue;
            if (level.clip(new ClipContext(eye, c, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)).getType() != HitResult.Type.MISS) continue;
            double score = angle + (e instanceof Enemy ? 0.0 : 30.0);
            if (locks.contains(e.getId())) {
                if (score < bestLockedScore) {
                    bestLockedScore = score;
                    bestLocked = e;
                }
            } else if (score < bestScore) {
                bestScore = score;
                best = e;
            }
        }
        return best != null ? best : bestLocked;
    }
}
