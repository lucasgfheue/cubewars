package com.proyectox.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.proyectox.client.fx.Draw;
import com.proyectox.client.fx.FxEntityRenderer;
import com.proyectox.client.fx.FxRender;
import com.proyectox.entity.Field;
import com.proyectox.entity.Orb;
import com.proyectox.entity.Piercer;
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

/**
 * Proyectiles y zonas que, además de sus efectos ({@link com.proyectox.client.fx.Looks}), se dibujan con un modelo
 * 3D: el cohete del RUGIDO, el meteorito del METEORO, el chakram del RETORNO, la torreta CENTINELA (trípode fijo y
 * cabeza que gira hacia su objetivo) y la granada de 40 mm del VOLCÁN. Los modelos son los de los objetos ocultos
 * "prop_*".
 */
public class PropRenderer<T extends Entity> extends EntityRenderer<T> {
    private final ItemRenderer items;
    private final FxEntityRenderer.Look<T> look;
    private ItemStack rocket = ItemStack.EMPTY;
    private ItemStack meteor = ItemStack.EMPTY;
    private ItemStack chakram = ItemStack.EMPTY;
    private ItemStack turretBase = ItemStack.EMPTY;
    private ItemStack turretHead = ItemStack.EMPTY;
    private ItemStack grenade = ItemStack.EMPTY;

    public PropRenderer(EntityRendererProvider.Context context, FxEntityRenderer.Look<T> look) {
        super(context);
        this.items = context.getItemRenderer();
        this.look = look;
        this.shadowRadius = 0.0F;
    }

    public static <E extends Entity> EntityRendererProvider<E> of(FxEntityRenderer.Look<E> look) {
        return ctx -> new PropRenderer<>(ctx, look);
    }

    private static ItemStack stack(ItemStack cached, Item item) {
        return cached.isEmpty() ? new ItemStack(item) : cached;
    }

    @Override
    public void render(T entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        if (entity instanceof Orb orb && orb.kind() == Orb.ROCKET) {
            this.rocket = stack(this.rocket, PXItems.PROP_COHETE.get());
            rocket(orb, partialTick, pose, buffers, light);
        } else if (entity instanceof Orb orb && orb.kind() == Orb.GRENADE40) {
            this.grenade = stack(this.grenade, PXItems.PROP_GRANADA40.get());
            grenade(orb, partialTick, pose, buffers, light);
        } else if (entity instanceof Orb orb && orb.kind() == Orb.METEOR) {
            this.meteor = stack(this.meteor, PXItems.PROP_METEORITO.get());
            meteor(orb, partialTick, pose, buffers);
        } else if (entity instanceof Piercer piercer && piercer.kind() == Piercer.CHAKRAM) {
            this.chakram = stack(this.chakram, PXItems.PROP_CHAKRAM.get());
            chakram(piercer, partialTick, pose, buffers, light);
        } else if (entity instanceof Field field && field.type() == Field.TURRET) {
            this.turretBase = stack(this.turretBase, PXItems.PROP_TORRETA_BASE.get());
            this.turretHead = stack(this.turretHead, PXItems.PROP_TORRETA_CABEZA.get());
            turret(field, partialTick, pose, buffers, light);
        }
        this.look.draw(entity, partialTick, new Draw(pose, buffers, entity.getPosition(partialTick)));
        super.render(entity, yaw, partialTick, pose, buffers, light);
    }

    /** Modelo del objeto centrado en el origen actual (el renderizador de objetos lo desplaza medio bloque). */
    private void model(ItemStack stack, PoseStack pose, MultiBufferSource buffers, int light, Entity entity) {
        this.items.renderStatic(stack, ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY, pose, buffers, entity.level(), entity.getId());
    }

    /** El cohete apunta en su dirección de vuelo (el modelo tiene la ojiva hacia -Z). */
    private void rocket(Orb orb, float pt, PoseStack pose, MultiBufferSource buffers, int light) {
        float yaw = Mth.rotLerp(pt, orb.yRotO, orb.getYRot());
        float pitch = Mth.lerp(pt, orb.xRotO, orb.getXRot());
        float roll = (orb.tickCount + pt) * 12.0F;
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(yaw + 180.0F));
        pose.mulPose(Axis.XP.rotationDegrees(pitch));
        pose.mulPose(Axis.ZP.rotationDegrees(roll));
        // el cohete mide de z = 1 a z = 13,4 px: se centra en su mitad
        pose.translate(0.0F, 0.0F, 0.05F);
        model(this.rocket, pose, buffers, light, orb);
        pose.popPose();
    }

    /** La granada vuela con la ojiva por delante y girando sobre su eje (va estabilizada por giro). */
    private void grenade(Orb orb, float pt, PoseStack pose, MultiBufferSource buffers, int light) {
        float yaw = Mth.rotLerp(pt, orb.yRotO, orb.getYRot());
        float pitch = Mth.lerp(pt, orb.xRotO, orb.getXRot());
        float roll = (orb.tickCount + pt) * 40.0F;
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(yaw + 180.0F));
        pose.mulPose(Axis.XP.rotationDegrees(pitch));
        pose.mulPose(Axis.ZP.rotationDegrees(roll));
        pose.scale(0.75F, 0.75F, 0.75F);
        model(this.grenade, pose, buffers, light, orb);
        pose.popPose();
    }

    /** El meteorito gira sobre sí mismo, grande e incandescente (siempre a plena luz). */
    private void meteor(Orb orb, float pt, PoseStack pose, MultiBufferSource buffers) {
        float t = orb.tickCount + pt;
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(t * 9.0F));
        pose.mulPose(Axis.XP.rotationDegrees(t * 13.0F));
        pose.scale(2.2F, 2.2F, 2.2F);
        model(this.meteor, pose, buffers, FxRender.FULL_BRIGHT, orb);
        pose.popPose();
    }

    /** El chakram vuela tumbado y girando muy deprisa. */
    private void chakram(Piercer piercer, float pt, PoseStack pose, MultiBufferSource buffers, int light) {
        float t = piercer.tickCount + pt;
        pose.pushPose();
        pose.translate(0.0F, 0.15F, 0.0F);
        pose.mulPose(Axis.YP.rotationDegrees(-t * 45.0F));
        pose.scale(1.4F, 1.4F, 1.4F);
        model(this.chakram, pose, buffers, light, piercer);
        pose.popPose();
    }

    /** Torreta: el trípode crece al desplegarse y la cabeza mira a su objetivo. */
    private void turret(Field field, float pt, PoseStack pose, MultiBufferSource buffers, int light) {
        float t = field.tickCount + pt;
        float grow = Mth.clamp(t / 8.0F, 0.0F, 1.0F);
        grow = grow * grow * (3.0F - 2.0F * grow);
        float s = Field.TURRET_SCALE * Math.max(0.05F, grow);
        pose.pushPose();
        pose.translate(0.0F, 0.5F * s, 0.0F);
        pose.mulPose(Axis.YP.rotationDegrees(-field.getYRot()));
        pose.scale(s, s, s);
        model(this.turretBase, pose, buffers, light, field);
        pose.popPose();
        pose.pushPose();
        pose.translate(0.0F, 0.7F * s, 0.0F);
        pose.mulPose(Axis.YP.rotationDegrees(field.headYaw(pt) + 180.0F));
        pose.mulPose(Axis.XP.rotationDegrees(field.headPitch(pt)));
        pose.scale(s, s, s);
        model(this.turretHead, pose, buffers, light, field);
        pose.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
