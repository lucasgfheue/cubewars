package com.proyectox.client.fx;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.proyectox.client.fx.FxAtlas.Sprite;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Dibujo de sprites del atlas de efectos en el espacio de una entidad (o de la mano): carteles de cara
 * a la cámara, planos orientados, trazos alargados y estelas. Los colores aditivos se premultiplican
 * por la intensidad; los suaves usan alfa.
 */
public final class Draw {
    private final Matrix4f matrix;
    private final MultiBufferSource buffers;
    /** Posición de la cámara relativa al origen local. */
    private final Vector3f cam;
    private final Quaternionf camRot;
    /** Origen local en coordenadas del mundo (para las estelas). */
    private final Vec3 origin;

    public Draw(PoseStack pose, MultiBufferSource buffers, Vec3 origin) {
        this.matrix = pose.last().pose();
        this.buffers = buffers;
        this.origin = origin;
        Minecraft mc = Minecraft.getInstance();
        Vec3 c = mc.gameRenderer.getMainCamera().getPosition();
        this.cam = new Vector3f((float) (c.x - origin.x), (float) (c.y - origin.y), (float) (c.z - origin.z));
        this.camRot = mc.getEntityRenderDispatcher().cameraOrientation();
    }

    /** Para dibujar en primera persona: la "cámara" está en el origen de la vista y mira a -Z. */
    public Draw(PoseStack pose, MultiBufferSource buffers, Vector3f localCam, Quaternionf localCamRot) {
        this.matrix = pose.last().pose();
        this.buffers = buffers;
        this.origin = Vec3.ZERO;
        this.cam = localCam;
        this.camRot = localCamRot;
    }

    private VertexConsumer vc(boolean additive) {
        return this.buffers.getBuffer(additive ? FxRender.glow() : FxRender.soft());
    }

    private void vertex(VertexConsumer vc, float x, float y, float z, float u, float v, float r, float g, float b, float a) {
        vc.addVertex(this.matrix, x, y, z).setColor(r, g, b, a).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(FxRender.FULL_BRIGHT).setNormal(0.0F, 1.0F, 0.0F);
    }

    private void quad(boolean additive, Sprite s, Vector3f c0, Vector3f c1, Vector3f c2, Vector3f c3, int rgb, float alpha, boolean alongU) {
        float a = Math.max(0.0F, Math.min(1.0F, alpha));
        if (a <= 0.003F) return;
        float r = (rgb >> 16 & 0xFF) / 255.0F;
        float g = (rgb >> 8 & 0xFF) / 255.0F;
        float b = (rgb & 0xFF) / 255.0F;
        if (additive) {
            r *= a;
            g *= a;
            b *= a;
            a = 1.0F;
        }
        VertexConsumer vc = vc(additive);
        if (alongU) {
            vertex(vc, c0.x, c0.y, c0.z, s.u0(), s.v0(), r, g, b, a);
            vertex(vc, c1.x, c1.y, c1.z, s.u0(), s.v1(), r, g, b, a);
            vertex(vc, c2.x, c2.y, c2.z, s.u1(), s.v1(), r, g, b, a);
            vertex(vc, c3.x, c3.y, c3.z, s.u1(), s.v0(), r, g, b, a);
        } else {
            vertex(vc, c0.x, c0.y, c0.z, s.u0(), s.v1(), r, g, b, a);
            vertex(vc, c1.x, c1.y, c1.z, s.u0(), s.v0(), r, g, b, a);
            vertex(vc, c2.x, c2.y, c2.z, s.u1(), s.v0(), r, g, b, a);
            vertex(vc, c3.x, c3.y, c3.z, s.u1(), s.v1(), r, g, b, a);
        }
    }

    /** Cartel de cara a la cámara centrado en {@code c}. */
    public void billboard(boolean additive, Sprite s, Vector3f c, float size, float roll, int rgb, float alpha) {
        Quaternionf q = new Quaternionf(this.camRot);
        if (roll != 0.0F) q.rotateZ(roll);
        Vector3f a = q.transform(new Vector3f(-size, -size, 0)).add(c);
        Vector3f b = q.transform(new Vector3f(-size, size, 0)).add(c);
        Vector3f d = q.transform(new Vector3f(size, size, 0)).add(c);
        Vector3f e = q.transform(new Vector3f(size, -size, 0)).add(c);
        quad(additive, s, a, b, d, e, rgb, alpha, false);
    }

