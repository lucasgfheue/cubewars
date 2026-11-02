package com.proyectox.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.proyectox.client.fx.Draw;
import com.proyectox.client.fx.FxEntityRenderer;
import com.proyectox.client.fx.FxRender;
import com.proyectox.entity.SpecBolt;
import com.proyectox.entity.SpecField;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Proyectiles y zonas de la serie v7 que, además de sus efectos ({@link com.proyectox.client.fx.Looks7}), se dibujan con
 * un modelo 3D: la botella del MOLOTOV, el bidón del BIDÓN, el gancho del GARFIO y el ojo del OBSERVADOR (modelos de los
 * objetos ocultos "prop_*", con la punta hacia -Z).
 */
public class Prop7Renderer<T extends Entity> extends EntityRenderer<T> {
    private final ItemRenderer items;
    private final FxEntityRenderer.Look<T> look;
    private ItemStack bottle = ItemStack.EMPTY;
    private ItemStack canister = ItemStack.EMPTY;
    private ItemStack hook = ItemStack.EMPTY;
    private ItemStack eye = ItemStack.EMPTY;

    public Prop7Renderer(EntityRendererProvider.Context context, FxEntityRenderer.Look<T> look) {
        super(context);
        this.items = context.getItemRenderer();
        this.look = look;
        this.shadowRadius = 0.0F;
    }

    public static <E extends Entity> EntityRendererProvider<E> of(FxEntityRenderer.Look<E> look) {
        return ctx -> new Prop7Renderer<>(ctx, look);
    }

    private static ItemStack stack(ItemStack cached, Item item) {
        return cached.isEmpty() ? new ItemStack(item) : cached;
    }

    @Override
    public void render(T entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        if (entity instanceof SpecBolt bolt) {
            switch (bolt.style()) {
                case Fx.MOLOTOV -> {
                    this.bottle = stack(this.bottle, PXItems.PROP_BOTELLA.get());
                    flying(bolt, this.bottle, partialTick, pose, buffers, light, 18.0F, 1.0F);
                }
                case Fx.BIDON -> {
                    this.canister = stack(this.canister, PXItems.PROP_BIDON.get());
                    flying(bolt, this.canister, partialTick, pose, buffers, light, 0.0F, 1.0F);
                }
                case Fx.GARFIO -> {
                    this.hook = stack(this.hook, PXItems.PROP_GARFIO.get());
                    flying(bolt, this.hook, partialTick, pose, buffers, light, 0.0F, 1.0F);
                }
                default -> {}
            }
        } else if (entity instanceof SpecField field && field.type() == SpecField.EYE) {
            this.eye = stack(this.eye, PXItems.PROP_OJO.get());
            eye(field, partialTick, pose, buffers);
        }
        this.look.draw(entity, partialTick, new Draw(pose, buffers, entity.getPosition(partialTick)));
        super.render(entity, yaw, partialTick, pose, buffers, light);
    }

    private void model(ItemStack stack, PoseStack pose, MultiBufferSource buffers, int light, Entity entity) {
        this.items.renderStatic(stack, ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY, pose, buffers, entity.level(), entity.getId());
    }

    /** Pieza que vuela con la punta por delante (y, si {@code tumble} &gt; 0, dando vueltas sobre sí misma). */
    private void flying(SpecBolt bolt, ItemStack stack, float pt, PoseStack pose, MultiBufferSource buffers, int light, float tumble, float scale) {
        float yaw = Mth.rotLerp(pt, bolt.yRotO, bolt.getYRot());
        float pitch = Mth.lerp(pt, bolt.xRotO, bolt.getXRot());
        float t = bolt.tickCount + pt;
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(yaw + 180.0F));
        pose.mulPose(Axis.XP.rotationDegrees(pitch));
        if (tumble > 0.0F) pose.mulPose(Axis.XP.rotationDegrees(t * tumble));
        pose.scale(scale, scale, scale);
        model(stack, pose, buffers, light, bolt);
        pose.popPose();
    }

    /** El ojo mira a su objetivo, se abre al aparecer y se cierra al irse (siempre con luz propia). */
    private void eye(SpecField field, float pt, PoseStack pose, MultiBufferSource buffers) {
        Vec3 g = field.gaze(pt);
        float yaw = (float) Math.toDegrees(Math.atan2(g.x, g.z));
        float pitch = (float) Math.toDegrees(Math.atan2(g.y, Math.sqrt(g.x * g.x + g.z * g.z)));
        float t = field.tickCount + pt;
        float grow = Mth.clamp(t / 8.0F, 0.0F, 1.0F) * Mth.clamp((field.remaining() - pt) / 8.0F, 0.0F, 1.0F);
        grow = grow * grow * (3.0F - 2.0F * grow);
        float s = 1.3F * Math.max(0.05F, grow);
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(yaw + 180.0F));
        pose.mulPose(Axis.XP.rotationDegrees(pitch));
        pose.scale(s, s, s);
        model(this.eye, pose, buffers, FxRender.FULL_BRIGHT, field);
        pose.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
