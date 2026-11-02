package com.proyectox.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.proyectox.client.fx.FxAtlas;
import com.proyectox.client.fx.FxAtlas.Sprite;
import com.proyectox.client.fx.FxRender;
import com.proyectox.client.fx.Muzzle;
import com.proyectox.client.fx.Palette;
import com.proyectox.item.AgujaItem;
import com.proyectox.item.AvisponItem;
import com.proyectox.item.BulletGun;
import com.proyectox.item.CentinelaItem;
import com.proyectox.item.ColmenaItem;
import com.proyectox.item.GunItem;
import com.proyectox.item.MeteoroItem;
import com.proyectox.item.MorteroItem;
import com.proyectox.item.PrismaItem;
import com.proyectox.item.SpecGun;
import com.proyectox.item.TempestadItem;
import com.proyectox.item.TruenoItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

/**
 * Retícula holográfica de las armas de fuego (sustituye a la cruz normal): su forma depende del arma,
 * se abre con la dispersión, muestra el calentamiento y marca los aciertos.
 * Con la AGUJA apuntando se mira por el visor. Las armas realistas (PX-51…PX-65) apuntan por su propia mira:
 * con alza y punto de mira no hay retícula; con mira réflex se ve el punto rojo y con la holográfica, su anillo.
 */
public final class Reticle {
    private Reticle() {}

    private static int hitTicks;
    private static boolean hitKill;

    public static void hit(boolean kill) {
        hitTicks = 6;
        hitKill = kill;
    }

    public static void tick() {
        if (hitTicks > 0) hitTicks--;
    }

