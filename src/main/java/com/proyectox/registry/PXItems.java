package com.proyectox.registry;

import com.proyectox.ProyectoX;
import com.proyectox.fx.Fx;
import com.proyectox.item.AgujaItem;
import com.proyectox.item.ArieteItem;
import com.proyectox.item.Arsenal7;
import com.proyectox.item.ArponItem;
import com.proyectox.item.AvisponItem;
import com.proyectox.item.BastionItem;
import com.proyectox.item.CausticaItem;
import com.proyectox.item.CenizaItem;
import com.proyectox.item.CentellaItem;
import com.proyectox.item.CentinelaItem;
import com.proyectox.item.ChacalItem;
import com.proyectox.item.ChispaItem;
import com.proyectox.item.CiclonItem;
import com.proyectox.item.CizallaItem;
import com.proyectox.item.ColibriItem;
import com.proyectox.item.ColmenaItem;
import com.proyectox.item.CoyoteItem;
import com.proyectox.item.EcoItem;
import com.proyectox.item.EnjambreItem;
import com.proyectox.item.EscarchaItem;
import com.proyectox.item.EsquirlaItem;
import com.proyectox.item.FallaItem;
import com.proyectox.item.GranizoItem;
import com.proyectox.item.GuadanaItem;
import com.proyectox.item.HalconItem;
import com.proyectox.item.LinceItem;
import com.proyectox.item.MangualItem;
import com.proyectox.item.MareaItem;
import com.proyectox.item.MartineteItem;
import com.proyectox.item.MeleeItem;
import com.proyectox.item.MeteoroItem;
import com.proyectox.item.MinadorItem;
import com.proyectox.item.MorteroItem;
import com.proyectox.item.PXWeapon;
import com.proyectox.item.PicaItem;
import com.proyectox.item.PrismaItem;
import com.proyectox.item.PropItem;
import com.proyectox.item.PulsarItem;
import com.proyectox.item.RealGun;
import com.proyectox.item.RemacheItem;
import com.proyectox.item.RetornoItem;
import com.proyectox.item.RotorItem;
import com.proyectox.item.RugidoItem;
import com.proyectox.item.SaetaItem;
import com.proyectox.item.SerpentinaItem;
import com.proyectox.item.SierraItem;
import com.proyectox.item.SolarItem;
import com.proyectox.item.SopleteItem;
import com.proyectox.item.SpecGun;
import com.proyectox.item.SpecMelee;
import com.proyectox.item.TaladroItem;
import com.proyectox.item.TempestadItem;
import com.proyectox.item.TrabucoItem;
import com.proyectox.item.TrituradoraItem;
import com.proyectox.item.TruenoItem;
import com.proyectox.item.VolcanItem;
import com.proyectox.item.VorticeItem;
import com.proyectox.item.XeranioCoreItem;
import com.proyectox.item.YunqueItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PXItems {
    private PXItems() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ProyectoX.MODID);

    /** Componente de fabricación: el cristal energético que alimenta todo el arsenal. */
    public static final DeferredItem<XeranioCoreItem> NUCLEO_XERANIO = ITEMS.register("nucleo_xeranio",
            () -> new XeranioCoreItem(new Item.Properties().stacksTo(16).rarity(Rarity.RARE).fireResistant()));

    public static final DeferredItem<MartineteItem> MARTINETE = ITEMS.register("martinete",
            () -> new MartineteItem(MeleeItem.meleeProperties(2400).attributes(MeleeItem.attributes(MartineteItem.DAMAGE, 0.9, 0.0))));

    public static final DeferredItem<AgujaItem> AGUJA = ITEMS.register("aguja",
            () -> new AgujaItem(PXWeapon.baseProperties()));

    public static final DeferredItem<AvisponItem> AVISPON = ITEMS.register("avispon",
            () -> new AvisponItem(PXWeapon.baseProperties()));

    public static final DeferredItem<ColmenaItem> COLMENA = ITEMS.register("colmena",
            () -> new ColmenaItem(PXWeapon.baseProperties()));

    public static final DeferredItem<VorticeItem> VORTICE = ITEMS.register("vortice",
            () -> new VorticeItem(PXWeapon.baseProperties()));

    public static final DeferredItem<CizallaItem> CIZALLA = ITEMS.register("cizalla",
            () -> new CizallaItem(MeleeItem.meleeProperties(2000).attributes(MeleeItem.attributes(CizallaItem.DAMAGE, 1.8, 0.0))));

    public static final DeferredItem<TaladroItem> TALADRO = ITEMS.register("taladro",
            () -> new TaladroItem(MeleeItem.meleeProperties(3000).attributes(TaladroItem.attributes())
                    .component(DataComponents.TOOL, TaladroItem.tool())));

    public static final DeferredItem<SerpentinaItem> SERPENTINA = ITEMS.register("serpentina",
            () -> new SerpentinaItem(MeleeItem.meleeProperties(2000).attributes(MeleeItem.attributes(6.0, 1.6, 3.0))));

    public static final DeferredItem<ChispaItem> CHISPA = ITEMS.register("chispa",
            () -> new ChispaItem(PXWeapon.baseProperties()));

    public static final DeferredItem<RemacheItem> REMACHE = ITEMS.register("remache",
            () -> new RemacheItem(PXWeapon.baseProperties()));

    public static final DeferredItem<TrituradoraItem> TRITURADORA = ITEMS.register("trituradora",
            () -> new TrituradoraItem(PXWeapon.baseProperties()));

    public static final DeferredItem<RotorItem> ROTOR = ITEMS.register("rotor",
            () -> new RotorItem(PXWeapon.baseProperties()));

    public static final DeferredItem<HalconItem> HALCON = ITEMS.register("halcon",
            () -> new HalconItem(PXWeapon.baseProperties()));

    public static final DeferredItem<TruenoItem> TRUENO = ITEMS.register("trueno",
            () -> new TruenoItem(PXWeapon.baseProperties()));

    public static final DeferredItem<SopleteItem> SOPLETE = ITEMS.register("soplete",
            () -> new SopleteItem(PXWeapon.baseProperties()));

    public static final DeferredItem<EscarchaItem> ESCARCHA = ITEMS.register("escarcha",
            () -> new EscarchaItem(PXWeapon.baseProperties()));

    public static final DeferredItem<EcoItem> ECO = ITEMS.register("eco",
            () -> new EcoItem(PXWeapon.baseProperties()));

    public static final DeferredItem<BastionItem> BASTION = ITEMS.register("bastion",
            () -> new BastionItem(PXWeapon.baseProperties()));

    public static final DeferredItem<ColibriItem> COLIBRI = ITEMS.register("colibri",
            () -> new ColibriItem(PXWeapon.baseProperties()));

    public static final DeferredItem<SierraItem> SIERRA = ITEMS.register("sierra",
            () -> new SierraItem(PXWeapon.baseProperties()));

    // ------------------------------------------------------------------ serie PX-21…PX-35 (v4)

    public static final DeferredItem<CentellaItem> CENTELLA = ITEMS.register("centella",
            () -> new CentellaItem(PXWeapon.baseProperties()));

    public static final DeferredItem<ArponItem> ARPON = ITEMS.register("arpon",
            () -> new ArponItem(PXWeapon.baseProperties()));

    public static final DeferredItem<MorteroItem> MORTERO = ITEMS.register("mortero",
            () -> new MorteroItem(PXWeapon.baseProperties()));

    public static final DeferredItem<PrismaItem> PRISMA = ITEMS.register("prisma",
            () -> new PrismaItem(PXWeapon.baseProperties()));

    public static final DeferredItem<EnjambreItem> ENJAMBRE = ITEMS.register("enjambre",
            () -> new EnjambreItem(PXWeapon.baseProperties()));

    public static final DeferredItem<CausticaItem> CAUSTICA = ITEMS.register("caustica",
            () -> new CausticaItem(PXWeapon.baseProperties()));

    public static final DeferredItem<SolarItem> SOLAR = ITEMS.register("solar",
            () -> new SolarItem(PXWeapon.baseProperties()));

    public static final DeferredItem<TempestadItem> TEMPESTAD = ITEMS.register("tempestad",
            () -> new TempestadItem(PXWeapon.baseProperties()));

    public static final DeferredItem<EsquirlaItem> ESQUIRLA = ITEMS.register("esquirla",
            () -> new EsquirlaItem(PXWeapon.baseProperties()));

    public static final DeferredItem<CiclonItem> CICLON = ITEMS.register("ciclon",
            () -> new CiclonItem(PXWeapon.baseProperties()));

    public static final DeferredItem<MinadorItem> MINADOR = ITEMS.register("minador",
            () -> new MinadorItem(PXWeapon.baseProperties()));

    public static final DeferredItem<CenizaItem> CENIZA = ITEMS.register("ceniza",
            () -> new CenizaItem(PXWeapon.baseProperties()));

    public static final DeferredItem<SaetaItem> SAETA = ITEMS.register("saeta",
            () -> new SaetaItem(PXWeapon.baseProperties()));

    public static final DeferredItem<GuadanaItem> GUADANA = ITEMS.register("guadana",
            () -> new GuadanaItem(MeleeItem.meleeProperties(2500).attributes(GuadanaItem.attributes())));

    public static final DeferredItem<PicaItem> PICA = ITEMS.register("pica",
            () -> new PicaItem(MeleeItem.meleeProperties(2500).attributes(PicaItem.attributes())));

    // ------------------------------------------------------------------ serie PX-36…PX-50 (v5)

    public static final DeferredItem<YunqueItem> YUNQUE = ITEMS.register("yunque",
            () -> new YunqueItem(PXWeapon.baseProperties()));

    public static final DeferredItem<ChacalItem> CHACAL = ITEMS.register("chacal",
            () -> new ChacalItem(PXWeapon.baseProperties()));

    public static final DeferredItem<TrabucoItem> TRABUCO = ITEMS.register("trabuco",
            () -> new TrabucoItem(PXWeapon.baseProperties()));

    public static final DeferredItem<CoyoteItem> COYOTE = ITEMS.register("coyote",
            () -> new CoyoteItem(PXWeapon.baseProperties()));

    public static final DeferredItem<GranizoItem> GRANIZO = ITEMS.register("granizo",
            () -> new GranizoItem(PXWeapon.baseProperties()));

    public static final DeferredItem<LinceItem> LINCE = ITEMS.register("lince",
            () -> new LinceItem(PXWeapon.baseProperties()));

    public static final DeferredItem<RugidoItem> RUGIDO = ITEMS.register("rugido",
            () -> new RugidoItem(PXWeapon.baseProperties()));

    public static final DeferredItem<MeteoroItem> METEORO = ITEMS.register("meteoro",
            () -> new MeteoroItem(PXWeapon.baseProperties()));

    public static final DeferredItem<PulsarItem> PULSAR = ITEMS.register("pulsar",
            () -> new PulsarItem(PXWeapon.baseProperties()));

    public static final DeferredItem<FallaItem> FALLA = ITEMS.register("falla",
            () -> new FallaItem(PXWeapon.baseProperties()));

    public static final DeferredItem<CentinelaItem> CENTINELA = ITEMS.register("centinela",
            () -> new CentinelaItem(PXWeapon.baseProperties()));

    public static final DeferredItem<RetornoItem> RETORNO = ITEMS.register("retorno",
            () -> new RetornoItem(PXWeapon.baseProperties()));

    public static final DeferredItem<MareaItem> MAREA = ITEMS.register("marea",
            () -> new MareaItem(PXWeapon.baseProperties()));

    public static final DeferredItem<MangualItem> MANGUAL = ITEMS.register("mangual",
            () -> new MangualItem(MeleeItem.meleeProperties(2500).attributes(MangualItem.attributes())));

    public static final DeferredItem<ArieteItem> ARIETE = ITEMS.register("ariete",
            () -> new ArieteItem(MeleeItem.meleeProperties(2500).attributes(ArieteItem.attributes())));

    // ------------------------------------------------------------------ serie PX-51…PX-65 (v6): armas militares realistas

    // revólveres
    public static final DeferredItem<RealGun> BISONTE = ITEMS.register("bisonte", () -> new RealGun("bisonte", "PX-51", 0xD6DADE, Fx.BISONTE,
            PXWeapon.baseProperties(), RealGun.spec().damage(12.0F).ammo(30, 5, 8).semi(6, 9).range(220.0).spread(0.5, 0.0).zoom(0.25F)
                    .kick(0.95F).sound(PXSounds.BISONTE_DISPARO, 4.0F)));

    public static final DeferredItem<RealGun> VIBORA = ITEMS.register("vibora", () -> new RealGun("vibora", "PX-52", 0xE8604C, Fx.VIBORA,
            PXWeapon.baseProperties(), RealGun.spec().damage(9.0F).ammo(35, 5, 8).semi(5, 7).range(200.0).spread(0.5, 0.0).zoom(0.3F)
                    .kick(0.75F).sound(PXSounds.VIBORA_DISPARO, 3.5F)));

    // pistolas
    public static final DeferredItem<RealGun> TEJON = ITEMS.register("tejon", () -> new RealGun("tejon", "PX-53", 0xB8BEC6, Fx.TEJON,
            PXWeapon.baseProperties(), RealGun.spec().damage(7.0F).ammo(45, 4, 8).semi(3, 5).range(160.0).spread(0.7, 0.1).zoom(0.2F)
                    .kick(0.5F).sound(PXSounds.TEJON_DISPARO, 3.0F)));

    public static final DeferredItem<RealGun> SOMBRA = ITEMS.register("sombra", () -> new RealGun("sombra", "PX-54", 0xC8B08A, Fx.SOMBRA,
            PXWeapon.baseProperties(), RealGun.spec().damage(8.0F).ammo(51, 4, 8).semi(4, 6).range(180.0).spread(0.6, 0.0).zoom(0.22F)
                    .kick(0.4F).sound(PXSounds.SOMBRA_DISPARO, 1.0F)));

    public static final DeferredItem<RealGun> GALGO = ITEMS.register("galgo", () -> new RealGun("galgo", "PX-55", 0xE0BE6A, Fx.GALGO,
            PXWeapon.baseProperties(), RealGun.spec().damage(8.0F).ammo(60, 3, 8).semi(3, 4).range(200.0).spread(0.4, 0.0).zoom(0.3F)
                    .kick(0.4F).sound(PXSounds.GALGO_DISPARO, 3.0F)));

    // fusiles
    public static final DeferredItem<RealGun> AZOR = ITEMS.register("azor", () -> new RealGun("azor", "PX-56", 0xC8A878, Fx.AZOR,
            PXWeapon.baseProperties(), RealGun.spec().damage(8.0F).ammo(120, 1, 10).auto(12.0).range(220.0).spread(1.2, 0.25).zoom(0.35F)
                    .kick(0.3F).sound(PXSounds.AZOR_DISPARO, 4.0F)));

    public static final DeferredItem<RealGun> CONDOR = ITEMS.register("condor", () -> new RealGun("condor", "PX-57", 0xA8B07A, Fx.CONDOR,
            PXWeapon.baseProperties(), RealGun.spec().damage(15.0F).ammo(40, 6, 10).semi(5, 6).range(280.0).spread(0.5, 0.0).zoom(0.65F)
                    .scoped().kick(0.75F).type(PXDamage.FRANCOTIRADOR).sound(PXSounds.CONDOR_DISPARO, 5.0F)));

    // subfusiles
    public static final DeferredItem<RealGun> HURON = ITEMS.register("huron", () -> new RealGun("huron", "PX-58", 0xA6ACB4, Fx.HURON,
            PXWeapon.baseProperties(), RealGun.spec().damage(5.0F).ammo(150, 1, 10).auto(15.0).range(160.0).spread(1.6, 0.5).zoom(0.25F)
                    .kick(0.22F).sound(PXSounds.HURON_DISPARO, 3.0F)));

    public static final DeferredItem<RealGun> BUHO = ITEMS.register("buho", () -> new RealGun("buho", "PX-59", 0x8EA4B8, Fx.BUHO,
            PXWeapon.baseProperties(), RealGun.spec().damage(6.0F).ammo(120, 1, 10).auto(13.0).range(180.0).spread(1.2, 0.3).zoom(0.3F)
                    .kick(0.18F).sound(PXSounds.BUHO_DISPARO, 1.2F)));

    public static final DeferredItem<RealGun> PIRANA = ITEMS.register("pirana", () -> new RealGun("pirana", "PX-60", 0xE07A5A, Fx.PIRANA,
            PXWeapon.baseProperties(), RealGun.spec().damage(4.0F).ammo(200, 1, 10).auto(20.0).range(140.0).spread(2.2, 0.9).zoom(0.2F)
                    .kick(0.2F).sound(PXSounds.PIRANA_DISPARO, 2.8F)));

    // escopetas
    public static final DeferredItem<RealGun> JABALI = ITEMS.register("jabali", () -> new RealGun("jabali", "PX-61", 0xC9A97E, Fx.JABALI,
            PXWeapon.baseProperties(), RealGun.spec().damage(3.5F).pellets(8, 5.0).ammo(32, 8, 10).semi(6, 8).range(64.0).spread(1.0, 0.3)
                    .zoom(0.2F).kick(0.9F).type(PXDamage.PERDIGON).sound(PXSounds.JABALI_DISPARO, 4.0F)));

    public static final DeferredItem<RealGun> AVALANCHA = ITEMS.register("avalancha", () -> new RealGun("avalancha", "PX-62", 0x9FAA78,
            Fx.AVALANCHA, PXWeapon.baseProperties(), RealGun.spec().damage(3.0F).pellets(8, 6.0).ammo(40, 6, 10).auto(4.0).range(56.0)
                    .spread(1.4, 0.5).zoom(0.2F).kick(0.8F).type(PXDamage.PERDIGON).sound(PXSounds.AVALANCHA_DISPARO, 4.0F)));

    // armas pesadas
    public static final DeferredItem<RealGun> DILUVIO = ITEMS.register("diluvio", () -> new RealGun("diluvio", "PX-63", 0xB4BA8A, Fx.DILUVIO,
            PXWeapon.baseProperties(), RealGun.spec().damage(4.0F).ammo(800, 1, 10).regenAmount(2).auto(40.0).range(200.0).spread(2.4, 1.2)
                    .zoom(0.15F).kick(0.15F).sound(PXSounds.DILUVIO_DISPARO, 5.0F).soundEvery(4)));

    public static final DeferredItem<VolcanItem> VOLCAN = ITEMS.register("volcan", () -> new VolcanItem(PXWeapon.baseProperties()));

    public static final DeferredItem<RealGun> MAMUT = ITEMS.register("mamut", () -> new RealGun("mamut", "PX-65", 0xD2B48C, Fx.MAMUT,
            PXWeapon.baseProperties(), RealGun.spec().damage(40.0F).ammo(10, 20, 10).semi(30, 30).range(400.0).spread(0.6, 0.0).zoom(0.85F)
                    .scoped().kick(1.0F).type(PXDamage.ANTIMATERIAL).sound(PXSounds.MAMUT_DISPARO, 6.0F)
                    .action(PXSounds.MAMUT_CERROJO, 12, 0.85F)));

    // ------------------------------------------------------------------ serie PX-66…PX-101 (v7): NEÓN, CHATARRA y XENO
    // (fichas y disparos en Arsenal7)

    // NEÓN: tecnología urbana y componentes electrónicos
    public static final DeferredItem<SpecGun> PIXEL = ITEMS.register("pixel", Arsenal7::pixel);
    public static final DeferredItem<SpecGun> VOLTIO = ITEMS.register("voltio", Arsenal7::voltio);
    public static final DeferredItem<SpecGun> ESTATICA = ITEMS.register("estatica", Arsenal7::estatica);
    public static final DeferredItem<SpecGun> DIODO = ITEMS.register("diodo", Arsenal7::diodo);
    public static final DeferredItem<SpecGun> FUSIBLE = ITEMS.register("fusible", Arsenal7::fusible);
    public static final DeferredItem<SpecGun> ANTENA = ITEMS.register("antena", Arsenal7::antena);
    public static final DeferredItem<SpecGun> VINILO = ITEMS.register("vinilo", Arsenal7::vinilo);
    public static final DeferredItem<SpecGun> BAJO = ITEMS.register("bajo", Arsenal7::bajo);
    public static final DeferredItem<SpecGun> NEXO = ITEMS.register("nexo", Arsenal7::nexo);
    public static final DeferredItem<SpecMelee> NEON = ITEMS.register("neon", Arsenal7::neon);
    public static final DeferredItem<SpecMelee> RELE = ITEMS.register("rele", Arsenal7::rele);
    public static final DeferredItem<SpecGun> FRECUENCIA = ITEMS.register("frecuencia", Arsenal7::frecuencia);

    // CHATARRA: armas improvisadas, oxidadas y modificadas
    public static final DeferredItem<SpecGun> TUBERIA = ITEMS.register("tuberia", Arsenal7::tuberia);
    public static final DeferredItem<SpecGun> CHAPA = ITEMS.register("chapa", Arsenal7::chapa);
    public static final DeferredItem<SpecGun> CLAVO = ITEMS.register("clavo", Arsenal7::clavo);
    public static final DeferredItem<SpecGun> SERRUCHO = ITEMS.register("serrucho", Arsenal7::serrucho);
    public static final DeferredItem<SpecGun> PERNO = ITEMS.register("perno", Arsenal7::perno);
    public static final DeferredItem<SpecGun> MOLOTOV = ITEMS.register("molotov", Arsenal7::molotov);
    public static final DeferredItem<SpecGun> BIDON = ITEMS.register("bidon", Arsenal7::bidon);
    public static final DeferredItem<SpecGun> CACHARRO = ITEMS.register("cacharro", Arsenal7::cacharro);
    public static final DeferredItem<SpecGun> MANIVELA = ITEMS.register("manivela", Arsenal7::manivela);
    public static final DeferredItem<SpecGun> GARFIO = ITEMS.register("garfio", Arsenal7::garfio);
    public static final DeferredItem<SpecMelee> PALANCA = ITEMS.register("palanca", Arsenal7::palanca);
    public static final DeferredItem<SpecMelee> OXIDO = ITEMS.register("oxido", Arsenal7::oxido);

    // XENO: tecnología extraterrestre y formas desconocidas
    public static final DeferredItem<SpecGun> ESPORA = ITEMS.register("espora", Arsenal7::espora);
    public static final DeferredItem<SpecGun> GLIFO = ITEMS.register("glifo", Arsenal7::glifo);
    public static final DeferredItem<SpecGun> ABISMO = ITEMS.register("abismo", Arsenal7::abismo);
    public static final DeferredItem<SpecGun> LARVA = ITEMS.register("larva", Arsenal7::larva);
    public static final DeferredItem<SpecGun> ECLIPSE = ITEMS.register("eclipse", Arsenal7::eclipse);
    public static final DeferredItem<SpecGun> NEBULA = ITEMS.register("nebula", Arsenal7::nebula);
    public static final DeferredItem<SpecMelee> SIMBIONTE = ITEMS.register("simbionte", Arsenal7::simbionte);
    public static final DeferredItem<SpecGun> ETER = ITEMS.register("eter", Arsenal7::eter);
    public static final DeferredItem<SpecGun> CENIT = ITEMS.register("cenit", Arsenal7::cenit);
    public static final DeferredItem<SpecGun> UMBRAL = ITEMS.register("umbral", Arsenal7::umbral);
    public static final DeferredItem<SpecGun> OBSERVADOR = ITEMS.register("observador", Arsenal7::observador);
    public static final DeferredItem<SpecMelee> AGUIJON = ITEMS.register("aguijon", Arsenal7::aguijon);

    // ------------------------------------------------------------------ piezas que se ven en el mundo (no se fabrican)

    public static final DeferredItem<PropItem> PROP_COHETE = ITEMS.register("prop_cohete", () -> new PropItem(new Item.Properties()));
    public static final DeferredItem<PropItem> PROP_METEORITO = ITEMS.register("prop_meteorito", () -> new PropItem(new Item.Properties()));
    public static final DeferredItem<PropItem> PROP_CHAKRAM = ITEMS.register("prop_chakram", () -> new PropItem(new Item.Properties()));
    public static final DeferredItem<PropItem> PROP_TORRETA_BASE = ITEMS.register("prop_torreta_base", () -> new PropItem(new Item.Properties()));
    public static final DeferredItem<PropItem> PROP_TORRETA_CABEZA = ITEMS.register("prop_torreta_cabeza", () -> new PropItem(new Item.Properties()));
    public static final DeferredItem<PropItem> PROP_GRANADA40 = ITEMS.register("prop_granada40", () -> new PropItem(new Item.Properties()));
    public static final DeferredItem<PropItem> PROP_OJO = ITEMS.register("prop_ojo", () -> new PropItem(new Item.Properties()));
    public static final DeferredItem<PropItem> PROP_BOTELLA = ITEMS.register("prop_botella", () -> new PropItem(new Item.Properties()));
    public static final DeferredItem<PropItem> PROP_BIDON = ITEMS.register("prop_bidon", () -> new PropItem(new Item.Properties()));
    public static final DeferredItem<PropItem> PROP_GARFIO = ITEMS.register("prop_garfio", () -> new PropItem(new Item.Properties()));
    /** El TALADRO en dos piezas: en primera persona, al perforar, solo gira el portabrocas con la broca. */
    public static final DeferredItem<PropItem> PROP_TALADRO_CUERPO = ITEMS.register("prop_taladro_cuerpo", () -> new PropItem(new Item.Properties()));
    public static final DeferredItem<PropItem> PROP_TALADRO_BROCA = ITEMS.register("prop_taladro_broca", () -> new PropItem(new Item.Properties()));
}
