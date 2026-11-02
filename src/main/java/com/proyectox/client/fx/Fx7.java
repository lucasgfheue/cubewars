package com.proyectox.client.fx;

import static com.proyectox.client.fx.FxEffects.cone;
import static com.proyectox.client.fx.FxEffects.embers;
import static com.proyectox.client.fx.FxEffects.flash;
import static com.proyectox.client.fx.FxEffects.level;
import static com.proyectox.client.fx.FxEffects.p;
import static com.proyectox.client.fx.FxEffects.randomDir;
import static com.proyectox.client.fx.FxEffects.rf;
import static com.proyectox.client.fx.FxEffects.ring;
import static com.proyectox.client.fx.FxEffects.rnd;
import static com.proyectox.client.fx.FxEffects.smoke;
import static com.proyectox.client.fx.FxEffects.sparks;

import com.proyectox.client.fx.FxParticle.Mode;
import com.proyectox.entity.SpecBolt;
import com.proyectox.entity.SpecField;
import com.proyectox.fx.Fx;
import com.proyectox.network.FxPayload;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Efectos de la serie v7 (PX-66…PX-101) en el cliente: haces de neón y alienígenas, conos de ondas, tajos, estocadas y
 * pulsos (descargas, ondas de choque, fuego, curación), estelas de sus proyectiles y el ambiente de sus zonas. Los
 * colores salen de {@link Palette}; la familia del arma (NEÓN, CHATARRA, XENO) decide el carácter del efecto.
 */
public final class Fx7 {
    private Fx7() {}

    private static final Vec3 UP = new Vec3(0, 1, 0);

    public static void play(FxPayload f, Vec3 a, Vec3 b) {
        ClientLevel level = level();
        if (level == null) return;
        Entity who = f.entity() >= 0 ? level.getEntity(f.entity()) : null;
        switch (f.kind()) {
            case Fx.BEAM7 -> beam(f.style(), a, b, f.value(), who);
            case Fx.CONE7 -> waves(f.style(), a, b, f.value(), who);
            case Fx.SWING7 -> swing(f.style(), a, b, f.value(), who);
            case Fx.THRUST7 -> thrust(f.style(), a, b, f.value(), who);
            case Fx.PULSE7 -> pulse(f.style(), a, b, f.value(), who);
            default -> {}
        }
    }

    /** Punto de salida: la boca del arma si dispara un jugador (pegada al arma en primera persona). */
    private static Vec3 start(Vec3 from, @Nullable Entity shooter) {
        return shooter instanceof Player ? Muzzle.world(shooter, 1.0F) : from;
    }

    private static boolean local(@Nullable Entity e) {
        return e != null && Muzzle.firstPerson(e);
    }

    // ================================================================ haces

