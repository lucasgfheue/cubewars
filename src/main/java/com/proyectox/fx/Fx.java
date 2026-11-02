package com.proyectox.fx;

import com.proyectox.network.FxPayload;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Efectos visuales. El servidor decide qué ocurre y dónde; cada cliente lo dibuja con sus
 * partículas HD propias (ver {@code com.proyectox.client.fx.FxEffects}).
 */
public final class Fx {
    private Fx() {}

    // ---------------------------------------------------------------- tipos de efecto
    /** Fogonazo en la boca del cañón (punto, dirección, tirador). */
    public static final int MUZZLE = 0;
    /** Rayo de riel de la AGUJA (origen → destino, valor = carga 0–1). */
    public static final int BEAM = 1;
    /** Impacto contra un bloque o una criatura (punto, normal, valor = intensidad). */
    public static final int IMPACT = 2;
    /** Explosión (punto, radio). */
    public static final int EXPLOSION = 3;
    /** Arco eléctrico (origen → destino, valor = grosor). */
    public static final int ARC = 4;
    /** Trazadora instantánea, p. ej. un remache (origen → destino). */
    public static final int TRACER = 5;
    /** Chorro de plasma ardiente (origen → final del chorro, valor = alcance). */
    public static final int FLAME = 6;
    /** Láser continuo del TRUENO (origen → destino, valor = calentamiento 0–1). */
    public static final int LASER = 7;
    /** Cuerda de la SERPENTINA (tirador → objetivo; valor = ticks; entidad = objetivo o -1). */
    public static final int ROPE = 8;
    /** Broca del TALADRO perforando (punto, dirección, valor = fase 0–2). */
    public static final int DRILL = 9;
    /** Objetivo fijado por la COLMENA (solo al tirador; entidad = objetivo, valor = nº de fijaciones). */
    public static final int LOCK = 10;
    /** Golpe cuerpo a cuerpo (punto, dirección). */
    public static final int MELEE = 11;
    /** Golpe detenido por el escudo (punto, dirección). */
    public static final int SHIELD = 12;
    /** Marcador de acierto (solo al tirador): estilo 1 = baja. */
    public static final int HITMARK = 13;
    /** Despliegue o retirada de un dron (punto). */
    public static final int DRONE = 14;
    /** El vórtice de la singularidad se abre (punto, radio de atracción). */
    public static final int VORTEX = 15;
    /** Rayos del PRISMA (origen → destino; valor = color 0–4, o -1 para el haz blanco antes de dividirse). */
    public static final int PRISM = 16;
    /** Rayo que cae de la nube de la TEMPESTAD (nube → objetivo). */
    public static final int LIGHTNING = 17;
    /** Barrido de la GUADAÑA (centro, dirección de la mirada; valor = radio; entidad = atacante). */
    public static final int SWEEP = 18;
    /** Estocada de la PICA (ojos → punta; entidad = atacante). */
    public static final int THRUST = 19;
    /** Criatura alcanzada por el anillo de la CENIZA (centro; valor = altura; entidad = objetivo). */
    public static final int DISINTEGRATE = 20;
    /** Nube de la TEMPESTAD formándose (punto, radio; entidad = tirador). */
    public static final int STORM = 21;
    /** Bala instantánea con trazadora (boca → destino; valor = 0 aire, 1 bloque, 2 criatura; entidad = tirador). */
    public static final int BULLET = 22;
    /** Picos de roca de la FALLA (punto en el suelo; valor = radio). */
    public static final int ERUPT = 23;
    /** Marca del METEORO en el suelo (punto; valor = ticks hasta el impacto). */
    public static final int MARK = 24;
    /** Giro del MANGUAL (centro del atacante; valor = radio; entidad = atacante). */
    public static final int SPIN = 25;
    /** Puñetazo de onda del ARIETE (ojos → final de la onda; entidad = atacante). */
    public static final int PUNCH = 26;
    /** Chorro de agua de la MAREA (boca → final del chorro; valor = alcance; entidad = tirador). */
    public static final int WATER = 27;
    /** Torreta que se despliega (valor 1) o se retira (valor 0) (punto). */
    public static final int DEPLOY = 28;
    /** Mini terremoto del MARTINETE al golpear cayendo (punto en el suelo; valor = radio). */
    public static final int QUAKE = 29;
    /** Réplica del terremoto del MARTINETE (cada segundo mientras dura). */
    public static final int TREMOR = 30;
    // v7
    /** Haz de energía de la serie v7 (origen → destino; valor = grosor; entidad = tirador o -1). */
    public static final int BEAM7 = 31;
    /** Cono de ondas (origen → final del cono; valor = semiángulo en grados; entidad = tirador). */
    public static final int CONE7 = 32;
    /** Tajo cuerpo a cuerpo de la serie v7 (centro, dirección de la mirada; valor = radio; entidad = atacante). */
    public static final int SWING7 = 33;
    /** Estocada de la serie v7 (ojos → punta; entidad = atacante). */
    public static final int THRUST7 = 34;
    /** Pulso: descarga, onda de choque o curación (punto, normal; valor = radio; entidad = criatura afectada o -1). */
    public static final int PULSE7 = 35;

