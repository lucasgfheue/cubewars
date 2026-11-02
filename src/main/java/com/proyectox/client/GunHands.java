package com.proyectox.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.proyectox.client.fx.Draw;
import com.proyectox.client.fx.FxAtlas;
import com.proyectox.client.fx.GunFx;
import com.proyectox.client.fx.Muzzle;
import com.proyectox.client.fx.Palette;
import com.proyectox.fx.Fx;
import com.proyectox.item.ArieteItem;
import com.proyectox.item.BastionItem;
import com.proyectox.item.GuadanaItem;
import com.proyectox.item.GunItem;
import com.proyectox.item.MangualItem;
import com.proyectox.item.PXWeapon;
import com.proyectox.item.PicaItem;
import com.proyectox.item.SaetaItem;
import com.proyectox.item.SpecMelee;
import com.proyectox.item.TaladroItem;
import com.proyectox.registry.PXItems;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Cómo se sostienen las armas: pose de los brazos en tercera persona y, en primera persona, la posición
 * de cadera o de apuntado (el arma sube al centro de la vista), el retroceso, el fogonazo pegado a la
 * boca del cañón y el giro del portabrocas del TALADRO.
 */
public final class GunHands implements IClientItemExtensions {
    private final PXWeapon weapon;
    @Nullable
    private final GunGeometry.Data geo;

    public GunHands(PXWeapon weapon) {
        this.weapon = weapon;
        this.geo = GunGeometry.of(weapon.key());
    }