    private static void beam(int style, Vec3 from, Vec3 to, float width, @Nullable Entity shooter) {
        ClientLevel level = level();
        Vec3 start = start(from, shooter);
        Vec3 d = to.subtract(start);
        double len = d.length();
        if (len < 0.2) return;
        Vec3 dir = d.scale(1.0 / len);
        int main = Palette.main(style);
        int hot = Palette.hot(style);
        int spark = Palette.spark(style);
        if (local(shooter)) Muzzle.fired(style, style == Fx.ECLIPSE || style == Fx.FRECUENCIA ? 0.03F : 0.3F);
        Entity anchor = shooter instanceof Player ? shooter : null;
        switch (style) {
            case Fx.DIODO -> {
                new FxBeam(level, start, to, 7).anchor(anchor).sprite(FxAtlas.BEAM).width(0.34F * width, 0.0F).color(hot, main).alpha(1.0F, 0.0F).spawn();
                new FxBeam(level, start, to, 5).anchor(anchor).sprite(FxAtlas.BEAM_THIN).width(0.09F * width, 0.0F).color(0xFFFFFF).alpha(1.0F, 0.0F).spawn();
                new FxBeam(level, start, to, 9).anchor(anchor).sprite(FxAtlas.BEAM_THIN).helix(0.16F * width, 0.6F, 0.5F, rf(0, 6.28F))
                        .width(0.03F, 0.0F).color(spark, main).alpha(0.9F, 0.0F).spawn();
                ring(start.add(dir.scale(0.3)), dir, FxAtlas.RING, main, 0.1F, 0.7F * width, 6, 0.9F);
                flash(to, main, hot, 0.55F * width, 4);
                sparks(to, dir.reverse(), 70, 10, spark, 0.45F, 0.045F, 8, 0.02);
            }
            case Fx.ANTENA -> {
                new FxBeam(level, start, to, 16).sprite(FxAtlas.BEAM_THIN).width(0.04F, 0.12F).color(hot, main).alpha(0.9F, 0.0F).spawn();
                new FxBeam(level, start, to, 4).anchor(anchor).sprite(FxAtlas.BEAM).width(0.12F, 0.0F).color(0xFFFFFF, main).alpha(1.0F, 0.0F).spawn();
                new FxBeam(level, start, to, 14).sprite(FxAtlas.BEAM_THIN).helix(0.1F, 0.5F, 0.3F, rf(0, 6.28F)).width(0.02F, 0.0F).color(spark)
                        .alpha(0.7F, 0.0F).spawn();
                flash(to, main, hot, 0.4F, 4);
                sparks(to, dir.reverse(), 60, 6, spark, 0.35F, 0.035F, 7, 0.02);
            }
            case Fx.FRECUENCIA -> {
                float ph = rf(0, 6.28F);
                new FxBeam(level, start, to, 3).anchor(anchor).sprite(FxAtlas.BEAM_THIN).helix(0.22F, 0.9F, 0.8F, ph).width(0.05F, 0.03F)
                        .color(hot, main).alpha(0.95F, 0.0F).spawn();
                new FxBeam(level, start, to, 3).anchor(anchor).sprite(FxAtlas.BEAM_THIN).helix(0.22F, 0.9F, 0.8F, ph + 3.14F).width(0.05F, 0.03F)
                        .color(hot, main).alpha(0.95F, 0.0F).spawn();
                new FxBeam(level, start, to, 3).anchor(anchor).sprite(FxAtlas.BEAM).width(0.1F, 0.06F).color(main).alpha(0.5F, 0.0F).spawn();
                Vec3 at = start.add(dir.scale(rf(1.0F, (float) Math.max(1.5, len))));
                p(at, FxAtlas.SOUNDWAVE, 6).mode(Mode.FIXED).axis(dir).size(0.2F, 0.6F, 0.5F).color(main).alpha(0.8F, 0.0F).spawn();
                if (rnd().nextFloat() < 0.5F) flash(to, main, hot, 0.25F, 2);
            }
            case Fx.GLIFO -> {
                new FxBeam(level, start, to, 6).anchor(anchor).sprite(FxAtlas.BEAM).width(0.26F, 0.0F).color(hot, main).alpha(1.0F, 0.0F).spawn();
                new FxBeam(level, start, to, 5).anchor(anchor).sprite(FxAtlas.BEAM_THIN).width(0.07F, 0.0F).color(0xFFFFFF).alpha(1.0F, 0.0F).spawn();
                for (int i = 0; i < 3; i++) {
                    Vec3 at = start.add(dir.scale(0.6 + i * 0.9));
                    p(at, FxAtlas.HEX, 7 + i * 2).mode(Mode.FIXED).axis(dir).size(0.18F, 0.42F, 0.5F).color(hot, main).roll(rf(0, 6.28F), 0.15F)
                            .alpha(0.9F, 0.0F).spawn();
                }
                p(to, FxAtlas.STAR6, 5).size(0.5F, 0.2F, 1.0F).color(hot, main).roll(rf(0, 6.28F), 0.0F).alpha(1.0F, 0.0F).spawn();
                sparks(to, dir.reverse(), 70, 8, spark, 0.4F, 0.04F, 8, 0.0);
            }
            case Fx.ECLIPSE -> {
                // corona ardiente por fuera y un núcleo oscuro en el centro: un eclipse que avanza
                new FxBeam(level, start, to, 3).anchor(anchor).sprite(FxAtlas.BEAM).width(0.62F * width, 0.5F * width).color(hot, main).alpha(0.95F, 0.0F)
                        .spawn();
                new FxBeam(level, start, to, 3).anchor(anchor).sprite(FxAtlas.BEAM_NOISE).tile(2.0F, -0.4F).width(0.4F * width, 0.32F * width)
                        .color(main, 0xFF5A10).alpha(0.8F, 0.0F).spawn();
                new FxBeam(level, start, to, 3).anchor(anchor).soft().sprite(FxAtlas.BEAM_THIN).width(0.13F * width, 0.1F * width).color(0x140600)
                        .alpha(0.9F, 0.0F).spawn();
                p(to, FxAtlas.CORONA, 3).size(0.9F, 1.1F, 1.0F).color(main).roll(rf(0, 6.28F), 0.0F).alpha(0.9F, 0.0F).spawn();
                if (rnd().nextFloat() < 0.6F) sparks(to, dir.reverse(), 80, 3, spark, 0.35F, 0.04F, 8, 0.02);
                if (rnd().nextFloat() < 0.3F) embers(to, 2, 0xFF7A20, 0.08F, 14);
            }
            case Fx.OBSERVADOR -> {
                new FxBeam(level, from, to, 6).sprite(FxAtlas.BEAM).width(0.22F, 0.0F).color(hot, main).alpha(1.0F, 0.0F).spawn();
                new FxBeam(level, from, to, 5).sprite(FxAtlas.BEAM_THIN).width(0.06F, 0.0F).color(0xFFFFFF).alpha(1.0F, 0.0F).spawn();
                flash(from, main, hot, 0.35F, 3);
                flash(to, main, hot, 0.45F, 4);
                sparks(to, dir.reverse(), 70, 6, spark, 0.35F, 0.035F, 7, 0.02);
            }
            case Fx.UMBRAL -> {
                long seed = rnd().nextLong();
                new FxBeam(level, start, to, 8).anchor(anchor).sprite(FxAtlas.BEAM_NOISE).jagged(0.12F, 2, seed).width(0.08F, 0.0F).color(hot, main)
                        .alpha(0.9F, 0.0F).spawn();
                new FxBeam(level, start, to, 6).anchor(anchor).sprite(FxAtlas.BEAM_THIN).width(0.03F, 0.0F).color(0xFFFFFF).alpha(0.8F, 0.0F).spawn();
            }
            default -> {
                new FxBeam(level, start, to, 5).anchor(anchor).sprite(FxAtlas.BEAM).width(0.2F * width, 0.0F).color(hot, main).alpha(1.0F, 0.0F).spawn();
                flash(to, main, hot, 0.35F, 3);
            }
        }
    }

