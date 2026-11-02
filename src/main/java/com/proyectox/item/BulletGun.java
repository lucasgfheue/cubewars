package com.proyectox.item;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import com.proyectox.util.Hits;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Armas de balas (PX-36…PX-41 y la serie realista PX-51…PX-65): cada bala es un rayo instantáneo desde los ojos que
 * se detiene en la primera criatura o bloque; el cliente dibuja la trazadora, el casquillo que sale despedido y el
 * impacto con el color y los fragmentos del bloque alcanzado. Las de varios perdigones (escopetas) reparten el daño:
 * de cerca aciertan todos.
 *
 * <p>Las automáticas pueden tener una cadencia que no sea un número entero de ticks ({@link #fireRate()}): 12 o 15
 * disparos por segundo, o 40 (dos balas por tick, la ametralladora rotativa).
 */
public abstract class BulletGun extends GunItem {
    /** Momento (en ticks, con decimales) en que cada jugador puede volver a disparar un arma de cadencia fraccionaria. */
    private record Clock(Item item, double next) {}

    private static final Map<UUID, Clock> CLOCK = new ConcurrentHashMap<>();

    protected BulletGun(String key, String code, int color, int style, Properties properties) {
        super(key, code, color, style, properties);
    }

    /** Daño de cada bala (o de cada perdigón). */
    public abstract float damage();

    /** Balas por disparo. */
    public int pellets() {
        return 1;
    }

    /** Apertura (grados) del cono de perdigones, además de la dispersión del arma. */
    public double pelletSpread() {
        return 0.0;
    }

    /** Alcance de las balas en bloques. */
    public double range() {
        return 200.0;
    }

    public ResourceKey<DamageType> damageType() {
        return PXDamage.BALA;
    }

    /**
     * Semiautomáticas: ticks entre disparos si se mantiene el gatillo (cada clic dispara al momento si ya pasó
     * la cadencia). 0 = automática.
     */
    public int holdInterval() {
        return 0;
    }

    /**
     * Automáticas: disparos por segundo (puede tener decimales y pasar de 20). 0 = la cadencia la marca
     * {@link #interval()}.
     */
    public double fireRate() {
        return 0.0;
    }

    /** Si es mayor que 0, el sonido del disparo solo suena cada tantos ticks (la ráfaga de la ametralladora rotativa). */
    protected int soundEvery() {
        return 0;
    }

    protected abstract SoundEvent shotSound();

    protected float shotVolume() {
        return 1.4F;
    }

    protected float shotPitch() {
        return 1.0F;
    }

    /** Algo más tras el disparo (el sonido de la corredera o del cerrojo). */
    protected void afterShot(Shot shot, ItemStack stack) {}

    public static void forget(Player player) {
        CLOCK.remove(player.getUUID());
    }

    @Override
    public void triggerTick(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming) {
        double rate = fireRate();
        if (rate <= 0.0) {
            int hold = holdInterval();
            if (hold > 0 && heldTicks != 0 && heldTicks % hold != 0) return;
            super.triggerTick(player, stack, level, now, heldTicks, aiming);
            return;
        }
        if (player.isSpectator()) return;
        double step = 20.0 / rate;
        Clock clock = CLOCK.get(player.getUUID());
        double next = clock == null || clock.item() != this || clock.next() < now - step ? now : clock.next();
        if (next > now + 1.0E-6) return;
        Shot shot = Shot.of(player, level, aiming, aiming ? aimSpread() : hipSpread(), muzzleForward());
        int shots = 0;
        while (next <= now + 1.0E-6 && shots < 8) {
            if (!spend(player, stack, level, now, cost())) break;
            bullets(shot);
            shots++;
            next += step;
        }
        CLOCK.put(player.getUUID(), new Clock(this, next));
        if (shots == 0) return;
        int every = soundEvery();
        if (every <= 0 || heldTicks % every == 0) playShot(shot);
        afterShot(shot, stack);
        if (muzzleFlash()) Fx.muzzle(level, player, style(), shot.muzzle(), shot.look());
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        bullets(shot);
        playShot(shot);
        afterShot(shot, stack);
    }

    /** Las balas (o los perdigones) de un disparo. */
    protected void bullets(Shot shot) {
        ServerLevel level = shot.level();
        ServerPlayer player = shot.player();
        DamageSource source = PXDamage.source(level, damageType(), player, player);
        int n = Math.max(1, pellets());
        Vec3 base = shot.eyeDir();
        for (int i = 0; i < n; i++) {
            Vec3 dir = n > 1 ? Aim.spread(base, level.random, pelletSpread()) : base;
            Aim.Ray ray = Aim.pierce(level, player, shot.eye(), dir, range(), 1);
            for (Entity e : ray.hits()) Hits.hurt(e, source, damage(), player);
            float what = !ray.hits().isEmpty() ? 2.0F : ray.hitBlock() ? 1.0F : 0.0F;
            Fx.line(level, Fx.BULLET, style(), shot.muzzle(), ray.end(), what, player.getId());
        }
    }

    protected void playShot(Shot shot) {
        ServerLevel level = shot.level();
        FX.sound(level, shot.player(), shotSound(), shotVolume(), shotPitch() * (0.95F + level.random.nextFloat() * 0.1F));
    }
}
