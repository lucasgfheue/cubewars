package com.proyectox.client.fx;

import com.proyectox.client.Reticle;
import com.proyectox.client.fx.FxParticle.Mode;
import com.proyectox.entity.Drone;
import com.proyectox.entity.EnergyBolt;
import com.proyectox.entity.Field;
import com.proyectox.entity.Grenade;
import com.proyectox.entity.MicroRocket;
import com.proyectox.entity.Orb;
import com.proyectox.entity.Piercer;
import com.proyectox.entity.PXProjectile;
import com.proyectox.entity.PlasmaBolt;
import com.proyectox.entity.SpecBolt;
import com.proyectox.entity.SawDisc;
import com.proyectox.entity.Singularity;
import com.proyectox.entity.SlashWave;
import com.proyectox.entity.Sticky;
import com.proyectox.fx.Fx;
import com.proyectox.item.GunItem;
import com.proyectox.network.FxPayload;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

/**
 * Composición de los efectos visuales en el cliente: cada aviso del servidor ({@link FxPayload})
 * se convierte en un conjunto de partículas HD, haces y anillos con el estilo de su arma.
 */
public final class FxEffects {
    private FxEffects() {}

    private static final Vec3 UP = new Vec3(0, 1, 0);

    @Nullable
    static ClientLevel level() {
        return Minecraft.getInstance().level;
    }

    static RandomSource rnd() {
        ClientLevel l = level();
        return l != null ? l.random : RandomSource.create();
    }

    /** Partícula nueva (sin añadir todavía). */
    static FxParticle p(Vec3 pos, FxAtlas.Sprite s, int life) {
        return new FxParticle(level(), pos, s, life);
    }

    static float rf(float a, float b) {
        return a + rnd().nextFloat() * (b - a);
    }

    static Vec3 randomDir() {
        RandomSource r = rnd();
        double z = r.nextDouble() * 2.0 - 1.0;
        double t = r.nextDouble() * Math.PI * 2.0;
        double s = Math.sqrt(1.0 - z * z);
        return new Vec3(s * Math.cos(t), z, s * Math.sin(t));
    }

    /** Dirección dentro de un cono de {@code deg} grados alrededor de {@code dir}. */
    static Vec3 cone(Vec3 dir, double deg) {
        Vec3 n = dir.lengthSqr() < 1.0E-8 ? UP : dir.normalize();
        Vec3 a = Math.abs(n.y) < 0.99 ? UP : new Vec3(1, 0, 0);
        Vec3 u = n.cross(a).normalize();
        Vec3 v = n.cross(u).normalize();
        RandomSource r = rnd();
        double ang = r.nextDouble() * Math.PI * 2.0;
        double mag = Math.tan(Math.toRadians(deg)) * Math.sqrt(r.nextDouble());
        return n.add(u.scale(Math.cos(ang) * mag)).add(v.scale(Math.sin(ang) * mag)).normalize();
    }

    // ================================================================ entrada desde la red

    public static void play(FxPayload f) {
        ClientLevel level = level();
        if (level == null) return;
        Vec3 a = f.from();
        Vec3 b = f.to();
        switch (f.kind()) {
            case Fx.MUZZLE -> muzzle(f.style(), a, b, level.getEntity(f.entity()));
            case Fx.BEAM -> rail(a, b, f.value(), level.getEntity(f.entity()));
            case Fx.IMPACT -> impact(f.style(), a, b, f.value());
            case Fx.EXPLOSION -> explosion(f.style(), a, f.value());
            case Fx.ARC -> arc(f.style(), a, b, f.value(), f.entity() >= 0 ? level.getEntity(f.entity()) : null);
            case Fx.TRACER -> tracer(f.style(), a, b, level.getEntity(f.entity()));
            case Fx.FLAME -> flame(a, b, f.value(), level.getEntity(f.entity()));
            case Fx.LASER -> laser(a, b, f.value(), level.getEntity(f.entity()));
            case Fx.ROPE -> rope(a, b, f.value(), level.getEntity(f.entity()), f.other() >= 0 ? level.getEntity(f.other()) : null);
            case Fx.DRILL -> drill(a, b, (int) f.value());
            case Fx.LOCK -> lock(level.getEntity(f.entity()), (int) f.value());
            case Fx.MELEE -> melee(f.style(), a, b, f.value());
            case Fx.SHIELD -> shield(a, b);
            case Fx.HITMARK -> Reticle.hit(f.style() == 1);
            case Fx.DRONE -> drone(a, b);
            case Fx.VORTEX -> vortexOpen(a, f.value());
            case Fx.PRISM -> prism(a, b, f.value(), f.entity() >= 0 ? level.getEntity(f.entity()) : null);
            case Fx.LIGHTNING -> lightning(a, b, f.value(), f.entity() >= 0 ? level.getEntity(f.entity()) : null);
            case Fx.SWEEP -> sweep(a, b, f.value(), level.getEntity(f.entity()));
            case Fx.THRUST -> thrust(a, b, f.value(), level.getEntity(f.entity()));
            case Fx.DISINTEGRATE -> disintegrate(a, f.value(), level.getEntity(f.entity()));
            case Fx.STORM -> stormForm(a, f.value());
            case Fx.BULLET -> bullet(f.style(), a, b, f.value(), f.entity() >= 0 ? level.getEntity(f.entity()) : null);
            case Fx.ERUPT -> erupt(a, f.value());
            case Fx.MARK -> mark(a, f.value(), f.entity() >= 0 ? level.getEntity(f.entity()) : null);
            case Fx.SPIN -> spin(f.style(), a, f.value(), f.entity() >= 0 ? level.getEntity(f.entity()) : null);
            case Fx.PUNCH -> punch(a, b, f.value(), f.entity() >= 0 ? level.getEntity(f.entity()) : null);
            case Fx.WATER -> water(a, b, f.value(), f.entity() >= 0 ? level.getEntity(f.entity()) : null);
            case Fx.DEPLOY -> deploy(a, f.value());
            case Fx.QUAKE -> quake(a, f.value());
            case Fx.TREMOR -> tremor(a, f.value());
            case Fx.BEAM7, Fx.CONE7, Fx.SWING7, Fx.THRUST7, Fx.PULSE7 -> Fx7.play(f, a, b);
            default -> {}
        }
    }

    // ================================================================ piezas básicas

    /** Destello: halo de color y núcleo blanco. */
    static void flash(Vec3 pos, int color, int hot, float size, int life) {
        p(pos, FxAtlas.GLOW, life + 1).size(size, size * 1.3F, 1.0F).color(color).alpha(0.9F, 0.0F).spawn();
        p(pos, FxAtlas.CORE, life).size(size * 0.4F, size * 0.2F, 1.0F).color(hot).alpha(1.0F, 0.0F).spawn();
    }

    static void sparks(Vec3 pos, Vec3 dir, double coneDeg, int n, int color, float speed, float size, int life, double gravity) {
        for (int i = 0; i < n; i++) {
            Vec3 d = coneDeg >= 180.0 ? randomDir() : cone(dir, coneDeg);
            float s = speed * rf(0.4F, 1.0F);
            p(pos, FxAtlas.SPARK, (int) (life * rf(0.6F, 1.2F))).mode(Mode.VELOCITY).vel(d.scale(s)).drag(0.86).gravity(gravity)
                    .size(size, size * 0.4F, 1.0F).aspect(3.0F).stretch(1.6F).color(0xFFFFFF, color).alpha(1.0F, 0.0F).spawn();
        }
    }

    static void smoke(Vec3 pos, int n, float size, int life, int shade, Vec3 drift, float spread) {
        for (int i = 0; i < n; i++) {
            Vec3 at = pos.add(randomDir().scale(spread * rnd().nextFloat()));
            Vec3 v = drift.add(randomDir().scale(0.02));
            p(at, FxAtlas.SMOKE[rnd().nextInt(FxAtlas.SMOKE.length)], (int) (life * rf(0.7F, 1.3F))).soft().vel(v).drag(0.92)
                    .size(size * rf(0.6F, 1.0F), size * rf(1.8F, 2.6F), 0.6F).color(shade, Palette.mix(shade, 0x101010, 0.3F))
                    .alpha(rf(0.35F, 0.6F), 0.0F).fadeIn(0.1F).roll(rf(0.0F, 6.28F), rf(-0.03F, 0.03F)).spawn();
        }
    }

    static void ring(Vec3 pos, Vec3 normal, FxAtlas.Sprite sprite, int color, float from, float to, int life, float alpha) {
        p(pos, sprite, life).mode(Mode.FIXED).axis(normal).size(from, to, 0.5F).color(color).alpha(alpha, 0.0F).roll(rf(0.0F, 6.28F), 0.0F).spawn();
    }

    static void embers(Vec3 pos, int n, int color, float speed, int life) {
        for (int i = 0; i < n; i++) {
            p(pos, FxAtlas.EMBER, (int) (life * rf(0.5F, 1.4F))).vel(randomDir().scale(speed * rf(0.3F, 1.0F)).add(0, 0.03, 0)).drag(0.9).gravity(-0.002)
                    .size(rf(0.03F, 0.07F), 0.0F, 1.0F).color(0xFFFFFF, color).alpha(1.0F, 0.2F).spawn();
        }
    }

    // ================================================================ fogonazos

    private static void muzzle(int style, Vec3 pos, Vec3 dir, @Nullable Entity shooter) {
        Vec3 d = dir.lengthSqr() < 1.0E-6 ? UP : dir.normalize();
        int main = Palette.main(style);
        int hot = Palette.hot(style);
        boolean local = shooter != null && Muzzle.firstPerson(shooter);
        boolean inHand = false;
        if (local && shooter instanceof Player player && player.getMainHandItem().getItem() instanceof GunItem gun) {
            // en primera persona el fogonazo grande se dibuja pegado al arma (GunHands)
            Muzzle.fired(style, gun.kick());
            inHand = gun.aims() && com.proyectox.client.GunGeometry.of(gun.key()) != null;
            pos = Muzzle.world(shooter, 1.0F);
        }
        // armas de pólvora: fogonazo de fuego y humo (ver GunFx)
        GunFx.Profile real = GunFx.of(style);
        if (real != null) {
            realMuzzle(real, pos, d, shooter, inHand);
            return;
        }
        float big = switch (style) {
            case Fx.REMACHE, Fx.ECO, Fx.MORTERO, Fx.SOLAR, Fx.TRABUCO, Fx.LINCE, Fx.RUGIDO -> 1.4F;
            case Fx.AGUJA, Fx.TRITURADORA, Fx.VORTICE, Fx.COLMENA, Fx.CENTELLA, Fx.ARPON, Fx.CENIZA, Fx.YUNQUE, Fx.COYOTE, Fx.PULSAR -> 1.15F;
            case Fx.ROTOR, Fx.COLIBRI, Fx.HALCON, Fx.CAUSTICA, Fx.SAETA, Fx.GRANIZO, Fx.RETORNO -> 0.7F;
            case Fx.METEORO, Fx.FALLA -> 0.5F;
            default -> 0.9F;
        };
        if (!inHand) {
            // estrella frontal + lengua lateral + halo
            p(pos, style % 2 == 0 ? FxAtlas.STAR4 : FxAtlas.STAR6, 2).size(0.32F * big, 0.42F * big, 1.0F).color(hot, main)
                    .roll(rf(0.0F, 6.28F), 0.0F).alpha(1.0F, 0.0F).spawn();
            p(pos, FxAtlas.FLASH_SIDE, 2).mode(Mode.AXIS).axis(d).size(0.16F * big).aspect(2.2F).color(hot, main).alpha(1.0F, 0.0F).spawn();
            p(pos, FxAtlas.GLOW, 3).size(0.6F * big, 0.8F * big, 1.0F).color(main).alpha(0.55F, 0.0F).spawn();
        }
        switch (style) {
            case Fx.AGUJA -> {
                ring(pos.add(d.scale(0.2)), d, FxAtlas.RING, main, 0.08F, 0.7F, 7, 0.9F);
                ring(pos.add(d.scale(0.5)), d, FxAtlas.RING_NOISE, hot, 0.05F, 0.45F, 5, 0.7F);
                p(pos, FxAtlas.LENS, 3).size(0.12F, 0.05F, 1.0F).aspect(9.0F).color(main).alpha(0.8F, 0.0F).spawn();
                sparks(pos, d, 35, 6, Palette.spark(style), 0.35F, 0.05F, 6, 0.0);
            }
            case Fx.REMACHE -> {
                sparks(pos, d, 28, 14, 0xFFB050, 0.7F, 0.06F, 7, 0.02);
                smoke(pos.add(d.scale(0.4)), 4, 0.18F, 26, 0x9A9692, d.scale(0.05).add(0, 0.01, 0), 0.2F);
                ring(pos.add(d.scale(0.3)), d, FxAtlas.SHOCK, 0xFFD8A0, 0.1F, 0.9F, 5, 0.5F);
            }
            case Fx.COLMENA -> {
                // escape trasero del lanzacohetes
                smoke(pos.subtract(d.scale(0.9)), 5, 0.25F, 30, 0xB0ACA8, d.scale(-0.08).add(0, 0.015, 0), 0.3F);
                p(pos.subtract(d.scale(0.6)), FxAtlas.FLASH_SIDE, 3).mode(Mode.AXIS).axis(d.reverse()).size(0.22F).aspect(2.4F)
                        .color(0xFFE0A0, 0xFF5A20).alpha(0.9F, 0.0F).spawn();
                sparks(pos, d, 20, 5, 0xFFB060, 0.4F, 0.05F, 6, 0.01);
            }
            case Fx.TRITURADORA -> {
                ring(pos.add(d.scale(0.2)), d, FxAtlas.SHOCK, 0xE8F0A0, 0.1F, 0.8F, 6, 0.6F);
                smoke(pos.add(d.scale(0.3)), 4, 0.2F, 28, 0xA8A89A, d.scale(0.04).add(0, 0.01, 0), 0.2F);
            }
            case Fx.VORTICE -> {
                ring(pos.add(d.scale(0.3)), d, FxAtlas.ACCRETION, main, 0.9F, 0.15F, 8, 0.9F);
                ring(pos.add(d.scale(0.3)), d, FxAtlas.RING, hot, 0.1F, 1.0F, 6, 0.7F);
            }
            case Fx.ECO -> {
                for (int i = 0; i < 3; i++) {
                    ring(pos.add(d.scale(0.4 + i * 0.5)), d, FxAtlas.SOUNDWAVE, main, 0.2F + i * 0.1F, 1.3F + i * 0.4F, 8 + i * 2, 0.8F - i * 0.2F);
                }
            }
            case Fx.ESCARCHA -> {
                p(pos, FxAtlas.SNOW, 8).vel(d.scale(0.12).add(randomDir().scale(0.03))).size(0.08F, 0.02F, 1.0F).color(0xFFFFFF, main)
                        .roll(rf(0, 6.28F), rf(-0.3F, 0.3F)).alpha(1.0F, 0.0F).spawn();
                smoke(pos.add(d.scale(0.2)), 1, 0.12F, 14, 0xD8F4FF, d.scale(0.05), 0.05F);
            }
            case Fx.COLIBRI -> p(pos, FxAtlas.GLINT, 3).size(0.18F, 0.05F, 1.0F).color(main).roll(rf(0, 6.28F), 0.0F).alpha(1.0F, 0.0F).spawn();
            case Fx.SIERRA -> {
                ring(pos.add(d.scale(0.25)), d, FxAtlas.SAW_BLUR, main, 0.15F, 0.55F, 5, 0.8F);
                sparks(pos, d, 40, 6, Palette.spark(style), 0.4F, 0.05F, 6, 0.02);
            }
            case Fx.BASTION -> ring(pos.add(d.scale(0.2)), d, FxAtlas.HEX, main, 0.12F, 0.4F, 4, 0.7F);
            case Fx.CIZALLA -> p(pos.add(d.scale(0.5)), FxAtlas.CRESCENT, 5).mode(Mode.FIXED).axis(d).size(0.6F, 1.0F, 0.5F).color(hot, main)
                    .alpha(1.0F, 0.0F).spawn();
            case Fx.AVISPON_HOT -> embers(pos, 2, 0xFF7A20, 0.08F, 10);
            case Fx.ROTOR -> {
                if (rnd().nextFloat() < 0.4F) smoke(pos.add(d.scale(0.2)), 1, 0.1F, 16, 0xA0A0A0, d.scale(0.04), 0.05F);
            }
            case Fx.CENTELLA -> {
                ring(pos.add(d.scale(0.25)), d, FxAtlas.RING, main, 0.08F, 0.75F, 6, 0.9F);
                sparks(pos, d, 30, 8, 0xFFD070, 0.5F, 0.05F, 7, 0.02);
                smoke(pos.add(d.scale(0.4)), 2, 0.16F, 22, 0xB0A890, d.scale(0.04).add(0, 0.01, 0), 0.15F);
                p(pos, FxAtlas.LENS, 3).size(0.16F, 0.06F, 1.0F).aspect(8.0F).color(main).alpha(0.8F, 0.0F).spawn();
            }
            case Fx.ARPON -> {
                ring(pos.add(d.scale(0.3)), d, FxAtlas.RING_NOISE, main, 0.1F, 0.7F, 7, 0.8F);
                smoke(pos.add(d.scale(0.2)), 3, 0.15F, 20, 0xDDEEF4, d.scale(0.05).add(0, 0.01, 0), 0.12F);
            }
            case Fx.MORTERO -> {
                ring(pos.add(d.scale(0.3)), d, FxAtlas.SHOCK, 0xFFD8A0, 0.15F, 1.2F, 7, 0.7F);
                smoke(pos.add(d.scale(0.5)), 7, 0.3F, 40, 0x9A948C, d.scale(0.08).add(0, 0.02, 0), 0.35F);
                sparks(pos, d, 25, 10, 0xFFB060, 0.6F, 0.06F, 8, 0.03);
            }
            case Fx.PRISMA -> {
                p(pos, FxAtlas.LENS, 4).size(0.25F, 0.1F, 1.0F).aspect(9.0F).color(0xFFFFFF).alpha(0.9F, 0.0F).spawn();
                for (int i = 0; i < 5; i++) {
                    p(pos.add(randomDir().scale(0.15)), FxAtlas.GLINT, 6 + i).size(0.1F, 0.0F, 1.0F).color(Palette.RAINBOW[i])
                            .roll(rf(0, 6.28F), 0.2F).alpha(1.0F, 0.0F).spawn();
                }
            }
            case Fx.ENJAMBRE -> {
                sparks(pos, d, 35, 8, 0xFFC840, 0.35F, 0.04F, 8, 0.0);
                smoke(pos.add(d.scale(0.3)), 2, 0.12F, 18, 0xC8B080, d.scale(0.04), 0.1F);
            }
            case Fx.CAUSTICA -> {
                for (int i = 0; i < 3; i++) {
                    p(pos, FxAtlas.EMBER, 8 + rnd().nextInt(5)).vel(cone(d, 25).scale(rf(0.1F, 0.25F))).gravity(0.02).size(0.04F, 0.02F, 1.0F)
                            .color(0xE0FF90, main).alpha(1.0F, 0.0F).spawn();
                }
                if (rnd().nextFloat() < 0.4F) smoke(pos.add(d.scale(0.2)), 1, 0.1F, 16, 0xB8E890, d.scale(0.03).add(0, 0.01, 0), 0.05F);
            }
            case Fx.SOLAR -> {
                p(pos, FxAtlas.CORONA, 6).size(0.5F, 1.1F, 0.6F).color(0xFFF0B0, main).roll(rf(0, 6.28F), 0.05F).alpha(1.0F, 0.0F).spawn();
                ring(pos.add(d.scale(0.3)), d, FxAtlas.SHOCK, 0xFFE8A0, 0.2F, 1.4F, 8, 0.7F);
                p(pos, FxAtlas.LENS, 5).size(0.35F, 0.12F, 1.0F).aspect(9.0F).color(0xFFD060).alpha(0.9F, 0.0F).spawn();
                embers(pos, 6, 0xFFB020, 0.15F, 14);
            }
            case Fx.TEMPESTAD -> {
                for (int i = 0; i < 3; i++) {
                    Vec3 end = pos.add(cone(d, 50).scale(rf(0.5F, 1.1F)));
                    new FxBeam(level(), pos, end, 3).sprite(FxAtlas.BEAM_THIN).jagged(0.15F, 1, rnd().nextLong()).taper(0.3F).width(0.035F, 0.015F)
                            .color(0xFFFFFF, main).alpha(1.0F, 0.0F).spawn();
                }
            }
            case Fx.ESQUIRLA -> {
                for (int i = 0; i < 5; i++) {
                    p(pos, FxAtlas.SHARD, 8 + rnd().nextInt(6)).soft().vel(cone(d, 30).scale(rf(0.15F, 0.35F))).gravity(0.02).drag(0.9)
                            .size(0.05F, 0.02F, 1.0F).color(0xF0E0FF, main).roll(rf(0, 6.28F), rf(-0.5F, 0.5F)).alpha(0.95F, 0.0F).spawn();
                }
                ring(pos.add(d.scale(0.2)), d, FxAtlas.RING, main, 0.06F, 0.5F, 5, 0.8F);
            }
            case Fx.CICLON -> {
                ring(pos.add(d.scale(0.3)), d, FxAtlas.VORTEX, main, 0.2F, 1.0F, 8, 0.8F);
                for (int i = 0; i < 6; i++) {
                    p(pos.add(randomDir().scale(0.2)), FxAtlas.SPARK, 8).mode(Mode.VELOCITY).vel(cone(d, 35).scale(rf(0.3F, 0.6F))).drag(0.85)
                            .size(0.04F, 0.01F, 1.0F).aspect(3.0F).stretch(1.4F).color(0xFFFFFF, main).alpha(0.8F, 0.0F).spawn();
                }
            }
            case Fx.MINADOR -> {
                smoke(pos.add(d.scale(0.3)), 3, 0.16F, 26, 0x8C8480, d.scale(0.05).add(0, 0.01, 0), 0.15F);
                ring(pos.add(d.scale(0.2)), d, FxAtlas.RING_NOISE, main, 0.08F, 0.5F, 5, 0.7F);
            }
            case Fx.CENIZA -> {
                ring(pos.add(d.scale(0.3)), d, FxAtlas.RING, main, 0.2F, 1.1F, 9, 0.95F);
                ring(pos.add(d.scale(0.6)), d, FxAtlas.ACCRETION, 0x20C070, 0.3F, 0.9F, 8, 0.7F);
            }
            case Fx.SAETA -> {
                for (int i = 0; i < 4; i++) {
                    p(pos.add(randomDir().scale(0.12)), FxAtlas.GLINT, 6 + i * 2).size(0.08F, 0.0F, 1.0F).color(0xFFF4C8, main)
                            .roll(rf(0, 6.28F), 0.1F).alpha(1.0F, 0.0F).spawn();
                }
                ring(pos.add(d.scale(0.25)), d, FxAtlas.RING, main, 0.05F, 0.45F, 5, 0.8F);
            }
            case Fx.RUGIDO -> {
                // chorro trasero del lanzacohetes
                Vec3 rear = pos.subtract(d.scale(1.1));
                smoke(rear, 8, 0.3F, 40, 0xB8B4AE, d.scale(-0.1).add(0, 0.015, 0), 0.35F);
                p(rear, FxAtlas.FLASH_SIDE, 4).mode(Mode.AXIS).axis(d.reverse()).size(0.3F).aspect(2.6F).color(0xFFE0A0, 0xFF5A20).alpha(1.0F, 0.0F).spawn();
                p(rear, FxAtlas.GLOW, 5).size(0.7F, 1.0F, 1.0F).color(0xFF7A30).alpha(0.7F, 0.0F).spawn();
                smoke(pos.add(d.scale(0.4)), 3, 0.2F, 30, 0xA8A49E, d.scale(0.06).add(0, 0.01, 0), 0.15F);
                sparks(pos, d, 25, 8, 0xFFB060, 0.5F, 0.05F, 7, 0.02);
            }
            case Fx.METEORO -> {
                ring(pos.add(d.scale(0.2)), d, FxAtlas.RING, main, 0.05F, 0.4F, 5, 0.8F);
                p(pos, FxAtlas.GLINT, 5).size(0.15F, 0.05F, 1.0F).color(0xFFD0C0, main).roll(rf(0, 6.28F), 0.0F).alpha(1.0F, 0.0F).spawn();
            }
            case Fx.PULSAR -> {
                ring(pos.add(d.scale(0.3)), d, FxAtlas.RING, main, 0.1F, 0.9F, 7, 0.9F);
                ring(pos.add(d.scale(0.5)), d, FxAtlas.RING_NOISE, hot, 0.1F, 0.6F, 6, 0.7F);
                for (int i = 0; i < 3; i++) {
                    Vec3 end = pos.add(cone(d, 60).scale(rf(0.4F, 0.9F)));
                    new FxBeam(level(), pos, end, 3).sprite(FxAtlas.BEAM_THIN).jagged(0.12F, 1, rnd().nextLong()).taper(0.3F).width(0.03F, 0.01F)
                            .color(0xFFFFFF, main).alpha(1.0F, 0.0F).spawn();
                }
            }
            case Fx.FALLA -> {
                sparks(pos, d, 40, 8, Palette.spark(style), 0.4F, 0.045F, 7, 0.03);
                embers(pos, 4, main, 0.1F, 12);
            }
            case Fx.RETORNO -> {
                for (int i = 0; i < 4; i++) {
                    p(pos.add(randomDir().scale(0.15)), FxAtlas.GLINT, 6 + i * 2).size(0.09F, 0.0F, 1.0F).color(0xFFF4C8, main)
                            .roll(rf(0, 6.28F), 0.1F).alpha(1.0F, 0.0F).spawn();
                }
                p(pos.add(d.scale(0.3)), FxAtlas.SAW_BLUR, 5).mode(Mode.FIXED).axis(UP).size(0.25F, 0.5F, 0.5F).color(main).alpha(0.8F, 0.0F).spawn();
            }
            default -> {}
        }
    }