    // ================================================================ conos de ondas

    private static void waves(int style, Vec3 from, Vec3 to, float halfAngle, @Nullable Entity shooter) {
        Vec3 start = start(from, shooter);
        Vec3 d = to.subtract(from);
        double range = d.length();
        if (range < 0.2) return;
        Vec3 dir = d.scale(1.0 / range);
        int main = Palette.main(style);
        int hot = Palette.hot(style);
        float spread = (float) Math.tan(Math.toRadians(halfAngle));
        if (local(shooter)) Muzzle.fired(style, style == Fx.BAJO ? 0.8F : 0.4F);
        int life = 10;
        double speed = range / life;
        boolean bass = style == Fx.BAJO;
        // frentes de onda que viajan por el cono y se abren con la distancia
        for (int i = 0; i < (bass ? 5 : 4); i++) {
            int l = life - i;
            float endSize = (float) (spread * range * 1.05);
            p(start.add(dir.scale(0.3 + i * 0.25)), bass ? FxAtlas.SOUNDWAVE : FxAtlas.RING, l).mode(Mode.FIXED).axis(dir).vel(dir.scale(speed))
                    .size(0.25F, endSize, 1.0F).color(i == 0 ? hot : main).alpha(bass ? 0.95F : 0.8F, 0.0F).spawn();
        }
        p(start, FxAtlas.SHOCK, 5).mode(Mode.FIXED).axis(dir).size(0.2F, 1.2F, 0.5F).color(hot).alpha(0.8F, 0.0F).spawn();
        for (int i = 0; i < (bass ? 26 : 18); i++) {
            Vec3 v = cone(dir, halfAngle).scale(speed * rf(0.6F, 1.1F));
            if (bass) {
                p(start, FxAtlas.SPARK, 8 + rnd().nextInt(4)).mode(Mode.VELOCITY).vel(v).drag(0.97).size(0.04F, 0.02F, 1.0F).aspect(3.0F)
                        .stretch(2.0F).color(0xFFFFFF, main).alpha(0.9F, 0.0F).spawn();
            } else {
                p(start, FxAtlas.GLINT, 12 + rnd().nextInt(8)).vel(v.scale(0.8)).drag(0.9).gravity(-0.01).size(0.08F, 0.0F, 1.0F).color(0xFFFFFF, main)
                        .roll(rf(0, 6.28F), 0.1F).alpha(1.0F, 0.0F).spawn();
            }
        }
    }

    // ================================================================ cuerpo a cuerpo