    @Override
    public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
        if (this.weapon instanceof BastionItem) {
            return entity.isUsingItem() && entity.getUseItem() == stack ? HumanoidModel.ArmPose.BLOCK : HumanoidModel.ArmPose.CROSSBOW_HOLD;
        }
        if (this.weapon instanceof SaetaItem) return HumanoidModel.ArmPose.BOW_AND_ARROW;
        if (this.weapon instanceof GunItem) {
            boolean aiming = entity == Minecraft.getInstance().player && Muzzle.aim(1.0F) > 0.5F;
            return aiming ? HumanoidModel.ArmPose.BOW_AND_ARROW : HumanoidModel.ArmPose.CROSSBOW_HOLD;
        }
        if (this.weapon instanceof TaladroItem) return HumanoidModel.ArmPose.CROSSBOW_HOLD;
        return null;
    }

    @Override
    public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack, float pt,
                                           float equip, float swing) {
        boolean main = player.getMainHandItem() == stack;
        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        if (this.weapon instanceof GunItem gun && gun.aims() && this.geo != null) {
            float aim = main ? Muzzle.aim(pt) : 0.0F;
            float rec = main ? Muzzle.recoil(pt) : 0.0F;
            float x = Mth.lerp(aim, side * 0.56F, side * this.geo.adsX());
            float y = Mth.lerp(aim, -0.52F, this.geo.adsY()) + equip * -0.6F;
            float z = Mth.lerp(aim, -0.72F, this.geo.adsZ());
            float kick = gun.kick();
            // mirando por la mira el arma apenas retrocede hacia la cara (si no, el alza se metería en el ojo)
            float back = this.geo.optic() > 0 ? 1.0F - 0.75F * aim : 1.0F;
            pose.translate(x, y + rec * 0.012F, z + rec * (0.04F + 0.1F * kick) * back);
            if (aim > 0.0F) pose.mulPose(Axis.YP.rotationDegrees(-side * this.geo.yaw() * aim));
            if (rec > 0.0F) {
                pose.mulPose(Axis.XP.rotationDegrees(rec * (3.0F + 9.0F * kick)));
                pose.mulPose(Axis.ZP.rotationDegrees(side * rec * 2.0F * (Muzzle.flashSeed() - 0.5F)));
            }
            if (main) {
                muzzleFlash(pose, side, pt);
                chargeGlow(pose, side, gun, player, pt);
            }
            return true;
        }
        if ((this.weapon instanceof GuadanaItem || this.weapon instanceof PicaItem || this.weapon instanceof MangualItem || this.weapon instanceof ArieteItem
                || this.weapon instanceof SpecMelee)
                && main && Muzzle.melee(pt) >= 0.0F) {
            meleeSwing(pose, side, equip, Muzzle.melee(pt));
            return true;
        }
        if (this.weapon instanceof TaladroItem && main && Muzzle.spinning()) {
            // la perforadora tiembla y solo gira el portabrocas con la broca: se dibujan las dos piezas por separado
            // (cuerpo fijo y portabrocas girando sobre su eje) y el modelo entero no se dibuja
            float shake = 0.004F;
            float t = player.tickCount + pt;
            pose.translate(side * 0.56F + Mth.sin(t * 2.9F) * shake, -0.52F + equip * -0.6F + Mth.cos(t * 3.7F) * shake, -0.72F);
            drillParts(pose, player, arm, pt);
            pose.scale(0.0F, 0.0F, 0.0F);
            return true;
        }
        return false;
    }

    private ItemStack drillBody = ItemStack.EMPTY;
    private ItemStack drillBit = ItemStack.EMPTY;

    /** TALADRO perforando: cuerpo fijo y portabrocas con la broca girando sobre el eje de la broca. */
    private void drillParts(PoseStack pose, LocalPlayer player, HumanoidArm arm, float pt) {
        if (this.drillBody.isEmpty()) this.drillBody = new ItemStack(PXItems.PROP_TALADRO_CUERPO.get());
        if (this.drillBit.isEmpty()) this.drillBit = new ItemStack(PXItems.PROP_TALADRO_BROCA.get());
        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource buffers = mc.renderBuffers().bufferSource();
        int light = mc.getEntityRenderDispatcher().getPackedLightCoords(player, pt);
        boolean left = arm == HumanoidArm.LEFT;
        int side = left ? -1 : 1;
        ItemDisplayContext ctx = left ? ItemDisplayContext.FIRST_PERSON_LEFT_HAND : ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
        mc.getItemRenderer().renderStatic(player, this.drillBody, ctx, left, pose, buffers, player.level(), light, OverlayTexture.NO_OVERLAY,
                player.getId());
        float[] a = GunGeometry.DRILL_AXIS;
        pose.pushPose();
        pose.translate(side * a[0], a[1], a[2]);
        pose.mulPose(new Quaternionf().rotationAxis((float) Math.toRadians(Muzzle.spin(pt)), side * a[3], a[4], a[5]));
        pose.translate(-side * a[0], -a[1], -a[2]);
        mc.getItemRenderer().renderStatic(player, this.drillBit, ctx, left, pose, buffers, player.level(), light, OverlayTexture.NO_OVERLAY,
                player.getId() + 1);
        pose.popPose();
    }

    /**
     * Animación del golpe en primera persona: la GUADAÑA baja y barre de derecha a izquierda; la PICA se
     * lanza hacia delante y vuelve; el MANGUAL da una vuelta completa; el ARIETE lanza un puñetazo seco.
     */
    private void meleeSwing(PoseStack pose, int side, float equip, float m) {
        pose.translate(side * 0.56F, -0.52F + equip * -0.6F, -0.72F);
        float arc = Mth.sin(m * (float) Math.PI);
        if (this.weapon instanceof ArieteItem) {
            // golpe rapidísimo hacia delante y vuelta más lenta
            float push = m < 0.2F ? Mth.sin(m / 0.2F * (float) Math.PI * 0.5F) : 1.0F - (m - 0.2F) / 0.8F;
            push = push * push * (3.0F - 2.0F * push);
            pose.translate(-side * 0.18F * push, 0.08F * push, -0.5F * push);
            pose.mulPose(Axis.XP.rotationDegrees(-6.0F * push));
            pose.mulPose(Axis.ZP.rotationDegrees(side * 8.0F * push));
            return;
        }
        if (this.weapon instanceof MangualItem) {
            // la bola gira alrededor de la mano: una vuelta y algo en cada golpe
            float e = m * m * (3.0F - 2.0F * m);
            pose.translate(-side * 0.12F * arc, 0.12F * arc, -0.1F * arc);
            pose.mulPose(Axis.YP.rotationDegrees(side * (40.0F - 400.0F * e)));
            pose.mulPose(Axis.XP.rotationDegrees(-35.0F * arc));
            return;
        }
        if (this.weapon instanceof SpecMelee sm && sm.kind() == SpecMelee.Kind.SMASH) {
            // golpe seco de arriba abajo (bate y mazo): baja deprisa y vuelve despacio
            float down = m < 0.3F ? m / 0.3F : 1.0F - (m - 0.3F) / 0.7F;
            down = down * down * (3.0F - 2.0F * down);
            pose.translate(-side * 0.12F * down, -0.08F * down, -0.3F * down);
            pose.mulPose(Axis.XP.rotationDegrees(-70.0F * down));
            pose.mulPose(Axis.ZP.rotationDegrees(side * 10.0F * down));
            return;
        }
        if (this.weapon instanceof PicaItem || this.weapon instanceof SpecMelee sm2 && sm2.kind() == SpecMelee.Kind.THRUST) {
            float push = m < 0.35F ? Mth.sin(m / 0.35F * (float) Math.PI * 0.5F) : 1.0F - (m - 0.35F) / 0.65F;
            push = push * push * (3.0F - 2.0F * push);
            pose.translate(-side * 0.12F * push, 0.06F * push, -0.65F * push);
            pose.mulPose(Axis.XP.rotationDegrees(-8.0F * push));
            return;
        }
        float e = m * m * (3.0F - 2.0F * m);
        pose.translate(-side * 0.25F * arc, -0.1F * arc, -0.15F * arc);
        pose.mulPose(Axis.YP.rotationDegrees(side * (55.0F - 120.0F * e)));
        pose.mulPose(Axis.XP.rotationDegrees(-65.0F * arc));
        pose.mulPose(Axis.ZP.rotationDegrees(side * 25.0F * arc));
    }

    /** Armas de carga (AGUJA, SOLAR): un resplandor crece en la boca del cañón mientras se mantiene el gatillo. */
    private void chargeGlow(PoseStack pose, int side, GunItem gun, LocalPlayer player, float pt) {
        if (gun.chargeTicks() <= 0 || this.geo == null) return;
        int held = GunInput.fireTicks();
        if (held <= 0) return;
        float c = Mth.clamp((held + pt) / gun.chargeTicks(), 0.0F, 1.0F);
        int main = Palette.main(gun.style());
        int hot = Palette.hot(gun.style());
        float t = player.tickCount + pt;
        float pulse = c >= 1.0F ? 0.85F + 0.15F * Mth.sin(t * 1.3F) : 1.0F;
        Draw d = new Draw(pose, Minecraft.getInstance().renderBuffers().bufferSource(), new Vector3f(0, 0, 0), new Quaternionf());
        Vector3f tip = new Vector3f(side * this.geo.tipX(), this.geo.tipY(), this.geo.tipZ());
        d.billboard(true, FxAtlas.GLOW, tip, (0.05F + 0.2F * c) * pulse, 0.0F, main, 0.4F + 0.5F * c);
        d.billboard(true, gun.style() == Fx.SOLAR ? FxAtlas.CORONA : FxAtlas.RING, tip, (0.04F + 0.12F * c) * pulse, t * 0.05F, hot, 0.3F + 0.6F * c);
        d.billboard(true, FxAtlas.CORE, tip, 0.015F + 0.05F * c, 0.0F, 0xFFFFFF, 0.5F + 0.5F * c);
    }

    /**
     * Fogonazo de fuego de las armas de pólvora, en el espacio de la mano: llamarada a lo largo del cañón, estrella de
     * fuego en la boca, chorros del freno de boca o del compensador y el resplandor naranja.
     */
    private void realFlash(PoseStack pose, int side, float f, GunFx.Profile g) {
        Draw d = new Draw(pose, Minecraft.getInstance().renderBuffers().bufferSource(), new Vector3f(0, 0, 0), new Quaternionf());
        Vector3f tip = new Vector3f(side * this.geo.tipX(), this.geo.tipY(), this.geo.tipZ());
        float k = g.size();
        if (g.suppressed()) {
            d.billboard(true, FxAtlas.GLOW, tip, 0.07F * k, 0.0F, 0xFF9A50, 0.3F * f);
            return;
        }
        int fr = ((int) (Muzzle.flashSeed() * 4.0F)) & 3;
        float roll = Muzzle.flashSeed() * 6.2832F;
        float grow = 0.75F + 0.25F * f;
        // bola de fuego opaca debajo de la llamarada (se ve naranja también a pleno sol)
        d.billboard(false, FxAtlas.SMOKE_1, new Vector3f(tip.x, tip.y, tip.z - 0.05F * k), 0.11F * k * grow, roll, 0xFFC868, 0.8F * f);
        d.billboard(true, FxAtlas.GLOW, tip, 0.3F * k, 0.0F, 0xFF8A30, 0.55F * f);
        d.cross(true, FxAtlas.MFLASH_SIDE[fr], tip, new Vector3f(0, 0, -1), 0.02F, 0.55F * k * grow, 0.14F * k, 0xFFFFFF, f);
        d.billboard(true, FxAtlas.MFLASH_FRONT[fr], tip, 0.19F * k * grow, roll, 0xFFFFFF, f);
        if (g.brake()) {
            for (int s = -1; s <= 1; s += 2) {
                d.cross(true, FxAtlas.MFLASH_SIDE[(fr + 1) & 3], tip, new Vector3f(s, 0.0F, 0.2F), 0.0F, 0.2F * k * grow, 0.055F * k, 0xFFFFFF, f);
            }
        }
        if (g.comp()) {
            for (int s = -1; s <= 1; s += 2) {
                d.cross(true, FxAtlas.MFLASH_SIDE[(fr + 2) & 3], new Vector3f(tip.x, tip.y, tip.z + 0.04F), new Vector3f(0.25F * s, 1.0F, -0.2F),
                        0.0F, 0.14F * k * grow, 0.045F * k, 0xFFFFFF, f);
            }
        }
    }

    /** Fogonazo dibujado en el espacio de la mano, justo en la boca del cañón. */
    private void muzzleFlash(PoseStack pose, int side, float pt) {
        float f = Muzzle.flash(pt);
        if (f <= 0.0F || this.geo == null) return;
        int style = Muzzle.flashStyle();
        if (style < 0) return;
        GunFx.Profile real = GunFx.of(style);
        if (real != null) {
            realFlash(pose, side, f, real);
            return;
        }
        int main = Palette.main(style);
        int hot = Palette.hot(style);
        float k = switch (style) {
            case Fx.REMACHE, Fx.ECO, Fx.MORTERO, Fx.SOLAR, Fx.TRABUCO, Fx.LINCE, Fx.RUGIDO -> 1.5F;
            case Fx.AGUJA, Fx.TRITURADORA, Fx.VORTICE, Fx.COLMENA, Fx.CENTELLA, Fx.ARPON, Fx.CENIZA, Fx.YUNQUE, Fx.COYOTE, Fx.PULSAR -> 1.2F;
            case Fx.ROTOR, Fx.COLIBRI, Fx.CAUSTICA, Fx.GRANIZO, Fx.CHACAL, Fx.RETORNO -> 0.75F;
            case Fx.TRUENO, Fx.CHISPA, Fx.SOPLETE, Fx.MAREA, Fx.METEORO, Fx.FALLA -> 0.55F;
            default -> 1.0F;
        };
        Draw d = new Draw(pose, Minecraft.getInstance().renderBuffers().bufferSource(), new Vector3f(0, 0, 0), new Quaternionf());
        Vector3f tip = new Vector3f(side * this.geo.tipX(), this.geo.tipY(), this.geo.tipZ());
        float roll = Muzzle.flashSeed() * 6.2832F;
        boolean beamWeapon = style == Fx.TRUENO || style == Fx.CHISPA || style == Fx.SOPLETE || style == Fx.MAREA || style == Fx.METEORO
                || style == Fx.FALLA;
        d.billboard(true, FxAtlas.GLOW, tip, 0.22F * k, 0.0F, main, 0.75F * f);
        if (style == Fx.SOPLETE) {
            // lengua de fuego opaca que sale de la boquilla (se ve también de día); el chorro sigue en el mundo
            float on = Math.min(1.0F, f * 2.0F);
            Vector3f fwd = new Vector3f(0, 0, -1);
            d.cross(false, FxAtlas.SMOKE_1, tip, fwd, 0.02F, 0.95F, 0.075F, 0xFFC24A, 0.9F * on);
            d.cross(true, FxAtlas.FLASH_SIDE, tip, fwd, 0.0F, 0.55F, 0.05F, 0xFFE0A0, 0.5F * on);
        }
        if (!beamWeapon) {
            d.billboard(true, style % 2 == 0 ? FxAtlas.STAR4 : FxAtlas.STAR6, tip, 0.13F * k, roll, hot, f);
            d.cross(true, FxAtlas.FLASH_SIDE, tip, new Vector3f(0, 0, -1), 0.0F, 0.32F * k * (0.7F + 0.3F * f), 0.075F * k, hot, f);
        }
        d.billboard(true, FxAtlas.CORE, tip, 0.05F * k, 0.0F, 0xFFFFFF, f);
    }
}
