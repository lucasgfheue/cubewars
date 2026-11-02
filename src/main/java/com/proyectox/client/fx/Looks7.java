package com.proyectox.client.fx;

import com.proyectox.entity.SpecBolt;
import com.proyectox.entity.SpecField;
import com.proyectox.fx.Fx;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/** Aspecto de los proyectiles y las zonas de la serie v7 (los que tienen modelo 3D se dibujan además en {@link com.proyectox.client.Prop7Renderer}). */
public final class Looks7 {
    private Looks7() {}

    private static final Vector3f ZERO = new Vector3f();
    private static final Vector3f UP = new Vector3f(0, 1, 0);

    private static Vector3f dir(SpecBolt e) {
        Vec3 h = e.heading();
        return new Vector3f((float) h.x, (float) h.y, (float) h.z);
    }

    private static List<Vec3> trail(SpecBolt e, float pt, int max) {
        List<Vec3> out = new ArrayList<>();
        out.add(e.getPosition(pt));
        int n = Math.min(max, e.trailSize());
        for (int i = 0; i < n; i++) out.add(e.trailPoint(i));
        return out;
    }

    private static float time(Entity e, float pt) {
        return e.tickCount + pt;
    }

    public static void bolt(SpecBolt e, float pt, Draw d) {
        int style = e.style();
        int main = Palette.main(style);
        int hot = Palette.hot(style);
        Vector3f dir = dir(e);
        float t = time(e, pt);
        Vector3f tangent = new Vector3f(dir.x, 0, dir.z);
        if (tangent.lengthSquared() < 1.0E-6) tangent.set(1, 0, 0);
        switch (style) {
            case Fx.PIXEL -> {
                d.trail(FxAtlas.BEAM, trail(e, pt, 5), 0.09F, main, 0x600050, 0.85F);
                d.cross(true, FxAtlas.BOLT, ZERO, dir, 0.9F, 0.2F, 0.09F, main, 1.0F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 0.32F, 0.0F, main, 0.8F);
                d.billboard(true, FxAtlas.CORE, ZERO, 0.1F, 0.0F, hot, 1.0F);
            }
            case Fx.VOLTIO -> {
                float flick = 0.8F + 0.2F * Mth.sin(t * 7.0F);
                d.trail(FxAtlas.BEAM_NOISE, trail(e, pt, 5), 0.08F, main, 0x004060, 0.85F);
                d.cross(true, FxAtlas.BOLT, ZERO, dir, 0.8F, 0.2F, 0.08F, main, 1.0F);
                d.billboard(true, FxAtlas.STAR4, ZERO, 0.24F * flick, t * 0.9F, hot, 0.9F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 0.3F, 0.0F, main, 0.7F);
            }
            case Fx.FUSIBLE -> {
                boolean on = ((int) (t * 0.6F)) % 2 == 0;
                d.billboard(false, FxAtlas.GRENADE, ZERO, 0.17F, t * 0.3F, 0xFFE6A0, 1.0F);
                d.billboard(true, FxAtlas.GLOW, ZERO, on ? 0.45F : 0.25F, 0.0F, main, on ? 0.9F : 0.5F);
                if (on) d.billboard(true, FxAtlas.CORE, ZERO, 0.08F, 0.0F, 0xFFFFFF, 1.0F);
            }
            case Fx.VINILO -> {
                d.trail(FxAtlas.BEAM, trail(e, pt, 6), 0.14F, main, 0x500040, 0.5F);
                d.flat(true, FxAtlas.GLOW, ZERO, UP, tangent, 0.6F, 0.6F, 0.0F, main, 0.55F);
                d.flat(false, FxAtlas.RING, ZERO, UP, tangent, 0.38F, 0.38F, -t * 1.4F, 0x18161C, 1.0F);
                d.flat(true, FxAtlas.RING, new Vector3f(0, 0.01F, 0), UP, tangent, 0.4F, 0.4F, -t * 1.4F, main, 0.9F);
                d.flat(true, FxAtlas.CORE, new Vector3f(0, 0.02F, 0), UP, tangent, 0.12F, 0.12F, 0.0F, 0xFFE070, 1.0F);
            }
            case Fx.NEXO -> {
                float blink = ((int) (t * 0.5F)) % 2 == 0 ? 1.0F : 0.4F;
                d.trail(FxAtlas.BEAM_THIN, trail(e, pt, 6), 0.05F, main, 0x004020, 0.7F);
                d.billboard(true, FxAtlas.STAR4, ZERO, 0.22F, t * 0.3F, main, 0.9F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 0.3F, 0.0F, main, 0.6F);
                d.billboard(true, FxAtlas.CORE, new Vector3f(0, 0.06F, 0), 0.05F, 0.0F, 0xFF3020, blink);
            }
            case Fx.CLAVO -> {
                d.cross(false, FxAtlas.BOLT, ZERO, dir, 0.35F, 0.12F, 0.025F, 0xB8B6B0, 1.0F);
                d.cross(true, FxAtlas.SPARK, ZERO, dir, 0.6F, 0.0F, 0.02F, 0xFFE0B0, 0.35F);
            }
            case Fx.SERRUCHO -> {
                d.flat(false, FxAtlas.SAW, ZERO, UP, tangent, 0.4F, 0.4F, -t * 1.6F, 0xC8784A, 1.0F);
                d.flat(true, FxAtlas.SAW_BLUR, new Vector3f(0, 0.01F, 0), UP, tangent, 0.43F, 0.43F, -t * 1.6F, 0xFFB070, 0.6F);
            }
            case Fx.PERNO -> {
                d.cross(false, FxAtlas.BOLT, ZERO, dir, 1.1F, 0.25F, 0.05F, 0x7A4A2A, 1.0F);
                d.cross(true, FxAtlas.BEAM_THIN, ZERO, dir, 1.6F, 0.0F, 0.04F, 0xFFE8C0, 0.25F);
                d.billboard(true, FxAtlas.GLINT, new Vector3f(dir).mul(0.25F), 0.1F, t * 0.4F, 0xFFFFFF, 0.8F);
            }
            case Fx.MOLOTOV -> {
                // el trapo arde en el cuello de la botella (la botella es un modelo 3D)
                Vector3f neck = new Vector3f(dir).mul(0.2F);
                int frame = ((int) (t * 1.5F)) % FxAtlas.FLAME.length;
                d.billboard(true, FxAtlas.FLAME[frame], neck, 0.16F, 0.0F, 0xFFA040, 1.0F);
                d.billboard(true, FxAtlas.GLOW, neck, 0.35F, 0.0F, 0xFF7020, 0.6F);
            }
            case Fx.BIDON -> d.billboard(true, FxAtlas.GLOW, new Vector3f(dir).mul(-0.3F), 0.2F, 0.0F, 0xFF8040, 0.25F);
            case Fx.CACHARRO -> {
                d.billboard(false, FxAtlas.DEBRIS, ZERO, 0.2F, t * 0.6F, 0x8A7A6A, 1.0F);
                d.billboard(false, FxAtlas.DEBRIS, new Vector3f(0.04F, 0.03F, 0), 0.14F, -t * 0.4F, 0x6A3A22, 1.0F);
            }
            case Fx.GARFIO -> chain(e, pt, d);
            case Fx.ESPORA -> {
                float pulse = 0.85F + 0.15F * Mth.sin(t * 1.3F);
                d.trail(FxAtlas.BEAM, trail(e, pt, 6), 0.08F, main, 0x204000, 0.5F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 0.38F * pulse, 0.0F, main, 0.75F);
                d.billboard(false, FxAtlas.BUBBLE, ZERO, 0.13F, t * 0.1F, 0xD8FFB0, 0.95F);
                d.billboard(true, FxAtlas.CORE, ZERO, 0.07F, 0.0F, hot, 1.0F);
            }
            case Fx.ABISMO -> {
                Vector3f tilt = new Vector3f(0.35F, 1.0F, 0.2F).normalize();
                d.trail(FxAtlas.BEAM, trail(e, pt, 8), 0.18F, main, 0x200040, 0.6F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 0.8F + 0.1F * Mth.sin(t * 0.8F), 0.0F, main, 0.75F);
                d.flat(true, FxAtlas.ACCRETION, ZERO, tilt, new Vector3f(1, 0, 0), 0.7F, 0.7F, t * 0.35F, hot, 1.0F);
                d.billboard(false, FxAtlas.CORE, ZERO, 0.24F, 0.0F, 0x050008, 1.0F);
                d.billboard(true, FxAtlas.RING, ZERO, 0.3F, 0.0F, hot, 0.8F);
            }
            case Fx.LARVA -> {
                d.billboard(true, FxAtlas.GLOW, ZERO, 0.26F, 0.0F, main, 0.6F);
                d.billboard(false, FxAtlas.BUBBLE, ZERO, 0.12F, t * 0.2F, 0xE8E0B0, 1.0F);
                d.billboard(true, FxAtlas.CORE, ZERO, 0.05F, 0.0F, main, 0.9F);
            }
            case Fx.NEBULA -> {
                for (int i = 0; i < 3; i++) {
                    float a = t * (0.05F + 0.03F * i) + i * 2.1F;
                    d.billboard(true, FxAtlas.CLOUD[i % FxAtlas.CLOUD.length], ZERO, 0.75F - 0.12F * i, a, i == 0 ? main : hot, 0.55F);
                }
                d.billboard(true, FxAtlas.GLOW, ZERO, 1.1F, 0.0F, main, 0.6F);
                d.billboard(false, FxAtlas.ORB_CORE, ZERO, 0.22F, t * 0.1F, 0xFFFFFF, 1.0F);
            }
            case Fx.CENIT -> {
                d.trail(FxAtlas.BEAM, trail(e, pt, 7), 0.07F, main, 0x604000, 0.8F);
                d.cross(true, FxAtlas.BOLT, ZERO, dir, 1.3F, 0.3F, 0.07F, main, 1.0F);
                d.billboard(true, FxAtlas.STAR4, new Vector3f(dir).mul(0.25F), 0.22F, t * 0.3F, hot, 0.9F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 0.3F, 0.0F, main, 0.6F);
            }
            default -> {
                d.cross(true, FxAtlas.BOLT, ZERO, dir, 0.8F, 0.2F, 0.08F, main, 1.0F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 0.3F, 0.0F, main, 0.7F);
            }
        }
    }