    private static void swing(int style, Vec3 center, Vec3 dir, float radius, @Nullable Entity attacker) {
        Vec3 l = new Vec3(dir.x, 0.0, dir.z);
        l = l.lengthSqr() < 1.0E-6 ? new Vec3(0, 0, 1) : l.normalize();
        float size = radius / 1.05F;
        Vec3 c = center.add(l.scale(0.55 * size));
        float roll = (float) Math.atan2(l.z, -l.x);
        int main = Palette.main(style);
        int hot = Palette.hot(style);
        int spark = Palette.spark(style);
        int fam = Fx.family(style);
        p(c, FxAtlas.CRESCENT, 6).mode(Mode.FIXED).axis(UP).size(size * 0.9F, size, 0.5F).color(hot, main).roll(roll, 0.0F).alpha(1.0F, 0.0F).spawn();
        if (style == Fx.NEON) {
            // filo de neón con su reflejo cian
            p(c.add(0, 0.12, 0), FxAtlas.CRESCENT, 8).mode(Mode.FIXED).axis(UP).size(size * 0.86F, size * 1.04F, 0.5F).color(0x20F0FF, 0x0050A0)
                    .roll(roll, 0.0F).alpha(0.7F, 0.0F).spawn();
        } else {
            p(c.add(0, 0.15, 0), FxAtlas.CRESCENT, 8).mode(Mode.FIXED).axis(UP).size(size * 0.85F, size * 1.05F, 0.5F)
                    .color(main, Palette.mix(main, 0x000000, 0.7F)).roll(roll, 0.0F).alpha(0.6F, 0.0F).spawn();
        }
        for (int i = 0; i < 12; i++) {
            double a = Math.toRadians(-60.0 + 120.0 * i / 11.0);
            Vec3 dd = new Vec3(l.x * Math.cos(a) - l.z * Math.sin(a), 0.0, l.x * Math.sin(a) + l.z * Math.cos(a));
            Vec3 at = center.add(dd.scale(radius * rf(0.8F, 1.0F)));
            Vec3 tangent = new Vec3(-dd.z, 0.0, dd.x);
            if (fam == 1) {
                // chatarra: lascas de óxido y polvo en lugar de chispas de energía
                p(at, FxAtlas.DEBRIS, 14 + rnd().nextInt(8)).soft().vel(tangent.scale(rf(0.1F, 0.25F)).add(0, 0.05, 0)).gravity(0.04).drag(0.95)
                        .size(rf(0.03F, 0.06F)).color(0x7A3A1C).roll(rf(0, 6.28F), rf(-0.3F, 0.3F)).alpha(1.0F, 0.5F).spawn();
            } else {
                p(at, FxAtlas.SPARK, 6 + rnd().nextInt(4)).mode(Mode.VELOCITY).vel(tangent.scale(rf(0.2F, 0.4F))).drag(0.8).size(0.05F, 0.01F, 1.0F)
                        .aspect(3.0F).stretch(1.5F).color(0xFFFFFF, spark).alpha(1.0F, 0.0F).spawn();
            }
        }
        if (fam == 1) smoke(center.add(l.scale(radius * 0.6)), 2, 0.25F, 20, 0x8A7A68, l.scale(0.02), radius * 0.4F);
        if (style == Fx.SIMBIONTE) {
            for (int i = 0; i < 6; i++) {
                p(center.add(randomDir().scale(radius * 0.6)), FxAtlas.GLINT, 14).vel(new Vec3(0, 0.03, 0)).size(0.08F, 0.0F, 1.0F)
                        .color(0xFFFFFF, main).roll(rf(0, 6.28F), 0.1F).alpha(1.0F, 0.0F).spawn();
            }
        }
        if (local(attacker)) Muzzle.swung(style, 9);
    }

    private static void thrust(int style, Vec3 from, Vec3 to, float hitWall, @Nullable Entity attacker) {
        ClientLevel level = level();
        Vec3 start = attacker != null ? Muzzle.world(attacker, 1.0F) : from;
        int main = Palette.main(style);
        int hot = Palette.hot(style);
        new FxBeam(level, start, to, 5).sprite(FxAtlas.BEAM).width(0.26F, 0.0F).taper(0.7F).color(hot, main).alpha(0.9F, 0.0F).spawn();
        new FxBeam(level, start, to, 4).sprite(FxAtlas.BEAM_THIN).width(0.07F, 0.0F).color(0xFFFFFF).alpha(1.0F, 0.0F).spawn();
        Vec3 d = to.subtract(start);
        Vec3 n = d.lengthSqr() < 1.0E-6 ? UP : d.normalize();
        ring(to, n, FxAtlas.RING, main, 0.1F, 0.7F, 6, 0.9F);
        p(to, FxAtlas.STAR4, 4).size(0.3F, 0.12F, 1.0F).color(0xFFFFFF, main).roll(rf(0, 6.28F), 0.0F).alpha(1.0F, 0.0F).spawn();
        // gotas de veneno que caen a lo largo de la estocada
        for (int i = 0; i < 8; i++) {
            Vec3 at = start.add(d.scale(rf(0.3F, 1.0F)));
            p(at, FxAtlas.DROPLET, 14 + rnd().nextInt(8)).vel(randomDir().scale(0.02)).gravity(0.03).size(0.05F, 0.02F, 1.0F).color(main)
                    .alpha(0.95F, 0.3F).spawn();
        }
        if (hitWall > 0.5F) sparks(to, n.reverse(), 70, 6, Palette.spark(style), 0.35F, 0.035F, 7, 0.03);
        if (local(attacker)) Muzzle.swung(style, 8);
    }

    // ================================================================ pulsos

