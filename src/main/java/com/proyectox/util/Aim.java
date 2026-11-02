package com.proyectox.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Geometría de disparo: bocachas, dispersión, trazado de rayos y selección de objetivos. */
public final class Aim {
    private Aim() {}

    /** Resultado de un rayo: entidades atravesadas (ordenadas), punto final y normal del bloque golpeado. */
    public record Ray(List<Entity> hits, Vec3 end, boolean hitBlock, Vec3 normal) {}

    private static final Vec3 UP = new Vec3(0, 1, 0);

    /** Punto de salida del disparo: delante, a un lado (mano hábil) y algo por debajo de los ojos. */
    public static Vec3 muzzle(LivingEntity shooter, double forward, double side, double down) {
        Vec3 eye = shooter.getEyePosition();
        Vec3 look = shooter.getLookAngle();
        Vec3 right = look.cross(UP);
        right = right.lengthSqr() < 1.0E-6 ? new Vec3(1, 0, 0) : right.normalize();
        Vec3 up = right.cross(look).normalize();
        double hand = shooter.getMainArm() == HumanoidArm.RIGHT ? 1.0 : -1.0;
        Vec3 p = eye.add(look.scale(forward)).add(right.scale(side * hand)).add(up.scale(-down));
        // si la bocacha queda dentro de un bloque (pegado a una pared) se dispara desde los ojos
        BlockHitResult wall = shooter.level().clip(new ClipContext(eye, p, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter));
        return wall.getType() == HitResult.Type.MISS ? p : eye;
    }

    /** Dirección desde la bocacha hacia el punto exacto que marca la mira (convergencia). */
    public static Vec3 converge(LivingEntity shooter, Vec3 muzzle, double range) {
        Vec3 eye = shooter.getEyePosition();
        Vec3 look = shooter.getLookAngle();
        Vec3 far = eye.add(look.scale(range));
        BlockHitResult hit = shooter.level().clip(new ClipContext(eye, far, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter));
        Vec3 target = hit.getType() == HitResult.Type.MISS ? far : hit.getLocation();
        Vec3 d = target.subtract(muzzle);
        if (d.lengthSqr() < 4.0) return look;
        return d.normalize();
    }

    /** Punto que marca la mira (primer bloque sólido o el final del alcance). */
    public static Vec3 lookPoint(LivingEntity shooter, double range) {
        BlockHitResult hit = lookBlock(shooter, range);
        return hit.getType() == HitResult.Type.MISS ? shooter.getEyePosition().add(shooter.getLookAngle().scale(range)) : hit.getLocation();
    }

    public static BlockHitResult lookBlock(LivingEntity shooter, double range) {
        Vec3 eye = shooter.getEyePosition();
        Vec3 far = eye.add(shooter.getLookAngle().scale(range));
        return shooter.level().clip(new ClipContext(eye, far, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter));
    }

