package com.proyectox.item;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXEffects;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import com.proyectox.util.FallGuard;
import com.proyectox.util.Hits;
import com.proyectox.util.Scheduler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * PX-01 MARTINETE — mazo hidráulico: 8 de daño por golpe, sin empujar al enemigo. Si golpeas a una criatura
 * cayendo desde más de 3 bloques, el golpe hace 25 de daño y abre un círculo de 4 bloques con un terremoto que
 * paraliza 3 s a todas las criaturas de alrededor y, mientras dura, las sacude cada segundo con 2 de daño; la onda
 * amortigua tu caída con un pequeño salto.
 * <p>El daño del golpe en caída se fija en {@code PXEvents.onIncomingDamage}: así es siempre 25, con o sin golpe
 * crítico.
 */
public class MartineteItem extends MeleeItem {
    /** Daño de un golpe normal. */
    public static final double DAMAGE = 8.0;
    /** Daño del golpe en caída. */
    public static final float SLAM_DAMAGE = 25.0F;
    /** Bloques de caída a partir de los que el golpe es en caída. */
    public static final float SLAM_FALL = 3.0F;
    public static final double QUAKE_RADIUS = 4.0;
    public static final int STUN_TICKS = 60;
    /** Sacudidas del terremoto: una cada segundo mientras dura la parálisis, de 2 de daño. */
    public static final float TREMOR_DAMAGE = 2.0F;
    public static final int TREMOR_EVERY = 20;
    public static final int TREMORS = STUN_TICKS / TREMOR_EVERY;

    public MartineteItem(Properties properties) {
        super("martinete", "PX-01", 0xFF9A1F, Fx.MARTINETE, properties);
    }

    /** Si el atacante viene cayendo desde más de 3 bloques (sin planear con élitros). */
    public static boolean slamming(LivingEntity attacker) {
        return attacker.fallDistance > SLAM_FALL && !attacker.onGround() && !attacker.isFallFlying();
    }

    @Override
    protected void onHit(ServerLevel level, ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (slamming(attacker)) {
            quake(level, target, attacker);
        } else {
            FX.sound(level, target, PXSounds.MARTINETE_RUPTURA.get(), 1.0F, 1.1F + level.random.nextFloat() * 0.2F);
        }
    }

    /**
     * Terremoto: paraliza 3 s a todo lo que hay en el círculo, lo sacude cada segundo con 2 de daño mientras dura y la
     * onda frena tu caída con un saltito.
     */
    private void quake(ServerLevel level, LivingEntity target, LivingEntity attacker) {
        Vec3 at = target.position();
        PXEffects.stun(target, STUN_TICKS, attacker);
        for (LivingEntity e : Aim.sphere(level, attacker, at.add(0.0, 0.5, 0.0), QUAKE_RADIUS)) {
            PXEffects.stun(e, STUN_TICKS, attacker);
        }
        for (int i = 1; i <= TREMORS; i++) {
            int stunLeft = STUN_TICKS - i * TREMOR_EVERY;
            Scheduler.schedule(level, i * TREMOR_EVERY, () -> tremor(level, at, attacker, stunLeft));
        }
        Fx.point(level, Fx.QUAKE, style(), at, new Vec3(0.0, 1.0, 0.0), (float) QUAKE_RADIUS, attacker.getId());
        FX.sound(level, at, PXSounds.MARTINETE_RUPTURA.get(), 2.0F, 0.75F + level.random.nextFloat() * 0.1F);
        FX.sound(level, at, PXSounds.FALLA_GRIETA.get(), 1.4F, 0.9F);
        // la onda amortigua la caída: no hay daño por caída y rebotas un poco hacia arriba
        attacker.fallDistance = 0.0F;
        Vec3 v = attacker.getDeltaMovement();
        attacker.setDeltaMovement(v.x * 0.3, 0.42, v.z * 0.3);
        attacker.hurtMarked = true;
        FallGuard.protect(attacker, 40);
    }

    /**
     * Sacudida del terremoto: 2 de daño a todas las criaturas del círculo (nunca a quien golpeó) y parálisis hasta el
     * final del terremoto para las que hayan entrado en él.
     */
    private void tremor(ServerLevel level, Vec3 at, LivingEntity attacker, int stunLeft) {
        DamageSource source = PXDamage.source(level, PXDamage.TERREMOTO, null, attacker);
        for (LivingEntity e : Aim.sphere(level, attacker, at.add(0.0, 0.5, 0.0), QUAKE_RADIUS)) {
            Hits.hurt(e, source, TREMOR_DAMAGE, attacker);
            if (stunLeft > 0) PXEffects.stun(e, stunLeft, attacker);
        }
        Fx.point(level, Fx.TREMOR, style(), at, new Vec3(0.0, 1.0, 0.0), (float) QUAKE_RADIUS, attacker.getId());
        FX.sound(level, at, PXSounds.FALLA_GRIETA.get(), 1.0F, 0.65F + level.random.nextFloat() * 0.15F);
    }
}
