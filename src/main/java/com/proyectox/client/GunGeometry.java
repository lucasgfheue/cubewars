package com.proyectox.client;

import javax.annotation.Nullable;

/**
 * Geometría de las armas en primera persona (generado por tools/display.py; no editar):
 * posición del arma al apuntar, giro que se anula al apuntar y boca del cañón (espacio de la mano).
 */
public final class GunGeometry {
    private GunGeometry() {}

    /** optic: 0 = sin mira (el arma baja bajo la retícula), 1 = alza y punto, 2 = réflex, 3 = holográfica, 4 = visor. */
    public record Data(float adsX, float adsY, float adsZ, float yaw, float tipX, float tipY, float tipZ, int optic) {}

    @Nullable
    public static Data of(String key) {
        return switch (key) {
            case "aguja" -> new Data(-0.0817F, -0.4302F, -0.6102F, 6.00F, 0.0036F, 0.2101F, -0.7519F, 0);
            case "avispon" -> new Data(-0.1009F, -0.5012F, -0.5598F, 6.00F, -0.0017F, 0.2496F, -0.8038F, 0);
            case "colmena" -> new Data(-0.1035F, -0.4902F, -0.6083F, 6.00F, 0.0680F, 0.2142F, -0.6915F, 0);
            case "vortice" -> new Data(-0.0822F, -0.5918F, -0.3538F, 6.00F, -0.0235F, 0.2901F, -1.0103F, 0);
            case "chispa" -> new Data(-0.0742F, -0.3990F, -0.2831F, 6.00F, -0.0105F, 0.2269F, -0.8100F, 0);
            case "remache" -> new Data(-0.0819F, -0.3258F, -0.5284F, 6.00F, -0.0051F, 0.2111F, -0.8318F, 0);
            case "trituradora" -> new Data(-0.1399F, -0.4960F, -0.5325F, 6.00F, 0.0601F, 0.3039F, -0.7667F, 0);
            case "rotor" -> new Data(-0.1152F, -0.4052F, -0.3063F, 6.00F, 0.0369F, 0.1885F, -0.9883F, 0);
            case "halcon" -> new Data(-0.0527F, -0.5347F, -0.3563F, 6.00F, -0.0080F, 0.2970F, -0.5808F, 0);
            case "trueno" -> new Data(-0.0091F, -0.6531F, -0.5111F, 6.00F, -0.0070F, 0.3147F, -0.8528F, 0);
            case "soplete" -> new Data(-0.0820F, -0.4560F, -0.4575F, 6.00F, -0.0127F, 0.2369F, -0.9055F, 0);
            case "escarcha" -> new Data(-0.0823F, -0.4974F, -0.4922F, 6.00F, -0.0087F, 0.2548F, -0.8708F, 0);
            case "eco" -> new Data(-0.1396F, -0.4688F, -0.4686F, 6.00F, 0.0530F, 0.2246F, -0.8305F, 0);
            case "colibri" -> new Data(-0.0742F, -0.2863F, -0.4415F, 6.00F, -0.0083F, 0.2008F, -0.7897F, 0);
            case "sierra" -> new Data(-0.0823F, -0.5183F, -0.4853F, 6.00F, -0.0111F, 0.3401F, -0.8771F, 0);
            case "centella" -> new Data(-0.0740F, -0.4867F, -0.2719F, 6.00F, -0.0268F, 0.3559F, -0.9628F, 0);
            case "arpon" -> new Data(-0.1119F, -0.3773F, -0.3781F, 6.00F, -0.0211F, 0.2029F, -0.9844F, 0);
            case "mortero" -> new Data(-0.1320F, -0.4656F, -0.4102F, 6.00F, 0.0656F, 0.2526F, -0.7731F, 0);
            case "prisma" -> new Data(-0.0821F, -0.4330F, -0.4529F, 6.00F, -0.0131F, 0.2336F, -0.9097F, 0);
            case "enjambre" -> new Data(-0.1399F, -0.5921F, -0.5770F, 6.00F, 0.0644F, 0.2462F, -0.7258F, 0);
            case "caustica" -> new Data(-0.0822F, -0.5175F, -0.5217F, 6.00F, -0.0058F, 0.2467F, -0.8419F, 0);
            case "solar" -> new Data(-0.1398F, -0.5188F, -0.5211F, 6.00F, 0.0587F, 0.2226F, -0.7785F, 0);
            case "tempestad" -> new Data(-0.1396F, -0.3338F, -0.5717F, 6.00F, 0.0639F, 0.2623F, -0.7268F, 0);
            case "esquirla" -> new Data(-0.0819F, -0.4749F, -0.4780F, 6.00F, -0.0106F, 0.2352F, -0.8846F, 0);
            case "ciclon" -> new Data(-0.1397F, -0.4275F, -0.5408F, 6.00F, 0.0607F, 0.2166F, -0.7588F, 0);
            case "minador" -> new Data(-0.1397F, -0.3775F, -0.5450F, 6.00F, 0.0611F, 0.2216F, -0.7555F, 0);
            case "ceniza" -> new Data(-0.0822F, -0.4156F, -0.5139F, 6.00F, -0.0067F, 0.2548F, -0.8502F, 0);
            case "yunque" -> new Data(-0.0744F, -0.5100F, -0.2837F, 6.00F, -0.0254F, 0.3185F, -0.9531F, 0);
            case "chacal" -> new Data(-0.0926F, -0.3863F, -0.4547F, 6.00F, -0.0128F, 0.2105F, -0.9086F, 0);
            case "trabuco" -> new Data(-0.0956F, -0.3856F, -0.5313F, 6.00F, -0.0050F, 0.2703F, -0.8327F, 0);
            case "coyote" -> new Data(-0.0744F, -0.4527F, -0.2526F, 6.00F, -0.0285F, 0.3382F, -0.9827F, 0);
            case "granizo" -> new Data(-0.1710F, -0.3768F, -0.4980F, 6.00F, 0.0565F, 0.1947F, -0.8014F, 0);
            case "lince" -> new Data(-0.1074F, -0.3989F, -0.4595F, 6.00F, -0.0279F, 0.1911F, -1.0523F, 0);
            case "rugido" -> new Data(-0.1484F, -0.3575F, -0.5092F, 6.00F, 0.0575F, 0.2112F, -0.7919F, 0);
            case "meteoro" -> new Data(-0.0823F, -0.5607F, -0.4880F, 6.00F, -0.0095F, 0.2750F, -0.8778F, 0);
            case "pulsar" -> new Data(-0.1399F, -0.4086F, -0.4699F, 6.00F, 0.0534F, 0.2167F, -0.8297F, 0);
            case "falla" -> new Data(-0.1397F, -0.4820F, -0.5389F, 6.00F, 0.0601F, 0.0432F, -0.7642F, 0);
            case "centinela" -> new Data(-0.0824F, -0.5390F, -0.5139F, 6.00F, -0.0067F, 0.2629F, -0.8522F, 0);
            case "retorno" -> new Data(-0.0746F, -0.6111F, -0.3283F, 6.00F, -0.0210F, 0.3329F, -0.9129F, 0);
            case "marea" -> new Data(-0.0819F, -0.4649F, -0.4929F, 6.00F, -0.0092F, 0.2426F, -0.8718F, 0);
            case "bisonte" -> new Data(-0.0744F, -0.4109F, 0.0359F, 6.00F, -0.0323F, 0.2892F, -1.0189F, 1);
            case "vibora" -> new Data(-0.0745F, -0.4382F, 0.1588F, 6.00F, -0.0134F, 0.3037F, -0.8394F, 2);
            case "tejon" -> new Data(-0.0743F, -0.4037F, -0.0346F, 6.00F, 0.0069F, 0.2982F, -0.6450F, 1);
            case "sombra" -> new Data(-0.0744F, -0.4006F, -0.0474F, 6.00F, -0.0375F, 0.2952F, -1.0682F, 1);
            case "galgo" -> new Data(-0.0741F, -0.3976F, 0.0574F, 6.00F, -0.0062F, 0.2948F, -0.7680F, 2);
            case "azor" -> new Data(-0.0822F, -0.3471F, 0.0785F, 6.00F, -0.0113F, 0.2478F, -0.8936F, 3);
            case "condor" -> new Data(-0.0824F, -0.2972F, -0.0101F, 6.00F, -0.0257F, 0.1913F, -1.0334F, 4);
            case "huron" -> new Data(-0.0823F, -0.3216F, 0.0778F, 6.00F, 0.0179F, 0.2153F, -0.6169F, 2);
            case "buho" -> new Data(-0.0820F, -0.3020F, -0.1360F, 6.00F, -0.0050F, 0.2354F, -0.8322F, 1);
            case "pirana" -> new Data(-0.0745F, -0.3577F, -0.1253F, 6.00F, 0.0150F, 0.2490F, -0.5700F, 1);
            case "jabali" -> new Data(-0.0824F, -0.3069F, -0.0141F, 6.00F, -0.0142F, 0.2256F, -0.9238F, 1);
            case "avalancha" -> new Data(-0.0824F, -0.2709F, -0.1761F, 6.00F, -0.0130F, 0.2428F, -0.9125F, 1);
            case "diluvio" -> new Data(0.3000F, -0.5000F, -0.6600F, 6.00F, 0.0323F, 0.1765F, -1.0300F, 0);
            case "volcan" -> new Data(-0.1396F, -0.3438F, 0.1680F, 6.00F, 0.0598F, 0.2245F, -0.7669F, 2);
            case "mamut" -> new Data(-0.1400F, -0.2743F, -0.0509F, 6.00F, 0.0242F, 0.1649F, -1.1091F, 4);
            case "pixel" -> new Data(-0.0824F, -0.4015F, -0.2930F, 6.00F, 0.0045F, 0.3071F, -0.7453F, 0);
            case "voltio" -> new Data(-0.0820F, -0.3918F, -0.4751F, 6.00F, -0.0035F, 0.2611F, -0.8183F, 0);
            case "estatica" -> new Data(-0.0849F, -0.4032F, -0.4468F, 6.00F, -0.0211F, 0.3161F, -0.9865F, 0);
            case "diodo" -> new Data(-0.0824F, -0.4122F, -0.6253F, 6.00F, -0.0104F, 0.2854F, -0.8880F, 0);
            case "fusible" -> new Data(-0.0820F, -0.5863F, -0.4001F, 6.00F, -0.0085F, 0.3312F, -0.8651F, 0);
            case "antena" -> new Data(-0.0951F, -0.3921F, -0.5454F, 6.00F, -0.0399F, 0.2225F, -1.1634F, 0);
            case "vinilo" -> new Data(-0.0823F, -0.5281F, -0.2921F, 6.00F, -0.0041F, 0.4095F, -0.8262F, 0);
            case "bajo" -> new Data(-0.1549F, -0.6333F, -0.4551F, 6.00F, 0.0852F, 0.3642F, -0.5270F, 0);
            case "nexo" -> new Data(-0.0820F, -0.5643F, -0.3034F, 6.00F, -0.0235F, 0.4223F, -0.8626F, 0);
            case "frecuencia" -> new Data(-0.0823F, -0.5919F, -0.4695F, 6.00F, -0.0061F, 0.2943F, -0.8448F, 0);
            case "tuberia" -> new Data(-0.0875F, -0.3790F, -0.2935F, 6.00F, -0.0036F, 0.2772F, -0.8213F, 0);
            case "chapa" -> new Data(-0.0913F, -0.3686F, -0.5041F, 6.00F, -0.0216F, 0.2585F, -0.9566F, 0);
            case "clavo" -> new Data(-0.0824F, -0.6220F, -0.2950F, 6.00F, -0.0036F, 0.2093F, -0.8222F, 0);
            case "serrucho" -> new Data(-0.1587F, -0.5003F, -0.3164F, 6.00F, 0.1386F, 0.2702F, -0.9154F, 0);
            case "perno" -> new Data(-0.0823F, -0.3402F, -0.5735F, 6.00F, 0.0047F, 0.2645F, -0.7428F, 0);
            case "molotov" -> new Data(-0.1499F, -0.4050F, -0.5482F, 6.00F, 0.0674F, 0.2314F, -0.7252F, 0);
            case "bidon" -> new Data(-0.1449F, -0.4950F, -0.3040F, 6.00F, 0.0482F, 0.3476F, -0.9676F, 0);
            case "cacharro" -> new Data(-0.1397F, -0.5062F, -0.3046F, 6.00F, 0.0388F, 0.2873F, -0.9668F, 0);
            case "manivela" -> new Data(-0.1761F, -0.4388F, -0.5585F, 6.00F, 0.0497F, 0.2346F, -0.8621F, 0);
            case "garfio" -> new Data(-0.1268F, -0.4051F, -0.5249F, 6.00F, -0.0054F, 0.2580F, -0.8390F, 0);
            case "espora" -> new Data(-0.0811F, -0.4957F, -0.3282F, 6.00F, -0.0005F, 0.2950F, -0.7907F, 0);
            case "glifo" -> new Data(-0.0824F, -0.4117F, -0.4817F, 6.00F, -0.0203F, 0.2295F, -0.9810F, 0);
            case "abismo" -> new Data(-0.0819F, -0.4729F, -0.3299F, 6.00F, -0.0110F, 0.2926F, -0.8873F, 0);
            case "larva" -> new Data(-0.0819F, -0.4523F, -0.3068F, 6.00F, -0.0133F, 0.2553F, -0.9093F, 0);
            case "eclipse" -> new Data(-0.1399F, -0.4759F, -0.3656F, 6.00F, 0.0350F, 0.2424F, -1.0047F, 0);
            case "nebula" -> new Data(-0.0819F, -0.4905F, -0.3063F, 6.00F, -0.0030F, 0.3166F, -0.8126F, 0);
            case "eter" -> new Data(-0.0820F, -0.4466F, -0.2939F, 6.00F, -0.0094F, 0.2750F, -0.8740F, 0);
            case "cenit" -> new Data(-0.0823F, -0.5359F, -0.4120F, 6.00F, -0.0072F, 0.2872F, -0.8556F, 0);
            case "umbral" -> new Data(-0.0822F, -0.5426F, -0.2943F, 6.00F, -0.0078F, 0.2927F, -0.8737F, 0);
            case "observador" -> new Data(-0.0823F, -0.4497F, -0.2971F, 6.00F, -0.0090F, 0.3151F, -0.8710F, 0);
            default -> null;
        };
    }

    /** Eje de la broca del TALADRO en el espacio de la mano: punto (x, y, z) y dirección (x, y, z). */
    public static final float[] DRILL_AXIS = {0.0706F, 0.2045F, -0.6621F, -0.1045F, 0.0000F, -0.9945F};
}
