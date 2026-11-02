package com.proyectox.client.fx;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/** Renderizador de proyectiles hecho solo con efectos: cada tipo aporta su "aspecto" ({@link Looks}). */
public class FxEntityRenderer<T extends Entity> extends EntityRenderer<T> {
    /** Dibuja la entidad con un {@link Draw} centrado en su posición interpolada. */
    public interface Look<T> {
        void draw(T entity, float partialTick, Draw draw);
    }

    private final Look<T> look;

    public FxEntityRenderer(EntityRendererProvider.Context context, Look<T> look) {
        super(context);
        this.look = look;
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(T entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
        Vec3 origin = entity.getPosition(partialTick);
        this.look.draw(entity, partialTick, new Draw(pose, buffers, origin));
        super.render(entity, yaw, partialTick, pose, buffers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TextureAtlas.LOCATION_PARTICLES;
    }

    /** Ayuda para registrar renderizadores con una expresión lambda. */
    public static <E extends Entity> EntityRendererProvider<E> of(Look<E> look) {
        return ctx -> new FxEntityRenderer<>(ctx, look);
    }
}
