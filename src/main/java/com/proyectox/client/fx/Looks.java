package com.proyectox.client.fx;

import com.proyectox.entity.EnergyBolt;
import com.proyectox.entity.Field;
import com.proyectox.entity.Grenade;
import com.proyectox.entity.MicroRocket;
import com.proyectox.entity.Orb;
import com.proyectox.entity.Piercer;
import com.proyectox.entity.PXProjectile;
import com.proyectox.entity.PlasmaBolt;
import com.proyectox.entity.SawDisc;
import com.proyectox.entity.Singularity;
import com.proyectox.entity.SlashWave;
import com.proyectox.entity.SonicWave;
import com.proyectox.entity.Sticky;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/** Aspecto de cada proyectil: núcleo, resplandor, estela y detalles propios del arma que lo dispara. */
public final class Looks {
    private Looks() {}

    private static final Vector3f ZERO = new Vector3f();

    private static Vector3f dir(PXProjectile p) {
        Vec3 h = p.heading();
        return new Vector3f((float) h.x, (float) h.y, (float) h.z);
    }

    /** Posiciones de la estela: la actual interpolada seguida de las guardadas. */
    private static List<Vec3> trail(PXProjectile p, float pt, int max) {
        List<Vec3> out = new ArrayList<>();
        out.add(p.getPosition(pt));
        int n = Math.min(max, p.trailSize());
        for (int i = 0; i < n; i++) out.add(p.trailPoint(i));
        return out;
    }

    private static float time(net.minecraft.world.entity.Entity e, float pt) {
        return e.tickCount + pt;
    }

    // ---------------------------------------------------------------- pulsos de energía

    /** AVISPÓN: gota de plasma verde (naranja cuando el cañón está al rojo). */
    public static void plasma(PlasmaBolt e, float pt, Draw d) {
        boolean hot = e.isHot();
        int main = hot ? 0xFF7A1E : 0x8CFF2E;
        int core = hot ? 0xFFF0C8 : 0xF0FFD8;
        Vector3f dir = dir(e);
        float pulse = 0.85F + 0.15F * Mth.sin(time(e, pt) * 2.1F);
        d.trail(FxAtlas.BEAM, trail(e, pt, 6), 0.11F, main, hot ? 0xFF2000 : 0x1E9A00, 0.9F);
        d.cross(true, FxAtlas.BOLT, ZERO, dir, 0.9F, 0.25F, 0.13F, main, 1.0F);
        d.billboard(true, FxAtlas.GLOW, ZERO, 0.42F * pulse, 0.0F, main, 0.85F);
        d.billboard(true, FxAtlas.CORE, ZERO, 0.14F, 0.0F, core, 1.0F);
    }