    /**
     * Fogonazo realista de un arma de pólvora: llamarada lateral y frontal, lenguas de fuego que salen despedidas,
     * granos de pólvora encendidos, humo que se queda flotando, chorros del freno de boca o del compensador, la
     * llamarada del hueco del tambor de los revólveres y el casquillo. En primera persona la llamarada principal se
     * dibuja pegada al arma (GunHands) y aquí solo salen las llamas, las chispas y el humo.
     */
    private static void realMuzzle(GunFx.Profile g, Vec3 pos, Vec3 d, @Nullable Entity shooter, boolean inHand) {
        float s = g.size();
        Vec3 right = d.cross(UP);
        right = right.lengthSqr() < 1.0E-6 ? new Vec3(1, 0, 0) : right.normalize();
        Vec3 up = right.cross(d).normalize();
        int f = rnd().nextInt(4);
        if (!g.suppressed()) {
            if (!inHand) {
                // bola de fuego opaca (se ve naranja también a pleno sol) y, encima, la llamarada luminosa
                p(pos.add(d.scale(0.08 * s)), FxAtlas.SMOKE[rnd().nextInt(FxAtlas.SMOKE.length)], 2).soft().size(0.16F * s, 0.24F * s, 1.0F)
                        .color(0xFFD27A, 0xFF7A2A).roll(rf(0.0F, 6.28F), 0.0F).alpha(0.85F, 0.0F).spawn();
                p(pos, FxAtlas.MFLASH_SIDE[f], 2).mode(Mode.AXIS).axis(d).size(0.14F * s).aspect(2.6F).color(0xFFFFFF).alpha(1.0F, 0.35F).spawn();
                p(pos.add(d.scale(0.04)), FxAtlas.MFLASH_FRONT[f], 2).size(0.24F * s, 0.3F * s, 1.0F).color(0xFFFFFF).roll(rf(0.0F, 6.28F), 0.0F)
                        .alpha(1.0F, 0.3F).spawn();
                p(pos, FxAtlas.GLOW, 3).size(0.55F * s, 0.75F * s, 1.0F).color(0xFF8A30).alpha(0.55F, 0.0F).spawn();
                if (g.brake()) {
                    for (int k = -1; k <= 1; k += 2) {
                        Vec3 jet = right.scale(k).add(d.scale(-0.2)).normalize();
                        p(pos.subtract(d.scale(0.03)), FxAtlas.MFLASH_SIDE[(f + k + 4) % 4], 2).mode(Mode.AXIS).axis(jet).size(0.06F * s).aspect(2.0F)
                                .color(0xFFFFFF).alpha(1.0F, 0.3F).spawn();
                    }
                }
                if (g.comp()) {
                    for (int k = -1; k <= 1; k += 2) {
                        Vec3 jet = up.add(right.scale(0.25 * k)).add(d.scale(0.2)).normalize();
                        p(pos.subtract(d.scale(0.06)), FxAtlas.MFLASH_SIDE[(f + 2) % 4], 2).mode(Mode.AXIS).axis(jet).size(0.045F * s).aspect(1.8F)
                                .color(0xFFFFFF).alpha(1.0F, 0.3F).spawn();
                    }
                }
                if (g.gap()) {
                    // el gas escapa entre el tambor y el cañón del revólver
                    Vec3 cyl = pos.subtract(d.scale(0.5 * s));
                    for (int k = -1; k <= 1; k += 2) {
                        p(cyl, FxAtlas.MFLASH_SIDE[(f + 1) % 4], 2).mode(Mode.AXIS).axis(right.scale(k).add(up.scale(0.2)).normalize()).size(0.04F * s)
                                .aspect(1.6F).color(0xFFFFFF).alpha(0.9F, 0.2F).spawn();
                    }
                    smoke(cyl, 1, 0.08F * s, 26, 0xB8B4AE, up.scale(0.01), 0.04F);
                }
            }
            // lenguas de fuego que salen despedidas hacia delante
            for (int i = 0; i < g.flames(); i++) {
                p(pos.add(d.scale(0.1 + 0.05 * i)), FxAtlas.FLAME[0], 3 + rnd().nextInt(3)).frames(FxAtlas.FLAME, true)
                        .vel(cone(d, 16).scale(rf(0.12F, 0.32F) * s)).drag(0.72).size(0.05F * s, 0.13F * s, 0.6F).color(0xFFE6A0, 0xFF4A10)
                        .roll(rf(0.0F, 6.28F), rf(-0.2F, 0.2F)).alpha(0.95F, 0.0F).spawn();
            }
            // granos de pólvora encendidos
            int grains = Math.max(1, Math.round(3 * s));
            for (int i = 0; i < grains; i++) {
                p(pos, FxAtlas.EMBER, 5 + rnd().nextInt(8)).vel(cone(d, 22).scale(rf(0.2F, 0.55F) * s)).drag(0.82).gravity(0.012)
                        .size(rf(0.015F, 0.03F), 0.0F, 1.0F).color(0xFFF2C0, 0xFF6A10).alpha(1.0F, 0.0F).spawn();
            }
        } else {
            // silenciador: solo un destello mínimo
            p(pos, FxAtlas.GLOW, 2).size(0.12F, 0.18F, 1.0F).color(0xFF9A50).alpha(0.35F, 0.0F).spawn();
        }
        gunSmoke(pos, d, up, g);
        if (g.casing() != GunFx.NONE) casing(g.casing(), pos, d, right, shooter);
    }

    /** Humo del disparo: bocanada hacia delante y una voluta que sube despacio desde la boca del cañón. */
    private static void gunSmoke(Vec3 pos, Vec3 d, Vec3 up, GunFx.Profile g) {
        float amount = g.smoke() * 1.6F;
        float s = g.size();
        int n = (int) amount + (rnd().nextFloat() < amount - (int) amount ? 1 : 0);
        for (int i = 0; i < n; i++) {
            Vec3 at = pos.add(d.scale(rf(0.15F, 0.7F) * s)).add(randomDir().scale(0.06));
            p(at, FxAtlas.SMOKE[rnd().nextInt(FxAtlas.SMOKE.length)], (int) (rf(40F, 70F))).soft()
                    .vel(d.scale(rf(0.03F, 0.09F) * s).add(randomDir().scale(0.012)).add(0, 0.012, 0)).drag(0.9)
                    .size(0.08F * s * rf(0.8F, 1.2F), 0.5F * s * rf(0.8F, 1.3F), 0.5F).color(0xE4E0DA, 0xB0ACA6)
                    .alpha(rf(0.42F, 0.6F), 0.0F).fadeIn(0.06F).roll(rf(0.0F, 6.28F), rf(-0.03F, 0.03F)).spawn();
        }
        if (rnd().nextFloat() < 0.35F + 0.4F * Math.min(1.0F, amount)) {
            p(pos.add(up.scale(0.02)), FxAtlas.SMOKE[rnd().nextInt(FxAtlas.SMOKE.length)], (int) rf(45F, 70F)).soft()
                    .vel(new Vec3(0, rf(0.008F, 0.016F), 0).add(d.scale(0.01))).drag(0.96).size(0.04F * s, 0.3F * s, 0.7F)
                    .color(0xC8C4BE, 0xB0ACA6).alpha(0.3F, 0.0F).fadeIn(0.2F).roll(rf(0.0F, 6.28F), rf(-0.02F, 0.02F)).spawn();
        }
    }

    /** Casquillo de latón (o cartucho rojo de escopeta) que sale girando hacia el lado de la mano. */
    private static void casing(int type, Vec3 pos, Vec3 d, Vec3 right, @Nullable Entity shooter) {
        Vec3 r = shooter instanceof LivingEntity l && l.getMainArm() == HumanoidArm.LEFT ? right.reverse() : right;
        float size;
        float aspect;
        int c;
        switch (type) {
            case GunFx.SHELL -> { size = 0.045F; aspect = 2.0F; c = 0xC8402E; }
            case GunFx.RIFLE -> { size = 0.03F; aspect = 2.6F; c = 0xE2BA68; }
            case GunFx.BIG -> { size = 0.05F; aspect = 3.0F; c = 0xD8AE5E; }
            default -> { size = 0.026F; aspect = 2.0F; c = 0xE8C070; }
        }
        Vec3 at = pos.subtract(d.scale(type == GunFx.SHELL ? 0.45 : 0.35)).add(r.scale(0.06));
        Vec3 v = r.scale(rf(0.08F, 0.14F)).add(0, rf(0.1F, 0.15F), 0).add(d.scale(-0.03)).add(randomDir().scale(0.02));
        p(at, FxAtlas.CASING, 16 + rnd().nextInt(6)).soft().vel(v).gravity(0.03).drag(0.97).size(size).aspect(aspect)
                .color(c, Palette.mix(c, 0x605040, 0.4F)).roll(rf(0, 6.28F), rf(-0.7F, 0.7F)).alpha(1.0F, 0.0F).alphaPow(6.0F).spawn();
        if (type != GunFx.SHELL && rnd().nextFloat() < 0.4F) {
            // un hilo de humo sale de la ventana de expulsión
            smoke(at, 1, 0.04F, 20, 0xC0BCB6, r.scale(0.01).add(0, 0.01, 0), 0.02F);
        }
    }

    // ================================================================ impactos

    private static void impact(int style, Vec3 pos, Vec3 normal, float strength) {
        Vec3 n = normal.lengthSqr() < 1.0E-6 ? UP : normal.normalize();
        int main = Palette.main(style);
        int hot = Palette.hot(style);
        int spark = Palette.spark(style);
        Vec3 at = pos.add(n.scale(0.05));
        switch (style) {
            case Fx.AGUJA -> {
                flash(at, main, hot, 0.7F, 5);
                ring(at, n, FxAtlas.RING, main, 0.1F, 1.0F, 8, 0.9F);
                sparks(at, n, 70, 14, spark, 0.5F, 0.05F, 9, 0.01);
                for (int i = 0; i < 4; i++) {
                    p(at.add(randomDir().scale(0.3)), FxAtlas.GLINT, 10 + rnd().nextInt(8)).size(0.12F, 0.0F, 1.0F).color(hot, main)
                            .roll(rf(0, 6.28F), 0.0F).vel(randomDir().scale(0.02)).alpha(1.0F, 0.0F).spawn();
                }
            }
            case Fx.AVISPON, Fx.AVISPON_HOT -> {
                flash(at, main, hot, 0.35F, 3);
                ring(at, n, FxAtlas.RING_NOISE, main, 0.05F, 0.45F, 6, 0.8F);
                for (int i = 0; i < 6; i++) {
                    p(at, FxAtlas.EMBER, 10 + rnd().nextInt(8)).vel(cone(n, 60).scale(rf(0.08F, 0.22F))).gravity(0.02).drag(0.9)
                            .size(0.05F, 0.0F, 1.0F).color(hot, main).alpha(1.0F, 0.3F).spawn();
                }
                if (style == Fx.AVISPON_HOT) {
                    p(at, FxAtlas.FLAME[0], 10).frames(FxAtlas.FLAME, true).size(0.15F, 0.3F, 1.0F).vel(n.scale(0.02).add(0, 0.03, 0))
                            .color(0xFFE0A0, 0xFF4010).alpha(0.9F, 0.0F).spawn();
                }
            }
            case Fx.CHISPA -> {
                flash(at, main, hot, 0.4F, 3);
                sparks(at, n, 80, 8, 0xEAF2FF, 0.45F, 0.04F, 6, 0.02);
                for (int i = 0; i < 2; i++) {
                    Vec3 end = at.add(cone(n, 70).scale(rf(0.6F, 1.4F)));
                    new FxBeam(level(), at, end, 3).sprite(FxAtlas.BEAM_THIN).jagged(0.15F, 1, rnd().nextLong()).width(0.04F, 0.02F)
                            .color(0xFFFFFF, main).alpha(1.0F, 0.0F).spawn();
                }
            }
            case Fx.ESCARCHA -> {
                flash(at, main, hot, 0.35F, 3);
                for (int i = 0; i < 6; i++) {
                    p(at, FxAtlas.SHARD, 14 + rnd().nextInt(10)).soft().vel(cone(n, 70).scale(rf(0.1F, 0.25F))).gravity(0.03).drag(0.93)
                            .size(rf(0.05F, 0.1F), 0.02F, 1.0F).color(0xFFFFFF, 0xC8F4FF).roll(rf(0, 6.28F), rf(-0.4F, 0.4F)).alpha(0.95F, 0.0F).spawn();
                }
                p(at, FxAtlas.SNOW, 12).size(0.15F, 0.05F, 1.0F).color(0xFFFFFF, main).roll(rf(0, 6.28F), 0.15F).alpha(1.0F, 0.0F).spawn();
                smoke(at, 2, 0.15F, 18, 0xE0F6FF, n.scale(0.02), 0.1F);
            }
            case Fx.COLIBRI -> {
                flash(at, main, hot, 0.25F, 3);
                for (int i = 0; i < 4; i++) {
                    p(at, FxAtlas.SHARD, 10 + rnd().nextInt(6)).soft().vel(cone(n, 70).scale(rf(0.08F, 0.2F))).gravity(0.03)
                            .size(0.04F, 0.01F, 1.0F).color(0xFFD8F0, main).roll(rf(0, 6.28F), rf(-0.5F, 0.5F)).alpha(0.9F, 0.0F).spawn();
                }
            }
            case Fx.REMACHE -> {
                p(at, FxAtlas.CORE, 3).size(0.2F, 0.05F, 1.0F).color(0xFFE8C0).alpha(1.0F, 0.0F).spawn();
                sparks(at, n, 70, 5, 0xFFA040, 0.35F, 0.035F, 8, 0.04);
                if (rnd().nextFloat() < 0.5F) smoke(at, 1, 0.1F, 16, 0x8C847C, n.scale(0.02), 0.05F);
            }
            case Fx.ROTOR, Fx.HALCON, Fx.BASTION -> {
                flash(at, main, hot, 0.25F, 2);
                sparks(at, n, 70, 4, spark, 0.3F, 0.035F, 6, 0.03);
            }
            case Fx.SIERRA -> {
                flash(at, main, hot, 0.3F, 3);
                sparks(at, n, 80, 10, 0xFFE0A0, 0.45F, 0.035F, 8, 0.05);
                sparks(at, n, 60, 5, spark, 0.3F, 0.04F, 6, 0.0);
            }
            case Fx.CIZALLA -> {
                flash(at, main, hot, 0.5F, 4);
                p(at, FxAtlas.CRESCENT, 5).size(0.5F, 0.8F, 0.5F).color(hot, main).roll(rf(0, 6.28F), 0.0F).alpha(1.0F, 0.0F).spawn();
                sparks(at, n, 80, 8, spark, 0.4F, 0.04F, 7, 0.0);
            }
            case Fx.CENTELLA -> {
                flash(at, main, hot, 0.6F, 4);
                ring(at, n, FxAtlas.RING_NOISE, main, 0.1F, 0.9F, 7, 0.9F);
                sparks(at, n, 70, 12, 0xFFD070, 0.5F, 0.05F, 9, 0.03);
                embers(at, 5, 0xFFA020, 0.12F, 14);
            }
            case Fx.ARPON -> {
                flash(at, main, hot, 0.4F, 3);
                sparks(at, n, 60, 10, spark, 0.45F, 0.045F, 8, 0.04);
            }
            case Fx.ESQUIRLA -> {
                flash(at, main, hot, 0.3F, 3);
                for (int i = 0; i < 5; i++) {
                    p(at, FxAtlas.SHARD, 12 + rnd().nextInt(8)).soft().vel(cone(n, 70).scale(rf(0.08F, 0.22F))).gravity(0.03).drag(0.93)
                            .size(rf(0.04F, 0.08F), 0.02F, 1.0F).color(0xF6E8FF, main).roll(rf(0, 6.28F), rf(-0.4F, 0.4F)).alpha(0.95F, 0.0F).spawn();
                }
            }
            case Fx.CENIZA -> {
                flash(at, main, hot, 0.6F, 5);
                ring(at, n, FxAtlas.RING, main, 0.2F, 1.4F, 9, 0.9F);
                ash(at, 10, 0.5F);
            }
            case Fx.SAETA -> {
                flash(at, main, hot, 0.4F, 3);
                ring(at, n, FxAtlas.RING, main, 0.05F, 0.6F, 6, 0.9F);
                sparks(at, n, 70, 6, 0xFFF0C0, 0.35F, 0.04F, 7, 0.02);
                for (int i = 0; i < 3; i++) {
                    p(at.add(randomDir().scale(0.2)), FxAtlas.GLINT, 8 + rnd().nextInt(6)).size(0.1F, 0.0F, 1.0F).color(0xFFFFFF, main)
                            .roll(rf(0, 6.28F), 0.0F).alpha(1.0F, 0.0F).spawn();
                }
            }
            case Fx.MINADOR -> {
                smoke(at, 2, 0.15F, 24, 0x6A6460, n.scale(0.02), 0.1F);
                sparks(at, n, 80, 4, 0xFF6040, 0.2F, 0.03F, 6, 0.03);
            }
            case Fx.RETORNO -> {
                flash(at, main, hot, 0.4F, 3);
                sparks(at, n, 75, 10, 0xFFE890, 0.45F, 0.04F, 8, 0.03);
                for (int i = 0; i < 3; i++) {
                    p(at.add(randomDir().scale(0.2)), FxAtlas.GLINT, 8 + rnd().nextInt(6)).size(0.1F, 0.0F, 1.0F).color(0xFFFFFF, main)
                            .roll(rf(0, 6.28F), 0.0F).alpha(1.0F, 0.0F).spawn();
                }
            }
            default -> {
                flash(at, main, hot, 0.35F * Math.max(0.5F, strength), 3);
                sparks(at, n, 70, 6, spark, 0.35F, 0.04F, 7, 0.02);
            }
        }
    }