    /** GARFIO: la cadena tensa desde el gancho hasta el arma del tirador. */
    private static void chain(SpecBolt e, float pt, Draw d) {
        Entity owner = e.getOwner();
        if (owner == null) return;
        Vec3 hand = Muzzle.firstPerson(owner) ? Muzzle.world(owner, pt) : owner.getPosition(pt).add(0.0, owner.getBbHeight() * 0.7, 0.0);
        Vector3f to = d.local(hand);
        float len = (float) Math.sqrt(to.lengthSquared());
        if (len < 0.1F) return;
        Vector3f mid = new Vector3f(to).mul(0.5F);
        d.streak(false, FxAtlas.ROPE, mid, to, len * 0.5F, len * 0.5F, 0.035F, 0x8A8E92, 1.0F);
    }

    // ---------------------------------------------------------------- zonas

    public static void field(SpecField f, float pt, Draw d) {
        int style = f.style();
        int main = Palette.main(style);
        int hot = Palette.hot(style);
        float t = time(f, pt);
        float life = Math.min(1.0F, (f.tickCount + pt) / 6.0F) * Mth.clamp((f.remaining() - pt) / 10.0F, 0.0F, 1.0F);
        float r = f.radius();
        Vector3f x = new Vector3f(1, 0, 0);
        switch (f.type()) {
            case SpecField.FIRE -> {
                d.flat(true, FxAtlas.GLOW, new Vector3f(0, 0.06F, 0), UP, x, r * 1.3F, r * 1.3F, 0.0F, 0xFF6A20, 0.45F * life);
                d.flat(false, FxAtlas.CRACK, new Vector3f(0, 0.04F, 0), UP, x, r * 0.9F, r * 0.9F, 0.3F, 0x201410, 0.6F * life);
            }
            case SpecField.WELL -> {
                d.flat(true, FxAtlas.VORTEX, ZERO, UP, x, r * 0.8F * life, r * 0.8F * life, t * 0.5F, main, 0.8F);
                d.flat(true, FxAtlas.ACCRETION, ZERO, UP, x, 1.4F * life, 1.4F * life, t * 0.6F, hot, 1.0F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 1.8F * life, 0.0F, main, 0.8F);
                d.billboard(false, FxAtlas.CORE, ZERO, 0.5F * life, 0.0F, 0x050008, 1.0F);
                d.billboard(true, FxAtlas.RING, ZERO, 0.55F * life, 0.0F, hot, 0.9F);
            }
            case SpecField.CLOUD -> {
                for (int i = 0; i < 3; i++) {
                    float a = t * (0.01F + 0.008F * i) + i * 2.0F;
                    Vector3f o = new Vector3f(Mth.cos(a) * r * 0.35F, 0.7F + 0.25F * i, Mth.sin(a) * r * 0.35F);
                    d.billboard(true, FxAtlas.CLOUD[i % FxAtlas.CLOUD.length], o, r * (0.85F - 0.15F * i) * life, a, i == 1 ? hot : main, 0.32F);
                }
            }
            case SpecField.RIFT -> {
                // grieta vertical que mira hacia quien la abrió
                float yaw = (float) Math.toRadians(f.getYRot());
                Vector3f n = new Vector3f(Mth.sin(yaw), 0, Mth.cos(yaw));
                Vector3f side = new Vector3f(n).cross(UP);
                float open = life * (0.85F + 0.15F * Mth.sin(t * 0.7F));
                d.flat(true, FxAtlas.GLOW, ZERO, n, UP, 1.6F * open, 2.4F * open, 0.0F, main, 0.8F);
                d.flat(false, FxAtlas.CRACK, ZERO, n, side, 0.9F * open, 1.9F * open, 1.5708F, 0x0A0012, 1.0F);
                d.flat(true, FxAtlas.CRACK, new Vector3f(n).mul(0.01F), n, side, 1.0F * open, 2.0F * open, 1.5708F, hot, 0.9F);
                d.flat(true, FxAtlas.RING_NOISE, ZERO, n, UP, r * 0.8F * open, r * 0.8F * open, t * 0.2F, main, 0.6F);
            }
            default -> {
                // el ojo es un modelo 3D: aquí solo su brillo
                float pulse = 0.8F + 0.2F * Mth.sin(t * 0.4F);
                d.billboard(true, FxAtlas.GLOW, ZERO, 0.75F * pulse * life, 0.0F, main, 0.45F);
            }
        }
    }
}