    /** ROTOR (trazadora), COLIBRÍ (aguja de cristal), HALCÓN (pulso), ESCARCHA (láser de hielo) y BASTIÓN (bala cinética). */
    public static void bolt(EnergyBolt e, float pt, Draw d) {
        Vector3f dir = dir(e);
        float t = time(e, pt);
        switch (e.style()) {
            case EnergyBolt.NEEDLE -> {
                d.trail(FxAtlas.BEAM_THIN, trail(e, pt, 5), 0.06F, 0xFF8FD0, 0x8A2060, 0.8F);
                d.cross(true, FxAtlas.BOLT, ZERO, dir, 0.7F, 0.15F, 0.05F, 0xFFB8E4, 1.0F);
                d.billboard(true, FxAtlas.GLINT, ZERO, 0.16F, t * 0.3F, 0xFFD8F0, 0.9F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 0.2F, 0.0F, 0xFF6FC8, 0.6F);
            }
            case EnergyBolt.DRONE -> {
                d.trail(FxAtlas.BEAM, trail(e, pt, 5), 0.08F, 0xFFB13B, 0x7A3000, 0.8F);
                d.cross(true, FxAtlas.BOLT, ZERO, dir, 0.7F, 0.2F, 0.09F, 0xFFC060, 1.0F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 0.3F, 0.0F, 0xFFA235, 0.8F);
                d.billboard(true, FxAtlas.CORE, ZERO, 0.1F, 0.0F, 0xFFF2D8, 1.0F);
            }
            case EnergyBolt.FROST -> {
                d.trail(FxAtlas.BEAM_NOISE, trail(e, pt, 7), 0.1F, 0xBFF6FF, 0x2A8AFF, 0.85F);
                d.cross(true, FxAtlas.BOLT, ZERO, dir, 1.1F, 0.2F, 0.08F, 0xBFF6FF, 1.0F);
                d.billboard(true, FxAtlas.SNOW, ZERO, 0.2F, t * 0.25F, 0xE8FDFF, 0.9F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 0.32F, 0.0F, 0x7FE4FF, 0.8F);
                d.billboard(true, FxAtlas.CORE, ZERO, 0.09F, 0.0F, 0xFFFFFF, 1.0F);
            }
            case EnergyBolt.SLUG -> {
                // CENTELLA: bala de plasma pesada, dorada, con estrella giratoria
                float pulse = 0.85F + 0.15F * Mth.sin(t * 1.7F);
                d.trail(FxAtlas.BEAM, trail(e, pt, 7), 0.17F, 0xFFD25A, 0x7A3A00, 0.9F);
                d.cross(true, FxAtlas.BOLT, ZERO, dir, 1.3F, 0.3F, 0.17F, 0xFFC83A, 1.0F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 0.6F * pulse, 0.0F, 0xFFA820, 0.9F);
                d.billboard(true, FxAtlas.STAR4, ZERO, 0.34F, t * 0.25F, 0xFFE8A0, 0.85F);
                d.billboard(true, FxAtlas.CORE, ZERO, 0.17F, 0.0F, 0xFFF8E0, 1.0F);
            }
            case EnergyBolt.BULLET -> {
                d.trail(FxAtlas.BEAM, trail(e, pt, 5), 0.07F, 0xFFD8A8, 0x8A4A10, 0.8F);
                d.cross(true, FxAtlas.BOLT, ZERO, dir, 1.0F, 0.2F, 0.08F, 0xFFC48A, 1.0F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 0.28F, 0.0F, 0xFFB070, 0.75F);
                d.billboard(true, FxAtlas.CORE, ZERO, 0.09F, 0.0F, 0xFFF4E0, 1.0F);
            }
            default -> {
                // trazadora del ROTOR: larga y fina
                d.cross(true, FxAtlas.BOLT, ZERO, dir, 2.4F, 0.2F, 0.06F, 0xFFC85A, 1.0F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 0.22F, 0.0F, 0xFFB040, 0.7F);
                d.billboard(true, FxAtlas.CORE, ZERO, 0.07F, 0.0F, 0xFFF6E0, 1.0F);
            }
        }
    }

    // ---------------------------------------------------------------- explosivos

    /** COLMENA: microcohete con llama de escape parpadeante y luz roja de guiado. */
    public static void rocket(MicroRocket e, float pt, Draw d) {
        Vector3f dir = dir(e);
        float t = time(e, pt);
        float flick = 0.8F + 0.2F * Mth.sin(t * 5.3F) + 0.1F * Mth.sin(t * 13.1F);
        d.trail(FxAtlas.BEAM, trail(e, pt, 8), 0.12F, 0xFFB060, 0x601000, 0.7F);
        // llama de escape (desde la cola hacia atrás)
        Vector3f back = new Vector3f(dir).negate();
        Vector3f tail = new Vector3f(back).mul(0.25F);
        d.cross(true, FxAtlas.FLASH_SIDE, tail, back, 0.0F, 0.9F * flick, 0.16F * flick, 0xFFB050, 1.0F);
        d.billboard(true, FxAtlas.GLOW, tail, 0.45F * flick, 0.0F, 0xFF7A30, 0.9F);
        d.billboard(true, FxAtlas.CORE, tail, 0.12F, 0.0F, 0xFFF0D0, 1.0F);
        // luz de guiado
        if (((int) (t * 2)) % 2 == 0) d.billboard(true, FxAtlas.CORE, new Vector3f(dir).mul(0.22F), 0.07F, 0.0F, 0xFF3020, 1.0F);
    }

    /** TRITURADORA: granada metálica que gira, con su banda luminosa ácida. */
    public static void grenade(Grenade e, float pt, Draw d) {
        boolean small = e.kind() == Grenade.BOMBLET;
        float t = time(e, pt);
        float size = small ? 0.11F : 0.2F;
        float blink = 0.6F + 0.4F * Mth.sin(t * (small ? 1.8F : 1.1F));
        d.billboard(true, FxAtlas.GLOW, ZERO, size * 2.4F * blink, 0.0F, 0xC8E04A, 0.6F);
        d.billboard(false, FxAtlas.GRENADE, ZERO, size, t * 0.35F, 0xFFFFFF, 1.0F);
        d.billboard(true, FxAtlas.CORE, ZERO, size * 0.5F * blink, 0.0F, 0xF4FFB0, 0.8F);
    }

