package com.proyectox.client.fx;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.proyectox.client.fx.FxAtlas.Sprite;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * Haz de luz como cinta orientada hacia la cámara: recto (rieles, láseres), en zigzag (rayos),
 * en espiral (la hélice del riel) o con vaivén (la cuerda). Sus extremos pueden seguir al tirador
 * (desde la boca del arma) y a una entidad objetivo.
 */
public class FxBeam extends Particle {
    public enum Shape { STRAIGHT, JAGGED, HELIX, WAVE }

    private final Vec3 from;
    private final Vec3 to;
    @Nullable
    private Entity anchor;
    @Nullable
    private Entity target;
    private int age;
    private final int life;
    private Sprite sprite = FxAtlas.BEAM;
    private boolean additive = true;
    private Shape shape = Shape.STRAIGHT;
    private float width0 = 0.1F, width1 = 0.1F;
    private float r0 = 1, g0 = 1, b0 = 1, r1 = 1, g1 = 1, b1 = 1;
    private float a0 = 1.0F, a1 = 0.0F;
    private float alphaPow = 1.0F;
    private float tile;
    private float scroll;
    private float taper;
    private float jag = 0.3F;
    private int flicker = 2;
    private long seed;
    private float helixRadius = 0.15F;
    private float helixTurns = 1.0F;
    private float helixSpin;
    private float helixPhase;
    private float grow;

    public FxBeam(ClientLevel level, Vec3 from, Vec3 to, int life) {
        super(level, (from.x + to.x) * 0.5, (from.y + to.y) * 0.5, (from.z + to.z) * 0.5);
        this.from = from;
        this.to = to;
        this.life = Math.max(1, life);
        this.seed = level.random.nextLong();
    }

    // ---------------------------------------------------------------- configuración

    public FxBeam sprite(Sprite sprite) {
        this.sprite = sprite;
        return this;
    }

    public FxBeam soft() {
        this.additive = false;
        return this;
    }

    public FxBeam shape(Shape shape) {
        this.shape = shape;
        return this;
    }

    /** Medio ancho al nacer y al morir. */
    public FxBeam width(float from, float to) {
        this.width0 = from;
        this.width1 = to;
        return this;
    }

    public FxBeam color(int rgb) {
        return color(rgb, rgb);
    }

    public FxBeam color(int from, int to) {
        this.r0 = (from >> 16 & 0xFF) / 255.0F;
        this.g0 = (from >> 8 & 0xFF) / 255.0F;
        this.b0 = (from & 0xFF) / 255.0F;
        this.r1 = (to >> 16 & 0xFF) / 255.0F;
        this.g1 = (to >> 8 & 0xFF) / 255.0F;
        this.b1 = (to & 0xFF) / 255.0F;
        return this;
    }

    public FxBeam alpha(float from, float to) {
        this.a0 = from;
        this.a1 = to;
        return this;
    }

    public FxBeam alphaPow(float pow) {
        this.alphaPow = pow;
        return this;
    }

    /** Longitud (en bloques) de una repetición de la textura y desplazamiento por tick. */
    public FxBeam tile(float tile, float scroll) {
        this.tile = tile;
        this.scroll = scroll;
        return this;
    }

    /** Afinado de los extremos (fracción de la longitud). */
    public FxBeam taper(float taper) {
        this.taper = taper;
        return this;
    }

    /** Zigzag: desviación máxima y ticks entre cambios de forma; {@code seed} igual = misma forma. */
    public FxBeam jagged(float amount, int flicker, long seed) {
        this.shape = Shape.JAGGED;
        this.jag = amount;
        this.flicker = Math.max(1, flicker);
        this.seed = seed;
        return this;
    }

    public FxBeam helix(float radius, float turnsPerBlock, float spin, float phase) {
        this.shape = Shape.HELIX;
        this.helixRadius = radius;
        this.helixTurns = turnsPerBlock;
        this.helixSpin = spin;
        this.helixPhase = phase;
        return this;
    }

    /** El haz crece desde el origen hasta el destino en {@code ticks}. */
    public FxBeam grow(float ticks) {
        this.grow = ticks;
        return this;
    }

    public FxBeam anchor(@Nullable Entity shooter) {
        this.anchor = shooter;
        return this;
    }