    /** Plano con normal fija; {@code tangent} marca la dirección "derecha" del sprite. */
    public void flat(boolean additive, Sprite s, Vector3f c, Vector3f normal, Vector3f tangent, float sizeU, float sizeV, float roll, int rgb, float alpha) {
        Vector3f n = new Vector3f(normal).normalize();
        Vector3f tu = new Vector3f(tangent);
        tu.sub(new Vector3f(n).mul(tu.dot(n)));
        if (tu.lengthSquared() < 1.0E-8) tu = Math.abs(n.y) < 0.95F ? new Vector3f(0, 1, 0).cross(n) : new Vector3f(1, 0, 0).cross(n);
        tu.normalize();
        if (roll != 0.0F) tu.rotateAxis(roll, n.x, n.y, n.z);
        Vector3f tv = new Vector3f(n).cross(tu);
        tu.mul(sizeU);
        tv.mul(sizeV);
        quad(additive, s, new Vector3f(c).sub(tu).sub(tv), new Vector3f(c).sub(tu).add(tv), new Vector3f(c).add(tu).add(tv),
                new Vector3f(c).add(tu).sub(tv), rgb, alpha, false);
    }

    /** Trazo alargado en la dirección {@code dir}, girado hacia la cámara (u: cola → cabeza). */
    public void streak(boolean additive, Sprite s, Vector3f c, Vector3f dir, float back, float front, float width, int rgb, float alpha) {
        Vector3f d = new Vector3f(dir).normalize();
        Vector3f toCam = new Vector3f(this.cam).sub(c);
        Vector3f side = new Vector3f(d).cross(toCam);
        if (side.lengthSquared() < 1.0E-8) side = Math.abs(d.y) < 0.95F ? new Vector3f(0, 1, 0).cross(d) : new Vector3f(1, 0, 0).cross(d);
        side.normalize(width);
        Vector3f tail = new Vector3f(d).mul(-back).add(c);
        Vector3f head = new Vector3f(d).mul(front).add(c);
        quad(additive, s, new Vector3f(tail).sub(side), new Vector3f(tail).add(side), new Vector3f(head).add(side), new Vector3f(head).sub(side),
                rgb, alpha, true);
    }

    /** Dos trazos cruzados a lo largo de {@code dir} (se ven con volumen desde cualquier ángulo). */
    public void cross(boolean additive, Sprite s, Vector3f c, Vector3f dir, float back, float front, float width, int rgb, float alpha) {
        Vector3f d = new Vector3f(dir).normalize();
        Vector3f a = Math.abs(d.y) < 0.95F ? new Vector3f(0, 1, 0).cross(d) : new Vector3f(1, 0, 0).cross(d);
        a.normalize(width);
        Vector3f b = new Vector3f(d).cross(a).normalize(width);
        Vector3f tail = new Vector3f(d).mul(-back).add(c);
        Vector3f head = new Vector3f(d).mul(front).add(c);
        for (Vector3f side : new Vector3f[]{a, b}) {
            quad(additive, s, new Vector3f(tail).sub(side), new Vector3f(tail).add(side), new Vector3f(head).add(side), new Vector3f(head).sub(side),
                    rgb, alpha, true);
        }
    }

    /** Estela por puntos del mundo (del más nuevo al más viejo), afinándose y apagándose hacia la cola. */
    public void trail(Sprite s, List<Vec3> world, float width, int head, int tail, float alpha) {
        int n = world.size();
        if (n < 2) return;
        for (int i = 0; i < n - 1; i++) {
            float f0 = i / (float) (n - 1);
            float f1 = (i + 1) / (float) (n - 1);
            Vector3f p0 = local(world.get(i));
            Vector3f p1 = local(world.get(i + 1));
            Vector3f d = new Vector3f(p1).sub(p0);
            if (d.lengthSquared() < 1.0E-8) continue;
            Vector3f toCam = new Vector3f(this.cam).sub(p0);
            Vector3f side = new Vector3f(d).cross(toCam);
            if (side.lengthSquared() < 1.0E-10) continue;
            side.normalize();
            float w0 = width * (1.0F - f0 * 0.85F);
            float w1 = width * (1.0F - f1 * 0.85F);
            int c = Palette.mix(head, tail, f0);
            float a = alpha * (1.0F - f0) * (1.0F - f0);
            float r = (c >> 16 & 0xFF) / 255.0F * a;
            float g = (c >> 8 & 0xFF) / 255.0F * a;
            float b = (c & 0xFF) / 255.0F * a;
            if (a <= 0.003F) continue;
            VertexConsumer vc = vc(true);
            vertex(vc, p0.x - side.x * w0, p0.y - side.y * w0, p0.z - side.z * w0, s.u(0.4F), s.v0(), r, g, b, 1.0F);
            vertex(vc, p0.x + side.x * w0, p0.y + side.y * w0, p0.z + side.z * w0, s.u(0.4F), s.v1(), r, g, b, 1.0F);
            vertex(vc, p1.x + side.x * w1, p1.y + side.y * w1, p1.z + side.z * w1, s.u(0.6F), s.v1(), r, g, b, 1.0F);
            vertex(vc, p1.x - side.x * w1, p1.y - side.y * w1, p1.z - side.z * w1, s.u(0.6F), s.v0(), r, g, b, 1.0F);
        }
    }

    public Vector3f local(Vec3 world) {
        return new Vector3f((float) (world.x - this.origin.x), (float) (world.y - this.origin.y), (float) (world.z - this.origin.z));
    }
}
