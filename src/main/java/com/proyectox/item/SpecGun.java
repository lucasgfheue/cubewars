package com.proyectox.item;

import com.proyectox.fx.Fx;
import com.proyectox.util.FX;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Armas de fuego de la serie v7 (PX-66…PX-101). Todas funcionan igual: clic izquierdo dispara, clic derecho apunta y la
 * reserva de energía se recarga sola. Cada una se describe con su {@link Spec} (modo de disparo, cadencia, reserva,
 * precisión y sonido) y una función que hace el disparo ({@link Payload}, ver {@link Arsenal7}).
 */
public class SpecGun extends GunItem {
    /** Modo de disparo. */
    public enum Mode {
        /** Un disparo por clic (y, si {@code hold} &gt; 0, uno cada {@code hold} ticks manteniendo el clic). */
        SEMI,
        /** Automática: mientras se mantiene el clic (la cadencia puede ser fraccionaria). */
        AUTO,
        /** Se mantiene el clic para cargar y dispara al soltar. */
        CHARGE,
        /** Haz continuo mientras se mantiene el clic. */
        BEAM
    }

    /** Lo que hace cada disparo. {@code power} = carga (0–1) en las armas de carga; 1 en las demás. */
    @FunctionalInterface
    public interface Payload {
        void fire(SpecGun gun, Shot shot, ItemStack stack, float power, int heldTicks);
    }

    /** Ficha técnica de un arma. */
    public static final class Spec {
        Mode mode = Mode.SEMI;
        int interval = 6;
        int hold;
        double rate;
        int charge;
        int cost = 1;
        int maxCharge = 30;
        int regenTicks = 4;
        int regenAmount = 1;
        int regenDelay = 8;
        double hip = 1.0;
        double aim = 0.3;
        double pellets;
        float zoom = 0.2F;
        float kick = 0.4F;
        boolean scoped;
        boolean flash = true;
        double forward = 0.95;
        @Nullable
        Supplier<SoundEvent> sound;
        float volume = 1.0F;
        float pitch = 1.0F;
        int soundEvery;
        @Nullable
        Supplier<SoundEvent> chargeSound;
        Payload payload = (g, s, st, p, h) -> {};

        /** Un disparo por clic: ticks mínimos entre disparos y, manteniendo el clic, ticks entre disparos (0 = solo al hacer clic). */
        public Spec semi(int interval, int hold) {
            this.mode = Mode.SEMI;
            this.interval = interval;
            this.hold = hold;
            return this;
        }

        /** Automática: disparos por segundo. */
        public Spec auto(double rate) {
            this.mode = Mode.AUTO;
            this.rate = rate;
            this.interval = Math.max(1, (int) Math.round(20.0 / rate));
            return this;
        }

        /** De carga: ticks de carga completa y ticks mínimos entre disparos. */
        public Spec charge(int ticks, int interval) {
            this.mode = Mode.CHARGE;
            this.charge = ticks;
            this.interval = interval;
            return this;
        }

        /** Haz continuo: un pulso de daño cada {@code interval} ticks. */
        public Spec beam(int interval) {
            this.mode = Mode.BEAM;
            this.interval = interval;
            this.flash = false;
            return this;
        }

        /** Reserva: capacidad, ticks por cada unidad recuperada y espera tras disparar antes de recargar. */
        public Spec ammo(int max, int regenTicks, int regenDelay) {
            this.maxCharge = max;
            this.regenTicks = regenTicks;
            this.regenDelay = regenDelay;
            return this;
        }

        public Spec regenAmount(int amount) {
            this.regenAmount = amount;
            return this;
        }

        /** Energía por disparo. */
        public Spec cost(int cost) {
            this.cost = cost;
            return this;
        }

        /** Dispersión en grados desde la cadera y apuntando. */
        public Spec spread(double hip, double aim) {
            this.hip = hip;
            this.aim = aim;
            return this;
        }

        /** Apertura del cono de perdigones (para la retícula). */
        public Spec pellets(double cone) {
            this.pellets = cone;
            return this;
        }

        public Spec zoom(float zoom) {
            this.zoom = zoom;
            return this;
        }

        public Spec kick(float kick) {
            this.kick = kick;
            return this;
        }

        public Spec scoped() {
            this.scoped = true;
            return this;
        }

        /** Sin el fogonazo genérico (el disparo dibuja el suyo). */
        public Spec noFlash() {
            this.flash = false;
            return this;
        }

        /** Distancia de la boca del cañón delante de los ojos. */
        public Spec forward(double forward) {
            this.forward = forward;
            return this;
        }

        /** Sonido del disparo; el volumen marca hasta dónde se oye (16 bloques por cada 1,0). */
        public Spec sound(Supplier<SoundEvent> sound, float volume, float pitch) {
            this.sound = sound;
            this.volume = volume;
            this.pitch = pitch;
            return this;
        }

        /** El sonido suena como mucho una vez cada tantos ticks (haces, ráfagas). */
        public Spec soundEvery(int ticks) {
            this.soundEvery = ticks;
            return this;
        }

        /** Sonido al empezar a cargar. */
        public Spec chargeSound(Supplier<SoundEvent> sound) {
            this.chargeSound = sound;
            return this;
        }