    public FxBeam target(@Nullable Entity target) {
        this.target = target;
        return this;
    }

    public FxBeam spawn() {
        Minecraft.getInstance().particleEngine.add(this);
        return this;
    }

    // ---------------------------------------------------------------- simulación

    @Override
    public void tick() {
        if (++this.age >= this.life || (this.target != null && !this.target.isAlive())) this.remove();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return this.additive ? FxRender.ADDITIVE : FxRender.SOFT;
    }

    @Override
    public AABB getRenderBoundingBox(float partialTicks) {
        return new AABB(this.from, this.to).inflate(Math.max(this.width0, this.width1) + 1.0 + this.jag + this.helixRadius);
    }

    private Vec3 start(float pt) {
        return this.anchor != null && this.anchor.isAlive() ? Muzzle.world(this.anchor, pt) : this.from;
    }

    private Vec3 end(float pt) {
        if (this.target == null) return this.to;
        return this.target.getPosition(pt).add(0.0, this.target.getBbHeight() * 0.55, 0.0);
    }

    // ---------------------------------------------------------------- dibujo

    @Override
    public void render(VertexConsumer vc, Camera camera, float pt) {
        float t = Math.min(1.0F, (this.age + pt) / this.life);
        float a = Mth.clamp(this.a0 + (this.a1 - this.a0) * (float) Math.pow(t, this.alphaPow), 0.0F, 1.0F);
        if (a <= 0.003F) return;
        float r = this.r0 + (this.r1 - this.r0) * t;
        float g = this.g0 + (this.g1 - this.g0) * t;
        float b = this.b0 + (this.b1 - this.b0) * t;
        float width = this.width0 + (this.width1 - this.width0) * t;
        Vec3 cam = camera.getPosition();
        Vec3 s = start(pt);
        Vec3 e = end(pt);
        if (this.grow > 0.0F) e = s.add(e.subtract(s).scale(Math.min(1.0, (this.age + pt) / this.grow)));
        List<Vec3> pts = points(s, e, pt);
        if (pts.size() < 2) return;
        float cr = this.additive ? r * a : r;
        float cg = this.additive ? g * a : g;
        float cb = this.additive ? b * a : b;
        float ca = this.additive ? 1.0F : a;
        ribbon(vc, pts, cam, width, cr, cg, cb, ca, pt);
    }

    private List<Vec3> points(Vec3 s, Vec3 e, float pt) {
        List<Vec3> out = new ArrayList<>();
        Vec3 d = e.subtract(s);
        double len = d.length();
        if (len < 1.0E-4) return out;
        Vec3 dir = d.scale(1.0 / len);
        Vec3 ref = Math.abs(dir.y) < 0.95 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        Vec3 u = dir.cross(ref).normalize();
        Vec3 v = dir.cross(u).normalize();
        switch (this.shape) {
            case STRAIGHT -> {
                out.add(s);
                out.add(e);
            }
            case JAGGED -> {
                Random rnd = new Random(this.seed * 31L + this.age / this.flicker);
                int n = Mth.clamp((int) (len / 0.6), 3, 48);
                for (int i = 0; i <= n; i++) {
                    double f = i / (double) n;
                    double env = Math.sin(f * Math.PI);
                    double ox = (rnd.nextDouble() - 0.5) * 2.0 * this.jag * env;
                    double oy = (rnd.nextDouble() - 0.5) * 2.0 * this.jag * env;
                    out.add(s.add(d.scale(f)).add(u.scale(ox)).add(v.scale(oy)));
                }
            }
            case HELIX -> {
                int n = Mth.clamp((int) (len * this.helixTurns * 10.0), 8, 400);
                float spin = this.helixPhase + this.helixSpin * (this.age + pt);
                for (int i = 0; i <= n; i++) {
                    double f = i / (double) n;
                    double ang = f * len * this.helixTurns * Math.PI * 2.0 + spin;
                    double rad = this.helixRadius * (0.3 + 0.7 * Math.min(1.0, f * len / 1.5));
                    out.add(s.add(d.scale(f)).add(u.scale(Math.cos(ang) * rad)).add(v.scale(Math.sin(ang) * rad)));
                }
            }
            case WAVE -> {
                int n = Mth.clamp((int) (len * 3.0), 6, 80);
                float time = this.age + pt;
                double amp = this.jag * Math.max(0.0, 1.0 - time / (this.life * 0.7));
                for (int i = 0; i <= n; i++) {
                    double f = i / (double) n;
                    double env = Math.sin(f * Math.PI);
                    double w = Math.sin(f * 9.0 - time * 1.3) * amp * env;
                    out.add(s.add(d.scale(f)).add(u.scale(w)).add(v.scale(-env * len * 0.04)));
                }
            }
        }
        return out;
    }

