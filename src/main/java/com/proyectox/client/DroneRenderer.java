package com.proyectox.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.proyectox.client.fx.Draw;
import com.proyectox.client.fx.FxAtlas;
import com.proyectox.entity.Drone;
import com.proyectox.registry.PXItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

/** El dron HALCÓN en vuelo se dibuja con el mismo modelo 3D del objeto. */
public class DroneRenderer extends EntityRenderer<Drone> {
    private final ItemRenderer items;
    private ItemStack stack;

    public DroneRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.items = context.getItemRenderer();
        this.shadowRadius = 0.35F;
    }

    @Override
    public void render(Drone drone, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        if (this.stack == null) this.stack = new ItemStack(PXItems.HALCON.get());
        float rot = Mth.lerp(partialTick, drone.yRotO, drone.getYRot());
        float bob = Mth.sin((drone.tickCount + partialTick) * 0.15F) * 0.04F;
        pose.pushPose();
        pose.translate(0.0F, 0.2F + bob, 0.0F);
        pose.mulPose(Axis.YP.rotationDegrees(180.0F - rot));
        pose.scale(0.9F, 0.9F, 0.9F);
        // el modelo está centrado en x = 0,5 y z = 0 (en bloques): se compensa el desplazamiento del renderizador de objetos
        pose.translate(0.0F, 0.0F, 0.5F);
        this.items.renderStatic(this.stack, ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY, pose, buffers, drone.level(), drone.getId());
        pose.popPose();
        // propulsores y luces de navegación
        Draw d = new Draw(pose, buffers, drone.getPosition(partialTick));
        float t = drone.tickCount + partialTick;
        float yawRad = (float) Math.toRadians(-rot);
        for (int i = 0; i < 4; i++) {
            float a = yawRad + (float) (Math.PI / 4 + i * Math.PI / 2);
            Vector3f p = new Vector3f(Mth.sin(a) * 0.38F, 0.12F + bob, Mth.cos(a) * 0.38F);
            d.billboard(true, FxAtlas.GLOW, p, 0.16F + 0.03F * Mth.sin(t * 3.1F + i), 0.0F, 0xFFA235, 0.7F);
        }
        if (((int) t / 6) % 2 == 0) d.billboard(true, FxAtlas.CORE, new Vector3f(0, 0.3F + bob, 0), 0.06F, 0.0F, 0xFF3A2A, 1.0F);
        super.render(drone, yaw, partialTick, pose, buffers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(Drone drone) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