        public Spec payload(Payload payload) {
            this.payload = payload;
            return this;
        }
    }

    public static Spec spec() {
        return new Spec();
    }

    /** Momento (en ticks, con decimales) en que cada jugador puede volver a disparar un arma automática. */
    private record Clock(Item item, double next) {}

    private static final Map<UUID, Clock> CLOCK = new ConcurrentHashMap<>();

    private final Spec spec;

    public SpecGun(String key, String code, int color, int style, Properties properties, Spec spec) {
        super(key, code, color, style, properties);
        this.spec = spec;
    }

    public static void forget(Player player) {
        CLOCK.remove(player.getUUID());
    }

    public Mode mode() {
        return this.spec.mode;
    }

    /** Apertura del cono de perdigones (0 si dispara una sola bala). */
    public double pelletSpread() {
        return this.spec.pellets;
    }

    @Override
    public int interval() {
        return this.spec.interval;
    }

    @Override
    public int cost() {
        return this.spec.cost;
    }

    @Override
    public int maxCharge() {
        return this.spec.maxCharge;
    }

    @Override
    public int regenTicks() {
        return this.spec.regenTicks;
    }

    @Override
    public int regenAmount() {
        return this.spec.regenAmount;
    }

    @Override
    public int regenDelay() {
        return this.spec.regenDelay;
    }

    @Override
    public double hipSpread() {
        return this.spec.hip;
    }

    @Override
    public double aimSpread() {
        return this.spec.aim;
    }

    @Override
    public float zoom() {
        return this.spec.zoom;
    }

    @Override
    public boolean scoped() {
        return this.spec.scoped;
    }

    @Override
    public float kick() {
        return this.spec.kick;
    }

    @Override
    public int chargeTicks() {
        return this.spec.mode == Mode.CHARGE ? this.spec.charge : 0;
    }

    @Override
    protected double muzzleForward() {
        return this.spec.forward;
    }

    @Override
    protected boolean muzzleFlash() {
        return this.spec.flash;
    }

    // ------------------------------------------------------------------ disparo

    @Override
    public void triggerTick(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming) {
        if (player.isSpectator()) return;
        switch (this.spec.mode) {
            case CHARGE -> {
                if (heldTicks == 0 && this.spec.chargeSound != null && ready(player, now) && charge(stack, now) >= cost()) {
                    FX.sound(level, player, this.spec.chargeSound.get(), 0.9F, 1.0F);
                }
            }
            case SEMI -> {
                int hold = this.spec.hold;
                if (heldTicks != 0 && (hold <= 0 || heldTicks % hold != 0)) return;
                super.triggerTick(player, stack, level, now, heldTicks, aiming);
            }
            case AUTO -> automatic(player, stack, level, now, heldTicks, aiming);
            default -> super.triggerTick(player, stack, level, now, heldTicks, aiming);
        }
    }

    /** Automáticas con cadencia fraccionaria (12 o 15 disparos por segundo). */
    private void automatic(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming) {
        double step = 20.0 / Math.max(0.5, this.spec.rate);
        Clock clock = CLOCK.get(player.getUUID());
        double next = clock == null || clock.item() != this || clock.next() < now - step ? now : clock.next();
        if (next > now + 1.0E-6) return;
        Shot shot = Shot.of(player, level, aiming, aiming ? aimSpread() : hipSpread(), muzzleForward());
        int shots = 0;
        while (next <= now + 1.0E-6 && shots < 4) {
            if (!spend(player, stack, level, now, cost())) break;
            this.spec.payload.fire(this, shot, stack, 1.0F, heldTicks);
            shots++;
            next += step;
        }
        CLOCK.put(player.getUUID(), new Clock(this, next));
        if (shots == 0) return;
        playShot(level, player, heldTicks);
        if (muzzleFlash()) Fx.muzzle(level, player, style(), shot.muzzle(), shot.look());
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        this.spec.payload.fire(this, shot, stack, 1.0F, heldTicks);
        playShot(shot.level(), shot.player(), heldTicks);
    }

    @Override
    public void triggerRelease(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming) {
        if (this.spec.mode != Mode.CHARGE || player.isSpectator() || !ready(player, now)) return;
        if (!spend(player, stack, level, now, cost())) return;
        delay(player, now, interval());
        float power = Mth.clamp(heldTicks / (float) Math.max(1, this.spec.charge), 0.0F, 1.0F);
        Shot shot = Shot.of(player, level, aiming, aiming ? aimSpread() : hipSpread(), muzzleForward());
        this.spec.payload.fire(this, shot, stack, power, heldTicks);
        playShot(level, player, 0);
        if (muzzleFlash()) Fx.muzzle(level, player, style(), shot.muzzle(), shot.look());
    }

    private void playShot(ServerLevel level, ServerPlayer player, int heldTicks) {
        Supplier<SoundEvent> sound = this.spec.sound;
        if (sound == null) return;
        int every = this.spec.soundEvery;
        if (every > 0 && heldTicks % every != 0) return;
        FX.sound(level, player, sound.get(), this.spec.volume, this.spec.pitch * (0.95F + level.random.nextFloat() * 0.1F));
    }
}
