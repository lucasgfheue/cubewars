package com.proyectox.client.fx;

import com.proyectox.fx.Fx;

/** Colores de los efectos de cada arma: tono principal, núcleo caliente y chispas. */
public final class Palette {
    private Palette() {}

    public static int main(int style) {
        return switch (style) {
            case Fx.MARTINETE -> 0xFF9A1F;
            case Fx.AGUJA -> 0x3CE6FF;
            case Fx.AVISPON -> 0x8CFF2E;
            case Fx.AVISPON_HOT -> 0xFF8A26;
            case Fx.COLMENA -> 0xFF5A2A;
            case Fx.VORTICE -> 0xA45CFF;
            case Fx.CIZALLA -> 0xFF2E7E;
            case Fx.TALADRO -> 0xFFD21A;
            case Fx.SERPENTINA -> 0x4F78FF;
            case Fx.CHISPA -> 0x9CC8FF;
            case Fx.REMACHE -> 0xFF9A40;
            case Fx.TRITURADORA -> 0xC8E04A;
            case Fx.BOMBLET -> 0xFFB840;
            case Fx.ROTOR -> 0xFFC85A;
            case Fx.HALCON -> 0xFFA235;
            case Fx.TRUENO -> 0x5FE6FF;
            case Fx.SOPLETE -> 0xFF6A1A;
            case Fx.ESCARCHA -> 0x9FEFFF;
            case Fx.ECO -> 0xD9A8FF;
            case Fx.BASTION -> 0xFFC48A;
            case Fx.COLIBRI -> 0xFF6FC8;
            case Fx.SIERRA -> 0x2EE6A0;
            case Fx.CENTELLA -> 0xFFC83A;
            case Fx.ARPON -> 0x39D6FF;
            case Fx.MORTERO -> 0xFF7A2A;
            case Fx.PRISMA -> 0xF4F6FF;
            case Fx.ENJAMBRE -> 0xFFB800;
            case Fx.CAUSTICA -> 0xB4FF1E;
            case Fx.SOLAR -> 0xFFD23A;
            case Fx.TEMPESTAD -> 0x8AA8FF;
            case Fx.ESQUIRLA -> 0xC07CFF;
            case Fx.CICLON -> 0x7FE8D8;
            case Fx.MINADOR -> 0xFF3A3A;
            case Fx.CENIZA -> 0x3CFF9E;
            case Fx.SAETA -> 0xFFE9A0;
            case Fx.GUADANA -> 0xFF1E46;
            case Fx.PICA -> 0x4FA8FF;
            case Fx.YUNQUE -> 0xFF8C3A;
            case Fx.CHACAL -> 0xE8D06A;
            case Fx.TRABUCO -> 0xE83A2A;
            case Fx.COYOTE -> 0xE8B070;
            case Fx.GRANIZO -> 0xB8E0FF;
            case Fx.LINCE -> 0x40FFD0;
            case Fx.RUGIDO -> 0xFF6A1A;
            case Fx.METEORO -> 0xFF3A1A;
            case Fx.PULSAR -> 0xFF40E0;
            case Fx.FALLA -> 0xE8A23C;
            case Fx.CENTINELA -> 0x34F5C5;
            case Fx.RETORNO -> 0xFFD84A;
            case Fx.MAREA -> 0x3AB4FF;
            case Fx.MANGUAL -> 0xB04CFF;
            case Fx.ARIETE -> 0x5AE0FF;
            // v6: armas realistas (el color de su nombre; la retícula toma este tono)
            case Fx.BISONTE -> 0xD6DADE;
            case Fx.VIBORA -> 0xE8604C;
            case Fx.TEJON -> 0xB8BEC6;
            case Fx.SOMBRA -> 0xC8B08A;
            case Fx.GALGO -> 0xE0BE6A;
            case Fx.AZOR -> 0xC8A878;
            case Fx.CONDOR -> 0xA8B07A;
            case Fx.HURON -> 0xA6ACB4;
            case Fx.BUHO -> 0x8EA4B8;
            case Fx.PIRANA -> 0xE07A5A;
            case Fx.JABALI -> 0xC9A97E;
            case Fx.AVALANCHA -> 0x9FAA78;
            case Fx.DILUVIO -> 0xB4BA8A;
            case Fx.VOLCAN -> 0xE8823A;
            case Fx.MAMUT -> 0xD2B48C;
            // v7: series NEÓN, CHATARRA y XENO
            case Fx.PIXEL -> 0xFF2BD6;
            case Fx.VOLTIO -> 0x20F0FF;
            case Fx.ESTATICA -> 0x9DFF2E;
            case Fx.DIODO -> 0xC8FF30;
            case Fx.FUSIBLE -> 0xFFB020;
            case Fx.ANTENA -> 0x9B5CFF;
            case Fx.VINILO -> 0xFF4FD8;
            case Fx.BAJO -> 0x40C8FF;
            case Fx.NEXO -> 0x5CFF9A;
            case Fx.NEON -> 0xFF2BD6;
            case Fx.RELE -> 0xFFB020;
            case Fx.FRECUENCIA -> 0x9B5CFF;
            case Fx.TUBERIA -> 0xC8783C;
            case Fx.CHAPA -> 0xC85A3C;
            case Fx.CLAVO -> 0xE8B830;
            case Fx.SERRUCHO -> 0x5C9AE0;
            case Fx.PERNO -> 0xB87A4A;
            case Fx.MOLOTOV -> 0xFF7A1A;
            case Fx.BIDON -> 0xE03A28;
            case Fx.CACHARRO -> 0xD84A30;
            case Fx.MANIVELA -> 0xB8A060;
            case Fx.GARFIO -> 0xA8B0B8;
            case Fx.PALANCA -> 0xC86A3A;
            case Fx.OXIDO -> 0xC0602A;
            case Fx.ESPORA -> 0xB4FF50;
            case Fx.GLIFO -> 0xE650FF;
            case Fx.ABISMO -> 0xAA5AFF;
            case Fx.LARVA -> 0xBEFF46;
            case Fx.ECLIPSE -> 0xFFB046;
            case Fx.NEBULA -> 0xFF78DC;
            case Fx.SIMBIONTE -> 0x3CFFA0;
            case Fx.ETER -> 0x96DCFF;
            case Fx.CENIT -> 0xFFECA0;
            case Fx.UMBRAL -> 0xA05AFF;
            case Fx.OBSERVADOR -> 0xFF5A46;
            case Fx.AGUIJON -> 0xAAFF3C;
            default -> 0xFFFFFF;
        };
    }