    // ================================================================ explosiones

    private static void explosion(int style, Vec3 pos, float radius) {
        int main = Palette.main(style);
        switch (style) {
            case Fx.VORTICE -> {
                collapse(pos, radius);
                return;
            }
            case Fx.SOLAR -> {
                nova(pos, radius);
                return;
            }
            case Fx.ARPON -> {
                fusion(pos, radius);
                return;
            }
            case Fx.CAUSTICA -> {
                acidSplash(pos, radius);
                return;
            }
            case Fx.ESQUIRLA -> {
                crystalBurst(pos, radius);
                return;
            }
            case Fx.ENJAMBRE -> {
                podBurst(pos, radius);
                return;
            }
            case Fx.METEORO -> {
                meteorImpact(pos, radius);
                return;
            }
            case Fx.PULSAR -> {
                electricBurst(pos, radius);
                return;
            }
            case Fx.VOLCAN -> {
                realExplosion(pos, radius);
                return;
            }
            default -> {}
        }
        float k = radius / 3.0F;
        int fireballs = style == Fx.BOMBLET ? 3 : style == Fx.TRITURADORA ? 9 : style == Fx.MORTERO || style == Fx.RUGIDO ? 12 : 6;
        // destello que ilumina todo
        p(pos, FxAtlas.GLOW, 6).size(radius * 1.4F, radius * 2.2F, 0.5F).color(0xFFE6B0, 0xFF7020).alpha(1.0F, 0.0F).spawn();
        p(pos, FxAtlas.LENS, 5).size(radius * 0.6F, radius * 0.2F, 1.0F).aspect(6.0F).color(0xFFD090).alpha(0.8F, 0.0F).spawn();
        // bolas de fuego
        for (int i = 0; i < fireballs; i++) {
            Vec3 at = pos.add(randomDir().scale(radius * 0.35 * rnd().nextFloat()));
            p(at, FxAtlas.FIRE[0], 12 + rnd().nextInt(8)).frames(FxAtlas.FIRE, false).vel(randomDir().scale(0.04 * k).add(0, 0.03, 0)).drag(0.88)
                    .size(radius * rf(0.25F, 0.4F), radius * rf(0.55F, 0.8F), 0.4F).color(0xFFF4D0, style == Fx.TRITURADORA ? 0xFF5A10 : 0xFF3A10)
                    .roll(rf(0, 6.28F), rf(-0.05F, 0.05F)).alpha(1.0F, 0.0F).alphaPow(1.6F).spawn();
        }
        // onda de choque y anillo en el suelo
        ring(pos, UP, FxAtlas.SHOCK, style == Fx.TRITURADORA ? 0xF0FFB0 : 0xFFE0B0, radius * 0.2F, radius * 1.6F, 9, 0.9F);
        ring(pos.add(0, 0.05, 0), UP, FxAtlas.RING, main, radius * 0.3F, radius * 2.0F, 12, 0.6F);
        p(pos, FxAtlas.SHOCK, 7).size(radius * 0.2F, radius * 1.5F, 0.5F).color(0xFFF0D0).alpha(0.6F, 0.0F).spawn();
        // chispas, escombros, ascuas y humo
        sparks(pos, UP, 180.0, style == Fx.BOMBLET ? 8 : 22, 0xFFB050, 0.6F * k + 0.3F, 0.06F, 12, 0.03);
        int debris = style == Fx.BOMBLET ? 3 : style == Fx.TRITURADORA ? 14 : style == Fx.MORTERO || style == Fx.RUGIDO ? 18 : 8;
        for (int i = 0; i < debris; i++) {
            p(pos, FxAtlas.DEBRIS, 24 + rnd().nextInt(16)).soft().vel(cone(UP, 75).scale(rf(0.25F, 0.6F) * k)).gravity(0.045).drag(0.97)
                    .size(rf(0.05F, 0.12F)).color(0x3A3430).roll(rf(0, 6.28F), rf(-0.4F, 0.4F)).alpha(1.0F, 0.6F).spawn();
        }
        embers(pos, style == Fx.BOMBLET ? 6 : 18, 0xFF6020, 0.25F * k, 30);
        smoke(pos.add(0, radius * 0.2, 0), style == Fx.BOMBLET ? 3 : style == Fx.MORTERO || style == Fx.RUGIDO ? 14 : 9, radius * 0.35F, 55, 0x5C5650,
                new Vec3(0, 0.035, 0), radius * 0.5F);
        if (style == Fx.RUGIDO) groundDebris(pos, radius, 12);
        if (style == Fx.MINADOR) ring(pos.add(0, 0.1, 0), UP, FxAtlas.RING_NOISE, 0xFF5030, radius * 0.4F, radius * 1.6F, 10, 0.8F);
    }

    /**
     * VOLCÁN: explosión realista de una granada de 40 mm. Destello breve, bola de fuego que en medio segundo se apaga
     * en humo negro, polvo y tierra levantados del suelo, metralla, escombros y la onda expansiva a ras de suelo.
     */
    private static void realExplosion(Vec3 pos, float radius) {
        float k = radius / 4.0F;
        // destello
        p(pos, FxAtlas.GLOW, 4).size(radius * 1.1F, radius * 1.7F, 0.5F).color(0xFFF2D0, 0xFF8A30).alpha(1.0F, 0.0F).spawn();
        p(pos, FxAtlas.CORE, 3).size(radius * 0.4F, radius * 0.15F, 1.0F).color(0xFFFFFF).alpha(1.0F, 0.0F).spawn();
        // bola de fuego corta
        for (int i = 0; i < 10; i++) {
            Vec3 at = pos.add(randomDir().scale(radius * 0.25 * rnd().nextFloat())).add(0, 0.2 * k, 0);
            p(at, FxAtlas.FIRE[0], 7 + rnd().nextInt(5)).frames(FxAtlas.FIRE, false).vel(randomDir().scale(0.06 * k).add(0, 0.02, 0)).drag(0.8)
                    .size(radius * rf(0.18F, 0.28F), radius * rf(0.4F, 0.55F), 0.35F).color(0xFFF0C0, 0xE04A10)
                    .roll(rf(0.0F, 6.28F), rf(-0.08F, 0.08F)).alpha(1.0F, 0.0F).alphaPow(1.3F).spawn();
        }
        // humo negro y gris que sale con la bola de fuego y sube despacio
        for (int i = 0; i < 16; i++) {
            Vec3 dir = randomDir();
            if (dir.y < 0.0) dir = new Vec3(dir.x, -dir.y * 0.4, dir.z).normalize();
            Vec3 at = pos.add(dir.scale(radius * 0.2 * rnd().nextFloat()));
            p(at, FxAtlas.SMOKE[rnd().nextInt(FxAtlas.SMOKE.length)], 60 + rnd().nextInt(50)).soft().vel(dir.scale(rf(0.08F, 0.2F) * k).add(0, 0.02, 0))
                    .drag(0.86).gravity(-0.0015).size(radius * rf(0.15F, 0.25F), radius * rf(0.6F, 0.9F), 0.4F).color(0x3A3632, 0x6A6660)
                    .alpha(rf(0.7F, 0.9F), 0.0F).fadeIn(0.05F).roll(rf(0.0F, 6.28F), rf(-0.02F, 0.02F)).spawn();
        }
        // polvo y tierra del suelo: anillo que se abre y penachos que suben
        BlockPos below = BlockPos.containing(pos.x, pos.y - 0.5, pos.z);
        BlockState ground = level().getBlockState(below);
        if (ground.isAir()) {
            below = below.below();
            ground = level().getBlockState(below);
        }
        int dust = ground.isAir() ? 0x8A8070 : dustColor(ground, below);
        int dark = Palette.mix(dust, 0x605850, 0.4F);
        for (int i = 0; i < 14; i++) {
            double a = rnd().nextDouble() * Math.PI * 2.0;
            Vec3 out = new Vec3(Math.cos(a), 0.0, Math.sin(a));
            p(pos.add(out.scale(radius * 0.2)), FxAtlas.SMOKE[rnd().nextInt(FxAtlas.SMOKE.length)], 40 + rnd().nextInt(40)).soft()
                    .vel(out.scale(rf(0.15F, 0.3F) * k).add(0, rf(0.02F, 0.06F), 0)).drag(0.85).size(radius * 0.15F, radius * rf(0.45F, 0.7F), 0.5F)
                    .color(dust, dark).alpha(rf(0.5F, 0.75F), 0.0F).fadeIn(0.05F).roll(rf(0.0F, 6.28F), rf(-0.02F, 0.02F)).spawn();
        }
        for (int i = 0; i < 6; i++) {
            p(pos, FxAtlas.SMOKE[rnd().nextInt(FxAtlas.SMOKE.length)], 30 + rnd().nextInt(20)).soft().vel(cone(UP, 28).scale(rf(0.3F, 0.5F) * k))
                    .drag(0.9).gravity(0.02).size(radius * 0.08F, radius * rf(0.3F, 0.45F), 0.5F).color(dark, dust).alpha(0.85F, 0.0F)
                    .roll(rf(0.0F, 6.28F), 0.0F).spawn();
        }
        // metralla, escombros y ascuas
        sparks(pos, UP, 180.0, 26, 0xFFC070, 1.1F * k, 0.05F, 7, 0.02);
        for (int i = 0; i < 14; i++) {
            p(pos, FxAtlas.DEBRIS, 24 + rnd().nextInt(16)).soft().vel(cone(UP, 70).scale(rf(0.25F, 0.55F) * k)).gravity(0.045).drag(0.97)
                    .size(rf(0.04F, 0.1F)).color(0x2E2A26).roll(rf(0.0F, 6.28F), rf(-0.4F, 0.4F)).alpha(1.0F, 0.6F).spawn();
        }
        groundDebris(pos, radius, 16);
        embers(pos, 8, 0xFF6A20, 0.25F * k, 20);
        // onda expansiva
        ring(pos.add(0, 0.08, 0), UP, FxAtlas.SHOCK, 0xFFE8C8, radius * 0.2F, radius * 1.8F, 6, 0.55F);
        p(pos, FxAtlas.SHOCK, 5).size(radius * 0.2F, radius * 1.6F, 0.5F).color(0xFFF0D8).alpha(0.4F, 0.0F).spawn();
    }

    /**
     * MARTINETE: mini terremoto al golpear cayendo. Grietas incandescentes que se abren en el suelo, onda de choque y
     * anillo de polvo del color del suelo que se abre en círculo, piedras y terrones que saltan.
     */
    private static void quake(Vec3 pos, float radius) {
        int main = Palette.main(Fx.MARTINETE);
        BlockPos below = BlockPos.containing(pos.x, pos.y - 0.2, pos.z);
        BlockState ground = level().getBlockState(below);
        if (ground.isAir()) {
            below = below.below();
            ground = level().getBlockState(below);
        }
        int dust = ground.isAir() ? 0x8A8070 : dustColor(ground, below);
        int dark = Palette.mix(dust, 0x50483E, 0.4F);
        Vec3 g = pos.add(0, 0.06, 0);
        // golpe: destello, onda de choque a ras de suelo y anillo de energía del mazo
        p(g.add(0, 0.3, 0), FxAtlas.GLOW, 5).size(radius * 0.6F, radius * 1.2F, 0.5F).color(0xFFE0B0, main).alpha(0.8F, 0.0F).spawn();
        ring(g, UP, FxAtlas.SHOCK, 0xFFE8C8, 0.3F, radius * 2.2F, 8, 0.75F);
        ring(g.add(0, 0.02, 0), UP, FxAtlas.RING_NOISE, main, radius * 0.3F, radius * 2.0F, 12, 0.6F);
        // grietas incandescentes que se apagan
        for (int i = 0; i < 3; i++) {
            p(g.add(0, 0.004 * i, 0), FxAtlas.CRACK, 40 + i * 10).mode(Mode.FIXED).axis(UP).size(radius * rf(0.55F, 0.85F)).color(0xFFD080, main)
                    .roll(rf(0.0F, 6.28F), 0.0F).alpha(0.9F, 0.0F).alphaPow(1.6F).spawn();
        }
        // anillo de polvo que se abre en círculo
        int n = 26;
        for (int i = 0; i < n; i++) {
            double a = (i + rnd().nextDouble() * 0.6) / n * Math.PI * 2.0;
            Vec3 out = new Vec3(Math.cos(a), 0.0, Math.sin(a));
            p(pos.add(out.scale(0.4)).add(0, 0.15, 0), FxAtlas.SMOKE[rnd().nextInt(FxAtlas.SMOKE.length)], 30 + rnd().nextInt(25)).soft()
                    .vel(out.scale(rf(0.18F, 0.28F) * radius / 4.0F).add(0, rf(0.01F, 0.04F), 0)).drag(0.86)
                    .size(0.25F, rf(0.9F, 1.3F), 0.5F).color(dust, dark).alpha(rf(0.55F, 0.8F), 0.0F).fadeIn(0.05F)
                    .roll(rf(0.0F, 6.28F), rf(-0.03F, 0.03F)).spawn();
        }
        // piedras y terrones
        blockChips(ground, pos.add(0, 0.2, 0), UP, 26, 0.55);
        for (int i = 0; i < 12; i++) {
            p(pos.add(0, 0.1, 0), FxAtlas.DEBRIS, 20 + rnd().nextInt(14)).soft().vel(cone(UP, 60).scale(rf(0.2F, 0.45F))).gravity(0.05).drag(0.97)
                    .size(rf(0.05F, 0.11F)).color(Palette.mix(dust, 0x302820, 0.5F)).roll(rf(0.0F, 6.28F), rf(-0.4F, 0.4F)).alpha(1.0F, 0.6F).spawn();
        }
        sparks(pos.add(0, 0.2, 0), UP, 75, 10, 0xFFC070, 0.5F, 0.04F, 8, 0.04);
    }

    /** Réplica del terremoto del MARTINETE: el suelo del círculo vuelve a sacudirse (onda, polvo y terrones). */
    private static void tremor(Vec3 pos, float radius) {
        BlockPos below = BlockPos.containing(pos.x, pos.y - 0.2, pos.z);
        BlockState ground = level().getBlockState(below);
        if (ground.isAir()) {
            below = below.below();
            ground = level().getBlockState(below);
        }
        int dust = ground.isAir() ? 0x8A8070 : dustColor(ground, below);
        int dark = Palette.mix(dust, 0x50483E, 0.4F);
        Vec3 g = pos.add(0, 0.06, 0);
        ring(g, UP, FxAtlas.SHOCK, 0xFFE8C8, 0.3F, radius * 1.9F, 7, 0.45F);
        ring(g.add(0, 0.02, 0), UP, FxAtlas.RING_NOISE, Palette.main(Fx.MARTINETE), radius * 0.4F, radius * 1.8F, 10, 0.35F);
        // polvo que salta por todo el círculo
        int n = 16;
        for (int i = 0; i < n; i++) {
            double a = (i + rnd().nextDouble() * 0.7) / n * Math.PI * 2.0;
            double r = radius * rf(0.25F, 0.95F);
            Vec3 at = pos.add(Math.cos(a) * r, 0.12, Math.sin(a) * r);
            p(at, FxAtlas.SMOKE[rnd().nextInt(FxAtlas.SMOKE.length)], 20 + rnd().nextInt(16)).soft().vel(new Vec3(0, rf(0.02F, 0.05F), 0)).drag(0.9)
                    .size(0.2F, rf(0.6F, 0.9F), 0.5F).color(dust, dark).alpha(rf(0.45F, 0.65F), 0.0F).fadeIn(0.05F)
                    .roll(rf(0.0F, 6.28F), rf(-0.03F, 0.03F)).spawn();
        }
        // terrones y piedras que botan
        for (int i = 0; i < 8; i++) {
            double a = rnd().nextDouble() * Math.PI * 2.0;
            double r = radius * Math.sqrt(rnd().nextDouble());
            blockChips(ground, pos.add(Math.cos(a) * r, 0.15, Math.sin(a) * r), UP, 2, 0.3);
        }
    }