    // ---------------------------------------------------------------- estilos (uno por arma)
    public static final int MARTINETE = 0;
    public static final int AGUJA = 1;
    public static final int AVISPON = 2;
    public static final int COLMENA = 3;
    public static final int VORTICE = 4;
    public static final int CIZALLA = 5;
    public static final int TALADRO = 6;
    public static final int SERPENTINA = 7;
    public static final int CHISPA = 8;
    public static final int REMACHE = 9;
    public static final int TRITURADORA = 10;
    public static final int ROTOR = 11;
    public static final int HALCON = 12;
    public static final int TRUENO = 13;
    public static final int SOPLETE = 14;
    public static final int ESCARCHA = 15;
    public static final int ECO = 16;
    public static final int BASTION = 17;
    public static final int COLIBRI = 18;
    public static final int SIERRA = 19;
    /** Bombetas del racimo de la TRITURADORA. */
    public static final int BOMBLET = 20;
    /** Pulsos incendiarios del AVISPÓN caliente. */
    public static final int AVISPON_HOT = 21;
    public static final int CENTELLA = 22;
    public static final int ARPON = 23;
    public static final int MORTERO = 24;
    public static final int PRISMA = 25;
    public static final int ENJAMBRE = 26;
    public static final int CAUSTICA = 27;
    public static final int SOLAR = 28;
    public static final int TEMPESTAD = 29;
    public static final int ESQUIRLA = 30;
    public static final int CICLON = 31;
    public static final int MINADOR = 32;
    public static final int CENIZA = 33;
    public static final int SAETA = 34;
    public static final int GUADANA = 35;
    public static final int PICA = 36;
    public static final int YUNQUE = 37;
    public static final int CHACAL = 38;
    public static final int TRABUCO = 39;
    public static final int COYOTE = 40;
    public static final int GRANIZO = 41;
    public static final int LINCE = 42;
    public static final int RUGIDO = 43;
    public static final int METEORO = 44;
    public static final int PULSAR = 45;
    public static final int FALLA = 46;
    public static final int CENTINELA = 47;
    public static final int RETORNO = 48;
    public static final int MAREA = 49;
    public static final int MANGUAL = 50;
    public static final int ARIETE = 51;
    // serie PX-51…PX-65 (armas militares realistas)
    public static final int BISONTE = 52;
    public static final int VIBORA = 53;
    public static final int TEJON = 54;
    public static final int SOMBRA = 55;
    public static final int GALGO = 56;
    public static final int AZOR = 57;
    public static final int CONDOR = 58;
    public static final int HURON = 59;
    public static final int BUHO = 60;
    public static final int PIRANA = 61;
    public static final int JABALI = 62;
    public static final int AVALANCHA = 63;
    public static final int DILUVIO = 64;
    public static final int VOLCAN = 65;
    public static final int MAMUT = 66;
    // serie PX-66…PX-101 (v7): NEÓN (tecnología urbana), CHATARRA (armas improvisadas) y XENO (tecnología alienígena)
    public static final int PIXEL = 67;
    public static final int VOLTIO = 68;
    public static final int ESTATICA = 69;
    public static final int DIODO = 70;
    public static final int FUSIBLE = 71;
    public static final int ANTENA = 72;
    public static final int VINILO = 73;
    public static final int BAJO = 74;
    public static final int NEXO = 75;
    public static final int NEON = 76;
    public static final int RELE = 77;
    public static final int FRECUENCIA = 78;
    public static final int TUBERIA = 79;
    public static final int CHAPA = 80;
    public static final int CLAVO = 81;
    public static final int SERRUCHO = 82;
    public static final int PERNO = 83;
    public static final int MOLOTOV = 84;
    public static final int BIDON = 85;
    public static final int CACHARRO = 86;
    public static final int MANIVELA = 87;
    public static final int GARFIO = 88;
    public static final int PALANCA = 89;
    public static final int OXIDO = 90;
    public static final int ESPORA = 91;
    public static final int GLIFO = 92;
    public static final int ABISMO = 93;
    public static final int LARVA = 94;
    public static final int ECLIPSE = 95;
    public static final int NEBULA = 96;
    public static final int SIMBIONTE = 97;
    public static final int ETER = 98;
    public static final int CENIT = 99;
    public static final int UMBRAL = 100;
    public static final int OBSERVADOR = 101;
    public static final int AGUIJON = 102;