    /** Criaturas cuyo volumen (ampliado en {@code radius}) corta el segmento, sin tener en cuenta los bloques. */
    public static List<LivingEntity> segment(Level level, Entity source, Vec3 from, Vec3 to, double radius) {
        List<LivingEntity> out = new ArrayList<>();
        AABB box = new AABB(from, to).inflate(radius + 1.0);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, x -> targetable(source, x))) {
            AABB bb = e.getBoundingBox().inflate(radius);
            if (bb.contains(from) || bb.clip(from, to).isPresent()) out.add(e);
        }
        return out;
    }

    /** Desvía una dirección dentro de un cono de {@code degrees} grados. */
    public static Vec3 spread(Vec3 dir, RandomSource random, double degrees) {
        Vec3 n = dir.normalize();
        if (degrees <= 0.0) return n;
        Vec3 a = Math.abs(n.y) < 0.99 ? UP : new Vec3(1, 0, 0);
        Vec3 u = n.cross(a).normalize();
        Vec3 v = n.cross(u).normalize();
        double angle = random.nextDouble() * Math.PI * 2.0;
        double mag = Math.tan(Math.toRadians(degrees)) * Math.sqrt(random.nextDouble());
        return n.add(u.scale(Math.cos(angle) * mag)).add(v.scale(Math.sin(angle) * mag)).normalize();
    }

    /** Ángulo en grados entre la dirección y el vector hacia un punto. */
    public static double angleTo(Vec3 from, Vec3 dir, Vec3 point) {
        Vec3 to = point.subtract(from);
        if (to.lengthSqr() < 1.0E-6) return 0.0;
        return Math.toDegrees(Math.acos(Mth.clamp(to.normalize().dot(dir.normalize()), -1.0, 1.0)));
    }

    /**
     * Si una criatura es un objetivo para las armas que eligen solas a quién disparar (torreta CENTINELA, rayos del
     * PULSAR): los monstruos, quien ataca o acaba de atacar al dueño y las criaturas que lo tienen como objetivo.
     * Así nunca disparan a mascotas, aldeanos, animales ni a otros jugadores que no peleen con el dueño.
     */
    public static boolean threat(@Nullable Entity owner, LivingEntity e) {
        if (e instanceof Enemy) return true;
        if (owner instanceof LivingEntity l && (l.getLastHurtByMob() == e || l.getLastHurtMob() == e)) return true;
        return owner != null && e instanceof Mob mob && mob.getTarget() == owner;
    }

    public static boolean targetable(Entity shooter, Entity e) {
        return e != shooter && e.isAlive() && !e.isSpectator() && e instanceof LivingEntity && !(e instanceof ArmorStand)
                && !(e instanceof Player p && p.isCreative());
    }

    /**
     * Rayo instantáneo: se detiene en el primer bloque sólido y devuelve hasta {@code maxHits}
     * entidades vivas cruzadas, de la más cercana a la más lejana.
     */
    public static Ray pierce(Level level, Entity shooter, Vec3 from, Vec3 dir, double range, int maxHits) {
        return pierce(level, shooter, from, dir, range, maxHits, null);
    }

    /** Como {@link #pierce(Level, Entity, Vec3, Vec3, double, int)}, sin alcanzar nunca a {@code ignore} (el dueño de una torreta). */
    public static Ray pierce(Level level, Entity shooter, Vec3 from, Vec3 dir, double range, int maxHits, @Nullable Entity ignore) {
        Vec3 to = from.add(dir.normalize().scale(range));
        BlockHitResult block = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter));
        boolean hitBlock = block.getType() != HitResult.Type.MISS;
        Vec3 end = hitBlock ? block.getLocation() : to;
        Vec3 normal = hitBlock ? Vec3.atLowerCornerOf(block.getDirection().getNormal()) : dir.normalize().reverse();

        record Hit(Entity entity, double dist) {}
        List<Hit> found = new ArrayList<>();
        AABB box = new AABB(from, end).inflate(1.0);
        for (Entity e : level.getEntities(shooter, box, x -> x != ignore && x.isAlive() && !x.isSpectator() && x.isPickable() && x instanceof LivingEntity)) {
            AABB bb = e.getBoundingBox().inflate(0.3);
            if (bb.contains(from)) {
                found.add(new Hit(e, 0.0));
                continue;
            }
            Optional<Vec3> clip = bb.clip(from, end);
            clip.ifPresent(p -> found.add(new Hit(e, p.distanceTo(from))));
        }
        found.sort(Comparator.comparingDouble(Hit::dist));
        List<Entity> hits = new ArrayList<>();
        for (Hit h : found) {
            if (hits.size() >= maxHits) break;
            hits.add(h.entity());
        }
        boolean blocked = hitBlock;
        if (hits.size() >= maxHits && !found.isEmpty()) {
            // el rayo se detiene en el último objetivo que puede atravesar
            double d = found.get(Math.min(found.size(), maxHits) - 1).dist();
            end = from.add(dir.normalize().scale(Math.max(d, 0.5)));
            blocked = false;
            normal = dir.normalize().reverse();
        }
        return new Ray(hits, end, blocked, normal);
    }

    /** Criaturas dentro de una esfera. */
    public static List<LivingEntity> sphere(Level level, Entity source, Vec3 center, double radius) {
        AABB box = new AABB(center, center).inflate(radius);
        List<LivingEntity> out = new ArrayList<>();
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, x -> targetable(source, x))) {
            if (e.getBoundingBox().getCenter().distanceTo(center) <= radius + e.getBbWidth() * 0.5) out.add(e);
        }
        return out;
    }

    /** Criaturas dentro de un cono (alcance y semiángulo en grados). */
    public static List<LivingEntity> cone(Level level, Entity source, Vec3 origin, Vec3 dir, double range, double halfAngle) {
        AABB box = new AABB(origin, origin.add(dir.normalize().scale(range))).inflate(range * 0.6);
        List<LivingEntity> out = new ArrayList<>();
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, x -> targetable(source, x))) {
            Vec3 c = e.getBoundingBox().getCenter();
            if (c.distanceTo(origin) <= range + e.getBbWidth() * 0.5 && angleTo(origin, dir, c) <= halfAngle) out.add(e);
        }
        return out;
    }
}
