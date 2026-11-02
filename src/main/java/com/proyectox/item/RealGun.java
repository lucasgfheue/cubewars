package com.proyectox.item;

import com.proyectox.registry.PXDamage;
import com.proyectox.util.FX;
import com.proyectox.util.Scheduler;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.ItemStack;

/**
 * Armas de fuego de la serie realista PX-51…PX-65 (revólveres, pistolas, fusiles, subfusiles, escopetas y la
 * ametralladora rotativa). Todas funcionan igual: clic izquierdo dispara, clic derecho apunta por la mira, el
 * cargador de energía se recarga solo y nunca empujan al objetivo. Cada una se describe con su {@link Spec}.
 */
public class RealGun extends BulletGun {
    /** Ficha técnica de un arma. */
    public static final class Spec {
        float damage = 6.0F;
        int pellets = 1;
        double pelletSpread;
        int maxCharge = 30;
        int regenTicks = 4;
        int regenAmount = 1;
        int regenDelay = 8;
        int interval = 4;
        int hold;
        double rate;
        double range = 200.0;
        double hip = 1.0;
        double aim;
        float zoom = 0.25F;
        float kick = 0.5F;
        boolean scoped;
        ResourceKey<DamageType> type = PXDamage.BALA;
        @Nullable
        Supplier<SoundEvent> sound;
        float volume = 3.0F;
        float pitch = 1.0F;
        int soundEvery;
        @Nullable
        Supplier<SoundEvent> action;
        int actionDelay;
        float actionPitch = 1.0F;

        /** Daño de cada bala o perdigón. */
        public Spec damage(float damage) {
            this.damage = damage;
            return this;
        }

        /** Perdigones por disparo y apertura del cono (grados). */
        public Spec pellets(int pellets, double spread) {
            this.pellets = pellets;
            this.pelletSpread = spread;
            return this;
        }

        /** Cargador: capacidad, ticks por cada bala recuperada y espera tras disparar antes de recargar. */
        public Spec ammo(int max, int regenTicks, int regenDelay) {
            this.maxCharge = max;
            this.regenTicks = regenTicks;
            this.regenDelay = regenDelay;
            return this;
        }

        /** Balas recuperadas de golpe en cada paso de recarga. */
        public Spec regenAmount(int amount) {
            this.regenAmount = amount;
            return this;
        }

        /** Semiautomática: ticks mínimos entre clics y ticks entre disparos manteniendo el gatillo. */
        public Spec semi(int interval, int hold) {
            this.interval = interval;
            this.hold = hold;
            return this;
        }

        /** Automática: disparos por segundo (con decimales; más de 20 = varias balas por tick). */
        public Spec auto(double rate) {
            this.rate = rate;
            this.interval = Math.max(1, (int) Math.round(20.0 / rate));
            return this;
        }

        public Spec range(double range) {
            this.range = range;
            return this;
        }

        /** Dispersión en grados desde la cadera y apuntando. */
        public Spec spread(double hip, double aim) {
            this.hip = hip;
            this.aim = aim;
            return this;
        }

        /** Cuánto se cierra el campo de visión al apuntar. */
        public Spec zoom(float zoom) {
            this.zoom = zoom;
            return this;
        }

        /** Retroceso visual en primera persona (0–1). */
        public Spec kick(float kick) {
            this.kick = kick;
            return this;
        }

        /** Al apuntar se mira por un visor a pantalla completa. */
        public Spec scoped() {
            this.scoped = true;
            return this;
        }

        public Spec type(ResourceKey<DamageType> type) {
            this.type = type;
            return this;
        }

        /** Sonido del disparo; el volumen marca hasta dónde se oye (16 bloques por cada 1,0). */
        public Spec sound(Supplier<SoundEvent> sound, float volume) {
            this.sound = sound;
            this.volume = volume;
            return this;
        }

        public Spec pitch(float pitch) {
            this.pitch = pitch;
            return this;
        }

        /** El sonido suena una vez cada tantos ticks (una ráfaga entera). */
        public Spec soundEvery(int ticks) {
            this.soundEvery = ticks;
            return this;
        }

        /** Sonido mecánico tras cada disparo (cerrojo, corredera). */
        public Spec action(Supplier<SoundEvent> sound, int delay, float pitch) {
            this.action = sound;
            this.actionDelay = delay;
            this.actionPitch = pitch;
            return this;
        }
    }

    public static Spec spec() {
        return new Spec();
    }

    private final Spec spec;

    public RealGun(String key, String code, int color, int style, Properties properties, Spec spec) {
        super(key, code, color, style, properties);
        this.spec = spec;
    }

    public Spec specs() {
        return this.spec;
    }

    @Override
    public float damage() {
        return spec.damage;
    }

    @Override
    public int pellets() {
        return spec.pellets;
    }

    @Override
    public double pelletSpread() {
        return spec.pelletSpread;
    }

    @Override
    public int maxCharge() {
        return spec.maxCharge;
    }

    @Override
    public int regenTicks() {
        return spec.regenTicks;
    }

    @Override
    public int regenAmount() {
        return spec.regenAmount;
    }

    @Override
    public int regenDelay() {
        return spec.regenDelay;
    }

    @Override
    public int interval() {
        return spec.interval;
    }

    @Override
    public int holdInterval() {
        return spec.hold;
    }

    @Override
    public double fireRate() {
        return spec.rate;
    }

    @Override
    public double range() {
        return spec.range;
    }

    @Override
    public double hipSpread() {
        return spec.hip;
    }

    @Override
    public double aimSpread() {
        return spec.aim;
    }

    @Override
    public float zoom() {
        return spec.zoom;
    }

    @Override
    public float kick() {
        return spec.kick;
    }

    @Override
    public boolean scoped() {
        return spec.scoped;
    }

    @Override
    public ResourceKey<DamageType> damageType() {
        return spec.type;
    }

    @Override
    protected int soundEvery() {
        return spec.soundEvery;
    }

    @Override
    protected SoundEvent shotSound() {
        return spec.sound.get();
    }

    @Override
    protected float shotVolume() {
        return spec.volume;
    }

    @Override
    protected float shotPitch() {
        return spec.pitch;
    }

    @Override
    protected void afterShot(Shot shot, ItemStack stack) {
        Supplier<SoundEvent> action = spec.action;
        if (action == null) return;
        ServerPlayer player = shot.player();
        Scheduler.schedule(shot.level(), spec.actionDelay, () -> {
            if (player.isAlive()) FX.sound(player.level(), player, action.get(), 0.8F, spec.actionPitch);
        });
    }
}