    // ---------------------------------------------------------------- especiales

    /** SIERRA: hoja metálica girando a toda velocidad con su halo verde. */
    public static void disc(SawDisc e, float pt, Draw d) {
        float t = time(e, pt);
        Vector3f dir = dir(e);
        Vector3f normal = new Vector3f(0, 1, 0);
        Vector3f tangent = new Vector3f(dir.x, 0, dir.z);
        if (tangent.lengthSquared() < 1.0E-6) tangent.set(1, 0, 0);
        d.trail(FxAtlas.BEAM, trail(e, pt, 6), 0.18F, 0x2EE6A0, 0x0A5A40, 0.55F);
        d.flat(true, FxAtlas.GLOW, ZERO, normal, tangent, 0.7F, 0.7F, 0.0F, 0x2EE6A0, 0.55F);
        d.flat(false, FxAtlas.SAW, ZERO, normal, tangent, 0.42F, 0.42F, -t * 1.6F, 0xFFFFFF, 1.0F);
        d.flat(true, FxAtlas.SAW_BLUR, new Vector3f(0, 0.01F, 0), normal, tangent, 0.45F, 0.45F, -t * 1.6F, 0x7FFFD0, 0.9F);
    }

    /** VÓRTICE: horizonte de sucesos negro con disco de acreción; abierto, un remolino gigante. */
    public static void singularity(Singularity e, float pt, Draw d) {
        float t = time(e, pt);
        int main = 0xA45CFF;
        if (!e.isOpen()) {
            Vector3f tilt = new Vector3f(0.35F, 1.0F, 0.2F).normalize();
            d.trail(FxAtlas.BEAM, trail(e, pt, 8), 0.2F, main, 0x200040, 0.7F);
            d.billboard(true, FxAtlas.GLOW, ZERO, 0.9F + 0.1F * Mth.sin(t * 0.8F), 0.0F, main, 0.8F);
            d.flat(true, FxAtlas.ACCRETION, ZERO, tilt, new Vector3f(1, 0, 0), 0.75F, 0.75F, t * 0.35F, 0xD8B0FF, 1.0F);
            d.billboard(false, FxAtlas.ORB_CORE, ZERO, 0.32F, 0.0F, 0xFFFFFF, 1.0F);
            d.billboard(true, FxAtlas.RING, ZERO, 0.36F, 0.0F, 0xF0E0FF, 0.8F);
            return;
        }
        float open = e.openTicks() + pt;
        float grow = Math.min(1.0F, open / 6.0F);
        float end = Math.max(0.0F, (open - (Singularity.VORTEX_TICKS - 8)) / 8.0F);
        float r = (float) Singularity.PULL_RADIUS * 0.75F * grow * (1.0F - end * 0.8F);
        Vector3f up = new Vector3f(0, 1, 0);
        d.flat(true, FxAtlas.VORTEX, ZERO, up, new Vector3f(1, 0, 0), r, r, t * 0.6F, main, 0.9F);
        d.flat(true, FxAtlas.VORTEX, new Vector3f(0, 0.02F, 0), up, new Vector3f(1, 0, 0), r * 0.6F, r * 0.6F, t * 1.1F, 0xE0C8FF, 0.7F);
        d.flat(true, FxAtlas.ACCRETION, ZERO, up, new Vector3f(1, 0, 0), 1.6F * grow, 1.6F * grow, t * 0.5F, 0xD8B0FF, 1.0F);
        d.billboard(true, FxAtlas.GLOW, ZERO, (1.6F + end * 3.0F) * grow, 0.0F, main, 0.9F);
        d.billboard(false, FxAtlas.ORB_CORE, ZERO, 0.6F * grow * (1.0F - end * 0.7F), 0.0F, 0xFFFFFF, 1.0F);
        d.billboard(true, FxAtlas.RING, ZERO, 0.65F * grow, 0.0F, 0xF4E8FF, 0.9F);
    }

