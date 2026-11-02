package com.proyectox.entity;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

/**
 * Ficha de comportamiento de cada proyectil de la serie v7 ({@link SpecBolt}), según el arma que lo dispara (su estilo
 * de efectos, {@link Fx}). La conocen el servidor y el cliente (el cliente la usa para predecir la caída).
 */
public final class BoltSpec {
    /** Tipo de daño del impacto directo. */
    ResourceKey<DamageType> type = PXDamage.NEON;
    /** Ticks de vida. */
    int life = 60;
    /** Caída por tick (0 = vuela recto a velocidad constante). */
    double gravity;
    /** Criaturas extra que atraviesa tras la primera (-1 = todas). */
    int pierce;
    /** Rebotes en bloques. */
    int bounces;
    /** Fracción de la velocidad que conserva en cada rebote. */
    double bounceKeep = 1.0;
    /** También rebota en las criaturas que golpea (CACHARRO). */
    boolean bounceEntities;
    /** Giro por tick hacia su objetivo (0–1; 0 = no busca). */
    double homing;
    /** Ticks antes de empezar a buscar. */
    int homingDelay;
    /** Alcance de la búsqueda. */
    double seek = 16.0;
    /** LARVA: al tocar el suelo salta hacia su objetivo. */
    boolean hop;
    int maxHops = 6;
    /** Radio de la explosión (0 = no explota). */
    float explode;
    float explodeDamage;
    ResourceKey<DamageType> explodeType = PXDamage.GRANADA;
    /** Explota al acabarse la mecha (y antes si golpea a una criatura); si no, explota al chocar. */
    boolean fuse;
    /** Efectos sobre lo alcanzado (ticks). */
    int stun;
    int poison;
    int poisonAmp;
    int slow;
    int slowAmp;
    /** Segundos ardiendo. */
    int ignite;
    /** Descarga en cadena a otras criaturas cercanas. */
    int chain;
    float chainDamage;
    double chainRange;
    /** Zona que deja al chocar o al acabarse ({@link SpecField}; -1 = ninguna). */
    int field = -1;
    /** NÉBULA: daño a su alrededor mientras vuela. */
    double aura;
    float auraDamage;
    /** GARFIO: engancha y tira. */
    boolean grapple;
    /** Discos: cortan lo que rozan (cada criatura, como mucho cada 10 ticks) y no se detienen al golpear. */
    boolean grind;

    private BoltSpec() {}

    public double gravity() {
        return this.gravity;
    }

    public boolean grind() {
        return this.grind;
    }

    public boolean grapple() {
        return this.grapple;
    }

    public int field() {
        return this.field;
    }

    private static final BoltSpec DEFAULT = new BoltSpec();
    private static final Map<Integer, BoltSpec> SPECS = new HashMap<>();

    private static BoltSpec put(int style) {
        BoltSpec s = new BoltSpec();
        SPECS.put(style, s);
        return s;
    }

    static {
        // NEÓN
        BoltSpec s = put(Fx.PIXEL);
        s.life = 40;
        s = put(Fx.VOLTIO);
        s.life = 30;
        s.chain = 1;
        s.chainDamage = 2.0F;
        s.chainRange = 4.5;
        s = put(Fx.FUSIBLE);
        s.type = PXDamage.GRANADA;
        s.life = 24;
        s.gravity = 0.05;
        s.bounces = 8;
        s.bounceKeep = 0.55;
        s.explode = 4.0F;
        s.explodeDamage = 10.0F;
        s.fuse = true;
        s.stun = 40;
        s = put(Fx.VINILO);
        s.life = 70;
        s.bounces = 4;
        s.grind = true;
        s = put(Fx.NEXO);
        s.life = 100;
        s.homing = 0.18;
        s.homingDelay = 4;
        s.seek = 24.0;
        s.explode = 1.5F;
        s.explodeDamage = 8.0F;
        s.explodeType = PXDamage.NEON;
        // CHATARRA
        s = put(Fx.CLAVO);
        s.type = PXDamage.CLAVO;
        s.life = 40;
        s.gravity = 0.01;
        s = put(Fx.SERRUCHO);
        s.type = PXDamage.DISCO;
        s.life = 80;
        s.gravity = 0.025;
        s.bounces = 3;
        s.bounceKeep = 0.85;
        s.grind = true;
        s = put(Fx.PERNO);
        s.type = PXDamage.VARILLA;
        s.life = 60;
        s.gravity = 0.008;
        s.pierce = 2;
        s = put(Fx.MOLOTOV);
        s.type = PXDamage.LLAMA;
        s.life = 80;
        s.gravity = 0.05;
        s.ignite = 4;
        s.field = SpecField.FIRE;
        s = put(Fx.BIDON);
        s.type = PXDamage.MORTERO;
        s.life = 100;
        s.gravity = 0.06;
        s.explode = 4.5F;
        s.explodeDamage = 24.0F;
        s.explodeType = PXDamage.MORTERO;
        s = put(Fx.CACHARRO);
        s.type = PXDamage.CHATARRA;
        s.life = 80;
        s.gravity = 0.04;
        s.bounces = 3;
        s.bounceKeep = 0.7;
        s.bounceEntities = true;
        s = put(Fx.GARFIO);
        s.type = PXDamage.CHATARRA;
        s.life = 20;
        s.gravity = 0.015;
        s.grapple = true;
        // XENO
        s = put(Fx.ESPORA);
        s.type = PXDamage.VENENO;
        s.life = 60;
        s.homing = 0.12;
        s.homingDelay = 3;
        s.seek = 20.0;
        s.poison = 40;
        s = put(Fx.ABISMO);
        s.type = PXDamage.GRAVEDAD;
        s.life = 30;
        s.field = SpecField.WELL;
        s = put(Fx.LARVA);
        s.type = PXDamage.XENO;
        s.life = 100;
        s.gravity = 0.06;
        s.hop = true;
        s.maxHops = 8;
        s.seek = 18.0;
        s = put(Fx.NEBULA);
        s.type = PXDamage.XENO;
        s.life = 64;
        s.aura = 2.5;
        s.auraDamage = 3.0F;
        s.slow = 40;
        s.slowAmp = 1;
        s.field = SpecField.CLOUD;
        s = put(Fx.CENIT);
        s.type = PXDamage.XENO;
        s.life = 50;
        s.homing = 0.2;
        s.homingDelay = 2;
        s.seek = 28.0;
    }

    public static BoltSpec of(int style) {
        return SPECS.getOrDefault(style, DEFAULT);
    }
}