    /** Núcleo casi blanco con un toque del color del arma. */
    public static int hot(int style) {
        return switch (style) {
            case Fx.AGUJA, Fx.TRUENO, Fx.ESCARCHA -> 0xE8FDFF;
            case Fx.AVISPON -> 0xF0FFD8;
            case Fx.VORTICE, Fx.ECO -> 0xF4E8FF;
            case Fx.CIZALLA, Fx.COLIBRI -> 0xFFE6F4;
            case Fx.SERPENTINA, Fx.CHISPA -> 0xF0F6FF;
            case Fx.SIERRA -> 0xE6FFF4;
            case Fx.ARPON, Fx.CICLON, Fx.PICA -> 0xE8FAFF;
            case Fx.PRISMA -> 0xFFFFFF;
            case Fx.CAUSTICA -> 0xF4FFD0;
            case Fx.TEMPESTAD -> 0xEEF2FF;
            case Fx.ESQUIRLA -> 0xF6E8FF;
            case Fx.CENIZA -> 0xE0FFF0;
            case Fx.GUADANA -> 0xFFE0E6;
            case Fx.GRANIZO, Fx.MAREA, Fx.ARIETE -> 0xEEF8FF;
            case Fx.LINCE, Fx.CENTINELA -> 0xE0FFF6;
            case Fx.PULSAR -> 0xFFE0FA;
            case Fx.MANGUAL -> 0xF4E4FF;
            case Fx.PIXEL -> 0xFFE0F8;
            case Fx.VOLTIO -> 0xE8FFFF;
            case Fx.ESTATICA -> 0xF0FFD8;
            case Fx.DIODO -> 0xF8FFD8;
            case Fx.FUSIBLE -> 0xFFF4D0;
            case Fx.ANTENA -> 0xF0E8FF;
            case Fx.VINILO -> 0xFFE8F8;
            case Fx.BAJO -> 0xE8F8FF;
            case Fx.NEXO -> 0xE8FFF0;
            case Fx.NEON -> 0xFFE0F8;
            case Fx.RELE -> 0xFFF4D0;
            case Fx.FRECUENCIA -> 0xF0E8FF;
            case Fx.MOLOTOV -> 0xFFF0C0;
            case Fx.BIDON -> 0xFFE8C0;
            case Fx.ESPORA -> 0xF4FFD8;
            case Fx.GLIFO -> 0xFCE8FF;
            case Fx.ABISMO -> 0xF0E4FF;
            case Fx.LARVA -> 0xF8FFD8;
            case Fx.ECLIPSE -> 0xFFF4D0;
            case Fx.NEBULA -> 0xFFE8F8;
            case Fx.SIMBIONTE -> 0xE0FFF0;
            case Fx.ETER -> 0xF0FAFF;
            case Fx.CENIT -> 0xFFFCF0;
            case Fx.UMBRAL -> 0xF0E4FF;
            case Fx.OBSERVADOR -> 0xFFE8E0;
            case Fx.AGUIJON -> 0xF4FFD8;
            default -> 0xFFF4DC;
        };
    }