    private static void pulse(int style, Vec3 at, Vec3 normal, float radius, @Nullable Entity who) {
        ClientLevel level = level();
        Vec3 n = normal.lengthSqr() < 1.0E-6 ? UP : normal.normalize();
        int main = Palette.main(style);
        int hot = Palette.hot(style);
        int spark = Palette.spark(style);
        switch (style) {
            case Fx.FUSIBLE -> {
                // pulso electromagnético: anillos eléctricos, rayos hacia fuera y chispas
                p(at, FxAtlas.GLOW, 6).size(radius * 0.8F, radius * 1.4F, 0.5F).color(0xFFE8A0).alpha(0.9F, 0.0F).spawn();
                ring(at, UP, FxAtlas.RING_NOISE, 0x60F0FF, radius * 0.2F, radius * 1.9F, 10, 1.0F);
                ring(at, UP, FxAtlas.SHOCK, hot, radius * 0.2F, radius * 1.5F, 8, 0.8F);
                p(at, FxAtlas.SHOCK, 6).size(radius * 0.3F, radius * 1.3F, 0.5F).color(0xE0FFFF).alpha(0.6F, 0.0F).spawn();
                for (int i = 0; i < 7; i++) {
                    Vec3 end = at.add(randomDir().multiply(1.0, 0.5, 1.0).normalize().scale(radius * rf(0.6F, 1.0F)));
                    new FxBeam(level, at, end, 4).sprite(FxAtlas.BEAM_THIN).jagged(0.25F, 2, rnd().nextLong()).taper(0.3F).width(0.06F, 0.02F)
                            .color(0xFFFFFF, i % 2 == 0 ? main : 0x40E8FF).alpha(1.0F, 0.0F).spawn();
                }
                sparks(at, UP, 180.0, 24, spark, 0.5F, 0.05F, 10, 0.03);
            }
            case Fx.RELE -> {
                flash(at, main, hot, 0.6F, 4);
                for (int i = 0; i < 5; i++) {
                    Vec3 end = at.add(randomDir().scale(rf(0.7F, 1.5F)));
                    new FxBeam(level, at, end, 3).sprite(FxAtlas.BEAM_THIN).jagged(0.18F, 1, rnd().nextLong()).taper(0.3F).width(0.05F, 0.02F)
                            .color(0xFFFFFF, main).alpha(1.0F, 0.0F).spawn();
                }
                ring(at, n, FxAtlas.RING_NOISE, main, 0.2F, 1.4F, 7, 0.9F);
                if (local(who)) Muzzle.swung(style, 9);
            }
            case Fx.PALANCA -> {
                flash(at, main, hot, 0.5F, 3);
                ring(at, UP, FxAtlas.SHOCK, 0xD8C8B0, radius * 0.2F, radius * 1.6F, 9, 0.8F);
                smoke(at, 6, 0.35F, 26, 0x8C7C68, new Vec3(0, 0.02, 0), radius * 0.5F);
                for (int i = 0; i < 12; i++) {
                    p(at, FxAtlas.DEBRIS, 20 + rnd().nextInt(12)).soft().vel(cone(UP, 70).scale(rf(0.2F, 0.45F))).gravity(0.045).drag(0.97)
                            .size(rf(0.04F, 0.09F)).color(i % 3 == 0 ? 0x7A3A1C : 0x4A4440).roll(rf(0, 6.28F), rf(-0.4F, 0.4F)).alpha(1.0F, 0.6F).spawn();
                }
                sparks(at, n, 80, 10, 0xFFB050, 0.45F, 0.04F, 8, 0.05);
                if (local(who)) Muzzle.swung(style, 10);
            }
            case Fx.MOLOTOV -> {
                // el cristal estalla y la gasolina se prende
                p(at, FxAtlas.GLOW, 7).size(radius * 0.8F, radius * 1.3F, 0.5F).color(0xFFC060).alpha(0.9F, 0.0F).spawn();
                for (int i = 0; i < 10; i++) {
                    Vec3 o = at.add(randomDir().multiply(1.0, 0.3, 1.0).scale(radius * 0.5 * rnd().nextFloat()));
                    p(o, FxAtlas.FIRE[0], 14 + rnd().nextInt(10)).frames(FxAtlas.FIRE, false).vel(new Vec3(0, rf(0.03F, 0.08F), 0)).drag(0.9)
                            .size(rf(0.3F, 0.5F), rf(0.6F, 0.9F), 0.5F).color(0xFFF0C0, 0xFF5010).roll(rf(0, 6.28F), rf(-0.05F, 0.05F)).alpha(1.0F, 0.0F)
                            .spawn();
                }
                for (int i = 0; i < 10; i++) {
                    p(at, FxAtlas.SHARD, 16 + rnd().nextInt(10)).vel(cone(UP, 80).scale(rf(0.15F, 0.35F))).gravity(0.05).drag(0.96).size(rf(0.03F, 0.06F))
                            .color(0x9AD890).roll(rf(0, 6.28F), rf(-0.5F, 0.5F)).alpha(0.9F, 0.4F).spawn();
                }
                smoke(at.add(0, 0.5, 0), 5, 0.4F, 40, 0x3A3632, new Vec3(0, 0.04, 0), radius * 0.4F);
            }
            case Fx.ABISMO -> {
                p(at, FxAtlas.VORTEX, 14).mode(Mode.FIXED).axis(UP).size(radius * 1.2F, radius * 0.2F, 0.5F).color(main).roll(0.0F, 0.25F)
                        .alpha(0.9F, 0.0F).spawn();
                p(at, FxAtlas.ACCRETION, 12).size(1.6F, 0.4F, 0.6F).color(hot).roll(0.0F, 0.2F).alpha(1.0F, 0.0F).spawn();
                for (int i = 0; i < 20; i++) {
                    Vec3 o = randomDir().scale(radius * rf(0.6F, 1.0F));
                    p(at.add(o), FxAtlas.GLINT, 12).vel(o.scale(-1.0 / 12.0)).size(0.08F, 0.02F, 1.0F).color(0xFFFFFF, main).alpha(0.4F, 1.0F).spawn();
                }
            }
            case Fx.NEBULA -> {
                for (int i = 0; i < 8; i++) {
                    Vec3 o = at.add(randomDir().scale(radius * 0.4 * rnd().nextFloat())).add(0, 0.5, 0);
                    p(o, FxAtlas.CLOUD[rnd().nextInt(FxAtlas.CLOUD.length)], 30 + rnd().nextInt(15)).vel(randomDir().scale(0.04)).drag(0.92)
                            .size(radius * 0.3F, radius * 0.6F, 0.5F).color(hot, main).roll(rf(0, 6.28F), rf(-0.03F, 0.03F)).alpha(0.7F, 0.0F).spawn();
                }
                flash(at.add(0, 0.5, 0), main, hot, radius * 0.5F, 5);
                ring(at.add(0, 0.1, 0), UP, FxAtlas.RING, main, radius * 0.2F, radius * 1.3F, 10, 0.8F);
            }
            case Fx.SIMBIONTE -> {
                // la espada bebe de la herida: chispas verdes que vuelan hacia el que la empuña
                Vec3 to = who != null ? who.getBoundingBox().getCenter() : at.add(0, 1.5, 0);
                for (int i = 0; i < 6; i++) {
                    Vec3 o = at.add(randomDir().scale(0.3));
                    Vec3 v = to.subtract(o).scale(1.0 / 12.0);
                    p(o, FxAtlas.GLINT, 12).vel(v).size(0.1F, 0.04F, 1.0F).color(0xFFFFFF, main).roll(rf(0, 6.28F), 0.2F).alpha(1.0F, 0.2F).spawn();
                }
            }
            case Fx.UMBRAL -> {
                p(at, FxAtlas.STAR6, 6).size(0.9F, 0.3F, 1.0F).color(hot, main).roll(rf(0, 6.28F), 0.0F).alpha(1.0F, 0.0F).spawn();
                ring(at, n, FxAtlas.RING_NOISE, main, 0.3F, radius * 1.6F, 10, 0.9F);
                p(at, FxAtlas.GLOW, 8).size(radius * 0.6F, radius, 0.5F).color(main).alpha(0.8F, 0.0F).spawn();
            }
            case Fx.OBSERVADOR -> {
                flash(at, main, hot, 0.6F * radius, 5);
                ring(at, UP, FxAtlas.RING, main, 0.2F, 1.2F * radius, 8, 0.9F);
                for (int i = 0; i < 10; i++) {
                    p(at, FxAtlas.GLINT, 14).vel(randomDir().scale(0.06)).drag(0.9).size(0.08F, 0.0F, 1.0F).color(0xFFFFFF, main).alpha(1.0F, 0.0F).spawn();
                }
            }
            default -> {
                flash(at, main, hot, 0.5F * Math.max(1.0F, radius * 0.5F), 4);
                ring(at, n, FxAtlas.RING, main, 0.2F, radius * 1.2F, 8, 0.8F);
            }
        }
    }