    /** Familia visual de la serie v7: 0 = NEÓN, 1 = CHATARRA, 2 = XENO, -1 = otra arma. */
    public static int family(int style) {
        if (style >= PIXEL && style <= FRECUENCIA) return 0;
        if (style >= TUBERIA && style <= OXIDO) return 1;
        if (style >= ESPORA && style <= AGUIJON) return 2;
        return -1;
    }

    /** Distancia a la que los jugadores reciben los efectos. */
    private static final double RANGE = 160.0;
    private static final Vec3 UP = new Vec3(0, 1, 0);

    public static void send(ServerLevel level, Vec3 near, double radius, FxPayload payload) {
        PacketDistributor.sendToPlayersNear(level, null, near.x, near.y, near.z, radius, payload);
    }

    /** Efecto en un punto; {@code dir} es una dirección o una normal. */
    public static void point(ServerLevel level, int kind, int style, Vec3 at, Vec3 dir, float value, int entity) {
        send(level, at, RANGE, new FxPayload(kind, style, at.x, at.y, at.z, dir.x, dir.y, dir.z, value, entity, -1));
    }

    /** Efecto entre dos puntos (lo reciben los jugadores cercanos a cualquiera de los dos). */
    public static void line(ServerLevel level, int kind, int style, Vec3 from, Vec3 to, float value, int entity) {
        Vec3 mid = from.add(to).scale(0.5);
        double radius = RANGE + from.distanceTo(to) * 0.5;
        send(level, mid, radius, new FxPayload(kind, style, from.x, from.y, from.z, to.x, to.y, to.z, value, entity, -1));
    }

    /** Efecto entre dos entidades que el cliente sigue mientras dura (la cuerda de la SERPENTINA). */
    public static void tether(ServerLevel level, int kind, int style, Entity from, Entity to, Vec3 start, float ticks) {
        Vec3 end = to.getBoundingBox().getCenter();
        Vec3 mid = start.add(end).scale(0.5);
        send(level, mid, RANGE + start.distanceTo(end) * 0.5,
                new FxPayload(kind, style, start.x, start.y, start.z, end.x, end.y, end.z, ticks, from.getId(), to.getId()));
    }

    public static void muzzle(ServerLevel level, Entity shooter, int style, Vec3 muzzle, Vec3 dir) {
        point(level, MUZZLE, style, muzzle, dir, 1.0F, shooter.getId());
    }

    public static void impact(ServerLevel level, int style, Vec3 at, Vec3 normal) {
        point(level, IMPACT, style, at, normal, 1.0F, -1);
    }

    public static void explosion(ServerLevel level, int style, Vec3 at, float radius) {
        point(level, EXPLOSION, style, at, UP, radius, -1);
    }

    /** Efecto que solo ve un jugador (marcas de fijación, aciertos). */
    public static void only(ServerPlayer player, int kind, int style, Vec3 at, float value, int entity) {
        PacketDistributor.sendToPlayer(player, new FxPayload(kind, style, at.x, at.y, at.z, 0, 0, 0, value, entity, -1));
    }

    /** Aviso de acierto para el jugador que disparó. */
    public static void hitmark(@Nullable Entity shooter, boolean kill) {
        if (shooter instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new FxPayload(HITMARK, kill ? 1 : 0, 0, 0, 0, 0, 0, 0, 0.0F, -1, -1));
        }
    }
}