    /** CIZALLA: media luna de plasma que avanza de canto. */
    public static void slash(SlashWave e, float pt, Draw d) {
        float t = time(e, pt);
        Vector3f dir = dir(e);
        Vector3f side = new Vector3f(dir).cross(new Vector3f(0, 1, 0));
        if (side.lengthSquared() < 1.0E-6) side.set(1, 0, 0);
        side.normalize();
        Vector3f normal = new Vector3f(side).cross(dir).normalize();
        float fade = 1.0F - Math.min(0.7F, t / 26.0F);
        // la curva del sprite (abajo en la textura) mira hacia delante
        d.flat(true, FxAtlas.CRESCENT, ZERO, normal, side, 1.3F, 1.3F, 0.0F, 0xFF2E7E, fade);
        d.flat(true, FxAtlas.CRESCENT, new Vector3f(dir).mul(0.1F), normal, side, 1.1F, 1.1F, 0.0F, 0xFFE0EE, fade * 0.8F);
        d.billboard(true, FxAtlas.GLOW, ZERO, 0.8F, 0.0F, 0xFF2E7E, 0.5F * fade);
    }

    /** ECO: anillos sónicos que se ensanchan mientras avanzan. */
    public static void wave(SonicWave e, float pt, Draw d) {
        float age = e.tickCount + pt;
        Vec3 dv = e.direction();
        Vector3f dir = new Vector3f((float) dv.x, (float) dv.y, (float) dv.z);
        Vector3f side = Math.abs(dir.y) < 0.95F ? new Vector3f(0, 1, 0).cross(dir) : new Vector3f(1, 0, 0).cross(dir);
        float fade = 1.0F - Math.max(0.0F, (age - SonicWave.LIFE * 0.6F) / (SonicWave.LIFE * 0.4F));
        for (int i = 0; i < 4; i++) {
            float a = age - i * 1.5F;
            if (a < 0) continue;
            float r = SonicWave.radius(a);
            Vector3f c = new Vector3f(dir).mul((float) (-i * 1.5F * SonicWave.SPEED / 1.5F));
            float alpha = fade * (1.0F - i * 0.22F);
            d.flat(true, i == 0 ? FxAtlas.SHOCK : FxAtlas.SOUNDWAVE, c, dir, side, r, r, a * 0.05F, i == 0 ? 0xF0D8FF : 0xD9A8FF, alpha * 0.8F);
        }
        d.billboard(true, FxAtlas.GLOW, ZERO, SonicWave.radius(age) * 0.6F, 0.0F, 0xD9A8FF, 0.35F * fade);
    }

    // ---------------------------------------------------------------- serie PX-2x / PX-3x