    /** SOLAR: nova dorada con corona, anillos y lluvia de ascuas. */
    private static void nova(Vec3 pos, float radius) {
        int main = Palette.main(Fx.SOLAR);
        float k = radius / 3.0F;
        p(pos, FxAtlas.GLOW, 10).size(radius * 1.2F, radius * 2.6F, 0.5F).color(0xFFFFF0, 0xFF8A10).alpha(1.0F, 0.0F).spawn();
        p(pos, FxAtlas.CORONA, 12).size(radius * 0.6F, radius * 1.6F, 0.5F).color(0xFFF4C0, 0xFFA020).roll(rf(0, 6.28F), 0.03F)
                .alpha(1.0F, 0.0F).spawn();
        p(pos, FxAtlas.CORE, 8).size(radius * 0.5F, radius * 0.1F, 1.0F).color(0xFFFFFF).alpha(1.0F, 0.0F).spawn();
        p(pos, FxAtlas.LENS, 8).size(radius, radius * 0.3F, 1.0F).aspect(8.0F).color(0xFFD060).alpha(0.9F, 0.0F).spawn();
        ring(pos, UP, FxAtlas.SHOCK, 0xFFF0C0, radius * 0.3F, radius * 2.0F, 10, 0.9F);
        ring(pos, randomDir(), FxAtlas.RING, 0xFFE080, radius * 0.2F, radius * 1.8F, 12, 0.8F);
        ring(pos, randomDir(), FxAtlas.RING, 0xFFB040, radius * 0.2F, radius * 1.5F, 14, 0.6F);
        for (int i = 0; i < 10; i++) {
            Vec3 at = pos.add(randomDir().scale(radius * 0.3 * rnd().nextFloat()));
            p(at, FxAtlas.FIRE[0], 14 + rnd().nextInt(8)).frames(FxAtlas.FIRE, false).vel(randomDir().scale(0.05 * k)).drag(0.9)
                    .size(radius * rf(0.25F, 0.4F), radius * rf(0.5F, 0.75F), 0.4F).color(0xFFFFF0, main).roll(rf(0, 6.28F), rf(-0.05F, 0.05F))
                    .alpha(1.0F, 0.0F).alphaPow(1.5F).spawn();
        }
        sparks(pos, UP, 180.0, 30, 0xFFE080, 0.8F * k, 0.06F, 14, 0.02);
        embers(pos, 24, 0xFFB020, 0.3F * k, 34);
        smoke(pos.add(0, radius * 0.2, 0), 5, radius * 0.3F, 50, 0x8A7A60, new Vec3(0, 0.03, 0), radius * 0.5F);
    }

    /** ARPÓN: estallido de fusión celeste con arcos de plasma. */
    private static void fusion(Vec3 pos, float radius) {
        int main = Palette.main(Fx.ARPON);
        p(pos, FxAtlas.GLOW, 8).size(radius * 1.2F, radius * 2.0F, 0.5F).color(0xE8FFFF, main).alpha(1.0F, 0.0F).spawn();
        p(pos, FxAtlas.CORE, 6).size(radius * 0.4F, radius * 0.1F, 1.0F).color(0xFFFFFF).alpha(1.0F, 0.0F).spawn();
        p(pos, FxAtlas.LENS, 6).size(radius * 0.8F, radius * 0.25F, 1.0F).aspect(7.0F).color(main).alpha(0.8F, 0.0F).spawn();
        ring(pos, UP, FxAtlas.SHOCK, 0xC8F8FF, radius * 0.2F, radius * 1.7F, 9, 0.9F);
        ring(pos, randomDir(), FxAtlas.RING, main, radius * 0.2F, radius * 1.6F, 11, 0.8F);
        for (int i = 0; i < 6; i++) {
            Vec3 at = pos.add(randomDir().scale(radius * 0.3 * rnd().nextFloat()));
            p(at, FxAtlas.FIRE[0], 10 + rnd().nextInt(6)).frames(FxAtlas.FIRE, false).vel(randomDir().scale(0.05)).drag(0.88)
                    .size(radius * rf(0.2F, 0.3F), radius * rf(0.45F, 0.6F), 0.4F).color(0xE8FFFF, 0x1A70FF).roll(rf(0, 6.28F), 0.0F)
                    .alpha(1.0F, 0.0F).alphaPow(1.5F).spawn();
        }
        for (int i = 0; i < 5; i++) {
            Vec3 end = pos.add(randomDir().scale(radius * rf(0.6F, 1.1F)));
            new FxBeam(level(), pos, end, 4).sprite(FxAtlas.BEAM_THIN).jagged(0.3F, 1, rnd().nextLong()).taper(0.3F).width(0.06F, 0.02F)
                    .color(0xFFFFFF, main).alpha(1.0F, 0.0F).spawn();
        }
        sparks(pos, UP, 180.0, 20, 0xC8F8FF, 0.6F, 0.05F, 10, 0.02);
        smoke(pos.add(0, radius * 0.2, 0), 5, radius * 0.3F, 45, 0xB8C8CC, new Vec3(0, 0.03, 0), radius * 0.4F);
    }

    /** CÁUSTICA: salpicadura de ácido con gotas y burbujas. */
    private static void acidSplash(Vec3 pos, float radius) {
        int main = Palette.main(Fx.CAUSTICA);
        p(pos, FxAtlas.GLOW, 5).size(radius * 0.5F, radius * 0.9F, 0.6F).color(0xD8FF80, main).alpha(0.8F, 0.0F).spawn();
        ring(pos, UP, FxAtlas.RING_NOISE, main, radius * 0.2F, radius * 1.1F, 7, 0.7F);
        for (int i = 0; i < 10; i++) {
            p(pos, FxAtlas.EMBER, 12 + rnd().nextInt(10)).vel(cone(UP, 80).scale(rf(0.12F, 0.3F))).gravity(0.04).drag(0.95)
                    .size(rf(0.04F, 0.07F), 0.02F, 1.0F).color(0xE8FFA0, main).alpha(1.0F, 0.2F).spawn();
        }
        for (int i = 0; i < 3; i++) {
            p(pos.add(randomDir().scale(radius * 0.4)), FxAtlas.BUBBLE, 14 + rnd().nextInt(10)).vel(new Vec3(0, rf(0.01F, 0.03F), 0))
                    .size(rf(0.06F, 0.12F), rf(0.1F, 0.18F), 1.0F).color(0xD8FF80).alpha(0.9F, 0.0F).spawn();
        }
        smoke(pos, 1, 0.25F, 26, 0xA8D860, new Vec3(0, 0.02, 0), 0.2F);
    }

    /** ESQUIRLA: el cristal estalla en fragmentos violetas. */
    private static void crystalBurst(Vec3 pos, float radius) {
        int main = Palette.main(Fx.ESQUIRLA);
        p(pos, FxAtlas.GLOW, 5).size(radius * 0.6F, radius * 1.2F, 0.6F).color(0xF6E8FF, main).alpha(0.9F, 0.0F).spawn();
        p(pos, FxAtlas.LENS, 4).size(radius * 0.6F, radius * 0.2F, 1.0F).aspect(7.0F).color(main).alpha(0.8F, 0.0F).spawn();
        ring(pos, randomDir(), FxAtlas.RING, main, radius * 0.1F, radius * 1.0F, 7, 0.9F);
        for (int i = 0; i < 12; i++) {
            p(pos, FxAtlas.SHARD, 14 + rnd().nextInt(10)).soft().vel(randomDir().scale(rf(0.12F, 0.3F))).gravity(0.03).drag(0.93)
                    .size(rf(0.05F, 0.1F), 0.02F, 1.0F).color(0xF6E8FF, main).roll(rf(0, 6.28F), rf(-0.5F, 0.5F)).alpha(0.95F, 0.0F).spawn();
        }
        for (int i = 0; i < 8; i++) {
            p(pos.add(randomDir().scale(radius * 0.6 * rnd().nextFloat())), FxAtlas.GLINT, 8 + rnd().nextInt(10)).size(0.12F, 0.0F, 1.0F)
                    .color(0xFFFFFF, main).roll(rf(0, 6.28F), 0.0F).alpha(1.0F, 0.0F).spawn();
        }
    }

    /** ENJAMBRE: la cápsula se abre y los nanobots se dispersan. */
    private static void podBurst(Vec3 pos, float radius) {
        int main = Palette.main(Fx.ENJAMBRE);
        p(pos, FxAtlas.GLOW, 6).size(radius * 0.6F, radius * 1.2F, 0.6F).color(0xFFF0B0, main).alpha(0.9F, 0.0F).spawn();
        ring(pos, randomDir(), FxAtlas.RING_NOISE, main, radius * 0.2F, radius * 1.2F, 8, 0.8F);
        for (int i = 0; i < 18; i++) {
            p(pos, FxAtlas.EMBER, 10 + rnd().nextInt(10)).vel(randomDir().scale(rf(0.1F, 0.35F))).drag(0.85).size(0.05F, 0.02F, 1.0F)
                    .color(0xFFFFFF, main).alpha(1.0F, 0.0F).spawn();
        }
        smoke(pos, 2, 0.2F, 24, 0xB0A080, new Vec3(0, 0.02, 0), 0.2F);
    }

    /** Ceniza que sube (motas oscuras y chispas verdes). */
    private static void ash(Vec3 pos, int n, float spread) {
        for (int i = 0; i < n; i++) {
            Vec3 at = pos.add(randomDir().scale(spread * rnd().nextFloat()));
            p(at, FxAtlas.DEBRIS, 20 + rnd().nextInt(20)).soft().vel(new Vec3(rf(-0.02F, 0.02F), rf(0.02F, 0.06F), rf(-0.02F, 0.02F))).drag(0.96)
                    .gravity(-0.002).size(rf(0.03F, 0.06F), 0.0F, 1.0F).color(0x202420).roll(rf(0, 6.28F), rf(-0.2F, 0.2F)).alpha(1.0F, 0.0F).spawn();
            if (i % 2 == 0) {
                p(at, FxAtlas.EMBER, 12 + rnd().nextInt(10)).vel(new Vec3(0, rf(0.01F, 0.05F), 0)).size(0.05F, 0.0F, 1.0F).color(0xD8FFE8, 0x3CFF9E)
                        .alpha(1.0F, 0.0F).spawn();
            }
        }
    }

    /** Colapso del VÓRTICE: implosión violeta y estallido. */
    private static void collapse(Vec3 pos, float radius) {
        int main = Palette.main(Fx.VORTICE);
        p(pos, FxAtlas.GLOW, 8).size(radius * 1.6F, radius * 0.4F, 0.6F).color(0xF4E8FF, main).alpha(1.0F, 0.0F).spawn();
        p(pos, FxAtlas.CORE, 6).size(radius * 0.6F, radius * 0.1F, 1.0F).color(0xFFFFFF).alpha(1.0F, 0.0F).spawn();
        p(pos, FxAtlas.LENS, 6).size(radius * 0.8F, radius * 0.3F, 1.0F).aspect(7.0F).color(main).alpha(0.9F, 0.0F).spawn();
        ring(pos, UP, FxAtlas.SHOCK, main, radius * 1.8F, radius * 0.2F, 7, 0.9F);
        ring(pos, UP, FxAtlas.RING, 0xF0D8FF, radius * 0.2F, radius * 2.2F, 12, 0.8F);
        ring(pos, randomDir(), FxAtlas.ACCRETION, main, radius * 0.4F, radius * 1.4F, 10, 0.8F);
        p(pos, FxAtlas.VORTEX, 12).size(radius * 1.2F, radius * 0.1F, 0.7F).color(main).roll(0.0F, 0.5F).alpha(1.0F, 0.0F).spawn();
        for (int i = 0; i < 24; i++) {
            Vec3 d = randomDir();
            p(pos.add(d.scale(radius * 1.2)), FxAtlas.SPARK, 6).mode(Mode.VELOCITY).vel(d.scale(-radius * 0.2)).size(0.06F, 0.02F, 1.0F)
                    .aspect(3.0F).stretch(1.4F).color(0xFFFFFF, main).alpha(1.0F, 0.0F).spawn();
        }
        sparks(pos, UP, 180.0, 16, 0xD8B8FF, 0.6F, 0.05F, 12, 0.0);
    }

    private static void vortexOpen(Vec3 pos, float radius) {
        int main = Palette.main(Fx.VORTICE);
        p(pos, FxAtlas.GLOW, 8).size(1.0F, 2.5F, 0.5F).color(0xF4E8FF, main).alpha(1.0F, 0.0F).spawn();
        ring(pos, UP, FxAtlas.SHOCK, main, 0.4F, radius * 1.1F, 10, 0.8F);
        ring(pos, UP, FxAtlas.RING, 0xF0D8FF, radius, 0.3F, 12, 0.7F);
    }

    // ================================================================ haces

    /**
     * Disparo de la AGUJA: un láser recto y nítido (núcleo blanco y halo celeste) que se apaga en medio segundo;
     * cuanto más cargado, más grueso y brillante. Sin espiral ni chispas: un haz de luz limpio.
     */
    private static void rail(Vec3 from, Vec3 to, float charge, @Nullable Entity shooter) {
        ClientLevel level = level();
        Vec3 start = shooter != null ? Muzzle.world(shooter, 1.0F) : from;
        float k = 0.6F + 0.4F * charge;
        int main = Palette.main(Fx.AGUJA);
        // núcleo blanco, halo celeste y resplandor exterior: tres capas rectas del mismo haz
        new FxBeam(level, start, to, 10).sprite(FxAtlas.BEAM_THIN).width(0.035F * k, 0.012F * k).color(0xFFFFFF).alpha(1.0F, 0.0F).alphaPow(0.8F).spawn();
        new FxBeam(level, start, to, 9).sprite(FxAtlas.BEAM).width(0.11F * k, 0.05F * k).color(0xBFF6FF, main).alpha(0.9F, 0.0F).alphaPow(0.9F).spawn();
        new FxBeam(level, start, to, 6).sprite(FxAtlas.BEAM).width(0.32F * k, 0.2F * k).color(main, 0x1E7CFF).alpha(0.35F, 0.0F).spawn();
        // destello en la boca del cañón y punto de impacto al final
        p(start, FxAtlas.GLOW, 4).size(0.3F * k, 0.15F * k, 1.0F).color(main).alpha(0.9F, 0.0F).spawn();
        p(start, FxAtlas.CORE, 3).size(0.08F * k).color(0xFFFFFF).alpha(1.0F, 0.0F).spawn();
        p(to, FxAtlas.GLOW, 5).size(0.45F * k, 0.2F * k, 1.0F).color(main).alpha(0.8F, 0.0F).spawn();
    }

    /** Rayo eléctrico de la CHISPA: resplandor azul y núcleo blanco con la misma forma en zigzag. */
    private static void arc(int style, Vec3 from, Vec3 to, float thickness, @Nullable Entity shooter) {
        ClientLevel level = level();
        if (shooter != null && Muzzle.firstPerson(shooter)) Muzzle.fired(style, 0.04F);
        long seed = rnd().nextLong();
        double len = from.distanceTo(to);
        float jag = (float) Mth.clamp(0.12 + len * 0.035, 0.15, 0.9);
        // la CHISPA es azul; los rayos de la esfera del PULSAR, magenta
        boolean pulsar = style == Fx.PULSAR;
        int outer0 = pulsar ? 0xFF7AF0 : 0x7FA8FF;
        int outer1 = pulsar ? 0xC020C0 : 0x4060FF;
        int inner1 = pulsar ? 0xFFE0FA : 0xFFF4C0;
        int branch = pulsar ? 0xE040D0 : 0x6080FF;
        int glow = pulsar ? 0xFF80F0 : 0x9CC8FF;
        new FxBeam(level, from, to, 3).anchor(shooter).sprite(FxAtlas.BEAM).jagged(jag, 1, seed).taper(0.1F)
                .width(0.22F * thickness, 0.12F * thickness).color(outer0, outer1).alpha(0.8F, 0.0F).spawn();
        new FxBeam(level, from, to, 3).anchor(shooter).sprite(FxAtlas.BEAM_THIN).jagged(jag, 1, seed).taper(0.1F)
                .width(0.06F * thickness, 0.03F * thickness).color(0xFFFFFF, inner1).alpha(1.0F, 0.2F).spawn();
        // ramas cortas
        if (rnd().nextFloat() < 0.6F) {
            Vec3 a = from.add(to.subtract(from).scale(rf(0.2F, 0.8F)));
            Vec3 b = a.add(randomDir().scale(rf(0.5F, 1.6F)));
            new FxBeam(level, a, b, 2).sprite(FxAtlas.BEAM_THIN).jagged(0.2F, 1, rnd().nextLong()).taper(0.3F).width(0.03F, 0.01F)
                    .color(0xE8F0FF, branch).alpha(0.9F, 0.0F).spawn();
        }
        p(to, FxAtlas.GLOW, 3).size(0.35F * thickness, 0.5F * thickness, 1.0F).color(glow).alpha(0.7F, 0.0F).spawn();
        if (shooter == null) p(from, FxAtlas.GLOW, 3).size(0.3F * thickness).color(glow).alpha(0.6F, 0.0F).spawn();
        if (pulsar) sparks(to, to.subtract(from).reverse(), 80, 4, Palette.spark(style), 0.3F, 0.035F, 6, 0.02);
    }

    /** Remache: trazo rapidísimo desde la boca del cañón hasta el punto de impacto. */
    private static void tracer(int style, Vec3 from, Vec3 to, @Nullable Entity shooter) {
        Vec3 start = shooter != null ? Muzzle.world(shooter, 1.0F) : from;
        Vec3 d = to.subtract(start);
        double len = d.length();
        if (len < 0.3) return;
        double speed = 9.0;
        int life = Math.max(1, (int) Math.ceil(len / speed));
        p(start, FxAtlas.SPARK, life + 1).mode(Mode.VELOCITY).vel(d.scale(speed / len)).size(0.035F).aspect(4.0F).stretch(0.8F)
                .color(0xFFF0D0, Palette.main(style)).alpha(1.0F, 0.6F).spawn();
    }

