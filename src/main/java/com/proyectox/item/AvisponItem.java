package com.proyectox.item;

import com.proyectox.entity.PlasmaBolt;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXComponents;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import com.proyectox.util.Times;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * PX-03 AVISPÓN — carabina de pulsos de plasma automática (10 por segundo). El fuego continuo calienta
 * el cañón y cada pulso hace más daño: de 3 en frío a 8 al llegar a la mitad del calor, cuando los pulsos
 * salen rojos e incendian al enemigo. Nunca se bloquea por el calor.
 */
public class AvisponItem extends GunItem {
    public static final float HEAT_PER_SHOT = 3.0F;
    public static final float IGNITE_HEAT = 50.0F;
    /** Daño de cada pulso en frío y al rojo (sube poco a poco con el calor). */
    public static final float DAMAGE = 3.0F;
    public static final float HOT_DAMAGE = 8.0F;

    public AvisponItem(Properties properties) {
        super("avispon", "PX-03", 0x96FF28, Fx.AVISPON, properties);
    }

    @Override
    public int maxCharge() {
        return 200;
    }

    @Override
    public int regenTicks() {
        return 1;
    }

    @Override
    public int regenDelay() {
        return 10;
    }

    @Override
    public int interval() {
        return 2;
    }

    @Override
    public double hipSpread() {
        return 1.0;
    }

    @Override
    public double aimSpread() {
        return 0.25;
    }

    @Override
    public float zoom() {
        return 0.25F;
    }

    @Override
    public float kick() {
        return 0.3F;
    }

    /** Calor 0–100: sube con cada pulso y se disipa a los 0,5 s de dejar de disparar. */
    public float heat(ItemStack stack, long now) {
        int stored = stack.getOrDefault(PXComponents.HEAT.get(), 0);
        long stamp = stack.getOrDefault(PXComponents.HEAT_STAMP.get(), now);
        long idle = Math.max(0L, now - stamp - 10L);
        return Math.max(0.0F, stored / 10.0F - idle * 4.0F);
    }

    /** Daño de un pulso con este calor: 3 en frío, 8 a partir de la mitad del calor (pulsos rojos). */
    public static float damage(float heat) {
        return DAMAGE + (HOT_DAMAGE - DAMAGE) * Math.min(1.0F, heat / IGNITE_HEAT);
    }

    private static void setHeat(ItemStack stack, long now, float heat) {
        stack.set(PXComponents.HEAT.get(), Math.round(Math.min(100.0F, heat) * 10.0F));
        stack.set(PXComponents.HEAT_STAMP.get(), now);
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        long now = shot.level().getGameTime();
        float heat = Math.min(100.0F, heat(stack, now) + HEAT_PER_SHOT);
        setHeat(stack, now, heat);
        boolean hot = heat >= IGNITE_HEAT;
        float damage = damage(heat);
        shot.level().addFreshEntity(PlasmaBolt.create(shot.level(), shot.player(), shot.muzzle(), shot.aimDir(160.0).scale(5.0), damage, 60, hot));
        FX.sound(shot.level(), shot.player(), PXSounds.AVISPON_DISPARO.get(), 0.7F, 0.9F + shot.level().random.nextFloat() * 0.2F + heat * 0.003F);
    }

    @Override
    public void triggerTick(net.minecraft.server.level.ServerPlayer player, ItemStack stack, net.minecraft.server.level.ServerLevel level,
                            long now, int heldTicks, boolean aiming) {
        if (player.isSpectator() || !ready(player, now)) return;
        if (!spend(player, stack, level, now, cost())) return;
        delay(player, now, interval());
        Shot shot = Shot.of(player, level, aiming, aiming ? aimSpread() : hipSpread(), muzzleForward());
        fire(shot, stack, heldTicks);
        Fx.muzzle(level, player, heat(stack, now) >= IGNITE_HEAT ? Fx.AVISPON_HOT : Fx.AVISPON, shot.muzzle(), shot.look());
    }

    @Override
    protected void appendStatus(ItemStack stack, long now, List<Component> lines) {
        super.appendStatus(stack, now, lines);
        float heat = heat(stack, now);
        ChatFormatting tone = heat >= IGNITE_HEAT ? ChatFormatting.GOLD : ChatFormatting.GRAY;
        lines.add(Component.translatable("tooltip.proyectox.avispon.calor", String.format(Locale.ROOT, "%.0f", heat)).withStyle(tone));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return heat(stack, Times.client()) >= IGNITE_HEAT ? 0xFFB020 : color();
    }
}
