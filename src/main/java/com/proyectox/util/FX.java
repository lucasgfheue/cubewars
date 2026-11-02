package com.proyectox.util;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/** Sonidos y partículas emitidos desde el servidor (todos los jugadores cercanos los ven). */
public final class FX {
    private FX() {}

    public static void sound(Level level, Vec3 pos, SoundEvent sound, float volume, float pitch) {
        level.playSound(null, pos.x, pos.y, pos.z, sound, SoundSource.PLAYERS, volume, pitch);
    }

    public static void sound(Level level, Entity at, SoundEvent sound, float volume, float pitch) {
        level.playSound(null, at.getX(), at.getY(), at.getZ(), sound, SoundSource.PLAYERS, volume, pitch);
    }

    public static DustParticleOptions dust(int rgb, float scale) {
        float r = ((rgb >> 16) & 0xFF) / 255F;
        float g = ((rgb >> 8) & 0xFF) / 255F;
        float b = (rgb & 0xFF) / 255F;
        return new DustParticleOptions(new Vector3f(r, g, b), scale);
    }

    public static void burst(ServerLevel level, ParticleOptions particle, Vec3 at, int count, double spread, double speed) {
        level.sendParticles(particle, at.x, at.y, at.z, count, spread, spread, spread, speed);
    }

    /** Partículas a lo largo de un segmento, una cada {@code step} bloques. */
    public static void line(ServerLevel level, ParticleOptions particle, Vec3 from, Vec3 to, double step, double jitter) {
        Vec3 d = to.subtract(from);
        double len = d.length();
        if (len < 1.0E-3) return;
        int n = Math.max(1, (int) (len / step));
        for (int i = 0; i <= n; i++) {
            Vec3 p = from.add(d.scale(i / (double) n));
            level.sendParticles(particle, p.x, p.y, p.z, 1, jitter, jitter, jitter, 0.0);
        }
    }

    /** Anillo horizontal de partículas que salen hacia fuera. */
    public static void ring(ServerLevel level, ParticleOptions particle, Vec3 center, double radius, int points, double speed) {
        for (int i = 0; i < points; i++) {
            double a = Math.PI * 2 * i / points;
            double x = Math.cos(a);
            double z = Math.sin(a);
            // count = 0 → (dx, dy, dz) se usan como dirección y speed como velocidad
            level.sendParticles(particle, center.x + x * radius, center.y, center.z + z * radius, 0, x, 0.05, z, speed);
        }
    }
}