    /**
     * Chorro del SOPLETE: fuego opaco de colores vivos, que se ve igual a pleno sol que de noche (el brillo aditivo de
     * antes se sumaba hasta quedarse en una mancha blanca). Lengua continua pegada a la boquilla, chorro de estelas y
     * llamas que se ensancha hasta el final, bocanadas naranjas que se vuelven rojas y suben al frenarse, humo negro,
     * gotas de combustible, salpicadura de fuego contra los bloques y llamas sobre las criaturas alcanzadas.
     */
    private static void flame(Vec3 from, Vec3 to, float range, @Nullable Entity shooter) {
        if (shooter != null && Muzzle.firstPerson(shooter)) Muzzle.fired(Fx.SOPLETE, 0.03F);
        Vec3 start = shooter != null ? Muzzle.world(shooter, 1.0F) : from;
        Vec3 d = to.subtract(start);
        double len = Math.max(1.0, d.length());
        Vec3 dir = d.scale(1.0 / len);
        // llama piloto azul en la boquilla
        p(start, FxAtlas.GLOW, 3).size(0.14F, 0.18F, 1.0F).color(0x6FA8FF).alpha(0.7F, 0.0F).spawn();
        p(start, FxAtlas.FLASH_SIDE, 2).mode(Mode.AXIS).axis(dir).size(0.05F).aspect(3.0F).color(0xDDEEFF, 0x4A7CFF).alpha(0.9F, 0.0F).spawn();
        // lengua continua pegada a la boquilla (dura 3 ticks: siempre hay una aunque el chorro avance un bloque por tick)
        float reach = (float) Math.min(2.6, len * 0.5);
        Vec3 lip = start.add(dir.scale(0.05));
        p(lip, smokeSprite(), 3).soft().mode(Mode.AXIS).axis(dir).size(0.1F, 0.14F, 1.0F).aspect(reach / 0.24F).color(0xFFD25A, 0xFF8A20)
                .alpha(0.95F, 0.5F).spawn();
        p(lip, FxAtlas.FLASH_SIDE, 3).mode(Mode.AXIS).axis(dir).size(0.06F, 0.08F, 1.0F).aspect(reach / 0.16F).color(0xFFE8B0, 0xFF8A30)
                .alpha(0.55F, 0.0F).spawn();
        // chorro continuo: estelas de combustible ardiendo, estiradas en la dirección de vuelo
        // (cada tanda nace adelantada hasta 2 ticks para que no queden huecos entre tanda y tanda)
        for (int i = 0; i < 8; i++) {
            float sp = rf(1.0F, 1.15F);
            Vec3 v = cone(dir, 2.5).scale(sp);
            float k = rf(0.7F, 2.0F);
            int life = Math.max(3, ticksFor(len * rf(0.8F, 1.0F), sp, 0.99, 26) - (int) k);
            p(start.add(v.scale(k)), smokeSprite(), life).soft().mode(Mode.VELOCITY).vel(v).drag(0.99).gravity(0.004)
                    .size(0.06F, rf(0.35F, 0.5F), 1.0F).stretch(0.6F).color(0xFFD860, 0xFF6A12).alpha(1.0F, 0.0F).alphaPow(3.0F)
                    .fadeIn(0.1F).spawn();
        }
        // llamas opacas alrededor del chorro: finas al salir y anchas al final
        for (int i = 0; i < 8; i++) {
            float sp = rf(1.0F, 1.15F);
            Vec3 v = cone(dir, 3.5).scale(sp);
            float k = rf(0.0F, 2.0F);
            int life = Math.max(3, ticksFor(len * rf(0.85F, 1.0F), sp, 0.99, 26) - (int) k);
            p(start.add(v.scale(k)), smokeSprite(), life).soft().vel(v).drag(0.99).gravity(0.004)
                    .size(0.09F, rf(0.6F, 0.85F), 0.9F).color(0xFFE27A, 0xFF6A14).roll(rf(0.0F, 6.28F), rf(-0.25F, 0.25F))
                    .alpha(1.0F, 0.0F).alphaPow(3.0F).spawn();
        }
        // cuerpo del fuego: bocanadas naranjas que se vuelven rojas, se ensanchan y suben al frenarse
        for (int i = 0; i < 7; i++) {
            float sp = rf(0.85F, 1.0F);
            Vec3 v = cone(dir, 5.0).scale(sp);
            float k = rf(0.0F, 2.0F);
            int life = Math.max(4, ticksFor(len * rf(0.75F, 1.0F), sp, 0.975, 30) - (int) k);
            p(start.add(v.scale(k)).add(randomDir().scale(0.05)), smokeSprite(), life).soft().vel(v).drag(0.975).gravity(-0.006)
                    .size(0.12F, rf(1.1F, 1.6F), 1.2F).color(0xFFA232, 0xA8240A).roll(rf(0.0F, 6.28F), rf(-0.12F, 0.12F))
                    .alpha(0.95F, 0.0F).alphaPow(1.6F).spawn();
        }
        // detalle caliente: llamaradas aditivas suaves encima del fuego opaco
        for (int i = 0; i < 3; i++) {
            float sp = rf(0.95F, 1.1F);
            Vec3 v = cone(dir, 3.5).scale(sp);
            float k = rf(0.0F, 2.0F);
            int life = Math.max(3, ticksFor(len * rf(0.6F, 0.95F), sp, 0.985, 24) - (int) k);
            p(start.add(v.scale(k)), FxAtlas.FIRE[0], life).frames(FxAtlas.FIRE, true).vel(v).drag(0.985)
                    .size(0.08F, rf(0.5F, 0.75F), 1.0F).color(0xFFD890, 0xFF5A10).roll(rf(0.0F, 6.28F), rf(-0.2F, 0.2F))
                    .alpha(0.45F, 0.0F).alphaPow(1.5F).spawn();
        }
        // humo negro: aparece en la segunda mitad del chorro y sube
        for (int i = 0; i < 2; i++) {
            float sp = rf(0.8F, 0.95F);
            Vec3 v = cone(dir, 6.0).scale(sp);
            float k = rf(0.0F, 2.0F);
            int life = Math.max(6, ticksFor(len * rf(0.8F, 1.0F), sp, 0.975, 32) + 10 - (int) k);
            p(start.add(v.scale(k)), smokeSprite(), life).soft().vel(v).drag(0.96).gravity(-0.01)
                    .size(0.25F, rf(1.6F, 2.1F), 0.8F).color(0x52443A, 0x1E1A18).roll(rf(0.0F, 6.28F), rf(-0.05F, 0.05F))
                    .alpha(0.45F, 0.0F).alphaPow(1.2F).fadeIn(0.5F).spawn();
        }
        // brillo anaranjado (poco, para no quemar la imagen a blanco)
        for (int i = 0; i < 2; i++) {
            float sp = rf(0.95F, 1.1F);
            Vec3 v = cone(dir, 2.5).scale(sp);
            float k = rf(0.0F, 2.0F);
            int life = Math.max(3, ticksFor(len * rf(0.6F, 0.95F), sp, 0.985, 22) - (int) k);
            p(start.add(v.scale(k)), FxAtlas.GLOW, life).vel(v).drag(0.985).size(0.2F, rf(1.1F, 1.5F), 1.0F).color(0xFF9A40, 0xFF3A10)
                    .alpha(0.22F, 0.0F).alphaPow(1.5F).spawn();
        }
        // gotas de combustible ardiendo que caen, y ascuas
        if (rnd().nextFloat() < 0.6F) {
            Vec3 v = cone(dir, 6.0).scale(rf(0.7F, 0.9F));
            p(start.add(dir.scale(rf(1.0F, 3.0F))), smokeSprite(), 14 + rnd().nextInt(9)).soft().vel(v).drag(0.97).gravity(0.03)
                    .size(0.08F, 0.16F, 1.0F).color(0xFFC050, 0xE04010).alpha(1.0F, 0.0F).alphaPow(2.0F).spawn();
        }
        if (rnd().nextFloat() < 0.7F) embers(start.add(dir.scale(len * rf(0.3F, 0.9F))), 2, 0xFF7020, 0.08F, 16);
        // salpicadura: el chorro choca contra un bloque, el fuego se extiende por la superficie y sube
        if (len < range - 0.6) {
            BlockHitResult hit = blockAt(start, to);
            if (hit != null) flameSplash(to, Vec3.atLowerCornerOf(hit.getDirection().getNormal()));
        }
        // las criaturas que alcanza el chorro arden con llamas visibles
        burnAlong(start, dir, len, shooter);
    }

    private static FxAtlas.Sprite smokeSprite() {
        return FxAtlas.SMOKE[rnd().nextInt(FxAtlas.SMOKE.length)];
    }

    /** Ticks que tarda una partícula (velocidad inicial {@code speed}, rozamiento {@code drag}) en recorrer {@code dist}. */
    private static int ticksFor(double dist, double speed, double drag, int max) {
        double x = 1.0 - dist * (1.0 - drag) / (speed * drag);
        if (x <= 0.0) return max;
        return Mth.clamp((int) (Math.log(x) / Math.log(drag)), 2, max);
    }

    /** Fuego del SOPLETE que choca contra un bloque: se extiende por la superficie, sube y deja humo. */
    private static void flameSplash(Vec3 to, Vec3 n) {
        Vec3 a = Math.abs(n.y) < 0.95 ? UP : new Vec3(1, 0, 0);
        Vec3 u = n.cross(a).normalize();
        Vec3 w = n.cross(u).normalize();
        Vec3 at = to.add(n.scale(0.15));
        for (int i = 0; i < 5; i++) {
            double ang = rnd().nextDouble() * Math.PI * 2.0;
            Vec3 v = u.scale(Math.cos(ang)).add(w.scale(Math.sin(ang))).scale(rf(0.12F, 0.25F)).add(n.scale(rf(0.02F, 0.06F)))
                    .add(0, rf(0.02F, 0.05F), 0);
            p(at, smokeSprite(), 10 + rnd().nextInt(9)).soft().vel(v).drag(0.9).gravity(-0.008).size(0.3F, rf(0.8F, 1.1F), 0.8F)
                    .color(0xFFB040, 0xB02A0A).roll(rf(0.0F, 6.28F), rf(-0.1F, 0.1F)).alpha(0.95F, 0.0F).alphaPow(1.5F).spawn();
        }
        for (int i = 0; i < 2; i++) {
            p(at.add(randomDir().scale(0.3)), FxAtlas.FLAME[0], 8 + rnd().nextInt(7)).frames(FxAtlas.FLAME, true).vel(n.scale(0.02).add(0, 0.05, 0))
                    .drag(0.9).size(0.3F, 0.6F, 1.0F).color(0xFFE0A0, 0xFF5A10).alpha(0.5F, 0.0F).alphaPow(1.4F).spawn();
        }
        p(at.add(n.scale(0.3)).add(0, 0.3, 0), smokeSprite(), 24 + rnd().nextInt(11)).soft().vel(n.scale(0.03).add(0, 0.05, 0)).drag(0.95)
                .size(0.4F, rf(1.3F, 1.8F), 0.7F).color(0x4A3E36, 0x1A1614).alpha(0.6F, 0.0F).fadeIn(0.2F).spawn();
    }