    /** MORTERO (obús), SOLAR (sol), ENJAMBRE (cápsula), CÁUSTICA (glóbulo) y ESQUIRLA (cristal y esquirlas). */
    public static void orb(Orb e, float pt, Draw d) {
        float t = time(e, pt);
        Vector3f dir = dir(e);
        switch (e.kind()) {
            case Orb.SHELL -> {
                d.billboard(false, FxAtlas.GRENADE, ZERO, 0.26F, t * 0.25F, 0xFFB890, 1.0F);
                Vector3f tail = new Vector3f(dir).mul(-0.28F);
                d.billboard(true, FxAtlas.GLOW, tail, 0.4F, 0.0F, 0xFF7A2A, 0.85F);
                d.billboard(true, FxAtlas.CORE, tail, 0.11F, 0.0F, 0xFFF0D0, 1.0F);
                if (((int) (t * 1.5F)) % 2 == 0) d.billboard(true, FxAtlas.CORE, new Vector3f(dir).mul(0.22F), 0.07F, 0.0F, 0xFF3020, 1.0F);
            }
            case Orb.SOLAR -> {
                float s = 0.35F + 0.65F * e.power();
                float pulse = 0.9F + 0.1F * Mth.sin(t * 1.9F);
                d.trail(FxAtlas.BEAM, trail(e, pt, 8), 0.32F * s, 0xFFD23A, 0x802000, 0.8F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 1.6F * s * pulse, 0.0F, 0xFF9A20, 0.9F);
                d.billboard(true, FxAtlas.CORONA, ZERO, 1.15F * s, t * 0.04F, 0xFFD860, 1.0F);
                d.billboard(true, FxAtlas.CORONA, ZERO, 0.95F * s, 1.3F - t * 0.06F, 0xFFF0B0, 0.7F);
                d.billboard(true, FxAtlas.CORE, ZERO, 0.42F * s, 0.0F, 0xFFFFF0, 1.0F);
            }
            case Orb.POD -> {
                float blink = ((int) (t * 0.8F)) % 2 == 0 ? 1.0F : 0.3F;
                d.trail(FxAtlas.BEAM_THIN, trail(e, pt, 5), 0.06F, 0xFFC840, 0x603000, 0.6F);
                d.billboard(false, FxAtlas.GRENADE, ZERO, 0.21F, t * 0.3F, 0xFFD870, 1.0F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 0.48F, 0.0F, 0xFFB800, 0.6F);
                d.billboard(true, FxAtlas.CORE, ZERO, 0.08F, 0.0F, 0xFFF0B0, blink);
            }
            case Orb.ACID -> {
                float wob = 0.9F + 0.1F * Mth.sin(t * 2.3F);
                d.trail(FxAtlas.BEAM, trail(e, pt, 5), 0.11F, 0xB4FF1E, 0x205000, 0.75F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 0.34F * wob, 0.0F, 0x8CFF10, 0.85F);
                d.billboard(true, FxAtlas.BUBBLE, ZERO, 0.17F, t * 0.1F, 0xE8FFB0, 0.9F);
                d.billboard(true, FxAtlas.CORE, ZERO, 0.08F, 0.0F, 0xF4FFD0, 1.0F);
            }
            case Orb.CRYSTAL -> {
                d.trail(FxAtlas.BEAM, trail(e, pt, 7), 0.13F, 0xD8A0FF, 0x3A0A70, 0.8F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 0.42F, 0.0F, 0xA45CFF, 0.8F);
                d.billboard(false, FxAtlas.SHARD, ZERO, 0.3F, t * 0.45F, 0xE6C8FF, 0.95F);
                d.billboard(true, FxAtlas.GLINT, ZERO, 0.24F, -t * 0.3F, 0xF6E8FF, 0.9F);
            }
            case Orb.ROCKET -> {
                // llama de escape (el cohete lo dibuja PropRenderer con su modelo 3D)
                float flick = 0.8F + 0.2F * Mth.sin(t * 5.3F) + 0.1F * Mth.sin(t * 13.1F);
                Vector3f back = new Vector3f(dir).negate();
                Vector3f tail = new Vector3f(back).mul(0.42F);
                d.cross(true, FxAtlas.FLASH_SIDE, tail, back, 0.0F, 1.1F * flick, 0.2F * flick, 0xFFB050, 1.0F);
                d.billboard(true, FxAtlas.GLOW, tail, 0.6F * flick, 0.0F, 0xFF7A30, 0.9F);
                d.billboard(true, FxAtlas.CORE, tail, 0.15F, 0.0F, 0xFFF0D0, 1.0F);
            }
            case Orb.METEOR -> {
                // envoltura de fuego alrededor de la roca y cola incandescente
                Vector3f back = new Vector3f(dir).negate();
                d.trail(FxAtlas.BEAM, trail(e, pt, 10), 0.9F, 0xFFB040, 0x601000, 0.8F);
                d.cross(true, FxAtlas.FLASH_SIDE, ZERO, back, 0.0F, 3.5F, 0.9F, 0xFF8030, 0.8F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 2.6F, 0.0F, 0xFF5A20, 0.9F);
                d.billboard(true, FxAtlas.CORONA, ZERO, 1.8F, t * 0.05F, 0xFFB040, 0.7F);
            }
            case Orb.GRENADE40 -> {
                // la granada de 40 mm se dibuja con su modelo 3D (PropRenderer) y no lleva efectos
            }
            case Orb.PULSAR -> {
                float pulse = 0.85F + 0.15F * Mth.sin(t * 1.7F);
                d.trail(FxAtlas.BEAM, trail(e, pt, 8), 0.25F, 0xFF60E8, 0x400040, 0.7F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 1.3F * pulse, 0.0F, 0xFF40E0, 0.85F);
                d.billboard(true, FxAtlas.RING_NOISE, ZERO, 0.7F, t * 0.2F, 0xFFA0F0, 0.9F);
                d.billboard(true, FxAtlas.ORB_CORE, ZERO, 0.45F, -t * 0.1F, 0xFFE0FA, 1.0F);
                d.billboard(true, FxAtlas.CORE, ZERO, 0.2F, 0.0F, 0xFFFFFF, 1.0F);
            }
            default -> {
                d.trail(FxAtlas.BEAM_THIN, trail(e, pt, 4), 0.06F, 0xE0B8FF, 0x40107A, 0.8F);
                d.billboard(false, FxAtlas.SHARD, ZERO, 0.14F, t * 0.6F, 0xEAD8FF, 0.95F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 0.17F, 0.0F, 0xC07CFF, 0.7F);
            }
        }
    }

