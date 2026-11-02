package com.proyectox.item;

import com.proyectox.registry.PXComponents;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import com.proyectox.util.Times;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * Base de todas las armas de Proyecto X.
 *
 * <p>Sistema de energía común: cada arma tiene una reserva ({@link #maxCharge()}) que se regenera
 * 1 unidad cada {@link #regenTicks()} ticks. Se guarda como "valor + marca de tiempo" en el propio
 * ItemStack y se calcula al vuelo, por lo que no hay que actualizar el objeto en cada tick.
 */
public abstract class PXWeapon extends Item {
    private final String key;
    private final String code;
    private final int color;
    private final int style;

    protected PXWeapon(String key, String code, int color, int style, Properties properties) {
        super(properties);
        this.key = key;
        this.code = code;
        this.color = color;
        this.style = style;
    }

    /** Propiedades comunes: una por ranura, épica e inmune al fuego y la lava. */
    public static Properties baseProperties() {
        return new Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant();
    }

    public String key() {
        return key;
    }

    public String code() {
        return code;
    }

    public int color() {
        return color;
    }

    /** Estilo de los efectos visuales ({@link com.proyectox.fx.Fx}). */
    public int style() {
        return style;
    }

    // ------------------------------------------------------------------ energía

    /** Capacidad de la reserva; 0 si el arma no usa reserva. */
    public int maxCharge() {
        return 0;
    }

    /** Ticks para recuperar una unidad; 0 o menos = no se regenera sola. */
    public int regenTicks() {
        return 0;
    }

    /** Unidades que se recuperan cada {@link #regenTicks()} ticks (la cinta de la DILUVIO recupera 2). */
    public int regenAmount() {
        return 1;
    }

    /** Carga con la que sale un arma recién fabricada. */
    public int initialCharge() {
        return maxCharge();
    }

    public int charge(ItemStack stack, long now) {
        int max = maxCharge();
        if (max <= 0) return 0;
        int stored = Mth.clamp(stack.getOrDefault(PXComponents.CHARGE.get(), initialCharge()), 0, max);
        int regen = regenTicks();
        if (stored >= max || regen <= 0) return stored;
        long stamp = stack.getOrDefault(PXComponents.STAMP.get(), now);
        long elapsed = Math.max(0L, now - stamp);
        return (int) Math.min(max, stored + (elapsed / regen) * regenAmount());
    }

    /**
     * Fija la carga conservando el progreso parcial de regeneración.
     * @param regenDelay ticks de espera antes de que vuelva a regenerarse (0 = sin espera)
     */
    public void setCharge(ItemStack stack, long now, int value, int regenDelay) {
        int max = maxCharge();
        int regen = regenTicks();
        long partial = 0L;
        if (regen > 0 && regenDelay <= 0) {
            int stored = Mth.clamp(stack.getOrDefault(PXComponents.CHARGE.get(), initialCharge()), 0, max);
            long stamp = stack.getOrDefault(PXComponents.STAMP.get(), now);
            long elapsed = Math.max(0L, now - stamp);
            if (stored < max && stored + (elapsed / regen) * regenAmount() < max) partial = elapsed % regen;
        }
        stack.set(PXComponents.CHARGE.get(), Mth.clamp(value, 0, max));
        stack.set(PXComponents.STAMP.get(), now - partial + Math.max(0, regenDelay));
    }

    /** Gasta {@code amount} unidades si las hay. */
    public boolean consume(ItemStack stack, long now, int amount, int regenDelay) {
        int current = charge(stack, now);
        if (current < amount) return false;
        setCharge(stack, now, current - amount, regenDelay);
        return true;
    }

    /** Ticks que faltan para la siguiente unidad (para el tooltip). */
    public int ticksToNext(ItemStack stack, long now) {
        int regen = regenTicks();
        if (regen <= 0 || charge(stack, now) >= maxCharge()) return 0;
        long stamp = stack.getOrDefault(PXComponents.STAMP.get(), now);
        long elapsed = now - stamp;
        if (elapsed < 0) return (int) (-elapsed) + regen;
        return (int) (regen - elapsed % regen);
    }

    protected void emptyClick(Level level, Player player, String messageKey) {
        if (!level.isClientSide) {
            FX.sound(level, player, PXSounds.VACIO.get(), 0.7F, 1.0F);
            player.displayClientMessage(Component.translatable(messageKey).withStyle(ChatFormatting.RED), true);
        }
    }

    // ------------------------------------------------------------------ uso

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    // ------------------------------------------------------------------ presentación

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack)).withColor(color);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.proyectox.codigo", code,
                Component.translatable("item.proyectox." + key + ".clase")).withStyle(ChatFormatting.GRAY));
        long now = Times.now(context.level());
        appendStatus(stack, now, lines);
        if (Times.shiftDown()) {
            lines.add(Component.translatable("item.proyectox." + key + ".energia").withStyle(ChatFormatting.DARK_AQUA));
            lines.add(bullet("item.proyectox." + key + ".uso1"));
            lines.add(bullet("item.proyectox." + key + ".uso2"));
        } else {
            lines.add(Component.translatable("tooltip.proyectox.shift").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    /** Línea de estado (carga, calor...) bajo el nombre. */
    protected void appendStatus(ItemStack stack, long now, List<Component> lines) {
        if (maxCharge() <= 0) return;
        int c = charge(stack, now);
        MutableComponent line = Component.translatable("tooltip.proyectox." + key + ".carga", c, maxCharge()).withColor(color);
        if (c < maxCharge() && regenTicks() > 0) {
            line.append(Component.translatable("tooltip.proyectox.siguiente",
                    String.format(java.util.Locale.ROOT, "%.1f", ticksToNext(stack, now) / 20.0)).withStyle(ChatFormatting.DARK_GRAY));
        }
        lines.add(line);
    }

    private MutableComponent bullet(String key) {
        return Component.literal("▸ ").withColor(color).append(Component.translatable(key).withStyle(ChatFormatting.GRAY));
    }

    /** Si la barra bajo el icono muestra la reserva de energía (si no, la durabilidad). */
    protected boolean chargeBar() {
        return maxCharge() > 0;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        if (!chargeBar()) return super.isBarVisible(stack);
        return charge(stack, Times.client()) < maxCharge();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (!chargeBar()) return super.getBarWidth(stack);
        return Math.round(13.0F * charge(stack, Times.client()) / maxCharge());
    }

    @Override
    public int getBarColor(ItemStack stack) {
        if (!chargeBar()) return super.getBarColor(stack);
        return color;
    }

    /** Cambiar la carga no debe hacer "rebotar" el arma en la mano. */
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() == newStack.getItem();
    }
}
