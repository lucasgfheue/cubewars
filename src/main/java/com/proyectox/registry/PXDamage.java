package com.proyectox.registry;

import com.proyectox.ProyectoX;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/** Tipos de daño propios (data/proyectox/damage_type) con sus mensajes de muerte. */
public final class PXDamage {
    private PXDamage() {}

    public static final ResourceKey<DamageType> IMPACTO = key("impacto");
    public static final ResourceKey<DamageType> RIEL = key("riel");
    public static final ResourceKey<DamageType> PLASMA = key("plasma");
    public static final ResourceKey<DamageType> PURGA = key("purga");
    public static final ResourceKey<DamageType> MICROCOHETE = key("microcohete");
    public static final ResourceKey<DamageType> COLAPSO = key("colapso");
    public static final ResourceKey<DamageType> CORTE = key("corte");
    public static final ResourceKey<DamageType> VIBROCORTE = key("vibrocorte");
    public static final ResourceKey<DamageType> EMBESTIDA = key("embestida");
    public static final ResourceKey<DamageType> ARCO = key("arco");
    public static final ResourceKey<DamageType> REMACHE = key("remache");
    public static final ResourceKey<DamageType> METRALLA = key("metralla");
    public static final ResourceKey<DamageType> TRAZADORA = key("trazadora");
    public static final ResourceKey<DamageType> DRON = key("dron");
    public static final ResourceKey<DamageType> ORBITAL = key("orbital");
    public static final ResourceKey<DamageType> LLAMA = key("llama");
    public static final ResourceKey<DamageType> FRIO = key("frio");
    public static final ResourceKey<DamageType> SONICO = key("sonico");
    public static final ResourceKey<DamageType> CINETICO = key("cinetico");
    public static final ResourceKey<DamageType> AGUJA_TOXICA = key("aguja_toxica");
    public static final ResourceKey<DamageType> DISCO = key("disco");
    public static final ResourceKey<DamageType> PERFORACION = key("perforacion");
    public static final ResourceKey<DamageType> LASER = key("laser");
    public static final ResourceKey<DamageType> FULGOR = key("fulgor");
    public static final ResourceKey<DamageType> ARPON = key("arpon");
    public static final ResourceKey<DamageType> MORTERO = key("mortero");
    public static final ResourceKey<DamageType> PRISMA = key("prisma");
    public static final ResourceKey<DamageType> ENJAMBRE = key("enjambre");
    public static final ResourceKey<DamageType> ACIDO = key("acido");
    public static final ResourceKey<DamageType> FUSION = key("fusion");
    public static final ResourceKey<DamageType> TORMENTA = key("tormenta");
    public static final ResourceKey<DamageType> ESQUIRLA = key("esquirla");
    public static final ResourceKey<DamageType> TORBELLINO = key("torbellino");
    public static final ResourceKey<DamageType> MINA = key("mina");
    public static final ResourceKey<DamageType> DESINTEGRACION = key("desintegracion");
    public static final ResourceKey<DamageType> FLECHA_LUZ = key("flecha_luz");
    public static final ResourceKey<DamageType> GUADANA = key("guadana");
    public static final ResourceKey<DamageType> PICA = key("pica");
    public static final ResourceKey<DamageType> BALA = key("bala");
    public static final ResourceKey<DamageType> PERDIGON = key("perdigon");
    public static final ResourceKey<DamageType> FRANCOTIRADOR = key("francotirador");
    public static final ResourceKey<DamageType> COHETE = key("cohete");
    public static final ResourceKey<DamageType> METEORITO = key("meteorito");
    public static final ResourceKey<DamageType> ELECTROCUCION = key("electrocucion");
    public static final ResourceKey<DamageType> SISMO = key("sismo");
    public static final ResourceKey<DamageType> TORRETA = key("torreta");
    public static final ResourceKey<DamageType> CHAKRAM = key("chakram");
    public static final ResourceKey<DamageType> HIDRO = key("hidro");
    public static final ResourceKey<DamageType> MANGUAL = key("mangual");
    public static final ResourceKey<DamageType> ARIETE = key("ariete");
    public static final ResourceKey<DamageType> GRANADA = key("granada");
    public static final ResourceKey<DamageType> ANTIMATERIAL = key("antimaterial");
    public static final ResourceKey<DamageType> TERREMOTO = key("terremoto");
    // v7
    public static final ResourceKey<DamageType> NEON = key("neon");
    public static final ResourceKey<DamageType> CLAVO = key("clavo");
    public static final ResourceKey<DamageType> VARILLA = key("varilla");
    public static final ResourceKey<DamageType> CHATARRA = key("chatarra");
    public static final ResourceKey<DamageType> XENO = key("xeno");
    public static final ResourceKey<DamageType> GRAVEDAD = key("gravedad");
    public static final ResourceKey<DamageType> VENENO = key("veneno");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ProyectoX.id(name));
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> type, @Nullable Entity direct, @Nullable Entity causing) {
        Holder<DamageType> holder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type);
        return new DamageSource(holder, direct, causing);
    }
}