    /** ARPÓN (arpón de fusión que parpadea cada vez más deprisa) y MINADOR (mina pegada a la superficie). */
    public static void sticky(Sticky e, float pt, Draw d) {
        float t = time(e, pt);
        Vector3f dir = dir(e);
        boolean stuck = e.isStuck();
        int st = e.stuckTicks();
        if (e.kind() == Sticky.HARPOON) {
            if (!stuck) d.trail(FxAtlas.BEAM_THIN, trail(e, pt, 6), 0.07F, 0x9AF0FF, 0x0A4060, 0.7F);
            d.cross(false, FxAtlas.HARPOON, ZERO, dir, 0.95F, 0.3F, 0.12F, 0xFFFFFF, 1.0F);
            Vector3f collar = new Vector3f(dir).mul(-0.125F);
            float blink = 1.0F;
            if (stuck) {
                float rate = 0.5F + st * 0.07F;
                blink = Mth.sin((st + pt) * rate) > 0.0F ? 1.0F : 0.25F;
            }
            d.billboard(true, FxAtlas.GLOW, collar, stuck ? 0.42F : 0.3F, 0.0F, 0x39D6FF, 0.75F * blink);
            d.billboard(true, FxAtlas.CORE, collar, 0.07F, 0.0F, 0xE8FAFF, blink);
            return;
        }
        Vec3 face = e.face();
        boolean armed = stuck && st >= Sticky.MINE_ARM;
        float blink = armed ? (((st / 4) % 2 == 0) ? 1.0F : 0.2F) : (((int) (t / 8.0F)) % 2 == 0 ? 0.8F : 0.3F);
        if (stuck && face != null) {
            Vector3f n = new Vector3f((float) face.x, (float) face.y, (float) face.z);
            Vector3f tangent = Math.abs(n.y) > 0.9F ? new Vector3f(1, 0, 0) : new Vector3f(0, 1, 0);
            d.flat(false, FxAtlas.MINE, new Vector3f(n).mul(0.03F), n, tangent, 0.3F, 0.3F, 0.0F, 0xFFFFFF, 1.0F);
            Vector3f dome = new Vector3f(n).mul(0.08F);
            d.billboard(true, FxAtlas.GLOW, dome, armed ? 0.34F : 0.2F, 0.0F, 0xFF3A2A, 0.8F * blink);
            d.billboard(true, FxAtlas.CORE, dome, 0.06F, 0.0F, 0xFFD0C0, blink);
            if (armed) {
                float r = (float) Sticky.MINE_TRIGGER * 0.45F * (0.9F + 0.1F * Mth.sin(t * 0.4F));
                d.flat(true, FxAtlas.RING, new Vector3f(n).mul(0.05F), n, tangent, r, r, t * 0.03F, 0xFF2A1A, 0.3F);
            }
        } else {
            d.billboard(false, FxAtlas.MINE, ZERO, 0.22F, t * 0.4F, 0xFFFFFF, 1.0F);
            d.billboard(true, FxAtlas.CORE, ZERO, 0.05F, 0.0F, 0xFF5040, blink);
        }
    }

