package com.proyectox.item;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import com.proyectox.util.Hits;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Armas cuerpo a cuerpo de la serie v7: con el clic izquierdo dan un barrido (katana NEÓN, machete ÓXIDO, espada viva
 * SIMBIONTE), una estocada (lanza AGUIJÓN) o un golpe seco (bate RELÉ, mazo PALANCA). Manteniendo el clic, golpean a su
 * cadencia. Cada una se describe con su {@link Spec}; lo que pasa además al golpear va en {@link OnHit}.
 */
public class SpecMelee extends MeleeItem implements TriggerWeapon {
    public enum Kind { SWEEP, THRUST, SMASH }

    /** Efecto extra en cada criatura alcanzada (servidor). */
    @FunctionalInterface
    public interface OnHit {
        void hit(ServerLevel level, ServerPlayer player, LivingEntity target);
    }

    public static final class Spec {
        Kind kind = Kind.SWEEP;
        float damage = 8.0F;
        double reach = 3.5;
        double halfAngle = 60.0;
        int interval = 10;
        float splash;
        double splashRadius;
        ResourceKey<DamageType> type = PXDamage.CORTE;
        @Nullable
        Supplier<SoundEvent> sound;
        float pitch = 1.0F;
        @Nullable
        OnHit onHit;

        /** Barrido: radio y ángulo total (grados). */
        public Spec sweep(double radius, double angle) {
            this.kind = Kind.SWEEP;
            this.reach = radius;
            this.halfAngle = angle / 2.0;
            return this;
        }

        /** Estocada en línea recta que atraviesa a todos. */
        public Spec thrust(double reach) {
            this.kind = Kind.THRUST;
            this.reach = reach;
            return this;
        }

        /** Golpe seco a la primera criatura delante (y, si {@code splash} &gt; 0, onda alrededor del impacto). */
        public Spec smash(double reach, float splash, double radius) {
            this.kind = Kind.SMASH;
            this.reach = reach;
            this.splash = splash;
            this.splashRadius = radius;
            return this;
        }

        public Spec damage(float damage) {
            this.damage = damage;
            return this;
        }

        /** Ticks entre golpes manteniendo el clic. */
        public Spec interval(int ticks) {
            this.interval = ticks;
            return this;
        }

        public Spec type(ResourceKey<DamageType> type) {
            this.type = type;
            return this;
        }

        public Spec sound(Supplier<SoundEvent> sound, float pitch) {
            this.sound = sound;
            this.pitch = pitch;
            return this;
        }

        public Spec onHit(OnHit onHit) {
            this.onHit = onHit;
            return this;
        }
    }

    public static Spec spec() {
        return new Spec();
    }

    /** Próximo tick en que cada jugador puede volver a golpear (también haciendo clic muy rápido). */
    private static final Map<UUID, Long> NEXT = new ConcurrentHashMap<>();

    private final Spec spec;

    public SpecMelee(String key, String code, int color, int style, Properties properties, Spec spec) {
        super(key, code, color, style, properties);
        this.spec = spec;
    }

    /** Atributos (daño y golpes por segundo) que se ven en la descripción del arma. */
    public static ItemAttributeModifiers attributes(Spec spec) {
        return MeleeItem.attributes(spec.damage, 20.0 / spec.interval, 0.0);
    }

    public static void forget(Player player) {
        NEXT.remove(player.getUUID());
    }

    public Kind kind() {
        return this.spec.kind;
    }

    @Override
    public void triggerTick(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming) {
        if (player.isSpectator()) return;
        Long next = NEXT.get(player.getUUID());
        if (next != null && now < next) return;
        NEXT.put(player.getUUID(), now + this.spec.interval);
        switch (this.spec.kind) {
            case THRUST -> thrust(player, level);
            case SMASH -> smash(player, level);
            default -> sweep(player, level);
        }
        player.swing(InteractionHand.MAIN_HAND, true);
        if (this.spec.sound != null) {
            FX.sound(level, player, this.spec.sound.get(), 1.1F, this.spec.pitch * (0.92F + level.random.nextFloat() * 0.16F));
        }
    }