    /** Si la retícula propia sustituye a la cruz del juego. */
    public static boolean active() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.options.getCameraType().isFirstPerson() && mc.player.getMainHandItem().getItem() instanceof GunItem;
    }

    public static void render(GuiGraphics g, float pt) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof GunItem gun)) return;
        float cx = g.guiWidth() / 2.0F;
        float cy = g.guiHeight() / 2.0F;
        float aim = gun.aims() ? Muzzle.aim(pt) : 0.0F;
        int color = Palette.main(gun.style());
        GunGeometry.Data geo = GunGeometry.of(gun.key());
        int optic = geo != null ? geo.optic() : 0;
        // apuntando por una mira propia, la retícula del arma se desvanece
        float sight = optic >= 1 && optic <= 3 ? Mth.clamp((aim - 0.55F) / 0.35F, 0.0F, 1.0F) : 0.0F;

        mc.getTextureManager().getTexture(FxRender.ATLAS).setFilter(true, false);
        RenderSystem.enableBlend();

        if (gun.scoped() && aim > 0.85F) {
            scope(g, cx, cy, (aim - 0.85F) / 0.15F);
        }

        // apertura según la dispersión del arma y el retroceso
        double spread = Mth.lerp(aim, (float) gun.hipSpread(), (float) gun.aimSpread());
        // escopetas: la retícula abarca todo el cono de perdigones
        boolean shotgun = gun instanceof BulletGun bullets && bullets.pellets() > 1;
        if (shotgun) spread += ((BulletGun) gun).pelletSpread();
        if (gun instanceof SpecGun sg && sg.pelletSpread() > 0.0) {
            shotgun = true;
            spread += sg.pelletSpread();
        }
        double fov = mc.options.fov().get() * (1.0 - gun.zoom() * aim);
        float spreadPx = (float) (Math.tan(Math.toRadians(spread)) / Math.tan(Math.toRadians(fov / 2.0)) * cy);
        float open = 1.0F + Muzzle.recoil(pt) * 0.6F;
        float base = 22.0F;
        Sprite shape = FxAtlas.RET_DOT;
        if (gun instanceof AgujaItem) {
            // fusil de carga: cruz fina de precisión, sin círculo
            shape = FxAtlas.RET_CROSS;
            base = 24.0F;
        } else if (gun instanceof ColmenaItem) {
            shape = FxAtlas.RET_CHEVRON;
            base = 30.0F;
        } else if (gun instanceof TruenoItem || gun instanceof PrismaItem) {
            shape = FxAtlas.RET_CROSS;
            base = 24.0F;
        } else if (gun instanceof MorteroItem) {
            shape = FxAtlas.RET_CHEVRON;
            base = 26.0F;
        } else if (gun instanceof TempestadItem || gun instanceof MeteoroItem) {
            shape = FxAtlas.RET_CIRCLE;
            base = 30.0F;
        } else if (gun instanceof CentinelaItem) {
            shape = FxAtlas.RET_CHEVRON;
            base = 26.0F;
        } else if (gun instanceof SpecGun sg2 && (sg2.mode() == SpecGun.Mode.CHARGE || sg2.mode() == SpecGun.Mode.BEAM)) {
            // v7: armas de carga y de haz, cruz fina de precisión
            shape = FxAtlas.RET_CROSS;
            base = 24.0F;
        } else if (shotgun) {
            shape = FxAtlas.RET_CIRCLE;
        } else if (gun.hipSpread() >= 2.0) {
            shape = FxAtlas.RET_CIRCLE;
        } else if (gun.hipSpread() >= 0.7) {
            shape = FxAtlas.RET_CROSS;
        }
        float size = Math.max(base, spreadPx * 2.2F + 10.0F) * open;
        if (!(gun.scoped() && aim > 0.85F) && sight < 1.0F) {
            glow(g, shape, cx, cy, size, color, 0.95F * (1.0F - sight));
        }
        if (sight > 0.0F && optic == 2) {
            sightMark(g, FxAtlas.CORE, cx, cy, 7.0F, 0xFF2412, sight);
        } else if (sight > 0.0F && optic == 3) {
            sightMark(g, FxAtlas.RET_CIRCLE, cx, cy, 34.0F, 0xFF2412, sight * 0.9F);
            sightMark(g, FxAtlas.CORE, cx, cy, 6.0F, 0xFF2412, sight);
        }

        // calentamiento (las armas de carga ya no dibujan un círculo en la mira)
        int held = GunInput.fireTicks();
        float progress = -1.0F;
        int ringColor = color;
        if (gun instanceof TruenoItem && held > 0) {
            progress = TruenoItem.heat(held);
            ringColor = Palette.mix(0x5FE6FF, 0xFF2A1A, progress);
        } else if (gun instanceof AvisponItem avispon) {
            float heat = avispon.heat(stack, player.level().getGameTime()) / 100.0F;
            if (heat > 0.02F) {
                progress = heat;
                ringColor = Palette.mix(0x8CFF2E, 0xFF7A1E, heat);
            }
        }
        if (progress >= 0.0F) {
            float ring = size * (1.0F + 1.6F * (1.0F - progress));
            float pulse = progress >= 1.0F ? 0.75F + 0.25F * Mth.sin((player.tickCount + pt) * 1.2F) : 1.0F;
            glow(g, FxAtlas.CHARGE_RING, cx, cy, ring, ringColor, (0.25F + 0.75F * progress) * pulse);
        }

        // marcador de acierto
        if (hitTicks > 0) {
            float a = Math.min(1.0F, (hitTicks - pt + 1.0F) / 6.0F);
            glow(g, FxAtlas.HITMARK, cx, cy, 18.0F + (6 - hitTicks) * 1.5F, hitKill ? 0xFF3A2A : 0xFFFFFF, a);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    /** Sprite luminoso con una sombra suave debajo para que se lea sobre el cielo. */
    private static void glow(GuiGraphics g, Sprite s, float cx, float cy, float size, int rgb, float alpha) {
        // sombra: oscurece donde el sprite es brillante
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
        RenderSystem.setShaderColor(0.55F * alpha, 0.55F * alpha, 0.55F * alpha, 1.0F);
        blit(g, s, cx + 0.5F, cy + 0.5F, size * 1.08F);
        // luz
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.setShaderColor((rgb >> 16 & 0xFF) / 255.0F, (rgb >> 8 & 0xFF) / 255.0F, (rgb & 0xFF) / 255.0F, alpha);
        blit(g, s, cx, cy, size);
    }

    /** Punto o anillo de una mira réflex: rojo puro también sobre el cielo (primero oscurece y luego suma el color). */
    private static void sightMark(GuiGraphics g, Sprite s, float cx, float cy, float size, int rgb, float alpha) {
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
        RenderSystem.setShaderColor(alpha, alpha, alpha, 1.0F);
        blit(g, s, cx, cy, size);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.setShaderColor((rgb >> 16 & 0xFF) / 255.0F, (rgb >> 8 & 0xFF) / 255.0F, (rgb & 0xFF) / 255.0F, alpha);
        blit(g, s, cx, cy, size);
    }

    private static void blit(GuiGraphics g, Sprite s, float cx, float cy, float size) {
        int sz = Math.max(2, Math.round(size));
        g.blit(FxRender.ATLAS, Math.round(cx - sz / 2.0F), Math.round(cy - sz / 2.0F), sz, sz, s.x(), s.y(), s.w(), s.h(), FxAtlas.SIZE, FxAtlas.SIZE);
    }

    /** Visor a pantalla completa: círculo con retícula fina y bordes negros. */
    private static void scope(GuiGraphics g, float cx, float cy, float fade) {
        int h = g.guiHeight();
        int side = Math.round(h * 1.02F);
        int x = Math.round(cx - side / 2.0F);
        int y = Math.round(cy - side / 2.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, Mth.clamp(fade, 0.0F, 1.0F));
        Sprite s = FxAtlas.SCOPE;
        g.blit(FxRender.ATLAS, x, y, side, side, s.x(), s.y(), s.w(), s.h(), FxAtlas.SIZE, FxAtlas.SIZE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int black = ((int) (Mth.clamp(fade, 0.0F, 1.0F) * 255.0F) << 24) | 0x030405;
        if (x > 0) g.fill(0, 0, x, g.guiHeight(), black);
        if (x + side < g.guiWidth()) g.fill(x + side, 0, g.guiWidth(), g.guiHeight(), black);
        if (y > 0) g.fill(x, 0, x + side, y, black);
        if (y + side < g.guiHeight()) g.fill(x, y + side, x + side, g.guiHeight(), black);
    }
}