    /** Color de las chispas. */
    public static int spark(int style) {
        return switch (style) {
            case Fx.AGUJA, Fx.TRUENO -> 0x9FF4FF;
            case Fx.AVISPON -> 0xC8FF6A;
            case Fx.VORTICE -> 0xD2A8FF;
            case Fx.CIZALLA -> 0xFF7AB0;
            case Fx.SERPENTINA, Fx.CHISPA -> 0xD8E8FF;
            case Fx.ESCARCHA -> 0xDDFBFF;
            case Fx.ECO -> 0xF0D8FF;
            case Fx.COLIBRI -> 0xFFB0E0;
            case Fx.SIERRA -> 0x9FFFD6;
            case Fx.ARPON -> 0xA8F0FF;
            case Fx.PRISMA -> 0xFFFFFF;
            case Fx.CAUSTICA -> 0xD8FF70;
            case Fx.TEMPESTAD -> 0xC8D8FF;
            case Fx.ESQUIRLA -> 0xE0B8FF;
            case Fx.CICLON -> 0xC8FFF4;
            case Fx.CENIZA -> 0xA8FFD0;
            case Fx.GUADANA -> 0xFF7A90;
            case Fx.PICA -> 0xB8DCFF;
            case Fx.TRABUCO, Fx.METEORO -> 0xFF9A60;
            case Fx.GRANIZO -> 0xD8EEFF;
            case Fx.LINCE, Fx.CENTINELA -> 0xA0FFE6;
            case Fx.PULSAR -> 0xFFA0F0;
            case Fx.RETORNO -> 0xFFE890;
            case Fx.MAREA -> 0xB0E0FF;
            case Fx.MANGUAL -> 0xD8A8FF;
            case Fx.ARIETE -> 0xB0F0FF;
            case Fx.PIXEL -> 0xFF8AE8;
            case Fx.VOLTIO -> 0xA0F8FF;
            case Fx.ESTATICA -> 0xD0FF90;
            case Fx.DIODO -> 0xE8FF90;
            case Fx.FUSIBLE -> 0xFFE070;
            case Fx.ANTENA -> 0xC8A8FF;
            case Fx.VINILO -> 0xFFE070;
            case Fx.BAJO -> 0xA8E8FF;
            case Fx.NEXO -> 0xA8FFC8;
            case Fx.NEON -> 0x80F8FF;
            case Fx.RELE -> 0xFFE070;
            case Fx.FRECUENCIA -> 0xC8A8FF;
            case Fx.TUBERIA -> 0xFFC070;
            case Fx.CHAPA -> 0xFFC070;
            case Fx.CLAVO -> 0xFFE0A0;
            case Fx.SERRUCHO -> 0xFFB060;
            case Fx.PERNO -> 0xFFD090;
            case Fx.MOLOTOV -> 0xFFA040;
            case Fx.BIDON -> 0xFFB050;
            case Fx.CACHARRO -> 0xFFB060;
            case Fx.MANIVELA -> 0xFFC070;
            case Fx.GARFIO -> 0xFFE0B0;
            case Fx.PALANCA -> 0xFFB060;
            case Fx.OXIDO -> 0xFFA050;
            case Fx.ESPORA -> 0xE0FF90;
            case Fx.GLIFO -> 0xF0A0FF;
            case Fx.ABISMO -> 0xD8B0FF;
            case Fx.LARVA -> 0xE8FF90;
            case Fx.ECLIPSE -> 0xFFD080;
            case Fx.NEBULA -> 0xFFB0E8;
            case Fx.SIMBIONTE -> 0xA0FFD0;
            case Fx.ETER -> 0xD0F0FF;
            case Fx.CENIT -> 0xFFF0C0;
            case Fx.UMBRAL -> 0xD0A8FF;
            case Fx.OBSERVADOR -> 0xFFA090;
            case Fx.AGUIJON -> 0xE0FF90;
            default -> 0xFFC870;
        };
    }

    /** Colores de los cinco rayos del PRISMA. */
    public static final int[] RAINBOW = {0xFF3B4A, 0xFFB43B, 0x6BFF5A, 0x3BC8FF, 0xB45CFF};

    /** Mezcla lineal de dos colores RGB. */
    public static int mix(int a, int b, float t) {
        float k = Math.max(0.0F, Math.min(1.0F, t));
        int r = Math.round(((a >> 16) & 0xFF) * (1 - k) + ((b >> 16) & 0xFF) * k);
        int g = Math.round(((a >> 8) & 0xFF) * (1 - k) + ((b >> 8) & 0xFF) * k);
        int bl = Math.round((a & 0xFF) * (1 - k) + (b & 0xFF) * k);
        return r << 16 | g << 8 | bl;
    }
}
