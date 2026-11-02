package com.proyectox.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Estado del arma del jugador local en primera persona (apuntado, retroceso y fogonazo en la mano)
 * y posición aproximada de la boca del cañón de cualquier tirador en el mundo.
 */
public final class Muzzle {
    private Muzzle() {}

    private static final Vec3 UP = new Vec3(0, 1, 0);

    // ---------------------------------------------------------------- apuntado (lo actualiza GunInput)
    private static float aim, aimO;
    // ---------------------------------------------------------------- retroceso y fogonazo
    private static float recoil, recoilO;
    private static int flash, flashMax;
    private static int flashStyle = -1;
    private static float flashSeed;
    /** Giro de la broca del TALADRO (grados) y su velocidad. */
    private static float spin, spinO, spinSpeed;
    /** Golpe de la GUADAÑA o estocada de la PICA en curso (ticks que quedan). */
    private static int melee, meleeMax;
    private static int meleeStyle = -1;

    public static void tick(boolean aiming, boolean drilling) {
        aimO = aim;
        aim = Mth.clamp(aim + (aiming ? 0.25F : -0.25F), 0.0F, 1.0F);
        recoilO = recoil;
        recoil *= 0.55F;
        if (recoil < 0.002F) recoil = 0.0F;
        if (flash > 0) flash--;
        if (melee > 0) melee--;
        spinO = spin;
        spinSpeed = Mth.lerp(0.3F, spinSpeed, drilling ? 55.0F : 0.0F);
        spin += spinSpeed;
        if (spin > 3600.0F) {
            spin -= 3600.0F;
            spinO -= 3600.0F;
        }
    }

    public static void reset() {
        aim = aimO = 0.0F;
        recoil = recoilO = 0.0F;
        flash = 0;
        melee = 0;
        spinSpeed = 0.0F;
    }

    /** El jugador local acaba de dar un barrido o una estocada (animación en primera persona). */
    public static void swung(int style, int ticks) {
        meleeStyle = style;
        meleeMax = Math.max(1, ticks);
        melee = meleeMax;
    }

    /** Progreso del golpe en curso (0 → 1), o -1 si no hay ninguno. */
    public static float melee(float pt) {
        if (melee <= 0) return -1.0F;
        return Mth.clamp(1.0F - (melee - pt) / meleeMax, 0.0F, 1.0F);
    }

    public static int meleeStyle() {
        return meleeStyle;
    }

    /** Progreso del apuntado (0 = cadera, 1 = mira). */
    public static float aim(float pt) {
        float a = Mth.lerp(pt, aimO, aim);
        return a * a * (3.0F - 2.0F * a);
    }

    public static float recoil(float pt) {
        return Mth.lerp(pt, recoilO, recoil);
    }

    public static float spin(float pt) {
        return Mth.lerp(pt, spinO, spin);
    }

    public static boolean spinning() {
        return spinSpeed > 1.0F;
    }

    /** El arma del jugador local acaba de disparar: retroceso y fogonazo en la mano. */
    public static void fired(int style, float kick) {
        if (kick < 0.1F) {
            // armas de chorro o de rayo continuo: solo vibran
            recoil = Math.max(recoil, kick * 3.0F + 0.04F);
        } else {
            recoil = Math.min(1.0F, recoil + 0.15F + kick * 0.85F);
            recoilO = Math.max(recoilO, recoil * 0.6F);
        }
        flashStyle = style;
        flashMax = kick > 0.6F ? 3 : 2;
        flash = flashMax;
        flashSeed = Minecraft.getInstance().level == null ? 0.0F : Minecraft.getInstance().level.random.nextFloat();
    }

    /** Intensidad del fogonazo en la mano (0–1). */
    public static float flash(float pt) {
        if (flash <= 0 || flashMax <= 0) return 0.0F;
        return Mth.clamp((flash - pt) / flashMax, 0.0F, 1.0F);
    }

    public static int flashStyle() {
        return flashStyle;
    }

    public static float flashSeed() {
        return flashSeed;
    }

    // ---------------------------------------------------------------- boca del cañón en el mundo

    /** Si la entidad es el jugador local visto en primera persona. */
    public static boolean firstPerson(Entity e) {
        Minecraft mc = Minecraft.getInstance();
        return e == mc.player && mc.options.getCameraType().isFirstPerson();
    }

    /** Boca del cañón de un tirador, interpolada (para haces y chorros que salen del arma). */
    public static Vec3 world(Entity e, float pt) {
        Vec3 eye = e.getEyePosition(pt);
        Vec3 look = e.getViewVector(pt);
        Vec3 right = look.cross(UP);
        right = right.lengthSqr() < 1.0E-6 ? new Vec3(1, 0, 0) : right.normalize();
        Vec3 up = right.cross(look).normalize();
        double hand = e instanceof LivingEntity l && l.getMainArm() == HumanoidArm.LEFT ? -1.0 : 1.0;
        double fwd, side, down;
        if (firstPerson(e)) {
            float a = aim(pt);
            fwd = 0.75;
            side = Mth.lerp(a, 0.24F, 0.0F);
            down = Mth.lerp(a, 0.2F, 0.12F);
        } else {
            fwd = 0.95;
            side = 0.26;
            down = 0.2;
        }
        return eye.add(look.scale(fwd)).add(right.scale(side * hand)).add(up.scale(-down));
    }
}