    // ================================================================ estelas de los proyectiles

    public static void boltTick(SpecBolt e) {
        if (level() == null) return;
        RandomSource r = rnd();
        Vec3 pos = e.position();
        Vec3 back = e.heading().reverse();
        int style = e.style();
        int main = Palette.main(style);
        switch (style) {
            case Fx.PIXEL -> {
                if (r.nextFloat() < 0.6F) {
                    p(pos.add(back.scale(0.3)), FxAtlas.CORE, 8 + r.nextInt(4)).vel(randomDir().scale(0.01)).size(0.05F, 0.0F, 1.0F).color(0xFFFFFF, main)
                            .alpha(0.9F, 0.0F).spawn();
                }
            }
            case Fx.VOLTIO -> {
                if (r.nextFloat() < 0.5F) {
                    p(pos, FxAtlas.SPARK, 4).mode(Mode.VELOCITY).vel(randomDir().scale(0.12)).size(0.03F, 0.01F, 1.0F).aspect(3.0F).color(0xFFFFFF, main)
                            .alpha(1.0F, 0.0F).spawn();
                }
            }
            case Fx.FUSIBLE -> {
                if (e.tickCount % 3 == 0) {
                    p(pos, FxAtlas.SPARK, 5).mode(Mode.VELOCITY).vel(randomDir().scale(0.08)).size(0.025F, 0.01F, 1.0F).aspect(3.0F).color(0xFFFFFF, main)
                            .alpha(1.0F, 0.0F).spawn();
                }
            }
            case Fx.VINILO -> {
                if (r.nextFloat() < 0.4F) {
                    p(pos, FxAtlas.GLINT, 8).size(0.08F, 0.0F, 1.0F).color(0xFFFFFF, r.nextBoolean() ? main : 0xFFE070).roll(r.nextFloat() * 6.28F, 0.0F)
                            .alpha(1.0F, 0.0F).spawn();
                }
            }
            case Fx.NEXO -> p(pos.add(back.scale(0.2)), FxAtlas.EMBER, 6 + r.nextInt(4)).vel(back.scale(0.04)).size(0.05F, 0.0F, 1.0F)
                    .color(0xFFFFFF, main).alpha(0.9F, 0.0F).spawn();
            case Fx.SERRUCHO -> {
                if (r.nextFloat() < 0.35F) {
                    p(pos, FxAtlas.SPARK, 6).mode(Mode.VELOCITY).vel(randomDir().scale(0.2)).gravity(0.03).size(0.03F, 0.01F, 1.0F).aspect(3.0F)
                            .color(0xFFFFFF, 0xFFA040).alpha(1.0F, 0.0F).spawn();
                }
            }
            case Fx.MOLOTOV -> {
                p(pos.add(back.scale(0.15)), FxAtlas.FLAME[0], 8 + r.nextInt(5)).frames(FxAtlas.FLAME, false).vel(new Vec3(0, 0.03, 0))
                        .size(0.12F, 0.22F, 0.6F).color(0xFFE0A0, 0xFF5010).roll(r.nextFloat() * 6.28F, 0.0F).alpha(1.0F, 0.0F).spawn();
                if (r.nextFloat() < 0.4F) smoke(pos.add(back.scale(0.3)), 1, 0.12F, 22, 0x3A3632, new Vec3(0, 0.03, 0), 0.05F);
            }
            case Fx.BIDON -> {
                if (r.nextFloat() < 0.7F) smoke(pos.add(back.scale(0.3)), 1, 0.16F, 26, 0x5A5450, new Vec3(0, 0.015, 0), 0.06F);
            }
            case Fx.CACHARRO -> {
                if (r.nextFloat() < 0.3F) smoke(pos, 1, 0.1F, 16, 0x7A706A, Vec3.ZERO, 0.05F);
            }
            case Fx.ESPORA -> {
                if (r.nextFloat() < 0.7F) {
                    p(pos.add(randomDir().scale(0.1)), FxAtlas.BUBBLE, 12 + r.nextInt(8)).vel(randomDir().scale(0.01)).gravity(-0.002)
                            .size(0.03F, 0.06F, 1.0F).color(main).alpha(0.8F, 0.0F).spawn();
                }
            }
            case Fx.ABISMO -> {
                for (int i = 0; i < 2; i++) {
                    Vec3 o = randomDir().scale(0.7);
                    p(pos.add(o), FxAtlas.GLINT, 8).vel(o.scale(-1.0 / 8.0).add(e.getDeltaMovement())).size(0.06F, 0.02F, 1.0F).color(0xFFFFFF, main)
                            .alpha(0.3F, 1.0F).spawn();
                }
            }
            case Fx.LARVA -> {
                if (r.nextFloat() < 0.3F) {
                    p(pos, FxAtlas.DROPLET, 14).vel(randomDir().scale(0.01)).gravity(0.02).size(0.04F, 0.02F, 1.0F).color(main).alpha(0.9F, 0.2F).spawn();
                }
            }
            case Fx.NEBULA -> {
                if (r.nextFloat() < 0.6F) {
                    p(pos.add(randomDir().scale(0.4)), FxAtlas.CLOUD[r.nextInt(FxAtlas.CLOUD.length)], 18 + r.nextInt(10)).vel(randomDir().scale(0.01))
                            .size(0.3F, 0.6F, 0.5F).color(Palette.hot(style), main).roll(r.nextFloat() * 6.28F, 0.02F).alpha(0.5F, 0.0F).spawn();
                }
            }
            case Fx.CENIT -> {
                if (r.nextFloat() < 0.6F) {
                    p(pos.add(back.scale(0.3)), FxAtlas.GLINT, 8).size(0.09F, 0.0F, 1.0F).color(0xFFFFFF, main).roll(r.nextFloat() * 6.28F, 0.0F)
                            .alpha(1.0F, 0.0F).spawn();
                }
            }
            default -> {}
        }
    }

