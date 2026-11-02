package com.proyectox.item;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import com.proyectox.util.Scheduler;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * PX-08 SERPENTINA — látigo de cadena electrificada. Clic izquierdo: latigazo de 6 de daño (+3 bloques
 * de alcance). Clic derecho: lanza una cuerda azul de 24 bloques que atrapa al enemigo y lo arrastra
 * hasta ti; el primer golpe que le des después hace 18 de daño.
 */
public class SerpentinaItem extends MeleeItem {
    public static final double RANGE = 24.0;
    public static final int PULL_TICKS = 16;
    public static final int COOLDOWN = 16;
    /** Ticks durante los que el primer golpe al enemigo arrastrado hace daño triple. */
    public static final int EMPOWER_TICKS = 100;
    public static final float EMPOWER = 3.0F;

    private record Drag(UUID player, long until) {}

    /** Enemigos arrastrados (id de entidad → quién y hasta cuándo). */
    private static final Map<Integer, Drag> DRAGGED = new ConcurrentHashMap<>();

    public SerpentinaItem(Properties properties) {
        super("serpentina", "PX-08", 0x5A7BFF, Fx.SERPENTINA, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel server) throwRope(server, player);
        player.getCooldowns().addCooldown(this, COOLDOWN);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private static void throwRope(ServerLevel level, Player player) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        LivingEntity target = target(level, player, eye, look);
        Vec3 hand = Aim.muzzle(player, 0.6, 0.3, 0.3);
        FX.sound(level, player, PXSounds.SERPENTINA_LATIGO.get(), 1.0F, 1.1F + level.random.nextFloat() * 0.2F);
        if (target == null) {
            Vec3 end = Aim.lookPoint(player, RANGE);
            Fx.line(level, Fx.ROPE, Fx.SERPENTINA, hand, end, 8.0F, player.getId());
            return;
        }
        long now = level.getGameTime();
        DRAGGED.put(target.getId(), new Drag(player.getUUID(), now + EMPOWER_TICKS));
        if (DRAGGED.size() > 256) DRAGGED.values().removeIf(d -> d.until() < now);
        Fx.tether(level, Fx.ROPE, Fx.SERPENTINA, player, target, hand, PULL_TICKS);
        FX.sound(level, target, PXSounds.SERPENTINA_ARCO.get(), 1.0F, 0.8F);
        for (int i = 0; i < PULL_TICKS; i++) {
            Scheduler.schedule(level, i, () -> pull(player, target));
        }
    }

    /** Un tick de arrastre: el enemigo se acerca al jugador hasta quedar a dos bloques. */
    private static void pull(Player player, LivingEntity target) {
        if (!target.isAlive() || !player.isAlive() || target.level() != player.level()) return;
        Vec3 to = player.position().add(0.0, 0.4, 0.0).subtract(target.position());
        double d = to.length();
        if (d < 2.2) {
            target.setDeltaMovement(target.getDeltaMovement().scale(0.2));
            target.hurtMarked = true;
            return;
        }
        Vec3 v = to.normalize().scale(Math.min(1.3, 0.35 + d * 0.12));
        target.setDeltaMovement(v.add(0.0, d > 4.0 ? 0.06 : 0.0, 0.0));
        target.resetFallDistance();
        target.hurtMarked = true;
    }

    /** El enemigo más centrado en la mira (hasta 6°) al que se ve, en 24 bloques. */
    @Nullable
    private static LivingEntity target(ServerLevel level, Player player, Vec3 eye, Vec3 look) {
        AABB box = new AABB(eye, eye.add(look.scale(RANGE))).inflate(3.0);
        LivingEntity best = null;
        double bestAngle = 6.0;
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, x -> Aim.targetable(player, x))) {
            Vec3 c = e.getBoundingBox().getCenter();
            if (c.distanceTo(eye) > RANGE) continue;
            double angle = e.getBoundingBox().inflate(0.3).clip(eye, eye.add(look.scale(RANGE))).isPresent() ? 0.0 : Aim.angleTo(eye, look, c);
            if (angle > bestAngle) continue;
            if (level.clip(new ClipContext(eye, c, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)).getType() != HitResult.Type.MISS) continue;
            bestAngle = angle;
            best = e;
        }
        return best;
    }

    /**
     * Daño de un golpe de la SERPENTINA: el primero tras arrastrar al enemigo hace el triple (18).
     * @return el daño final
     */
    public static float empower(Entity target, Player attacker, float amount) {
        Drag d = DRAGGED.get(target.getId());
        if (d == null || !d.player().equals(attacker.getUUID())) return amount;
        DRAGGED.remove(target.getId());
        if (target.level().getGameTime() > d.until()) return amount;
        if (target.level() instanceof ServerLevel level) {
            Fx.point(level, Fx.MELEE, Fx.SERPENTINA, target.getBoundingBox().getCenter(), attacker.getLookAngle(), 2.0F, attacker.getId());
        }
        return amount * EMPOWER;
    }
}
