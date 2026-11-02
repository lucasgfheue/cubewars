package com.proyectox.registry;

import com.proyectox.ProyectoX;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Eventos de sonido propios. En assets/proyectox/sounds.json la mayoría se compone a partir de sonidos de
 * Minecraft con su propio tono y volumen; los disparos de las armas de pólvora usan grabaciones sintetizadas
 * propias (assets/proyectox/sounds/arma/*.ogg). Todos pueden sustituirse por otros .ogg sin tocar el código.
 */
public final class PXSounds {
    private PXSounds() {}

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, ProyectoX.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> VACIO = reg("arma.vacia");
    public static final DeferredHolder<SoundEvent, SoundEvent> LISTO = reg("arma.lista");

    public static final DeferredHolder<SoundEvent, SoundEvent> MARTINETE_PRESION = reg("martinete.presion");
    public static final DeferredHolder<SoundEvent, SoundEvent> MARTINETE_CARGA = reg("martinete.carga");
    public static final DeferredHolder<SoundEvent, SoundEvent> MARTINETE_RUPTURA = reg("martinete.ruptura");

    public static final DeferredHolder<SoundEvent, SoundEvent> AGUJA_CARGA = reg("aguja.carga");
    public static final DeferredHolder<SoundEvent, SoundEvent> AGUJA_DISPARO = reg("aguja.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> AGUJA_RIEL = reg("aguja.riel");

    public static final DeferredHolder<SoundEvent, SoundEvent> AVISPON_DISPARO = reg("avispon.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> AVISPON_IMPACTO = reg("avispon.impacto");
    public static final DeferredHolder<SoundEvent, SoundEvent> AVISPON_SOBRECALOR = reg("avispon.sobrecalor");
    public static final DeferredHolder<SoundEvent, SoundEvent> AVISPON_PURGA = reg("avispon.purga");

    public static final DeferredHolder<SoundEvent, SoundEvent> COLMENA_FIJAR = reg("colmena.fijar");
    public static final DeferredHolder<SoundEvent, SoundEvent> COLMENA_LANZAR = reg("colmena.lanzar");
    public static final DeferredHolder<SoundEvent, SoundEvent> COLMENA_EXPLOSION = reg("colmena.explosion");

    public static final DeferredHolder<SoundEvent, SoundEvent> VORTICE_DISPARO = reg("vortice.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> VORTICE_ABRIR = reg("vortice.abrir");
    public static final DeferredHolder<SoundEvent, SoundEvent> VORTICE_COLAPSO = reg("vortice.colapso");
    public static final DeferredHolder<SoundEvent, SoundEvent> VORTICE_ANCLA = reg("vortice.ancla");

    public static final DeferredHolder<SoundEvent, SoundEvent> CIZALLA_TAJO = reg("cizalla.tajo");
    public static final DeferredHolder<SoundEvent, SoundEvent> CIZALLA_VIBRO = reg("cizalla.vibro");
    public static final DeferredHolder<SoundEvent, SoundEvent> TALADRO_CARGA = reg("taladro.carga");
    public static final DeferredHolder<SoundEvent, SoundEvent> TALADRO_EMBESTIDA = reg("taladro.embestida");
    public static final DeferredHolder<SoundEvent, SoundEvent> SERPENTINA_LATIGO = reg("serpentina.latigo");
    public static final DeferredHolder<SoundEvent, SoundEvent> SERPENTINA_ARCO = reg("serpentina.arco");
    public static final DeferredHolder<SoundEvent, SoundEvent> CHISPA_DISPARO = reg("chispa.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> REMACHE_DISPARO = reg("remache.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> TRITURADORA_LANZAR = reg("trituradora.lanzar");
    public static final DeferredHolder<SoundEvent, SoundEvent> TRITURADORA_REBOTE = reg("trituradora.rebote");
    public static final DeferredHolder<SoundEvent, SoundEvent> TRITURADORA_EXPLOSION = reg("trituradora.explosion");
    public static final DeferredHolder<SoundEvent, SoundEvent> ROTOR_GIRO = reg("rotor.giro");
    public static final DeferredHolder<SoundEvent, SoundEvent> ROTOR_DISPARO = reg("rotor.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> HALCON_DESPLIEGUE = reg("halcon.despliegue");
    public static final DeferredHolder<SoundEvent, SoundEvent> HALCON_DISPARO = reg("halcon.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> HALCON_RETIRADA = reg("halcon.retirada");
    public static final DeferredHolder<SoundEvent, SoundEvent> TRUENO_MARCAR = reg("trueno.marcar");
    public static final DeferredHolder<SoundEvent, SoundEvent> TRUENO_IMPACTO = reg("trueno.impacto");
    public static final DeferredHolder<SoundEvent, SoundEvent> TRUENO_BENGALA = reg("trueno.bengala");
    public static final DeferredHolder<SoundEvent, SoundEvent> SOPLETE_CHORRO = reg("soplete.chorro");
    public static final DeferredHolder<SoundEvent, SoundEvent> SOPLETE_NAPALM = reg("soplete.napalm");
    public static final DeferredHolder<SoundEvent, SoundEvent> ESCARCHA_RAYO = reg("escarcha.rayo");
    public static final DeferredHolder<SoundEvent, SoundEvent> ESCARCHA_CONGELAR = reg("escarcha.congelar");
    public static final DeferredHolder<SoundEvent, SoundEvent> ESCARCHA_NOVA = reg("escarcha.nova");
    public static final DeferredHolder<SoundEvent, SoundEvent> ECO_CARGA = reg("eco.carga");
    public static final DeferredHolder<SoundEvent, SoundEvent> ECO_ONDA = reg("eco.onda");
    public static final DeferredHolder<SoundEvent, SoundEvent> BASTION_PULSO = reg("bastion.pulso");
    public static final DeferredHolder<SoundEvent, SoundEvent> COLIBRI_DISPARO = reg("colibri.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> SIERRA_LANZAR = reg("sierra.lanzar");
    public static final DeferredHolder<SoundEvent, SoundEvent> SIERRA_REBOTE = reg("sierra.rebote");
    public static final DeferredHolder<SoundEvent, SoundEvent> SIERRA_CORTE = reg("sierra.corte");

    public static final DeferredHolder<SoundEvent, SoundEvent> CENTELLA_DISPARO = reg("centella.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> CENTELLA_TAMBOR = reg("centella.tambor");
    public static final DeferredHolder<SoundEvent, SoundEvent> ARPON_DISPARO = reg("arpon.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> ARPON_CLAVAR = reg("arpon.clavar");
    public static final DeferredHolder<SoundEvent, SoundEvent> ARPON_PITIDO = reg("arpon.pitido");
    public static final DeferredHolder<SoundEvent, SoundEvent> ARPON_EXPLOSION = reg("arpon.explosion");
    public static final DeferredHolder<SoundEvent, SoundEvent> MORTERO_DISPARO = reg("mortero.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> MORTERO_EXPLOSION = reg("mortero.explosion");
    public static final DeferredHolder<SoundEvent, SoundEvent> PRISMA_DISPARO = reg("prisma.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> PRISMA_DIVISION = reg("prisma.division");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENJAMBRE_DISPARO = reg("enjambre.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENJAMBRE_ZUMBIDO = reg("enjambre.zumbido");
    public static final DeferredHolder<SoundEvent, SoundEvent> CAUSTICA_DISPARO = reg("caustica.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> CAUSTICA_SALPICAR = reg("caustica.salpicar");
    public static final DeferredHolder<SoundEvent, SoundEvent> SOLAR_CARGA = reg("solar.carga");
    public static final DeferredHolder<SoundEvent, SoundEvent> SOLAR_DISPARO = reg("solar.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> SOLAR_EXPLOSION = reg("solar.explosion");
    public static final DeferredHolder<SoundEvent, SoundEvent> TEMPESTAD_DISPARO = reg("tempestad.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> TEMPESTAD_RAYO = reg("tempestad.rayo");
    public static final DeferredHolder<SoundEvent, SoundEvent> ESQUIRLA_DISPARO = reg("esquirla.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> ESQUIRLA_ESTALLIDO = reg("esquirla.estallido");
    public static final DeferredHolder<SoundEvent, SoundEvent> CICLON_DISPARO = reg("ciclon.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> CICLON_VIENTO = reg("ciclon.viento");
    public static final DeferredHolder<SoundEvent, SoundEvent> MINADOR_DISPARO = reg("minador.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> MINADOR_ADHERIR = reg("minador.adherir");
    public static final DeferredHolder<SoundEvent, SoundEvent> MINADOR_ARMAR = reg("minador.armar");
    public static final DeferredHolder<SoundEvent, SoundEvent> MINADOR_EXPLOSION = reg("minador.explosion");
    public static final DeferredHolder<SoundEvent, SoundEvent> CENIZA_DISPARO = reg("ceniza.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> CENIZA_DESINTEGRAR = reg("ceniza.desintegrar");
    public static final DeferredHolder<SoundEvent, SoundEvent> SAETA_TENSAR = reg("saeta.tensar");
    public static final DeferredHolder<SoundEvent, SoundEvent> SAETA_DISPARO = reg("saeta.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> SAETA_IMPACTO = reg("saeta.impacto");
    public static final DeferredHolder<SoundEvent, SoundEvent> GUADANA_TAJO = reg("guadana.tajo");
    public static final DeferredHolder<SoundEvent, SoundEvent> PICA_ESTOCADA = reg("pica.estocada");

    public static final DeferredHolder<SoundEvent, SoundEvent> YUNQUE_DISPARO = reg("yunque.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> CHACAL_DISPARO = reg("chacal.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> TRABUCO_DISPARO = reg("trabuco.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> TRABUCO_CORREDERA = reg("trabuco.corredera");
    public static final DeferredHolder<SoundEvent, SoundEvent> COYOTE_DISPARO = reg("coyote.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> GRANIZO_DISPARO = reg("granizo.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> LINCE_DISPARO = reg("lince.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> LINCE_CERROJO = reg("lince.cerrojo");
    public static final DeferredHolder<SoundEvent, SoundEvent> RUGIDO_DISPARO = reg("rugido.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> RUGIDO_EXPLOSION = reg("rugido.explosion");
    public static final DeferredHolder<SoundEvent, SoundEvent> METEORO_MARCAR = reg("meteoro.marcar");
    public static final DeferredHolder<SoundEvent, SoundEvent> METEORO_CAIDA = reg("meteoro.caida");
    public static final DeferredHolder<SoundEvent, SoundEvent> METEORO_IMPACTO = reg("meteoro.impacto");
    public static final DeferredHolder<SoundEvent, SoundEvent> PULSAR_DISPARO = reg("pulsar.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> PULSAR_DESCARGA = reg("pulsar.descarga");
    public static final DeferredHolder<SoundEvent, SoundEvent> PULSAR_EXPLOSION = reg("pulsar.explosion");
    public static final DeferredHolder<SoundEvent, SoundEvent> FALLA_GOLPE = reg("falla.golpe");
    public static final DeferredHolder<SoundEvent, SoundEvent> FALLA_GRIETA = reg("falla.grieta");
    public static final DeferredHolder<SoundEvent, SoundEvent> CENTINELA_DESPLEGAR = reg("centinela.desplegar");
    public static final DeferredHolder<SoundEvent, SoundEvent> CENTINELA_DISPARO = reg("centinela.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> CENTINELA_RETIRAR = reg("centinela.retirar");
    public static final DeferredHolder<SoundEvent, SoundEvent> RETORNO_LANZAR = reg("retorno.lanzar");
    public static final DeferredHolder<SoundEvent, SoundEvent> RETORNO_VOLVER = reg("retorno.volver");
    public static final DeferredHolder<SoundEvent, SoundEvent> RETORNO_ATRAPAR = reg("retorno.atrapar");
    public static final DeferredHolder<SoundEvent, SoundEvent> RETORNO_CORTE = reg("retorno.corte");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAREA_CHORRO = reg("marea.chorro");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAREA_SALPICAR = reg("marea.salpicar");
    public static final DeferredHolder<SoundEvent, SoundEvent> MANGUAL_GIRO = reg("mangual.giro");
    public static final DeferredHolder<SoundEvent, SoundEvent> ARIETE_GOLPE = reg("ariete.golpe");
    public static final DeferredHolder<SoundEvent, SoundEvent> ARIETE_ONDA = reg("ariete.onda");

    // v6: armas militares realistas (sonidos sintetizados propios, ver tools/sfx.py)
    public static final DeferredHolder<SoundEvent, SoundEvent> BISONTE_DISPARO = reg("bisonte.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> VIBORA_DISPARO = reg("vibora.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> TEJON_DISPARO = reg("tejon.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> SOMBRA_DISPARO = reg("sombra.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> GALGO_DISPARO = reg("galgo.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> AZOR_DISPARO = reg("azor.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> CONDOR_DISPARO = reg("condor.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> HURON_DISPARO = reg("huron.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> BUHO_DISPARO = reg("buho.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> PIRANA_DISPARO = reg("pirana.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> JABALI_DISPARO = reg("jabali.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> AVALANCHA_DISPARO = reg("avalancha.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> DILUVIO_DISPARO = reg("diluvio.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> VOLCAN_DISPARO = reg("volcan.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> VOLCAN_TAMBOR = reg("volcan.tambor");
    public static final DeferredHolder<SoundEvent, SoundEvent> VOLCAN_EXPLOSION = reg("volcan.explosion");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAMUT_DISPARO = reg("mamut.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAMUT_CERROJO = reg("mamut.cerrojo");

    // v7
    public static final DeferredHolder<SoundEvent, SoundEvent> PIXEL_DISPARO = reg("pixel.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> VOLTIO_DISPARO = reg("voltio.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> ESTATICA_DISPARO = reg("estatica.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> DIODO_CARGA = reg("diodo.carga");
    public static final DeferredHolder<SoundEvent, SoundEvent> DIODO_DISPARO = reg("diodo.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> FUSIBLE_DISPARO = reg("fusible.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> FUSIBLE_PULSO = reg("fusible.pulso");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANTENA_DISPARO = reg("antena.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> VINILO_DISPARO = reg("vinilo.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> VINILO_REBOTE = reg("vinilo.rebote");
    public static final DeferredHolder<SoundEvent, SoundEvent> BAJO_DISPARO = reg("bajo.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> NEXO_DISPARO = reg("nexo.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> NEXO_EXPLOSION = reg("nexo.explosion");
    public static final DeferredHolder<SoundEvent, SoundEvent> NEON_TAJO = reg("neon.tajo");
    public static final DeferredHolder<SoundEvent, SoundEvent> RELE_GOLPE = reg("rele.golpe");
    public static final DeferredHolder<SoundEvent, SoundEvent> FRECUENCIA_ONDA = reg("frecuencia.onda");
    public static final DeferredHolder<SoundEvent, SoundEvent> TUBERIA_DISPARO = reg("tuberia.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> CHAPA_DISPARO = reg("chapa.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> CLAVO_DISPARO = reg("clavo.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> SERRUCHO_LANZAR = reg("serrucho.lanzar");
    public static final DeferredHolder<SoundEvent, SoundEvent> PERNO_CARGA = reg("perno.carga");
    public static final DeferredHolder<SoundEvent, SoundEvent> PERNO_DISPARO = reg("perno.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOLOTOV_LANZAR = reg("molotov.lanzar");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOLOTOV_ROMPER = reg("molotov.romper");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOLOTOV_FUEGO = reg("molotov.fuego");
    public static final DeferredHolder<SoundEvent, SoundEvent> BIDON_DISPARO = reg("bidon.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> CACHARRO_DISPARO = reg("cacharro.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> CACHARRO_REBOTE = reg("cacharro.rebote");
    public static final DeferredHolder<SoundEvent, SoundEvent> MANIVELA_DISPARO = reg("manivela.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> MANIVELA_MANIVELA = reg("manivela.manivela");
    public static final DeferredHolder<SoundEvent, SoundEvent> GARFIO_DISPARO = reg("garfio.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> GARFIO_ENGANCHAR = reg("garfio.enganchar");
    public static final DeferredHolder<SoundEvent, SoundEvent> GARFIO_TIRAR = reg("garfio.tirar");
    public static final DeferredHolder<SoundEvent, SoundEvent> PALANCA_GOLPE = reg("palanca.golpe");
    public static final DeferredHolder<SoundEvent, SoundEvent> OXIDO_TAJO = reg("oxido.tajo");
    public static final DeferredHolder<SoundEvent, SoundEvent> ESPORA_DISPARO = reg("espora.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> GLIFO_DISPARO = reg("glifo.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> ABISMO_DISPARO = reg("abismo.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> ABISMO_POZO = reg("abismo.pozo");
    public static final DeferredHolder<SoundEvent, SoundEvent> LARVA_DISPARO = reg("larva.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> LARVA_SALTO = reg("larva.salto");
    public static final DeferredHolder<SoundEvent, SoundEvent> ECLIPSE_RAYO = reg("eclipse.rayo");
    public static final DeferredHolder<SoundEvent, SoundEvent> NEBULA_DISPARO = reg("nebula.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> NEBULA_ESTALLIDO = reg("nebula.estallido");
    public static final DeferredHolder<SoundEvent, SoundEvent> SIMBIONTE_TAJO = reg("simbionte.tajo");
    public static final DeferredHolder<SoundEvent, SoundEvent> SIMBIONTE_CURAR = reg("simbionte.curar");
    public static final DeferredHolder<SoundEvent, SoundEvent> ETER_DISPARO = reg("eter.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> CENIT_CARGA = reg("cenit.carga");
    public static final DeferredHolder<SoundEvent, SoundEvent> CENIT_DISPARO = reg("cenit.disparo");
    public static final DeferredHolder<SoundEvent, SoundEvent> UMBRAL_ABRIR = reg("umbral.abrir");
    public static final DeferredHolder<SoundEvent, SoundEvent> UMBRAL_GRIETA = reg("umbral.grieta");
    public static final DeferredHolder<SoundEvent, SoundEvent> OBSERVADOR_INVOCAR = reg("observador.invocar");
    public static final DeferredHolder<SoundEvent, SoundEvent> OBSERVADOR_RAYO = reg("observador.rayo");
    public static final DeferredHolder<SoundEvent, SoundEvent> AGUIJON_ESTOCADA = reg("aguijon.estocada");

    private static DeferredHolder<SoundEvent, SoundEvent> reg(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(ProyectoX.id(name)));
    }
}