    /** CENIZA (anillo desintegrador verde) y SAETA (flecha de luz). */
    public static void piercer(Piercer e, float pt, Draw d) {
        float t = time(e, pt);
        Vector3f dir = dir(e);
        if (e.kind() == Piercer.CHAKRAM) {
            // estela circular del filo dorado (el chakram lo dibuja PropRenderer)
            Vector3f c = new Vector3f(0, 0.15F, 0);
            Vector3f up = new Vector3f(0, 1, 0);
            Vector3f east = new Vector3f(1, 0, 0);
            d.flat(true, FxAtlas.SAW_BLUR, c, up, east, 0.55F, 0.55F, -t * 0.9F, 0xFFD84A, 0.8F);
            d.flat(true, FxAtlas.RING, c, up, east, 0.6F, 0.6F, 0.0F, 0xFFF0A0, e.returning() ? 0.7F : 0.4F);
            d.billboard(true, FxAtlas.GLOW, c, 0.6F, 0.0F, 0xFFC830, 0.35F);
            return;
        }
        if (e.kind() == Piercer.RING) {
            Vector3f tangent = Math.abs(dir.y) < 0.95F ? new Vector3f(0, 1, 0) : new Vector3f(1, 0, 0);
            float r = (float) Piercer.RING_RADIUS * 1.15F * (0.92F + 0.08F * Mth.sin(t * 1.4F));
            d.flat(true, FxAtlas.RING, ZERO, dir, tangent, r, r, t * 0.15F, 0x3CFF9E, 1.0F);
            d.flat(true, FxAtlas.RING_NOISE, new Vector3f(dir).mul(-0.15F), dir, tangent, r * 0.92F, r * 0.92F, -t * 0.22F, 0xB8FFD8, 0.8F);
            d.flat(true, FxAtlas.ACCRETION, new Vector3f(dir).mul(-0.3F), dir, tangent, r * 1.25F, r * 1.25F, t * 0.1F, 0x20C070, 0.6F);
            d.billboard(true, FxAtlas.GLOW, ZERO, r * 1.3F, 0.0F, 0x20FF80, 0.35F);
            return;
        }
        float p = e.power();
        float w = 0.07F + 0.06F * p;
        d.trail(FxAtlas.BEAM, trail(e, pt, 8), w * 1.6F, 0xFFE9A0, 0x6A4A00, 0.85F);
        d.cross(true, FxAtlas.BOLT, ZERO, dir, 1.6F, 0.25F, w, 0xFFF0C0, 1.0F);
        d.billboard(true, FxAtlas.GLOW, ZERO, 0.3F + 0.2F * p, 0.0F, 0xFFD060, 0.8F);
        d.billboard(true, FxAtlas.STAR4, new Vector3f(dir).mul(0.25F), 0.15F + 0.1F * p, t * 0.3F, 0xFFFFFF, 0.9F);
    }

    /** Zonas: charco de ácido, nube de tormenta, tornado y enjambre de nanobots. */
    public static void field(Field e, float pt, Draw d) {
        float t = time(e, pt);
        float fade = Math.min(1.0F, t / 6.0F) * Mth.clamp((e.end() - t) / 10.0F, 0.0F, 1.0F);
        if (fade <= 0.0F) return;
        Vector3f up = new Vector3f(0, 1, 0);
        Vector3f east = new Vector3f(1, 0, 0);
        switch (e.type()) {
            case Field.ACID_POOL -> {
                float r = e.size();
                d.flat(false, FxAtlas.SMOKE_1, new Vector3f(0, 0.03F, 0), up, east, r * 1.1F, r * 1.1F, 0.4F, 0x2A4A08, 0.8F * fade);
                d.flat(true, FxAtlas.GLOW, new Vector3f(0, 0.05F, 0), up, east, r * 1.35F, r * 1.35F, 0.0F, 0x6CFF10, 0.45F * fade);
                d.flat(true, FxAtlas.RING_NOISE, new Vector3f(0, 0.06F, 0), up, east, r * 1.15F, r * 1.15F, t * 0.02F, 0xB4FF1E, 0.55F * fade);
                d.flat(true, FxAtlas.ACCRETION, new Vector3f(0, 0.055F, 0), up, east, r * 0.9F, r * 0.9F, -t * 0.015F, 0x8CFF20, 0.35F * fade);
            }
            case Field.STORM -> {
                for (int i = 0; i < 10; i++) {
                    float a = i * 2.39996F + t * 0.008F;
                    float rad = i == 0 ? 0.0F : 1.1F + (i % 3) * 1.15F;
                    Vector3f c = new Vector3f(Mth.cos(a) * rad, (i % 2) * 0.4F - 0.2F, Mth.sin(a) * rad);
                    d.billboard(false, FxAtlas.CLOUD[i % 3], c, 2.4F - (i % 3) * 0.35F, i * 1.7F, 0x58627C, 0.92F * fade);
                }
                int flash = ((int) t + e.getId() * 7) % 9;
                if (flash < 2) {
                    float fx = Mth.sin(t * 3.1F + e.getId()) * 2.0F;
                    float fz = Mth.cos(t * 2.3F + e.getId()) * 2.0F;
                    d.billboard(true, FxAtlas.GLOW, new Vector3f(fx, -0.2F, fz), 3.2F, 0.0F, 0xA8C0FF, (flash == 0 ? 0.7F : 0.35F) * fade);
                }
                d.billboard(true, FxAtlas.GLOW, new Vector3f(0, -0.8F, 0), 4.5F, 0.0F, 0x2A3A7A, 0.3F * fade);
            }
            case Field.TORNADO -> {
                for (int i = 0; i < 9; i++) {
                    float r = 0.5F + i * 0.3F;
                    d.flat(true, FxAtlas.VORTEX, new Vector3f(0, i * 0.6F, 0), up, east, r, r, t * (0.55F - i * 0.03F) + i, 0x9FFFF0,
                            (0.55F - i * 0.03F) * fade);
                }
                for (int k = 0; k < 4; k++) {
                    float y = k * 1.3F + 0.65F;
                    d.streak(true, FxAtlas.FUNNEL, new Vector3f(0, y, 0), up, 0.7F, 0.7F, 0.55F + k * 0.45F, 0x7FE8D8, 0.5F * fade);
                }
                d.billboard(true, FxAtlas.GLOW, new Vector3f(0, 0.4F, 0), 1.8F, 0.0F, 0x3FAF9F, 0.3F * fade);
            }
            case Field.FISSURE -> {}
            case Field.TURRET -> turret(e, pt, d);
            default -> {
                for (int i = 0; i < 40; i++) {
                    float a = i * 3.8832F;
                    float sp = 0.22F + (i % 7) * 0.05F;
                    float rr = 0.35F + (i % 5) * 0.25F;
                    Vector3f c = new Vector3f(Mth.cos(a + t * sp) * rr, Mth.sin(t * 0.3F * (1 + i % 3) + i) * 0.5F, Mth.sin(a + t * sp) * rr);
                    d.billboard(true, FxAtlas.EMBER, c, 0.06F, 0.0F, 0xFFC020, 0.9F * fade);
                    if (i % 4 == 0) d.billboard(true, FxAtlas.GLINT, c, 0.1F, t * 0.2F + i, 0xFFF0A0, 0.7F * fade);
                }
                d.billboard(true, FxAtlas.GLOW, ZERO, 1.6F, 0.0F, 0xFF9A00, 0.25F * fade);
            }
        }
    }