    /** Cinta de cara a la cámara a lo largo de la polilínea, repitiendo la textura cada {@code tile} bloques. */
    private void ribbon(VertexConsumer vc, List<Vec3> pts, Vec3 cam, float width, float r, float g, float b, float a, float pt) {
        double total = 0.0;
        for (int i = 1; i < pts.size(); i++) total += pts.get(i).distanceTo(pts.get(i - 1));
        if (total < 1.0E-4) return;
        double run = 0.0;
        float phase = this.scroll * (this.age + pt);
        for (int i = 1; i < pts.size(); i++) {
            Vec3 p0 = pts.get(i - 1);
            Vec3 p1 = pts.get(i);
            double segLen = p0.distanceTo(p1);
            if (segLen < 1.0E-5) continue;
            double done = 0.0;
            while (done < segLen - 1.0E-6) {
                double step = segLen - done;
                float u0;
                float u1;
                if (this.tile > 0.0F) {
                    double pos = (run + done) / this.tile - phase;
                    double frac = pos - Math.floor(pos);
                    step = Math.min(step, (1.0 - frac) * this.tile);
                    if (step < 1.0E-5) step = Math.min(segLen - done, 1.0E-3);
                    u0 = (float) frac;
                    u1 = (float) Math.min(1.0, frac + step / this.tile);
                } else {
                    u0 = 0.35F;
                    u1 = 0.65F;
                }
                Vec3 a0 = p0.add(p1.subtract(p0).scale(done / segLen));
                Vec3 a1 = p0.add(p1.subtract(p0).scale((done + step) / segLen));
                float w0 = width * taper((run + done) / total);
                float w1 = width * taper((run + done + step) / total);
                segment(vc, a0, a1, cam, w0, w1, u0, u1, r, g, b, a);
                done += step;
            }
            run += segLen;
        }
    }

    private float taper(double f) {
        if (this.taper <= 0.0F) return 1.0F;
        double edge = Math.min(f, 1.0 - f) / this.taper;
        return (float) Mth.clamp(edge, 0.15, 1.0);
    }

    private void segment(VertexConsumer vc, Vec3 p0, Vec3 p1, Vec3 cam, float w0, float w1, float u0, float u1,
                         float r, float g, float b, float a) {
        Vector3f dir = new Vector3f((float) (p1.x - p0.x), (float) (p1.y - p0.y), (float) (p1.z - p0.z));
        Vector3f toCam = new Vector3f((float) (cam.x - (p0.x + p1.x) * 0.5), (float) (cam.y - (p0.y + p1.y) * 0.5), (float) (cam.z - (p0.z + p1.z) * 0.5));
        Vector3f side = dir.cross(toCam, new Vector3f());
        if (side.lengthSquared() < 1.0E-10) return;
        side.normalize();
        float x0 = (float) (p0.x - cam.x), y0 = (float) (p0.y - cam.y), z0 = (float) (p0.z - cam.z);
        float x1 = (float) (p1.x - cam.x), y1 = (float) (p1.y - cam.y), z1 = (float) (p1.z - cam.z);
        Sprite s = this.sprite;
        float su0 = s.u(u0), su1 = s.u(u1);
        vc.addVertex(x0 - side.x * w0, y0 - side.y * w0, z0 - side.z * w0).setUv(su0, s.v0()).setColor(r, g, b, a);
        vc.addVertex(x0 + side.x * w0, y0 + side.y * w0, z0 + side.z * w0).setUv(su0, s.v1()).setColor(r, g, b, a);
        vc.addVertex(x1 + side.x * w1, y1 + side.y * w1, z1 + side.z * w1).setUv(su1, s.v1()).setColor(r, g, b, a);
        vc.addVertex(x1 - side.x * w1, y1 - side.y * w1, z1 - side.z * w1).setUv(su1, s.v0()).setColor(r, g, b, a);
    }
}
