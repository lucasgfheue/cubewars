package com.proyectox.client.fx;

import com.proyectox.fx.Fx;
import javax.annotation.Nullable;

/**
 * Armas de fuego "de pólvora" (las de balas de la serie PX-36…PX-41 y las militares PX-51…PX-65): en lugar de los
 * destellos de energía, su disparo es un fogonazo de fuego realista con humo, chispas de pólvora, casquillos y
 * trazadoras discretas. Aquí está el perfil de cada una.
 */
public final class GunFx {
    private GunFx() {}

    /** Casquillos que expulsa el arma. */
    public static final int NONE = 0;
    public static final int PISTOL = 1;
    public static final int RIFLE = 2;
    public static final int SHELL = 3;
    public static final int BIG = 4;

    /**
     * @param size       escala del fogonazo
     * @param flames     lenguas de fuego que salen despedidas
     * @param smoke      cantidad de humo por disparo
     * @param casing     tipo de casquillo
     * @param suppressed con silenciador (sin fogonazo y con muy poco humo)
     * @param brake      freno de boca (chorros de fuego a los lados)
     * @param comp       compensador (chorros de fuego hacia arriba)
     * @param gap        revólver (llamarada por el hueco entre el tambor y el cañón)
     * @param vapor      estela de aire que queda tras la bala (francotiradores)
     */
    public record Profile(float size, int flames, float smoke, int casing, boolean suppressed, boolean brake, boolean comp, boolean gap,
                          boolean vapor) {}

    private static Profile p(float size, int flames, float smoke, int casing) {
        return new Profile(size, flames, smoke, casing, false, false, false, false, false);
    }

    @Nullable
    public static Profile of(int style) {
        return switch (style) {
            case Fx.YUNQUE -> new Profile(1.1F, 3, 0.9F, PISTOL, false, false, true, false, false);
            case Fx.CHACAL -> new Profile(1.0F, 3, 0.7F, RIFLE, false, true, false, false, false);
            case Fx.TRABUCO -> p(1.5F, 6, 1.5F, SHELL);
            case Fx.COYOTE -> new Profile(1.25F, 4, 1.1F, NONE, false, false, false, true, false);
            case Fx.GRANIZO -> p(0.9F, 2, 0.55F, RIFLE);
            case Fx.LINCE -> new Profile(1.5F, 4, 1.4F, RIFLE, false, true, false, false, true);
            case Fx.BISONTE -> new Profile(1.5F, 5, 1.3F, NONE, false, false, false, true, false);
            case Fx.VIBORA -> new Profile(1.15F, 3, 1.0F, NONE, false, false, false, true, false);
            case Fx.TEJON -> p(0.8F, 2, 0.6F, PISTOL);
            case Fx.SOMBRA -> new Profile(0.5F, 0, 0.45F, PISTOL, true, false, false, false, false);
            case Fx.GALGO -> new Profile(0.9F, 2, 0.7F, PISTOL, false, false, true, false, false);
            case Fx.AZOR -> p(0.9F, 2, 0.6F, RIFLE);
            case Fx.CONDOR -> new Profile(1.3F, 3, 1.1F, RIFLE, false, true, false, false, true);
            case Fx.HURON -> p(0.8F, 2, 0.5F, PISTOL);
            case Fx.BUHO -> new Profile(0.5F, 0, 0.4F, PISTOL, true, false, false, false, false);
            case Fx.PIRANA -> p(0.75F, 2, 0.45F, PISTOL);
            case Fx.JABALI -> p(1.5F, 5, 1.4F, SHELL);
            case Fx.AVALANCHA -> p(1.4F, 4, 1.0F, SHELL);
            case Fx.DILUVIO -> p(1.0F, 2, 0.5F, RIFLE);
            case Fx.VOLCAN -> p(1.0F, 2, 1.8F, NONE);
            case Fx.MAMUT -> new Profile(2.2F, 6, 2.2F, BIG, false, true, false, false, true);
            // v7: las armas de chatarra que disparan pólvora
            case Fx.TUBERIA -> p(1.3F, 4, 1.5F, NONE);
            case Fx.CHAPA -> p(1.6F, 6, 1.7F, SHELL);
            case Fx.MANIVELA -> p(0.85F, 2, 0.7F, RIFLE);
            default -> null;
        };
    }

    public static boolean is(int style) {
        return of(style) != null;
    }
}
