package com.proyectox.client.fx;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.proyectox.ProyectoX;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

/**
 * Formas de dibujar los efectos: todos usan el atlas HD de textures/fx con filtrado lineal.
 * "Aditivo" suma luz (fogonazos, haces, chispas); "suave" mezcla por transparencia (humo, metal).
 */
public final class FxRender {
    private FxRender() {}

    public static final ResourceLocation ATLAS = ProyectoX.id("textures/fx/atlas.png");

    /** Luz máxima (los efectos brillan por sí mismos). */
    public static final int FULL_BRIGHT = 0xF000F0;

    // ---------------------------------------------------------------- partículas

    public static final ParticleRenderType ADDITIVE = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textures) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            textures.getTexture(ATLAS).setFilter(true, false);
            RenderSystem.setShaderTexture(0, ATLAS);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        }

        @Override
        public String toString() {
            return "PROYECTOX_ADDITIVE";
        }
    };

    public static final ParticleRenderType SOFT = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textures) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            textures.getTexture(ATLAS).setFilter(true, false);
            RenderSystem.setShaderTexture(0, ATLAS);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        }

        @Override
        public String toString() {
            return "PROYECTOX_SOFT";
        }
    };

    // ---------------------------------------------------------------- entidades y mano

    private static RenderType glow;
    private static RenderType soft;

    /** Brillo aditivo para entidades y para el arma en primera persona (formato de entidad). */
    public static RenderType glow() {
        if (glow == null) {
            glow = RenderType.create("proyectox_glow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true,
                    RenderType.CompositeState.builder()
                            .setShaderState(RenderStateShard.RENDERTYPE_EYES_SHADER)
                            .setTextureState(new RenderStateShard.TextureStateShard(ATLAS, true, false))
                            .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                            .setCullState(RenderStateShard.NO_CULL)
                            .createCompositeState(false));
        }
        return glow;
    }

    /** Sprite translúcido para entidades (discos de sierra, granadas, núcleo de la singularidad). */
    public static RenderType soft() {
        if (soft == null) {
            soft = RenderType.create("proyectox_soft", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true,
                    RenderType.CompositeState.builder()
                            .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                            .setTextureState(new RenderStateShard.TextureStateShard(ATLAS, true, false))
                            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                            .setCullState(RenderStateShard.NO_CULL)
                            .setOverlayState(RenderStateShard.OVERLAY)
                            .createCompositeState(false));
        }
        return soft;
    }
}