    private void hit(ServerLevel level, ServerPlayer player, LivingEntity e, float damage, Vec3 dir) {
        DamageSource ds = PXDamage.source(level, this.spec.type, player, player);
        Hits.hurt(e, ds, damage, player);
        Fx.point(level, Fx.MELEE, style(), e.getBoundingBox().getCenter(), dir, 1.0F, player.getId());
        if (this.spec.onHit != null) this.spec.onHit.hit(level, player, e);
    }

    private static boolean wallBetween(ServerLevel level, Player player, Vec3 from, Vec3 to) {
        return level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)).getType() != HitResult.Type.MISS;
    }

    private void sweep(ServerPlayer player, ServerLevel level) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 flat = new Vec3(look.x, 0.0, look.z);
        flat = flat.lengthSqr() < 1.0E-4 ? Vec3.directionFromRotation(0.0F, player.getYRot()) : flat.normalize();
        Vec3 center = player.position().add(0.0, player.getBbHeight() * 0.55, 0.0);
        double r = this.spec.reach;
        AABB box = new AABB(center, center).inflate(r + 1.0, 2.5, r + 1.0);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, x -> Aim.targetable(player, x))) {
            Vec3 c = e.getBoundingBox().getCenter();
            Vec3 to = new Vec3(c.x - center.x, 0.0, c.z - center.z);
            double dist = to.length();
            if (dist > r + e.getBbWidth() * 0.5 || Math.abs(c.y - center.y) > 2.2 + e.getBbHeight() * 0.5) continue;
            if (dist > 0.6 && Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, to.normalize().dot(flat))))) > this.spec.halfAngle) continue;
            if (wallBetween(level, player, eye, c)) continue;
            hit(level, player, e, this.spec.damage, to.lengthSqr() < 1.0E-6 ? flat : to.normalize());
        }
        Fx.point(level, Fx.SWING7, style(), center, flat, (float) r, player.getId());
    }

    private void thrust(ServerPlayer player, ServerLevel level) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 far = eye.add(look.scale(this.spec.reach));
        BlockHitResult wall = level.clip(new ClipContext(eye, far, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        Vec3 end = wall.getType() == HitResult.Type.MISS ? far : wall.getLocation();
        for (LivingEntity e : Aim.segment(level, player, eye, end, 0.6)) hit(level, player, e, this.spec.damage, look);
        Fx.line(level, Fx.THRUST7, style(), eye, end, wall.getType() == HitResult.Type.MISS ? 0.0F : 1.0F, player.getId());
    }

    private void smash(ServerPlayer player, ServerLevel level) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        double reach = this.spec.reach;
        Vec3 far = eye.add(look.scale(reach));
        BlockHitResult wall = level.clip(new ClipContext(eye, far, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        Vec3 end = wall.getType() == HitResult.Type.MISS ? far : wall.getLocation();
        // la criatura más cercana en la línea de mira (con algo de margen)
        LivingEntity first = null;
        double best = Double.MAX_VALUE;
        for (LivingEntity e : Aim.segment(level, player, eye, end, 0.7)) {
            double d = e.getBoundingBox().getCenter().distanceToSqr(eye);
            if (d < best) {
                best = d;
                first = e;
            }
        }
        Vec3 at = null;
        if (first != null) {
            at = first.getBoundingBox().getCenter();
            hit(level, player, first, this.spec.damage, look);
        } else if (wall.getType() != HitResult.Type.MISS && this.spec.splash > 0.0F) {
            at = wall.getLocation();
        }
        if (at == null) {
            Fx.point(level, Fx.SWING7, style(), player.position().add(0.0, player.getBbHeight() * 0.55, 0.0), look, (float) reach * 0.6F,
                    player.getId());
            return;
        }
        if (this.spec.splash > 0.0F) {
            List<LivingEntity> around = new ArrayList<>(Aim.sphere(level, player, at, this.spec.splashRadius));
            for (LivingEntity e : around) {
                if (e == first) continue;
                hit(level, player, e, this.spec.splash, e.getBoundingBox().getCenter().subtract(at).normalize());
            }
        }
        Fx.point(level, Fx.PULSE7, style(), at, look.reverse(), (float) Math.max(1.0, this.spec.splashRadius), player.getId());
    }
}