    /** Luces de la torreta CENTINELA (el modelo lo dibuja PropRenderer): anillo de energía y el sensor de la cabeza. */
    private static void turret(Field e, float pt, Draw d) {
        float t = time(e, pt);
        float grow = Mth.clamp(t / 8.0F, 0.0F, 1.0F);
        grow = grow * grow * (3.0F - 2.0F * grow);
        float s = Field.TURRET_SCALE * Math.max(0.05F, grow);
        boolean locked = e.target() >= 0;
        int c = locked ? 0xFF4A3A : 0x34F5C5;
        float pulse = 0.8F + 0.2F * Mth.sin(t * (locked ? 1.4F : 0.25F));
        Vector3f up = new Vector3f(0, 1, 0);
        Vector3f east = new Vector3f(1, 0, 0);
        // anillo de energía del trípode (y = 7 px del modelo de la base)
        Vector3f band = new Vector3f(0, 0.4375F * s, 0);
        d.flat(true, FxAtlas.RING, band, up, east, 0.13F * s, 0.13F * s, t * 0.05F, 0x34F5C5, 0.6F * pulse);
        d.billboard(true, FxAtlas.GLOW, band, 0.22F * s, 0.0F, 0x34F5C5, 0.35F * pulse);
        // sensor de la cabeza: delante y algo por encima del eje
        float yaw = (float) Math.toRadians(e.headYaw(pt));
        float pitch = (float) Math.toRadians(e.headPitch(pt));
        Vector3f fwd = new Vector3f(Mth.sin(yaw) * Mth.cos(pitch), Mth.sin(pitch), Mth.cos(yaw) * Mth.cos(pitch));
        Vector3f headUp = new Vector3f(-Mth.sin(pitch) * Mth.sin(yaw), Mth.cos(pitch), -Mth.sin(pitch) * Mth.cos(yaw));
        Vector3f eye = new Vector3f(0, 0.7F * s, 0).add(new Vector3f(fwd).mul(0.18F * s)).add(new Vector3f(headUp).mul(0.0625F * s));
        d.billboard(true, FxAtlas.GLOW, eye, 0.14F * s * pulse, 0.0F, c, 0.9F);
        d.billboard(true, FxAtlas.CORE, eye, 0.04F * s, 0.0F, 0xFFFFFF, 0.9F);
        if (locked) {
            // láser de puntería hacia delante
            d.streak(true, FxAtlas.BEAM_THIN, new Vector3f(eye).add(new Vector3f(fwd).mul(1.2F)), fwd, 1.2F, 1.2F, 0.012F, 0xFF4A3A, 0.5F);
        }
    }
}
