package com.proyectox.registry;

import com.proyectox.ProyectoX;
import com.proyectox.entity.Drone;
import com.proyectox.entity.EnergyBolt;
import com.proyectox.entity.Field;
import com.proyectox.entity.Grenade;
import com.proyectox.entity.MicroRocket;
import com.proyectox.entity.Orb;
import com.proyectox.entity.Piercer;
import com.proyectox.entity.SawDisc;
import com.proyectox.entity.SlashWave;
import com.proyectox.entity.PlasmaBolt;
import com.proyectox.entity.Singularity;
import com.proyectox.entity.SonicWave;
import com.proyectox.entity.SpecBolt;
import com.proyectox.entity.SpecField;
import com.proyectox.entity.Sticky;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Entidades efímeras de las armas: ninguna se guarda en el mundo. */
public final class PXEntities {
    private PXEntities() {}

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, ProyectoX.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<PlasmaBolt>> PLASMA = ENTITIES.register("plasma",
            () -> EntityType.Builder.<PlasmaBolt>of(PlasmaBolt::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(16).updateInterval(1).noSave().fireImmune()
                    .build(ProyectoX.MODID + ":plasma"));

    public static final DeferredHolder<EntityType<?>, EntityType<MicroRocket>> MICROCOHETE = ENTITIES.register("microcohete",
            () -> EntityType.Builder.<MicroRocket>of(MicroRocket::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F).clientTrackingRange(16).updateInterval(1).noSave().fireImmune()
                    .build(ProyectoX.MODID + ":microcohete"));

    public static final DeferredHolder<EntityType<?>, EntityType<Singularity>> SINGULARIDAD = ENTITIES.register("singularidad",
            () -> EntityType.Builder.<Singularity>of(Singularity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(16).updateInterval(1).noSave().fireImmune()
                    .build(ProyectoX.MODID + ":singularidad"));

    public static final DeferredHolder<EntityType<?>, EntityType<EnergyBolt>> BOLT = ENTITIES.register("pulso",
            () -> EntityType.Builder.<EnergyBolt>of(EnergyBolt::new, MobCategory.MISC)
                    .sized(0.2F, 0.2F).clientTrackingRange(16).updateInterval(1).noSave().fireImmune()
                    .build(ProyectoX.MODID + ":pulso"));

    public static final DeferredHolder<EntityType<?>, EntityType<Grenade>> GRANADA = ENTITIES.register("granada",
            () -> EntityType.Builder.<Grenade>of(Grenade::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F).clientTrackingRange(16).updateInterval(1).noSave().fireImmune()
                    .build(ProyectoX.MODID + ":granada"));

    public static final DeferredHolder<EntityType<?>, EntityType<SawDisc>> DISCO = ENTITIES.register("disco",
            () -> EntityType.Builder.<SawDisc>of(SawDisc::new, MobCategory.MISC)
                    .sized(0.5F, 0.2F).clientTrackingRange(16).updateInterval(1).noSave().fireImmune()
                    .build(ProyectoX.MODID + ":disco"));

    public static final DeferredHolder<EntityType<?>, EntityType<SlashWave>> TAJO = ENTITIES.register("tajo",
            () -> EntityType.Builder.<SlashWave>of(SlashWave::new, MobCategory.MISC)
                    .sized(0.6F, 0.3F).clientTrackingRange(16).updateInterval(1).noSave().fireImmune()
                    .build(ProyectoX.MODID + ":tajo"));

    public static final DeferredHolder<EntityType<?>, EntityType<SonicWave>> ONDA = ENTITIES.register("onda",
            () -> EntityType.Builder.<SonicWave>of(SonicWave::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(2).noSave().fireImmune().noSummon()
                    .build(ProyectoX.MODID + ":onda"));

    public static final DeferredHolder<EntityType<?>, EntityType<Orb>> ORBE = ENTITIES.register("orbe",
            () -> EntityType.Builder.<Orb>of(Orb::new, MobCategory.MISC)
                    .sized(0.35F, 0.35F).clientTrackingRange(16).updateInterval(1).noSave().fireImmune()
                    .build(ProyectoX.MODID + ":orbe"));

    public static final DeferredHolder<EntityType<?>, EntityType<Sticky>> ANCLADO = ENTITIES.register("anclado",
            () -> EntityType.Builder.<Sticky>of(Sticky::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F).clientTrackingRange(16).updateInterval(1).noSave().fireImmune()
                    .build(ProyectoX.MODID + ":anclado"));

    public static final DeferredHolder<EntityType<?>, EntityType<Piercer>> PERFORANTE = ENTITIES.register("perforante",
            () -> EntityType.Builder.<Piercer>of(Piercer::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F).clientTrackingRange(16).updateInterval(1).noSave().fireImmune()
                    .build(ProyectoX.MODID + ":perforante"));

    public static final DeferredHolder<EntityType<?>, EntityType<Field>> CAMPO = ENTITIES.register("campo",
            () -> EntityType.Builder.<Field>of(Field::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F).clientTrackingRange(16).updateInterval(1).noSave().fireImmune().noSummon()
                    .build(ProyectoX.MODID + ":campo"));

    /** v7: proyectiles de las series NEÓN, CHATARRA y XENO (su comportamiento depende del arma, ver {@link SpecBolt}). */
    public static final DeferredHolder<EntityType<?>, EntityType<SpecBolt>> PROYECTIL = ENTITIES.register("proyectil",
            () -> EntityType.Builder.<SpecBolt>of(SpecBolt::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F).clientTrackingRange(16).updateInterval(1).noSave().fireImmune()
                    .build(ProyectoX.MODID + ":proyectil"));

    /** v7: zonas (fuego, pozo de gravedad, nube, grieta) y el ojo vigilante del OBSERVADOR (ver {@link SpecField}). */
    public static final DeferredHolder<EntityType<?>, EntityType<SpecField>> ZONA = ENTITIES.register("zona",
            () -> EntityType.Builder.<SpecField>of(SpecField::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F).clientTrackingRange(16).updateInterval(1).noSave().fireImmune().noSummon()
                    .build(ProyectoX.MODID + ":zona"));

    public static final DeferredHolder<EntityType<?>, EntityType<Drone>> DRON = ENTITIES.register("dron",
            () -> EntityType.Builder.<Drone>of(Drone::new, MobCategory.MISC)
                    .sized(0.8F, 0.4F).clientTrackingRange(16).updateInterval(1).noSave().fireImmune().noSummon()
                    .build(ProyectoX.MODID + ":dron"));
}