    // ================================================================ ambiente de las zonas

    public static void fieldTick(SpecField f) {
        if (level() == null) return;
        RandomSource r = rnd();
        Vec3 pos = f.position();
        int style = f.style();
        int main = Palette.main(style);
        float rad = f.radius();
        float fade = Mth.clamp(f.remaining() / 15.0F, 0.0F, 1.0F);
        switch (f.type()) {
            case SpecField.FIRE -> {
                for (int i = 0; i < 3; i++) {
                    if (r.nextFloat() > fade) continue;
                    double a = r.nextDouble() * Math.PI * 2.0;
                    double d = Math.sqrt(r.nextDouble()) * rad;
                    Vec3 at = pos.add(Math.cos(a) * d, 0.05, Math.sin(a) * d);
                    p(at, FxAtlas.FIRE[0], 14 + r.nextInt(10)).frames(FxAtlas.FIRE, false).vel(new Vec3(0, rf(0.02F, 0.05F), 0)).drag(0.95)
                            .size(rf(0.18F, 0.3F), rf(0.35F, 0.55F), 0.5F).color(0xFFE8B0, 0xFF4A10).roll(rf(0, 6.28F), rf(-0.04F, 0.04F)).alpha(1.0F, 0.0F)
                            .spawn();
                }
                if (r.nextFloat() < 0.25F * fade) smoke(pos.add((r.nextFloat() - 0.5) * rad, 0.6, (r.nextFloat() - 0.5) * rad), 1, 0.35F, 40, 0x2E2A26,
                        new Vec3(0, 0.04, 0), 0.2F);
                if (r.nextFloat() < 0.3F * fade) embers(pos.add(0, 0.3, 0), 1, 0xFF6020, 0.05F, 24);
            }
            case SpecField.WELL, SpecField.RIFT -> {
                boolean rift = f.type() == SpecField.RIFT;
                double pull = rift ? SpecField.RIFT_PULL : rad;
                for (int i = 0; i < (rift ? 3 : 2); i++) {
                    Vec3 o = randomDir().scale(pull * rf(0.5F, 1.0F));
                    Vec3 tangent = o.cross(UP);
                    Vec3 v = o.scale(-1.0 / 14.0).add(tangent.lengthSqr() < 1.0E-6 ? Vec3.ZERO : tangent.normalize().scale(0.05));
                    p(pos.add(o), rift ? FxAtlas.SPARK : FxAtlas.GLINT, 14).mode(rift ? Mode.VELOCITY : Mode.BILLBOARD).vel(v).size(0.07F, 0.02F, 1.0F)
                            .aspect(rift ? 3.0F : 1.0F).color(0xFFFFFF, main).alpha(0.2F, 1.0F).spawn();
                }
            }
            case SpecField.CLOUD -> {
                if (r.nextFloat() < 0.6F * fade) {
                    double a = r.nextDouble() * Math.PI * 2.0;
                    double d = Math.sqrt(r.nextDouble()) * rad;
                    Vec3 at = pos.add(Math.cos(a) * d, 0.3 + r.nextDouble() * 1.4, Math.sin(a) * d);
                    p(at, FxAtlas.CLOUD[r.nextInt(FxAtlas.CLOUD.length)], 30 + r.nextInt(20)).vel(randomDir().scale(0.008)).size(0.5F, 1.0F, 0.5F)
                            .color(Palette.hot(style), main).roll(rf(0, 6.28F), rf(-0.02F, 0.02F)).alpha(0.35F, 0.0F).fadeIn(0.3F).spawn();
                }
                if (r.nextFloat() < 0.3F) {
                    p(pos.add(randomDir().scale(rad * 0.8)).add(0, 0.8, 0), FxAtlas.GLINT, 12).size(0.07F, 0.0F, 1.0F).color(0xFFFFFF, main)
                            .alpha(1.0F, 0.0F).spawn();
                }
            }
            default -> {
                if (r.nextFloat() < 0.3F) {
                    p(pos.add(randomDir().scale(0.5)), FxAtlas.EMBER, 12).vel(new Vec3(0, -0.02, 0)).size(0.04F, 0.0F, 1.0F).color(0xFFFFFF, main)
                            .alpha(0.8F, 0.0F).spawn();
                }
            }
        }
    }
}