    /** Llamas sobre las criaturas que están dentro del chorro del SOPLETE (solo se ven: el daño lo decide el servidor). */
    private static void burnAlong(Vec3 start, Vec3 dir, double len, @Nullable Entity shooter) {
        ClientLevel level = level();
        AABB box = new AABB(start, start.add(dir.scale(len))).inflate(3.0);
        double tan = Math.tan(Math.toRadians(10.0));
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, x -> x != shooter && x.isAlive() && !x.isSpectator())) {
            AABB bb = e.getBoundingBox();
            Vec3 rel = bb.getCenter().subtract(start);
            double along = rel.dot(dir);
            if (along < 0.3 || along > len + 0.6) continue;
            double off = rel.subtract(dir.scale(along)).length();
            if (off > 0.4 + along * tan + e.getBbWidth() * 0.5) continue;
            for (int i = 0; i < 2; i++) {
                Vec3 at = new Vec3(Mth.lerp(rnd().nextDouble(), bb.minX, bb.maxX), Mth.lerp(rnd().nextDouble() * 0.8, bb.minY, bb.maxY),
                        Mth.lerp(rnd().nextDouble(), bb.minZ, bb.maxZ));
                p(at, smokeSprite(), 8 + rnd().nextInt(6)).soft().vel(new Vec3(0, rf(0.04F, 0.08F), 0)).drag(0.92)
                        .size(0.12F, rf(0.35F, 0.5F), 0.8F).color(0xFFC048, 0xC0300A).roll(rf(0.0F, 6.28F), rf(-0.15F, 0.15F))
                        .alpha(0.95F, 0.0F).alphaPow(1.5F).spawn();
            }
            Vec3 c = bb.getCenter();
            Vec3 tip = new Vec3(c.x, bb.minY + (bb.maxY - bb.minY) * rf(0.3F, 0.8F), c.z).add(randomDir().scale(e.getBbWidth() * 0.4));
            p(tip, FxAtlas.FLAME[0], 8 + rnd().nextInt(5)).frames(FxAtlas.FLAME, true).vel(new Vec3(0, 0.06, 0)).drag(0.9)
                    .size(0.15F, 0.4F, 1.0F).color(0xFFE0A0, 0xFF5A10).alpha(0.5F, 0.0F).alphaPow(1.4F).spawn();
        }
    }

    /** Láser del TRUENO: celeste que se vuelve rojo al calentarse. */
    private static void laser(Vec3 from, Vec3 to, float heat, @Nullable Entity shooter) {
        ClientLevel level = level();
        if (shooter != null && Muzzle.firstPerson(shooter)) Muzzle.fired(Fx.TRUENO, 0.02F + heat * 0.04F);
        int color = Palette.mix(0x5FE6FF, 0xFF2A1A, heat);
        int core = Palette.mix(0xE8FDFF, 0xFFE0D0, heat);
        float w = 1.0F + heat;
        new FxBeam(level, from, to, 3).anchor(shooter).sprite(FxAtlas.BEAM_THIN).width(0.045F * w, 0.04F * w).color(core).alpha(1.0F, 0.6F).spawn();
        new FxBeam(level, from, to, 3).anchor(shooter).sprite(FxAtlas.BEAM).width(0.2F * w, 0.18F * w).color(color).alpha(0.75F, 0.4F).spawn();
        new FxBeam(level, from, to, 3).anchor(shooter).sprite(FxAtlas.BEAM_NOISE).tile(1.6F, 0.5F).width(0.12F * w, 0.1F * w).color(color)
                .alpha(0.7F, 0.3F).spawn();
        Vec3 start = shooter != null ? Muzzle.world(shooter, 1.0F) : from;
        p(start, FxAtlas.GLOW, 3).size(0.2F * w).color(color).alpha(0.8F, 0.0F).spawn();
        // punto de impacto incandescente
        Vec3 back = start.subtract(to);
        Vec3 n = back.lengthSqr() < 1.0E-6 ? UP : back.normalize();
        p(to, FxAtlas.GLOW, 3).size(0.4F * w, 0.5F * w, 1.0F).color(color).alpha(0.9F, 0.0F).spawn();
        p(to, FxAtlas.CORE, 2).size(0.12F * w).color(core).alpha(1.0F, 0.0F).spawn();
        if (rnd().nextFloat() < 0.6F) sparks(to, n, 70, 2 + (int) (heat * 4), Palette.mix(0xBFF8FF, 0xFFB060, heat), 0.3F, 0.03F, 6, 0.03);
        if (heat > 0.6F && rnd().nextFloat() < 0.4F) smoke(to, 1, 0.15F, 22, 0x605A56, new Vec3(0, 0.03, 0), 0.05F);
    }

    /** Cuerda azul de la SERPENTINA: une el látigo con el enemigo mientras lo arrastra. */
    private static void rope(Vec3 from, Vec3 to, float ticks, @Nullable Entity shooter, @Nullable Entity target) {
        ClientLevel level = level();
        int life = Math.max(4, (int) ticks);
        boolean miss = target == null;
        new FxBeam(level, from, to, life).anchor(shooter).target(target).sprite(FxAtlas.ROPE).jagged(0.25F, 1, 0L)
                .shape(FxBeam.Shape.WAVE).tile(0.9F, 0.0F).width(0.07F, 0.05F).color(0x6C8CFF, 0x3050FF).alpha(1.0F, miss ? 0.0F : 0.7F)
                .grow(miss ? 3.0F : 2.0F).spawn();
        new FxBeam(level, from, to, life).anchor(shooter).target(target).sprite(FxAtlas.BEAM).width(0.16F, 0.1F).color(0x3A5CFF)
                .alpha(0.55F, miss ? 0.0F : 0.3F).grow(miss ? 3.0F : 2.0F).spawn();
        if (target != null) {
            p(target.position().add(0, target.getBbHeight() * 0.55, 0), FxAtlas.RING, 8).follow(target, new Vec3(0, target.getBbHeight() * 0.55, 0))
                    .size(0.6F, 0.9F, 1.0F).color(0x7090FF).alpha(0.8F, 0.0F).spawn();
            sparks(target.getBoundingBox().getCenter(), UP, 180.0, 8, 0xD8E4FF, 0.3F, 0.04F, 6, 0.0);
        }
    }

    private static void drill(Vec3 pos, Vec3 dir, int stage) {
        int color = stage == 2 ? 0xFF5020 : stage == 1 ? 0xFFA020 : 0xFFE060;
        Vec3 n = dir.lengthSqr() < 1.0E-6 ? UP : dir.normalize().reverse();
        p(pos, FxAtlas.DRILL, 4).mode(Mode.FIXED).axis(n).size(0.25F + stage * 0.08F).color(color).roll(rf(0, 6.28F), 0.8F).alpha(0.9F, 0.0F).spawn();
        p(pos, FxAtlas.GLOW, 3).size(0.3F + stage * 0.15F).color(color).alpha(0.8F, 0.0F).spawn();
        sparks(pos, n, 75, 4 + stage * 5, color, 0.35F + stage * 0.12F, 0.04F, 7, 0.04);
        if (stage == 2) {
            embers(pos, 3, 0xFF4010, 0.12F, 14);
            if (rnd().nextFloat() < 0.4F) smoke(pos, 1, 0.12F, 18, 0x5A5450, new Vec3(0, 0.02, 0), 0.05F);
        }
    }

    private static void lock(@Nullable Entity target, int count) {
        if (target == null) return;
        Vec3 off = new Vec3(0, target.getBbHeight() * 0.55, 0);
        float size = Math.max(0.45F, target.getBbWidth() * 0.7F);
        p(target.position().add(off), FxAtlas.LOCK, 40).follow(target, off).size(size * 1.6F, size, 0.3F).color(0xFF4A3A)
                .alpha(1.0F, 0.6F).spawn();
        p(target.position().add(off), FxAtlas.RING, 8).follow(target, off).size(size * 2.0F, size * 0.6F, 0.5F).color(0xFF6A4A)
                .alpha(0.9F, 0.0F).spawn();
    }

    private static void melee(int style, Vec3 pos, Vec3 dir, float power) {
        Vec3 d = dir.lengthSqr() < 1.0E-6 ? UP : dir.normalize();
        int main = Palette.main(style);
        int hot = Palette.hot(style);
        switch (style) {
            case Fx.MARTINETE -> {
                flash(pos, main, hot, 0.6F, 4);
                ring(pos, d, FxAtlas.SHOCK, 0xFFD8A0, 0.2F, 1.4F, 7, 0.8F);
                sparks(pos, d.reverse(), 80, 10, 0xFFB050, 0.5F, 0.05F, 9, 0.04);
                smoke(pos, 3, 0.2F, 24, 0x9A928A, d.scale(0.03), 0.3F);
            }
            case Fx.CIZALLA -> {
                p(pos, FxAtlas.CRESCENT, 6).size(0.7F, 1.1F, 0.5F).color(hot, main).roll(rf(0, 6.28F), 0.0F).alpha(1.0F, 0.0F).spawn();
                flash(pos, main, hot, 0.45F, 3);
                sparks(pos, d, 70, 8, Palette.spark(style), 0.4F, 0.04F, 7, 0.0);
            }
            case Fx.SERPENTINA -> {
                float k = power > 1.5F ? 1.8F : 1.0F;
                flash(pos, main, hot, 0.45F * k, 4);
                for (int i = 0; i < (int) (3 * k); i++) {
                    Vec3 end = pos.add(randomDir().scale(rf(0.6F, 1.4F) * k));
                    new FxBeam(level(), pos, end, 3).sprite(FxAtlas.BEAM_THIN).jagged(0.15F, 1, rnd().nextLong()).taper(0.3F).width(0.04F, 0.02F)
                            .color(0xFFFFFF, main).alpha(1.0F, 0.0F).spawn();
                }
                if (k > 1.0F) ring(pos, d, FxAtlas.RING_NOISE, main, 0.2F, 1.6F, 8, 0.9F);
            }
            case Fx.GUADANA -> {
                p(pos, FxAtlas.CRESCENT, 6).size(0.6F, 1.0F, 0.5F).color(hot, main).roll(rf(0, 6.28F), 0.0F).alpha(1.0F, 0.0F).spawn();
                flash(pos, main, hot, 0.45F, 3);
                sparks(pos, d, 70, 10, Palette.spark(style), 0.45F, 0.045F, 8, 0.02);
                embers(pos, 4, 0xFF2040, 0.1F, 12);
            }
            case Fx.PICA -> {
                flash(pos, main, hot, 0.5F, 4);
                ring(pos, d, FxAtlas.RING, main, 0.1F, 0.9F, 7, 0.9F);
                sparks(pos, d, 40, 10, Palette.spark(style), 0.55F, 0.045F, 8, 0.0);
            }
            default -> {
                flash(pos, main, hot, 0.4F, 3);
                sparks(pos, d, 70, 6, Palette.spark(style), 0.35F, 0.04F, 7, 0.02);
            }
        }
    }

    private static void shield(Vec3 pos, Vec3 dir) {
        int main = Palette.main(Fx.BASTION);
        Vec3 d = dir.lengthSqr() < 1.0E-6 ? UP : dir.normalize();
        p(pos, FxAtlas.HEX, 9).mode(Mode.FIXED).axis(d).size(0.5F, 0.9F, 0.5F).color(0xFFF0D8, main).alpha(1.0F, 0.0F).spawn();
        ring(pos, d, FxAtlas.RING, main, 0.2F, 1.0F, 7, 0.8F);
        sparks(pos, d, 70, 8, 0xFFD8A0, 0.35F, 0.04F, 7, 0.03);
    }

    private static void drone(Vec3 pos, Vec3 dir) {
        int main = Palette.main(Fx.HALCON);
        flash(pos, main, 0xFFF0D8, 0.5F, 4);
        ring(pos, UP, FxAtlas.RING, main, 0.2F, 1.2F, 8, 0.8F);
        sparks(pos, UP, 180.0, 8, 0xFFD090, 0.3F, 0.04F, 7, 0.02);
    }

    // ================================================================ serie PX-2x / PX-3x

    /** PRISMA: haz blanco hasta el cristal (valor -1) y los cinco rayos de colores (valor 0–4). */
    private static void prism(Vec3 from, Vec3 to, float value, @Nullable Entity shooter) {
        ClientLevel level = level();
        int idx = Math.round(value);
        if (idx < 0) {
            Vec3 start = shooter != null ? Muzzle.world(shooter, 1.0F) : from;
            new FxBeam(level, start, to, 6).anchor(shooter).sprite(FxAtlas.BEAM).width(0.24F, 0.05F).color(0xFFFFFF, 0xD8E0FF).alpha(0.9F, 0.0F).spawn();
            new FxBeam(level, start, to, 5).anchor(shooter).sprite(FxAtlas.BEAM_THIN).width(0.07F, 0.0F).color(0xFFFFFF).alpha(1.0F, 0.0F).spawn();
            p(to, FxAtlas.STAR6, 5).size(0.35F, 0.6F, 1.0F).color(0xFFFFFF).roll(rf(0, 6.28F), 0.1F).alpha(1.0F, 0.0F).spawn();
            p(to, FxAtlas.GLOW, 6).size(0.5F, 0.9F, 1.0F).color(0xF0F4FF).alpha(0.9F, 0.0F).spawn();
            p(to, FxAtlas.LENS, 5).size(0.4F, 0.15F, 1.0F).aspect(9.0F).color(0xFFFFFF).alpha(0.8F, 0.0F).spawn();
            return;
        }
        int c = Palette.RAINBOW[Mth.clamp(idx, 0, Palette.RAINBOW.length - 1)];
        int core = Palette.mix(c, 0xFFFFFF, 0.6F);
        new FxBeam(level, from, to, 8).sprite(FxAtlas.BEAM).width(0.2F, 0.04F).color(c).alpha(0.85F, 0.0F).alphaPow(0.8F).spawn();
        new FxBeam(level, from, to, 6).sprite(FxAtlas.BEAM_THIN).width(0.05F, 0.0F).color(core).alpha(1.0F, 0.0F).spawn();
        new FxBeam(level, from, to, 8).sprite(FxAtlas.BEAM_NOISE).tile(2.0F, 0.8F).width(0.1F, 0.02F).color(c).alpha(0.6F, 0.0F).spawn();
        Vec3 back = from.subtract(to);
        Vec3 n = back.lengthSqr() < 1.0E-6 ? UP : back.normalize();
        flash(to, c, core, 0.35F, 4);
        sparks(to, n, 70, 5, c, 0.35F, 0.04F, 7, 0.02);
        double len = from.distanceTo(to);
        int k = Mth.clamp((int) (len / 3.0), 1, 20);
        for (int i = 0; i < k; i++) {
            Vec3 at = from.add(to.subtract(from).scale(rnd().nextDouble()));
            p(at, FxAtlas.GLINT, 6 + rnd().nextInt(8)).size(rf(0.04F, 0.09F), 0.0F, 1.0F).color(core, c).roll(rf(0, 6.28F), 0.0F).alpha(1.0F, 0.0F).spawn();
        }
    }

    /** TEMPESTAD: rayo que cae de la nube (valor 1) o que sube desde el cañón a formarla (valor &lt; 1). */
    private static void lightning(Vec3 from, Vec3 to, float value, @Nullable Entity shooter) {
        ClientLevel level = level();
        long seed = rnd().nextLong();
        double len = from.distanceTo(to);
        float jag = (float) Mth.clamp(0.15 + len * 0.05, 0.3, 1.4);
        float w = 0.6F + 0.4F * value;
        new FxBeam(level, from, to, 5).anchor(shooter).sprite(FxAtlas.BEAM).jagged(jag, 2, seed).width(0.5F * w, 0.3F * w).color(0x9AB8FF, 0x5A70FF)
                .alpha(0.9F, 0.0F).spawn();
        new FxBeam(level, from, to, 5).anchor(shooter).sprite(FxAtlas.BEAM_THIN).jagged(jag, 2, seed).width(0.12F * w, 0.08F * w).color(0xFFFFFF, 0xE0E8FF)
                .alpha(1.0F, 0.0F).spawn();
        for (int i = 0; i < 3; i++) {
            Vec3 a = from.add(to.subtract(from).scale(rf(0.2F, 0.85F)));
            Vec3 b = a.add(randomDir().scale(rf(0.8F, 2.2F))).add(0, -rf(0.3F, 1.2F), 0);
            new FxBeam(level, a, b, 3).sprite(FxAtlas.BEAM_THIN).jagged(0.3F, 1, rnd().nextLong()).taper(0.4F).width(0.05F, 0.02F)
                    .color(0xE8F0FF, 0x7090FF).alpha(0.9F, 0.0F).spawn();
        }
        if (value >= 0.99F) {
            p(to, FxAtlas.GLOW, 6).size(1.2F, 2.0F, 0.5F).color(0xE0E8FF, 0x6A80FF).alpha(1.0F, 0.0F).spawn();
            p(to, FxAtlas.CORE, 4).size(0.4F, 0.1F, 1.0F).color(0xFFFFFF).alpha(1.0F, 0.0F).spawn();
            ring(to.add(0, 0.1, 0), UP, FxAtlas.SHOCK, 0xC8D8FF, 0.3F, 2.2F, 8, 0.8F);
            sparks(to, UP, 75, 12, 0xD8E4FF, 0.5F, 0.045F, 8, 0.04);
            smoke(to, 2, 0.3F, 30, 0x5A5A66, new Vec3(0, 0.03, 0), 0.3F);
        }
        p(from, FxAtlas.GLOW, 4).size(1.5F, 2.5F, 0.6F).color(0xC8D8FF).alpha(0.7F, 0.0F).spawn();
    }

    /** TEMPESTAD: la nube se forma (destello y cúmulos que se expanden). */
    private static void stormForm(Vec3 pos, float radius) {
        p(pos, FxAtlas.GLOW, 10).size(1.0F, radius * 1.6F, 0.5F).color(0xB8C8FF, 0x4A5AA0).alpha(0.8F, 0.0F).spawn();
        for (int i = 0; i < 8; i++) {
            Vec3 off = randomDir().multiply(1.0, 0.3, 1.0).scale(radius * 0.4);
            p(pos.add(off), FxAtlas.CLOUD[rnd().nextInt(FxAtlas.CLOUD.length)], 20 + rnd().nextInt(10)).soft().vel(off.scale(0.04)).drag(0.9)
                    .size(0.6F, 1.8F, 0.5F).color(0x6A7490).alpha(0.8F, 0.0F).fadeIn(0.2F).roll(rf(0, 6.28F), rf(-0.02F, 0.02F)).spawn();
        }
        ring(pos, UP, FxAtlas.SHOCK, 0xA8B8FF, 0.5F, radius * 1.2F, 10, 0.7F);
    }

    /** GUADAÑA: media luna roja enorme delante del atacante y chispas a lo largo del arco. */
    private static void sweep(Vec3 center, Vec3 dir, float radius, @Nullable Entity attacker) {
        Vec3 l = new Vec3(dir.x, 0.0, dir.z);
        l = l.lengthSqr() < 1.0E-6 ? new Vec3(0, 0, 1) : l.normalize();
        float size = radius / 1.05F;
        Vec3 c = center.add(l.scale(0.55 * size));
        // la curva del sprite (abajo en la textura) mira hacia delante
        float roll = (float) Math.atan2(l.z, -l.x);
        int main = Palette.main(Fx.GUADANA);
        int hot = Palette.hot(Fx.GUADANA);
        p(c, FxAtlas.CRESCENT, 6).mode(Mode.FIXED).axis(UP).size(size * 0.9F, size, 0.5F).color(hot, main).roll(roll, 0.0F).alpha(1.0F, 0.0F).spawn();
        p(c.add(0, 0.15, 0), FxAtlas.CRESCENT, 8).mode(Mode.FIXED).axis(UP).size(size * 0.85F, size * 1.05F, 0.5F).color(main, 0x600010).roll(roll, 0.0F)
                .alpha(0.7F, 0.0F).spawn();
        for (int i = 0; i < 14; i++) {
            double a = Math.toRadians(-72.0 + 144.0 * i / 13.0);
            Vec3 d = new Vec3(l.x * Math.cos(a) - l.z * Math.sin(a), 0.0, l.x * Math.sin(a) + l.z * Math.cos(a));
            Vec3 at = center.add(d.scale(radius * rf(0.8F, 1.0F)));
            Vec3 tangent = new Vec3(-d.z, 0.0, d.x);
            p(at, FxAtlas.SPARK, 6 + rnd().nextInt(4)).mode(Mode.VELOCITY).vel(tangent.scale(rf(0.2F, 0.4F))).drag(0.8).size(0.05F, 0.01F, 1.0F)
                    .aspect(3.0F).stretch(1.5F).color(0xFFFFFF, main).alpha(1.0F, 0.0F).spawn();
        }
        if (attacker != null && Muzzle.firstPerson(attacker)) Muzzle.swung(Fx.GUADANA, 9);
    }

    /** PICA: lanza de plasma que sale disparada hasta la punta de la estocada. */
    private static void thrust(Vec3 from, Vec3 to, float hitWall, @Nullable Entity attacker) {
        ClientLevel level = level();
        Vec3 start = attacker != null ? Muzzle.world(attacker, 1.0F) : from;
        int main = Palette.main(Fx.PICA);
        new FxBeam(level, start, to, 5).sprite(FxAtlas.BEAM).width(0.3F, 0.0F).taper(0.6F).color(0xE8F4FF, main).alpha(0.9F, 0.0F).spawn();
        new FxBeam(level, start, to, 4).sprite(FxAtlas.BEAM_THIN).width(0.08F, 0.0F).color(0xFFFFFF).alpha(1.0F, 0.0F).spawn();
        new FxBeam(level, start, to, 6).sprite(FxAtlas.BEAM_THIN).helix(0.12F, 0.8F, 0.4F, rf(0, 6.28F)).width(0.03F, 0.0F).color(0xB8DCFF, main)
                .alpha(0.9F, 0.0F).spawn();
        Vec3 d = to.subtract(start);
        Vec3 n = d.lengthSqr() < 1.0E-6 ? UP : d.normalize();
        ring(to, n, FxAtlas.RING, main, 0.1F, 0.8F, 6, 0.9F);
        p(to, FxAtlas.STAR4, 4).size(0.3F, 0.15F, 1.0F).color(0xFFFFFF, main).roll(rf(0, 6.28F), 0.0F).alpha(1.0F, 0.0F).spawn();
        if (hitWall > 0.5F) sparks(to, n.reverse(), 70, 8, Palette.spark(Fx.PICA), 0.4F, 0.04F, 7, 0.03);
        if (attacker != null && Muzzle.firstPerson(attacker)) Muzzle.swung(Fx.PICA, 8);
    }

    /** CENIZA: la criatura alcanzada se deshace en ceniza y chispas verdes. */
    private static void disintegrate(Vec3 pos, float height, @Nullable Entity target) {
        int main = Palette.main(Fx.CENIZA);
        float h = Math.max(0.5F, height);
        p(pos, FxAtlas.GLOW, 8).size(h * 0.6F, h, 0.6F).color(0xC8FFE0, main).alpha(0.9F, 0.0F).spawn();
        ring(pos, UP, FxAtlas.RING, main, h * 0.2F, h * 0.9F, 10, 0.9F);
        for (int i = 0; i < 18; i++) {
            Vec3 at = pos.add((rnd().nextFloat() - 0.5) * 0.8, (rnd().nextFloat() - 0.5) * h, (rnd().nextFloat() - 0.5) * 0.8);
            p(at, FxAtlas.DEBRIS, 20 + rnd().nextInt(20)).soft().vel(new Vec3(rf(-0.02F, 0.02F), rf(0.02F, 0.06F), rf(-0.02F, 0.02F))).drag(0.96)
                    .gravity(-0.002).size(rf(0.03F, 0.06F), 0.0F, 1.0F).color(0x202420).roll(rf(0, 6.28F), rf(-0.2F, 0.2F)).alpha(1.0F, 0.0F).spawn();
            if (i % 2 == 0) {
                p(at, FxAtlas.EMBER, 12 + rnd().nextInt(10)).vel(new Vec3(0, rf(0.01F, 0.05F), 0)).size(0.05F, 0.0F, 1.0F).color(0xD8FFE8, main)
                        .alpha(1.0F, 0.0F).spawn();
            }
        }
        if (target != null) {
            Vec3 off = new Vec3(0, h * 0.5, 0);
            p(target.position().add(off), FxAtlas.GLOW, 10).follow(target, off).size(h * 0.7F, h * 0.4F, 1.0F).color(main).alpha(0.6F, 0.0F).spawn();
        }
    }

    // ================================================================ serie PX-36…PX-50

    /** Bloque contra el que terminó un rayo (para teñir el polvo y soltar sus fragmentos), o null. */
    @Nullable
    private static BlockHitResult blockAt(Vec3 from, Vec3 to) {
        Vec3 d = to.subtract(from);
        if (d.lengthSqr() < 1.0E-6) return null;
        Vec3 n = d.normalize();
        BlockHitResult hit = level().clip(new ClipContext(to.subtract(n.scale(0.4)), to.add(n.scale(0.4)), ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, CollisionContext.empty()));
        return hit.getType() == HitResult.Type.MISS ? null : hit;
    }

    /** Color del polvo de un bloque (su color en el mapa, algo agrisado). */
    private static int dustColor(BlockState state, BlockPos pos) {
        int c = state.getMapColor(level(), pos).col;
        return c == 0 ? 0x8C8478 : Palette.mix(c, 0x9A948A, 0.3F);
    }

    /** Fragmentos con la textura del propio bloque. */
    private static void blockChips(BlockState state, Vec3 at, Vec3 n, int count, double speed) {
        if (state.isAir()) return;
        ClientLevel level = level();
        BlockParticleOption chip = new BlockParticleOption(ParticleTypes.BLOCK, state);
        for (int i = 0; i < count; i++) {
            Vec3 v = cone(n, 60).scale(speed * rf(0.5F, 1.0F));
            level.addParticle(chip, at.x, at.y, at.z, v.x, v.y, v.z);
        }
    }

    /** Fragmentos del suelo que salen despedidos en una explosión. */
    private static void groundDebris(Vec3 pos, float radius, int count) {
        BlockPos below = BlockPos.containing(pos.x, pos.y - 0.5, pos.z);
        BlockState state = level().getBlockState(below);
        if (state.isAir()) {
            below = below.below();
            state = level().getBlockState(below);
        }
        if (!state.isAir()) blockChips(state, pos.add(0, 0.2, 0), UP, count, 0.6 + radius * 0.1);
    }

    /** Fogonazo de un arma sin tirador (la torreta CENTINELA). */
    private static void turretFlash(int style, Vec3 pos, Vec3 d) {
        int main = Palette.main(style);
        p(pos, FxAtlas.STAR4, 2).size(0.2F, 0.28F, 1.0F).color(Palette.hot(style), main).roll(rf(0.0F, 6.28F), 0.0F).alpha(1.0F, 0.0F).spawn();
        p(pos, FxAtlas.FLASH_SIDE, 2).mode(Mode.AXIS).axis(d).size(0.1F).aspect(2.2F).color(0xFFFFFF, main).alpha(1.0F, 0.0F).spawn();
        p(pos, FxAtlas.GLOW, 3).size(0.4F, 0.5F, 1.0F).color(main).alpha(0.5F, 0.0F).spawn();
        if (rnd().nextFloat() < 0.4F) smoke(pos, 1, 0.08F, 16, 0xA8B0AE, d.scale(0.03).add(0, 0.01, 0), 0.05F);
    }

    /** Bala: trazadora desde la boca del cañón; al final, chispas y fragmentos del bloque o un destello en la criatura. */
    private static void bullet(int style, Vec3 from, Vec3 to, float what, @Nullable Entity shooter) {
        ClientLevel level = level();
        boolean byPlayer = shooter instanceof Player;
        Vec3 start = byPlayer ? Muzzle.world(shooter, 1.0F) : from;
        Vec3 d = to.subtract(start);
        double len = d.length();
        if (len < 0.2) return;
        Vec3 dir = d.scale(1.0 / len);
        int main = Palette.main(style);
        int hot = Palette.hot(style);
        // armas de pólvora: trazadora fina y cálida (la bala no brilla con el color del arma)
        GunFx.Profile real = GunFx.of(style);
        if (real != null) {
            main = 0xFFB050;
            hot = 0xFFF6D8;
        }
        boolean sniper = real != null ? real.vapor() : style == Fx.LINCE;
        if (!byPlayer) turretFlash(style, start, dir);
        // trazadora: un trazo que nace en la boca del cañón y llega justo al impacto
        double streak = Math.min(len, sniper ? 7.0 : 3.0);
        double rest = len - streak;
        int steps = Mth.clamp((int) Math.ceil(rest / (sniper ? 36.0 : 12.0)), 1, 40);
        double speed = rest / steps;
        float w = real != null ? (sniper ? 0.03F : 0.016F) : sniper ? 0.05F : style == Fx.GRANIZO || style == Fx.TRABUCO ? 0.022F : 0.03F;
        double v = Math.max(speed, 1.0E-3);
        // el trazo mide siempre "streak": su cola sale de la boca y su cabeza llega al impacto en el último tick
        p(start.add(dir.scale(streak)), FxAtlas.SPARK, steps + 1).mode(Mode.VELOCITY).vel(dir.scale(v)).size(w).aspect(4.0F)
                .stretch((float) (Math.max(0.0, streak - w * 4.0) / v)).color(hot, main).alpha(1.0F, 0.8F).spawn();
        if (sniper) {
            // estela de aire ionizado que tarda en desvanecerse
            new FxBeam(level, start, to, 18).sprite(FxAtlas.BEAM_THIN).width(0.02F, 0.07F).color(0xD8FFF4, main).alpha(0.35F, 0.0F).spawn();
        }
        if (what > 1.5F) {
            // criatura: destello y chispas
            Vec3 at = to.subtract(dir.scale(0.05));
            if (real != null) {
                p(at, FxAtlas.GLOW, 2).size(sniper ? 0.3F : 0.15F).color(0xFFB060).alpha(0.5F, 0.0F).spawn();
                smoke(at, 1, sniper ? 0.12F : 0.06F, 14, 0xB8B0A8, dir.scale(0.02), 0.03F);
                sparks(at, dir.reverse(), 70, sniper ? 5 : 2, 0xFFD8A0, 0.25F, 0.025F, 5, 0.04);
            } else {
                flash(at, main, hot, sniper ? 0.45F : 0.22F, 2);
                sparks(at, dir.reverse(), 70, sniper ? 8 : 3, Palette.spark(style), 0.3F, 0.03F, 6, 0.03);
            }
        } else if (what > 0.5F) {
            BlockHitResult hit = blockAt(start, to);
            Vec3 n = hit != null ? Vec3.atLowerCornerOf(hit.getDirection().getNormal()) : dir.reverse();
            Vec3 at = to.add(n.scale(0.02));
            p(at, FxAtlas.CORE, 2).size(0.12F, 0.04F, 1.0F).color(0xFFF0D0).alpha(1.0F, 0.0F).spawn();
            sparks(at, n, 65, sniper ? 8 : 3, 0xFFD8A0, 0.3F, 0.03F, 6, 0.04);
            if (hit != null) {
                BlockState state = level.getBlockState(hit.getBlockPos());
                if (!state.isAir()) {
                    boolean light = switch (style) {
                        case Fx.GRANIZO, Fx.CENTINELA, Fx.TRABUCO, Fx.HURON, Fx.BUHO, Fx.PIRANA, Fx.AZOR, Fx.JABALI, Fx.AVALANCHA, Fx.DILUVIO -> true;
                        default -> false;
                    };
                    if (!light || rnd().nextFloat() < 0.45F) {
                        smoke(at, sniper ? 3 : 1, sniper ? 0.2F : 0.1F, 20, dustColor(state, hit.getBlockPos()), n.scale(0.025), 0.05F);
                        blockChips(state, at, n, sniper ? 8 : 3, 0.7);
                    }
                    float hole = sniper ? 0.09F : style == Fx.TRABUCO ? 0.04F : 0.06F;
                    p(to.add(n.scale(0.008)), FxAtlas.HOLE, 100).soft().mode(Mode.FIXED).axis(n).size(hole).color(0xFFFFFF)
                            .roll(rf(0.0F, 6.28F), 0.0F).alpha(0.95F, 0.0F).alphaPow(5.0F).spawn();
                }
            }
        }
    }

    /** FALLA: picos de roca que brotan del suelo, polvo, fragmentos del bloque y la grieta incandescente. */
    private static void erupt(Vec3 pos, float radius) {
        int main = Palette.main(Fx.FALLA);
        boolean slam = radius > 2.0F;
        BlockPos below = BlockPos.containing(pos.x, pos.y - 0.2, pos.z);
        BlockState state = level().getBlockState(below);
        int rock = state.isAir() ? 0x8A8278 : dustColor(state, below);
        int spikes = slam ? 7 : 3;
        for (int i = 0; i < spikes; i++) {
            double a = rnd().nextDouble() * Math.PI * 2.0;
            double r = Math.sqrt(rnd().nextDouble()) * radius * 0.55;
            Vec3 at = pos.add(Math.cos(a) * r, -0.1, Math.sin(a) * r);
            float sz = rf(0.18F, 0.3F) * (slam ? 1.3F : 1.0F);
            p(at, FxAtlas.SPIKE, 14 + rnd().nextInt(6)).soft().mode(Mode.AXIS).axis(cone(UP, 22)).size(sz * 0.3F, sz, 0.25F).aspect(2.2F)
                    .color(Palette.mix(rock, 0xFFFFFF, 0.15F), Palette.mix(rock, 0x202020, 0.35F)).alpha(1.0F, 0.0F).alphaPow(3.0F).spawn();
        }
        smoke(pos.add(0, 0.2, 0), slam ? 6 : 2, slam ? 0.4F : 0.25F, 26, Palette.mix(rock, 0xB8B0A4, 0.4F), new Vec3(0, 0.03, 0), radius * 0.4F);
        if (!state.isAir()) blockChips(state, pos.add(0, 0.1, 0), UP, slam ? 14 : 5, 0.9);
        // la grieta brilla en el suelo un rato
        p(pos.add(0, 0.03, 0), FxAtlas.CRACK, 36).mode(Mode.FIXED).axis(UP).size(radius * 0.75F).color(0xFFE0A0, main)
                .roll(rf(0.0F, 6.28F), 0.0F).alpha(0.9F, 0.0F).alphaPow(1.5F).spawn();
        p(pos.add(0, 0.05, 0), FxAtlas.GLOW, 10).mode(Mode.FIXED).axis(UP).size(radius * 0.6F, radius * 0.9F, 0.5F).color(main).alpha(0.6F, 0.0F).spawn();
        sparks(pos, UP, 40, slam ? 10 : 3, 0xFFD080, 0.35F, 0.04F, 8, 0.04);
        if (slam) {
            ring(pos.add(0, 0.08, 0), UP, FxAtlas.SHOCK, 0xFFE0B0, 0.4F, radius * 1.6F, 9, 0.8F);
            embers(pos, 8, main, 0.15F, 18);
        }
    }

    /** METEORO: diana roja en el suelo que se cierra hasta el impacto, columna de luz y el láser del designador. */
    private static void mark(Vec3 pos, float ticks, @Nullable Entity shooter) {
        int life = Math.max(4, Math.round(ticks));
        int main = Palette.main(Fx.METEORO);
        float r = (float) Orb.METEOR_RADIUS;
        Vec3 at = pos.add(0, 0.04, 0);
        p(at, FxAtlas.RET_CIRCLE, life + 4).mode(Mode.FIXED).axis(UP).size(r).color(main).roll(0.0F, 0.05F).alpha(0.9F, 0.4F).spawn();
        p(at.add(0, 0.01, 0), FxAtlas.RING, life).mode(Mode.FIXED).axis(UP).size(r * 1.15F, r * 0.15F, 1.0F).color(0xFF8060, main).alpha(0.5F, 1.0F).spawn();
        p(at, FxAtlas.GLOW, life + 4).mode(Mode.FIXED).axis(UP).size(r * 0.5F, r * 1.1F, 1.0F).color(main).alpha(0.25F, 0.7F).spawn();
        p(at.add(0, 0.02, 0), FxAtlas.LOCK, life).mode(Mode.FIXED).axis(UP).size(1.2F, 0.8F, 1.0F).color(0xFF5A3A).roll(rf(0.0F, 6.28F), -0.08F)
                .alpha(1.0F, 0.6F).spawn();
        new FxBeam(level(), at, at.add(0, 22, 0), life).sprite(FxAtlas.BEAM).width(0.35F, 0.1F).color(0xFF8060, main).alpha(0.45F, 0.15F).spawn();
        if (shooter != null) {
            Vec3 start = Muzzle.world(shooter, 1.0F);
            new FxBeam(level(), start, at, 6).anchor(shooter).sprite(FxAtlas.BEAM_THIN).width(0.04F, 0.0F).color(0xFFD0C0, main).alpha(1.0F, 0.0F).spawn();
            p(at, FxAtlas.STAR4, 6).size(0.4F, 0.1F, 1.0F).color(0xFFFFFF, main).alpha(1.0F, 0.0F).spawn();
        }
    }

    /** METEORO: impacto con bola de fuego, onda de choque, roca fundida, cráter incandescente y columna de humo. */
    private static void meteorImpact(Vec3 pos, float radius) {
        int main = Palette.main(Fx.METEORO);
        float k = radius / 3.0F;
        p(pos, FxAtlas.GLOW, 8).size(radius * 1.6F, radius * 2.8F, 0.5F).color(0xFFF0C0, 0xFF4010).alpha(1.0F, 0.0F).spawn();
        p(pos, FxAtlas.CORE, 6).size(radius * 0.5F, radius * 0.1F, 1.0F).color(0xFFFFFF).alpha(1.0F, 0.0F).spawn();
        p(pos, FxAtlas.LENS, 7).size(radius * 0.9F, radius * 0.3F, 1.0F).aspect(8.0F).color(0xFFB080).alpha(0.9F, 0.0F).spawn();
        for (int i = 0; i < 16; i++) {
            Vec3 at = pos.add(randomDir().scale(radius * 0.35 * rnd().nextFloat())).add(0, radius * 0.15, 0);
            p(at, FxAtlas.FIRE[0], 16 + rnd().nextInt(10)).frames(FxAtlas.FIRE, false).vel(randomDir().scale(0.05 * k).add(0, 0.05, 0)).drag(0.9)
                    .size(radius * rf(0.25F, 0.4F), radius * rf(0.55F, 0.8F), 0.4F).color(0xFFF4D0, 0xFF3010).roll(rf(0.0F, 6.28F), rf(-0.05F, 0.05F))
                    .alpha(1.0F, 0.0F).alphaPow(1.6F).spawn();
        }
        ring(pos.add(0, 0.1, 0), UP, FxAtlas.SHOCK, 0xFFE0B0, radius * 0.2F, radius * 2.2F, 10, 0.9F);
        ring(pos.add(0, 0.12, 0), UP, FxAtlas.RING_NOISE, main, radius * 0.3F, radius * 1.8F, 14, 0.7F);
        p(pos, FxAtlas.SHOCK, 8).size(radius * 0.2F, radius * 1.8F, 0.5F).color(0xFFF0D0).alpha(0.6F, 0.0F).spawn();
        sparks(pos, UP, 180.0, 30, 0xFFB050, 0.9F * k, 0.07F, 14, 0.03);
        for (int i = 0; i < 26; i++) {
            p(pos, FxAtlas.DEBRIS, 30 + rnd().nextInt(20)).soft().vel(cone(UP, 70).scale(rf(0.3F, 0.7F) * k)).gravity(0.05).drag(0.97)
                    .size(rf(0.08F, 0.16F)).color(0x302824).roll(rf(0.0F, 6.28F), rf(-0.4F, 0.4F)).alpha(1.0F, 0.6F).spawn();
        }
        groundDebris(pos, radius, 22);
        embers(pos, 30, 0xFF5010, 0.35F * k, 40);
        // cráter incandescente
        p(pos.add(0, 0.05, 0), FxAtlas.GLOW, 60).mode(Mode.FIXED).axis(UP).size(radius * 0.7F, radius * 0.5F, 1.0F).color(0xFF5A10).alpha(0.8F, 0.0F).spawn();
        for (int i = 0; i < 3; i++) {
            p(pos.add(0, 0.04 + i * 0.005, 0), FxAtlas.CRACK, 70 + i * 10).mode(Mode.FIXED).axis(UP).size(radius * rf(0.7F, 0.95F)).color(0xFFD080, main)
                    .roll(rf(0.0F, 6.28F), 0.0F).alpha(1.0F, 0.0F).alphaPow(1.4F).spawn();
        }
        // columna de humo que sube
        smoke(pos.add(0, radius * 0.3, 0), 18, radius * 0.4F, 80, 0x4A423C, new Vec3(0, 0.06, 0), radius * 0.5F);
        smoke(pos.add(0, radius * 0.8, 0), 8, radius * 0.5F, 90, 0x2E2824, new Vec3(0, 0.08, 0), radius * 0.4F);
    }

    /** PULSAR: estallido eléctrico magenta con arcos en todas direcciones. */
    private static void electricBurst(Vec3 pos, float radius) {
        int main = Palette.main(Fx.PULSAR);
        p(pos, FxAtlas.GLOW, 8).size(radius * 1.2F, radius * 2.0F, 0.5F).color(0xFFE8FA, main).alpha(1.0F, 0.0F).spawn();
        p(pos, FxAtlas.CORE, 6).size(radius * 0.4F, radius * 0.1F, 1.0F).color(0xFFFFFF).alpha(1.0F, 0.0F).spawn();
        p(pos, FxAtlas.LENS, 6).size(radius * 0.8F, radius * 0.25F, 1.0F).aspect(7.0F).color(main).alpha(0.8F, 0.0F).spawn();
        ring(pos, UP, FxAtlas.SHOCK, 0xFFD0F6, radius * 0.2F, radius * 1.7F, 9, 0.9F);
        ring(pos, randomDir(), FxAtlas.RING, main, radius * 0.2F, radius * 1.6F, 11, 0.8F);
        ring(pos, randomDir(), FxAtlas.RING_NOISE, 0xFFA0F0, radius * 0.2F, radius * 1.3F, 12, 0.7F);
        for (int i = 0; i < 8; i++) {
            Vec3 end = pos.add(randomDir().scale(radius * rf(0.7F, 1.2F)));
            new FxBeam(level(), pos, end, 4).sprite(FxAtlas.BEAM_THIN).jagged(0.3F, 1, rnd().nextLong()).taper(0.3F).width(0.07F, 0.02F)
                    .color(0xFFFFFF, main).alpha(1.0F, 0.0F).spawn();
        }
        for (int i = 0; i < 12; i++) {
            p(pos.add(randomDir().scale(radius * 0.6 * rnd().nextFloat())), FxAtlas.GLINT, 8 + rnd().nextInt(10)).size(0.14F, 0.0F, 1.0F)
                    .color(0xFFFFFF, main).roll(rf(0.0F, 6.28F), 0.0F).alpha(1.0F, 0.0F).spawn();
        }
        sparks(pos, UP, 180.0, 24, 0xFFC8F4, 0.7F, 0.05F, 10, 0.02);
        smoke(pos.add(0, radius * 0.2, 0), 4, radius * 0.25F, 40, 0x6A5470, new Vec3(0, 0.03, 0), radius * 0.4F);
    }

    /** MANGUAL: la bola describe un círculo de energía violeta alrededor del atacante. */
    private static void spin(int style, Vec3 center, float radius, @Nullable Entity attacker) {
        int main = Palette.main(style);
        int hot = Palette.hot(style);
        p(center, FxAtlas.SAW_BLUR, 6).mode(Mode.FIXED).axis(UP).size(radius * 0.95F, radius * 1.05F, 0.5F).color(hot, main).roll(rf(0.0F, 6.28F), 0.9F)
                .alpha(0.9F, 0.0F).spawn();
        p(center, FxAtlas.RING, 7).mode(Mode.FIXED).axis(UP).size(radius * 0.7F, radius * 1.1F, 0.5F).color(main).alpha(0.8F, 0.0F).spawn();
        for (int i = 0; i < 16; i++) {
            double a = Math.PI * 2.0 * i / 16.0;
            Vec3 d = new Vec3(Math.cos(a), 0.0, Math.sin(a));
            Vec3 at = center.add(d.scale(radius * rf(0.85F, 1.0F))).add(0, rf(-0.15F, 0.15F), 0);
            Vec3 tangent = new Vec3(-d.z, 0.0, d.x);
            p(at, FxAtlas.SPARK, 5 + rnd().nextInt(4)).mode(Mode.VELOCITY).vel(tangent.scale(rf(0.25F, 0.45F))).drag(0.8).size(0.05F, 0.01F, 1.0F)
                    .aspect(3.0F).stretch(1.5F).color(0xFFFFFF, main).alpha(1.0F, 0.0F).spawn();
        }
        if (attacker != null && Muzzle.firstPerson(attacker)) Muzzle.swung(style, 6);
    }

    /** ARIETE: onda de choque en cono desde el puño. */
    private static void punch(Vec3 from, Vec3 to, float hitWall, @Nullable Entity attacker) {
        Vec3 start = attacker != null ? Muzzle.world(attacker, 1.0F) : from;
        Vec3 d = to.subtract(start);
        double len = d.length();
        if (len < 0.3) return;
        Vec3 n = d.scale(1.0 / len);
        int main = Palette.main(Fx.ARIETE);
        flash(start.add(n.scale(0.3)), main, 0xFFFFFF, 0.5F, 3);
        for (int i = 0; i < 4; i++) {
            double t = (i + 1) / 4.0;
            Vec3 at = start.add(n.scale(len * t));
            float r = (float) (0.35 + Math.tan(Math.toRadians(30.0)) * len * t);
            ring(at, n, FxAtlas.SHOCK, Palette.mix(0xFFFFFF, main, (float) t), r * 0.4F, r, 5 + i, 0.75F - i * 0.12F);
        }
        p(start, FxAtlas.FLASH_SIDE, 4).mode(Mode.AXIS).axis(n).size(0.45F).aspect(Math.max(1.0F, (float) len * 0.9F)).color(0xE8FAFF, main)
                .alpha(0.7F, 0.0F).spawn();
        for (int i = 0; i < 10; i++) {
            Vec3 v = cone(n, 28).scale(rf(0.5F, 0.9F));
            p(start.add(n.scale(0.4)), FxAtlas.SPARK, 6).mode(Mode.VELOCITY).vel(v).drag(0.75).size(0.04F, 0.01F, 1.0F).aspect(3.0F).stretch(1.4F)
                    .color(0xFFFFFF, main).alpha(0.9F, 0.0F).spawn();
        }
        if (hitWall > 0.5F) {
            sparks(to, n.reverse(), 70, 10, 0xD8F8FF, 0.45F, 0.04F, 8, 0.04);
            BlockHitResult hit = blockAt(start, to);
            if (hit != null) {
                BlockState state = level().getBlockState(hit.getBlockPos());
                blockChips(state, to, n.reverse(), 8, 0.8);
                if (!state.isAir()) smoke(to, 3, 0.25F, 24, dustColor(state, hit.getBlockPos()), n.scale(-0.03), 0.2F);
            }
        }
        if (attacker != null && Muzzle.firstPerson(attacker)) Muzzle.swung(Fx.ARIETE, 8);
    }

    /** MAREA: chorro de agua a presión con gotas, espuma y salpicadura donde choca. */
    private static void water(Vec3 from, Vec3 to, float range, @Nullable Entity shooter) {
        ClientLevel level = level();
        if (shooter != null && Muzzle.firstPerson(shooter)) Muzzle.fired(Fx.MAREA, 0.04F);
        Vec3 start = shooter != null ? Muzzle.world(shooter, 1.0F) : from;
        Vec3 d = to.subtract(start);
        double len = Math.max(0.5, d.length());
        Vec3 dir = d.scale(1.0 / len);
        int main = Palette.main(Fx.MAREA);
        new FxBeam(level, start, to, 3).anchor(shooter).sprite(FxAtlas.BEAM_NOISE).tile(1.2F, -1.5F).width(0.1F, 0.2F).color(0xC8ECFF, main)
                .alpha(0.55F, 0.25F).spawn();
        new FxBeam(level, start, to, 3).anchor(shooter).sprite(FxAtlas.BEAM).width(0.07F, 0.11F).color(0xE8F8FF, 0x6AC8FF).alpha(0.5F, 0.2F).spawn();
        for (int i = 0; i < 6; i++) {
            double t = rnd().nextDouble();
            Vec3 at = start.add(d.scale(t)).add(randomDir().scale(0.06 + 0.12 * t));
            p(at, FxAtlas.DROPLET, 8 + rnd().nextInt(8)).soft().vel(cone(dir, 6).scale(rf(0.5F, 0.9F))).gravity(0.03).drag(0.95)
                    .size(rf(0.03F, 0.06F), 0.02F, 1.0F).color(0xF0FAFF, 0xA8DCFF).alpha(0.9F, 0.0F).spawn();
        }
        if (rnd().nextFloat() < 0.5F) smoke(start.add(d.scale(rnd().nextDouble())), 1, 0.15F, 16, 0xE8F6FF, dir.scale(0.05), 0.1F);
        if (len < range - 0.5) {
            Vec3 n = dir.reverse();
            for (int i = 0; i < 8; i++) {
                p(to.add(n.scale(0.1)), FxAtlas.DROPLET, 10 + rnd().nextInt(10)).soft().vel(cone(n, 70).scale(rf(0.12F, 0.3F))).gravity(0.04).drag(0.95)
                        .size(rf(0.04F, 0.08F), 0.02F, 1.0F).color(0xF0FAFF, 0xA8DCFF).alpha(0.95F, 0.0F).spawn();
            }
            smoke(to.add(n.scale(0.2)), 2, 0.3F, 22, 0xE0F2FF, n.scale(0.03).add(0, 0.02, 0), 0.2F);
            p(to.add(n.scale(0.05)), FxAtlas.RING_NOISE, 5).mode(Mode.FIXED).axis(n).size(0.2F, 0.7F, 0.5F).color(0xE8F8FF).alpha(0.6F, 0.0F).spawn();
        }
        p(start, FxAtlas.GLOW, 2).size(0.18F).color(0x8CD0FF).alpha(0.6F, 0.0F).spawn();
    }

    /** CENTINELA: la torreta se despliega (valor 1) o se pliega y desaparece (valor 0). */
    private static void deploy(Vec3 pos, float value) {
        int main = Palette.main(Fx.CENTINELA);
        if (value > 0.5F) {
            flash(pos.add(0, 0.6, 0), main, 0xFFFFFF, 0.6F, 5);
            ring(pos.add(0, 0.05, 0), UP, FxAtlas.RING, main, 0.2F, 1.8F, 10, 0.9F);
            ring(pos.add(0, 0.06, 0), UP, FxAtlas.HEX, main, 1.2F, 1.4F, 14, 0.5F);
            sparks(pos.add(0, 0.3, 0), UP, 80, 12, Palette.spark(Fx.CENTINELA), 0.35F, 0.04F, 8, 0.04);
            smoke(pos.add(0, 0.2, 0), 3, 0.3F, 26, 0xB8C0C0, new Vec3(0, 0.02, 0), 0.5F);
        } else {
            flash(pos.add(0, 0.7, 0), main, 0xFFFFFF, 0.5F, 4);
            ring(pos.add(0, 0.05, 0), UP, FxAtlas.RING, main, 1.6F, 0.1F, 8, 0.8F);
            for (int i = 0; i < 12; i++) {
                Vec3 off = randomDir().scale(1.0).add(0, 0.6, 0);
                p(pos.add(off), FxAtlas.SPARK, 6).mode(Mode.VELOCITY).vel(off.scale(-0.15)).size(0.04F, 0.01F, 1.0F).aspect(3.0F).stretch(1.4F)
                        .color(0xFFFFFF, main).alpha(1.0F, 0.0F).spawn();
            }
            smoke(pos.add(0, 0.5, 0), 3, 0.25F, 24, 0xB8C0C0, new Vec3(0, 0.02, 0), 0.4F);
        }
    }

    // ================================================================ estelas de los proyectiles (cada tick en el cliente)

    public static void projectileTick(PXProjectile e) {
        if (level() == null) return;
        RandomSource r = rnd();
        Vec3 pos = e.position();
        Vec3 back = e.heading().reverse();
        if (e instanceof SpecBolt spec) {
            Fx7.boltTick(spec);
        } else if (e instanceof PlasmaBolt bolt) {
            int c = bolt.isHot() ? 0xFF7A20 : 0x9CFF3A;
            p(pos.add(back.scale(0.3)), FxAtlas.EMBER, 6 + r.nextInt(4)).vel(randomDir().scale(0.01)).size(0.05F, 0.0F, 1.0F).color(0xFFFFFF, c)
                    .alpha(0.9F, 0.0F).spawn();
            if (bolt.isHot() && r.nextFloat() < 0.5F) embers(pos, 1, 0xFF5010, 0.03F, 10);
        } else if (e instanceof EnergyBolt bolt) {
            switch (bolt.style()) {
                case EnergyBolt.FROST -> {
                    if (r.nextFloat() < 0.6F) {
                        p(pos.add(back.scale(0.4)), FxAtlas.SNOW, 10 + r.nextInt(6)).vel(randomDir().scale(0.01)).size(0.06F, 0.0F, 1.0F)
                                .color(0xFFFFFF, 0x9FEFFF).roll(r.nextFloat() * 6.28F, 0.2F).alpha(1.0F, 0.0F).spawn();
                    }
                    if (r.nextFloat() < 0.3F) smoke(pos, 1, 0.06F, 12, 0xE8FAFF, Vec3.ZERO, 0.02F);
                }
                case EnergyBolt.NEEDLE -> {
                    if (r.nextFloat() < 0.4F) {
                        p(pos, FxAtlas.GLINT, 5).size(0.07F, 0.0F, 1.0F).color(0xFFB0E0).roll(r.nextFloat() * 6.28F, 0.0F).alpha(1.0F, 0.0F).spawn();
                    }
                }
                case EnergyBolt.SLUG -> {
                    p(pos.add(back.scale(0.3)), FxAtlas.EMBER, 8 + r.nextInt(4)).vel(randomDir().scale(0.015)).size(0.06F, 0.0F, 1.0F).color(0xFFFFFF, 0xFFC83A)
                            .alpha(0.9F, 0.0F).spawn();
                    if (r.nextFloat() < 0.3F) embers(pos, 1, 0xFFA020, 0.03F, 10);
                }
                case EnergyBolt.DRONE, EnergyBolt.BULLET -> {
                    if (r.nextFloat() < 0.5F) {
                        p(pos.add(back.scale(0.3)), FxAtlas.EMBER, 5).size(0.04F, 0.0F, 1.0F).color(0xFFFFFF, Palette.main(bolt.style() == EnergyBolt.DRONE ? Fx.HALCON : Fx.BASTION))
                                .alpha(0.9F, 0.0F).spawn();
                    }
                }
                default -> {}
            }
        } else if (e instanceof MicroRocket) {
            Vec3 tail = pos.add(back.scale(0.35));
            smoke(tail, 1, 0.12F, 30, 0xB4B0AA, back.scale(0.02).add(0, 0.008, 0), 0.05F);
            p(tail, FxAtlas.GLOW, 3).size(0.3F, 0.15F, 1.0F).color(0xFF8A30).alpha(0.8F, 0.0F).spawn();
            if (r.nextFloat() < 0.5F) embers(tail, 1, 0xFF7020, 0.04F, 8);
        } else if (e instanceof Grenade g) {
            boolean small = g.kind() == Grenade.BOMBLET;
            p(pos, FxAtlas.EMBER, 6).vel(randomDir().scale(0.02)).size(small ? 0.03F : 0.05F, 0.0F, 1.0F).color(0xFFFFFF, 0xFFA040).alpha(1.0F, 0.0F).spawn();
            if (!small && r.nextFloat() < 0.35F) smoke(pos, 1, 0.08F, 18, 0x9A9890, new Vec3(0, 0.01, 0), 0.02F);
        } else if (e instanceof SawDisc) {
            if (r.nextFloat() < 0.3F) sparks(pos, UP, 180.0, 1, 0x9FFFD6, 0.15F, 0.03F, 5, 0.03);
        } else if (e instanceof Singularity s) {
            if (s.isOpen()) {
                // la materia cae en espiral hacia el centro
                for (int i = 0; i < 3; i++) {
                    double ang = r.nextDouble() * Math.PI * 2.0;
                    double rad = Singularity.PULL_RADIUS * (0.6 + 0.4 * r.nextDouble());
                    Vec3 at = pos.add(Math.cos(ang) * rad, (r.nextDouble() - 0.5) * 2.0, Math.sin(ang) * rad);
                    Vec3 to = pos.subtract(at);
                    Vec3 swirl = to.cross(UP).normalize().scale(0.25);
                    p(at, FxAtlas.SPARK, 14).mode(Mode.VELOCITY).vel(to.scale(0.07).add(swirl)).drag(0.98).size(0.05F, 0.01F, 1.0F).aspect(3.0F)
                            .stretch(2.0F).color(0xF0E0FF, 0xA45CFF).alpha(0.0F, 1.0F).fadeIn(0.3F).spawn();
                }
            } else if (r.nextFloat() < 0.7F) {
                p(pos.add(randomDir().scale(0.3)), FxAtlas.GLINT, 8).vel(back.scale(0.05)).size(0.08F, 0.0F, 1.0F).color(0xE8D8FF, 0xA45CFF)
                        .roll(r.nextFloat() * 6.28F, 0.1F).alpha(1.0F, 0.0F).spawn();
            }
        } else if (e instanceof Orb o) {
            switch (o.kind()) {
                case Orb.SHELL -> {
                    smoke(pos.add(back.scale(0.3)), 1, 0.14F, 34, 0xA8A49E, back.scale(0.01).add(0, 0.006, 0), 0.04F);
                    if (r.nextFloat() < 0.5F) embers(pos, 1, 0xFF7020, 0.03F, 8);
                }
                case Orb.SOLAR -> {
                    embers(pos, 2, 0xFFB020, 0.06F, 14);
                    if (r.nextFloat() < 0.4F) {
                        p(pos.add(randomDir().scale(0.4)), FxAtlas.FLAME[0], 10).frames(FxAtlas.FLAME, true).size(0.15F, 0.3F, 1.0F).vel(randomDir().scale(0.02))
                                .color(0xFFF0B0, 0xFF6010).alpha(0.8F, 0.0F).spawn();
                    }
                }
                case Orb.POD -> {
                    if (r.nextFloat() < 0.6F) {
                        p(pos.add(back.scale(0.2)), FxAtlas.EMBER, 8).vel(randomDir().scale(0.02)).size(0.04F, 0.0F, 1.0F).color(0xFFFFFF, 0xFFB800)
                                .alpha(0.9F, 0.0F).spawn();
                    }
                }
                case Orb.ACID -> {
                    if (r.nextFloat() < 0.5F) {
                        p(pos, FxAtlas.EMBER, 10).vel(new Vec3(0, -0.02, 0)).gravity(0.01).size(0.035F, 0.015F, 1.0F).color(0xE0FF90, 0x8CFF10)
                                .alpha(0.9F, 0.0F).spawn();
                    }
                }
                case Orb.CRYSTAL -> {
                    if (r.nextFloat() < 0.6F) {
                        p(pos.add(randomDir().scale(0.15)), FxAtlas.GLINT, 8).vel(back.scale(0.04)).size(0.07F, 0.0F, 1.0F).color(0xF0E0FF, 0xC07CFF)
                                .roll(r.nextFloat() * 6.28F, 0.1F).alpha(1.0F, 0.0F).spawn();
                    }
                }
                case Orb.ROCKET -> {
                    Vec3 tail = pos.add(back.scale(0.45));
                    smoke(tail, 2, 0.16F, 40, 0xC8C4BE, back.scale(0.03).add(0, 0.01, 0), 0.06F);
                    p(tail, FxAtlas.FLAME[0], 4).frames(FxAtlas.FLAME, true).size(0.2F, 0.35F, 1.0F).vel(back.scale(0.08)).color(0xFFF0C0, 0xFF5A10)
                            .alpha(0.9F, 0.0F).spawn();
                    if (r.nextFloat() < 0.6F) embers(tail, 1, 0xFF7020, 0.05F, 10);
                }
                case Orb.METEOR -> {
                    for (int i = 0; i < 2; i++) {
                        p(pos.add(randomDir().scale(0.6)), FxAtlas.FIRE[0], 12 + r.nextInt(6)).frames(FxAtlas.FIRE, false)
                                .vel(back.scale(0.1).add(randomDir().scale(0.03))).drag(0.9).size(0.5F, 1.1F, 0.5F).color(0xFFF0C0, 0xFF3A10)
                                .roll(r.nextFloat() * 6.28F, 0.05F).alpha(1.0F, 0.0F).alphaPow(1.5F).spawn();
                    }
                    smoke(pos.add(back.scale(0.8)), 2, 0.5F, 50, 0x3A3430, back.scale(0.02), 0.4F);
                    embers(pos, 3, 0xFF6020, 0.12F, 20);
                }
                case Orb.GRENADE40 -> {
                    // la granada no brilla: solo un hilo de humo del trazador
                    if (r.nextFloat() < 0.6F) smoke(pos.add(back.scale(0.15)), 1, 0.05F, 18, 0xB8B4AE, back.scale(0.01), 0.01F);
                }
                case Orb.PULSAR -> {
                    if (r.nextFloat() < 0.5F) {
                        Vec3 a = pos.add(randomDir().scale(0.35));
                        Vec3 b = a.add(randomDir().scale(rf(0.4F, 0.9F)));
                        new FxBeam(level(), a, b, 2).sprite(FxAtlas.BEAM_THIN).jagged(0.12F, 1, r.nextLong()).taper(0.3F).width(0.03F, 0.01F)
                                .color(0xFFFFFF, 0xFF40E0).alpha(1.0F, 0.0F).spawn();
                    }
                    p(pos.add(randomDir().scale(0.3)), FxAtlas.EMBER, 10).vel(back.scale(0.02)).size(0.05F, 0.0F, 1.0F).color(0xFFE0FA, 0xFF40E0)
                            .alpha(0.9F, 0.0F).spawn();
                }
                default -> {}
            }
        } else if (e instanceof Sticky s) {
            if (s.isStuck()) {
                if (s.kind() == Sticky.HARPOON) {
                    if (r.nextFloat() < 0.5F) sparks(pos, UP, 180.0, 1, 0xA8F0FF, 0.12F, 0.03F, 5, 0.02);
                    if (s.stuckTicks() % 5 == 0) ring(pos, randomDir(), FxAtlas.RING, 0x39D6FF, 0.1F, 0.6F, 5, 0.7F);
                }
            } else if (s.kind() == Sticky.HARPOON) {
                if (r.nextFloat() < 0.4F) smoke(pos.add(back.scale(0.6)), 1, 0.06F, 14, 0xE0F0F4, Vec3.ZERO, 0.02F);
            } else if (r.nextFloat() < 0.3F) {
                smoke(pos, 1, 0.06F, 14, 0x8A8480, Vec3.ZERO, 0.02F);
            }
        } else if (e instanceof Piercer pc) {
            if (pc.kind() == Piercer.CHAKRAM) {
                if (r.nextFloat() < 0.7F) {
                    double ang = r.nextDouble() * Math.PI * 2.0;
                    Vec3 at = pos.add(Math.cos(ang) * 0.45, 0.15, Math.sin(ang) * 0.45);
                    p(at, FxAtlas.GLINT, 6).size(0.08F, 0.0F, 1.0F).color(0xFFF4C8, 0xFFD84A).roll(r.nextFloat() * 6.28F, 0.0F).alpha(1.0F, 0.0F).spawn();
                }
            } else if (pc.kind() == Piercer.RING) {
                Vec3 h = pc.heading();
                Vec3 u = h.cross(UP);
                u = u.lengthSqr() < 1.0E-6 ? new Vec3(1, 0, 0) : u.normalize();
                Vec3 v = u.cross(h).normalize();
                for (int i = 0; i < 2; i++) {
                    double ang = r.nextDouble() * Math.PI * 2.0;
                    Vec3 at = pos.add(u.scale(Math.cos(ang) * Piercer.RING_RADIUS)).add(v.scale(Math.sin(ang) * Piercer.RING_RADIUS));
                    p(at, FxAtlas.EMBER, 10 + r.nextInt(8)).vel(back.scale(0.03).add(0, 0.01, 0)).size(0.045F, 0.0F, 1.0F).color(0xD8FFE8, 0x3CFF9E)
                            .alpha(1.0F, 0.0F).spawn();
                }
                if (r.nextFloat() < 0.3F) ash(pos, 1, 0.8F);
            } else if (r.nextFloat() < 0.6F) {
                p(pos.add(back.scale(0.3)), FxAtlas.GLINT, 6).size(0.07F, 0.0F, 1.0F).color(0xFFF4C8).roll(r.nextFloat() * 6.28F, 0.0F).alpha(1.0F, 0.0F).spawn();
            }
        } else if (e instanceof SlashWave) {
            if (r.nextFloat() < 0.6F) {
                p(pos.add(randomDir().scale(0.6)), FxAtlas.GLINT, 8).vel(back.scale(0.04)).size(0.1F, 0.0F, 1.0F).color(0xFFE0F0, 0xFF2E7E)
                        .roll(r.nextFloat() * 6.28F, 0.0F).alpha(1.0F, 0.0F).spawn();
            }
        }
    }

    /** Partículas de ambiente de las zonas (cada tick en el cliente). */
    public static void fieldTick(Field f) {
        if (level() == null) return;
        RandomSource r = rnd();
        Vec3 pos = f.position();
        switch (f.type()) {
            case Field.ACID_POOL -> {
                float rad = f.size();
                if (r.nextFloat() < 0.7F) {
                    double a = r.nextDouble() * Math.PI * 2.0;
                    double d = Math.sqrt(r.nextDouble()) * rad;
                    p(pos.add(Math.cos(a) * d, 0.05, Math.sin(a) * d), FxAtlas.BUBBLE, 10 + r.nextInt(10)).vel(new Vec3(0, 0.01, 0))
                            .size(0.03F, 0.09F, 1.0F).color(0xD8FF80).alpha(0.9F, 0.0F).spawn();
                }
                if (r.nextFloat() < 0.15F) {
                    smoke(pos.add((r.nextFloat() - 0.5) * rad, 0.1, (r.nextFloat() - 0.5) * rad), 1, 0.2F, 30, 0x9ACC40, new Vec3(0, 0.02, 0), 0.1F);
                }
            }
            case Field.STORM -> {
                for (int i = 0; i < 2; i++) {
                    Vec3 at = pos.add((r.nextFloat() - 0.5) * 6.0, -0.8, (r.nextFloat() - 0.5) * 6.0);
                    p(at, FxAtlas.SPARK, 14).mode(Mode.VELOCITY).vel(new Vec3(0, -0.6, 0)).size(0.02F, 0.015F, 1.0F).aspect(3.0F).stretch(1.5F)
                            .color(0xA8B8D8).alpha(0.5F, 0.3F).spawn();
                }
            }
            case Field.TORNADO -> {
                for (int i = 0; i < 2; i++) {
                    double a = r.nextDouble() * Math.PI * 2.0;
                    double h = r.nextDouble() * 4.5;
                    double rad = 0.6 + h * 0.35;
                    Vec3 at = pos.add(Math.cos(a) * rad, h, Math.sin(a) * rad);
                    Vec3 tangent = new Vec3(-Math.sin(a), 0.12, Math.cos(a)).scale(0.35);
                    p(at, FxAtlas.SMOKE[r.nextInt(FxAtlas.SMOKE.length)], 16 + r.nextInt(10)).soft().vel(tangent).drag(0.92).size(0.25F, 0.6F, 0.6F)
                            .color(0xB8C8C0).alpha(0.45F, 0.0F).roll(r.nextFloat() * 6.28F, 0.05F).spawn();
                }
                if (r.nextFloat() < 0.6F) {
                    double a = r.nextDouble() * Math.PI * 2.0;
                    double rad = 1.0 + r.nextDouble() * 1.2;
                    p(pos.add(Math.cos(a) * rad, r.nextDouble() * 3.0, Math.sin(a) * rad), FxAtlas.DEBRIS, 20).soft()
                            .vel(new Vec3(-Math.sin(a) * 0.4, 0.08, Math.cos(a) * 0.4)).drag(0.95).size(0.06F).color(0x4A4038)
                            .roll(r.nextFloat() * 6.28F, 0.4F).alpha(1.0F, 0.5F).spawn();
                }
                if (r.nextFloat() < 0.5F) {
                    double a = r.nextDouble() * Math.PI * 2.0;
                    Vec3 at = pos.add(Math.cos(a) * 2.0, 0.5 + r.nextDouble() * 3.5, Math.sin(a) * 2.0);
                    p(at, FxAtlas.SPARK, 8).mode(Mode.VELOCITY).vel(new Vec3(-Math.sin(a) * 0.5, 0.05, Math.cos(a) * 0.5)).drag(0.9)
                            .size(0.03F, 0.01F, 1.0F).aspect(3.0F).stretch(1.6F).color(0xE8FFFA, 0x7FE8D8).alpha(0.7F, 0.0F).spawn();
                }
            }
            case Field.FISSURE -> {}
            case Field.TURRET -> {
                if (f.target() >= 0 && r.nextFloat() < 0.15F) {
                    p(f.headPos(), FxAtlas.GLINT, 5).size(0.08F, 0.0F, 1.0F).color(0xFFD0C0, 0xFF4A3A).alpha(0.8F, 0.0F).spawn();
                }
            }
            default -> {
                if (r.nextFloat() < 0.5F) {
                    p(pos.add(randomDir().scale(0.8)), FxAtlas.EMBER, 8).vel(randomDir().scale(0.03)).size(0.04F, 0.0F, 1.0F).color(0xFFFFFF, 0xFFB800)
                            .alpha(0.8F, 0.0F).spawn();
                }
            }
        }
    }

    public static void droneTick(Drone drone) {
        if (level() == null || drone.tickCount % 2 != 0) return;
        Vec3 pos = drone.position();
        for (int i = 0; i < 2; i++) {
            Vec3 at = pos.add((rnd().nextFloat() - 0.5) * 0.5, -0.1, (rnd().nextFloat() - 0.5) * 0.5);
            p(at, FxAtlas.EMBER, 6).vel(new Vec3(0, -0.06, 0)).size(0.05F, 0.0F, 1.0F).color(0xFFFFFF, 0xFFA235).alpha(0.8F, 0.0F).spawn();
        }
    }
}
