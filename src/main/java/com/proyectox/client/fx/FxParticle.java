package com.proyectox.client.fx;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.proyectox.client.fx.FxAtlas.Sprite;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Partícula HD configurable: sprite (o animación) del atlas de efectos, color y opacidad que cambian
 * con la vida, tamaño con curva, giro, gravedad, rozamiento y cuatro orientaciones:
 * <ul>
 *   <li>{@link Mode#BILLBOARD}: de cara a la cámara (destellos, humo, ascuas);</li>
 *   <li>{@link Mode#VELOCITY}: estirada en la dirección de vuelo (chispas, trazadoras);</li>
 *   <li>{@link Mode#FIXED}: plana con una normal fija (anillos de choque, hexágonos del escudo);</li>
 *   <li>{@link Mode#AXIS}: estirada a lo largo de un eje fijo y girada hacia la cámara (lenguas de fuego).</li>
 * </ul>
 */
public class FxParticle extends Particle {
    public enum Mode { BILLBOARD, VELOCITY, FIXED, AXIS }

    private Sprite[] frames;
    private boolean loop;
    private boolean additive = true;
    private Mode mode = Mode.BILLBOARD;

    private double ox, oy, oz;
    private double vx, vy, vz;
    private int age;
    private int life;
    private double drag = 1.0;
    private double grav;

    private float size0 = 0.5F, size1 = 0.5F;
    private float sizePow = 1.0F;
    private float aspect = 1.0F;
    private float stretch;
    private float r0 = 1, g0 = 1, b0 = 1, r1 = 1, g1 = 1, b1 = 1;
    private float a0 = 1.0F, a1 = 0.0F;
    private float fadeIn;
    private float alphaPow = 1.0F;
    private float roll, spin;
    private final Vector3f axis = new Vector3f(0, 1, 0);
    private final Vector3f tangent = new Vector3f(1, 0, 0);

    @Nullable
    private Entity follow;
    private Vec3 followOffset = Vec3.ZERO;

    public FxParticle(ClientLevel level, Vec3 pos, Sprite sprite, int life) {
        super(level, pos.x, pos.y, pos.z);
        this.frames = new Sprite[]{sprite};
        this.life = Math.max(1, life);
        this.ox = pos.x;
        this.oy = pos.y;
        this.oz = pos.z;
    }

    // ---------------------------------------------------------------- configuración (encadenable)

    public FxParticle frames(Sprite[] frames, boolean loop) {
        this.frames = frames;
        this.loop = loop;
        return this;
    }

    public FxParticle soft() {
        this.additive = false;
        return this;
    }

    public FxParticle mode(Mode mode) {
        this.mode = mode;
        return this;
    }

    public FxParticle vel(Vec3 v) {
        this.vx = v.x;
        this.vy = v.y;
        this.vz = v.z;
        return this;
    }

    public FxParticle drag(double drag) {
        this.drag = drag;
        return this;
    }

    public FxParticle gravity(double g) {
        this.grav = g;
        return this;
    }

    /** Tamaño (medio lado, en bloques) al nacer y al morir; {@code pow} curva la transición. */
    public FxParticle size(float from, float to, float pow) {
        this.size0 = from;
        this.size1 = to;
        this.sizePow = pow;
        return this;
    }

    public FxParticle size(float s) {
        return size(s, s, 1.0F);
    }

    /** Relación largo/ancho (sprites alargados). */
    public FxParticle aspect(float aspect) {
        this.aspect = aspect;
        return this;
    }

    /** En {@link Mode#VELOCITY}: bloques de estela por cada bloque/tick de velocidad. */
    public FxParticle stretch(float stretch) {
        this.stretch = stretch;
        return this;
    }

    public FxParticle color(int rgb) {
        return color(rgb, rgb);
    }

    public FxParticle color(int from, int to) {
        this.r0 = (from >> 16 & 0xFF) / 255.0F;
        this.g0 = (from >> 8 & 0xFF) / 255.0F;
        this.b0 = (from & 0xFF) / 255.0F;
        this.r1 = (to >> 16 & 0xFF) / 255.0F;
        this.g1 = (to >> 8 & 0xFF) / 255.0F;
        this.b1 = (to & 0xFF) / 255.0F;
        return this;
    }

    /** Opacidad (o intensidad, en los aditivos) al nacer y al morir. */
    public FxParticle alpha(float from, float to) {
        this.a0 = from;
        this.a1 = to;
        return this;
    }

    public FxParticle alphaPow(float pow) {
        this.alphaPow = pow;
        return this;
    }

    /** Fracción de la vida durante la que aparece gradualmente. */
    public FxParticle fadeIn(float fraction) {
        this.fadeIn = fraction;
        return this;
    }

    public FxParticle roll(float roll, float spin) {
        this.roll = roll;
        this.spin = spin;
        return this;
    }

    /** Normal (FIXED) o eje (AXIS) del sprite. */
    public FxParticle axis(Vec3 a) {
        Vec3 n = a.lengthSqr() < 1.0E-8 ? new Vec3(0, 1, 0) : a.normalize();
        this.axis.set((float) n.x, (float) n.y, (float) n.z);
        Vec3 ref = Math.abs(n.y) < 0.95 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        Vec3 t = ref.cross(n).normalize();
        this.tangent.set((float) t.x, (float) t.y, (float) t.z);
        return this;
    }

    /** La partícula acompaña a una entidad (con un desplazamiento fijo). */
    public FxParticle follow(Entity entity, Vec3 offset) {
        this.follow = entity;
        this.followOffset = offset;
        return this;
    }

    public FxParticle spawn() {
        net.minecraft.client.Minecraft.getInstance().particleEngine.add(this);
        return this;
    }

    // ---------------------------------------------------------------- simulación

    @Override
    public void tick() {
        this.ox = this.x;
        this.oy = this.y;
        this.oz = this.z;
        if (++this.age >= this.life) {
            this.remove();
            return;
        }
        if (this.follow != null) {
            if (!this.follow.isAlive()) {
                this.remove();
                return;
            }
            Vec3 p = this.follow.position().add(this.followOffset);
            this.setPos(p.x, p.y, p.z);
            return;
        }
        this.vy -= this.grav;
        this.vx *= this.drag;
        this.vy *= this.drag;
        this.vz *= this.drag;
        this.setPos(this.x + this.vx, this.y + this.vy, this.z + this.vz);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return this.additive ? FxRender.ADDITIVE : FxRender.SOFT;
    }

    @Override
    public AABB getRenderBoundingBox(float partialTicks) {
        double r = Math.max(this.size0, this.size1) * Math.max(1.0F, this.aspect) * 2.0
                + Math.sqrt(this.vx * this.vx + this.vy * this.vy + this.vz * this.vz) * this.stretch + 0.5;
        return new AABB(this.x - r, this.y - r, this.z - r, this.x + r, this.y + r, this.z + r);
    }

    // ---------------------------------------------------------------- dibujo

    @Override
    public void render(VertexConsumer vc, Camera camera, float pt) {
        float t = Math.min(1.0F, (this.age + pt) / this.life);
        Vec3 cam = camera.getPosition();
        float px = (float) (Mth.lerp(pt, this.ox, this.x) - cam.x);
        float py = (float) (Mth.lerp(pt, this.oy, this.y) - cam.y);
        float pz = (float) (Mth.lerp(pt, this.oz, this.z) - cam.z);
        float size = this.size0 + (this.size1 - this.size0) * (float) Math.pow(t, this.sizePow);
        float a = this.a0 + (this.a1 - this.a0) * (float) Math.pow(t, this.alphaPow);
        if (this.fadeIn > 0.0F && t < this.fadeIn) a *= t / this.fadeIn;
        a = Mth.clamp(a, 0.0F, 1.0F);
        float r = this.r0 + (this.r1 - this.r0) * t;
        float g = this.g0 + (this.g1 - this.g0) * t;
        float b = this.b0 + (this.b1 - this.b0) * t;
        float cr, cg, cb, ca;
        if (this.additive) {
            cr = r * a;
            cg = g * a;
            cb = b * a;
            ca = 1.0F;
        } else {
            cr = r;
            cg = g;
            cb = b;
            ca = a;
        }
        if (ca <= 0.003F || (this.additive && cr + cg + cb <= 0.003F)) return;
        Sprite s = frame(t);
        float rollNow = this.roll + this.spin * (this.age + pt);

        switch (this.mode) {
            case BILLBOARD -> {
                Quaternionf q = new Quaternionf(camera.rotation());
                if (rollNow != 0.0F) q.rotateZ(rollNow);
                float hw = size * this.aspect;
                quad(vc, s, px, py, pz, q.transform(new Vector3f(-hw, -size, 0)), q.transform(new Vector3f(-hw, size, 0)),
                        q.transform(new Vector3f(hw, size, 0)), q.transform(new Vector3f(hw, -size, 0)), cr, cg, cb, ca);
            }
            case FIXED -> {
                Vector3f n = this.axis;
                Vector3f tu = new Vector3f(this.tangent);
                if (rollNow != 0.0F) tu.rotateAxis(rollNow, n.x, n.y, n.z);
                Vector3f tv = new Vector3f(n).cross(tu);
                tu.mul(size * this.aspect);
                tv.mul(size);
                quad(vc, s, px, py, pz, new Vector3f(tu).negate().sub(tv), new Vector3f(tu).negate().add(tv),
                        new Vector3f(tu).add(tv), new Vector3f(tu).sub(tv), cr, cg, cb, ca);
            }
            case VELOCITY, AXIS -> {
                Vector3f dir;
                float length;
                if (this.mode == Mode.VELOCITY) {
                    double dx = this.vx, dy = this.vy, dz = this.vz;
                    double speed = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (speed < 1.0E-5) {
                        dx = this.x - this.ox;
                        dy = this.y - this.oy;
                        dz = this.z - this.oz;
                        speed = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    }
                    dir = speed < 1.0E-5 ? new Vector3f(this.axis) : new Vector3f((float) (dx / speed), (float) (dy / speed), (float) (dz / speed));
                    length = size * this.aspect + (float) speed * this.stretch;
                } else {
                    dir = new Vector3f(this.axis);
                    length = size * this.aspect;
                }
                // ancho perpendicular a la dirección y a la línea de visión
                Vector3f toCam = new Vector3f(-px, -py, -pz);
                Vector3f side = new Vector3f(dir).cross(toCam);
                if (side.lengthSquared() < 1.0E-8) side.set(this.tangent);
                side.normalize(size);
                Vector3f tail = new Vector3f(dir).mul(this.mode == Mode.VELOCITY ? -length : 0.0F);
                Vector3f head = new Vector3f(dir).mul(this.mode == Mode.VELOCITY ? 0.0F : length * 2.0F);
                // u: 0 en la cola → 1 en la cabeza
                quadUV(vc, s, px, py, pz,
                        new Vector3f(tail).sub(side), new Vector3f(tail).add(side), new Vector3f(head).add(side), new Vector3f(head).sub(side),
                        cr, cg, cb, ca);
            }
        }
    }

    private Sprite frame(float t) {
        if (this.frames.length == 1) return this.frames[0];
        int i = this.loop ? (this.age / 2) % this.frames.length : Math.min(this.frames.length - 1, (int) (t * this.frames.length));
        return this.frames[i];
    }

    /** Cuadrado con las esquinas en orden (abajo-izq, arriba-izq, arriba-der, abajo-der) del sprite. */
    private static void quad(VertexConsumer vc, Sprite s, float x, float y, float z, Vector3f c0, Vector3f c1, Vector3f c2, Vector3f c3,
                             float r, float g, float b, float a) {
        vc.addVertex(x + c0.x, y + c0.y, z + c0.z).setUv(s.u0(), s.v1()).setColor(r, g, b, a);
        vc.addVertex(x + c1.x, y + c1.y, z + c1.z).setUv(s.u0(), s.v0()).setColor(r, g, b, a);
        vc.addVertex(x + c2.x, y + c2.y, z + c2.z).setUv(s.u1(), s.v0()).setColor(r, g, b, a);
        vc.addVertex(x + c3.x, y + c3.y, z + c3.z).setUv(s.u1(), s.v1()).setColor(r, g, b, a);
    }

    /** Cuadrado alargado: u recorre de la cola a la cabeza y v de un lado al otro. */
    private static void quadUV(VertexConsumer vc, Sprite s, float x, float y, float z, Vector3f tailA, Vector3f tailB, Vector3f headB, Vector3f headA,
                               float r, float g, float b, float a) {
        vc.addVertex(x + tailA.x, y + tailA.y, z + tailA.z).setUv(s.u0(), s.v0()).setColor(r, g, b, a);
        vc.addVertex(x + tailB.x, y + tailB.y, z + tailB.z).setUv(s.u0(), s.v1()).setColor(r, g, b, a);
        vc.addVertex(x + headB.x, y + headB.y, z + headB.z).setUv(s.u1(), s.v1()).setColor(r, g, b, a);
        vc.addVertex(x + headA.x, y + headA.y, z + headA.z).setUv(s.u1(), s.v0()).setColor(r, g, b, a);
    }
}
