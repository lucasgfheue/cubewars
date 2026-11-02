package com.proyectox.item;

import com.proyectox.fx.Fx;
import com.proyectox.util.Aim;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Arma de fuego: dispara con el clic izquierdo y apunta con el derecho.
 *
 * <p>El cliente envía el estado de los botones ({@link com.proyectox.network.InputPayload}) y el
 * servidor llama a {@link #triggerTick} cada tick mientras se mantiene el gatillo; por defecto el arma
 * dispara a su cadencia mientras tenga energía. Las armas de carga (AGUJA, COLMENA) disparan al soltar.
 * Apuntar estrecha la dispersión y centra la salida del disparo.
 */
public abstract class GunItem extends PXWeapon implements TriggerWeapon {
    /** Próximo tick en que cada jugador puede volver a disparar (por arma). */
    private record Ready(Item item, long tick) {}

    private static final Map<UUID, Ready> NEXT = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> LAST_EMPTY = new ConcurrentHashMap<>();

    protected GunItem(String key, String code, int color, int style, Properties properties) {
        super(key, code, color, style, properties);
    }

    // ------------------------------------------------------------------ parámetros

    /** Ticks entre disparos (1 = 20 disparos por segundo). */
    public abstract int interval();

    /** Energía que gasta cada disparo. */
    public int cost() {
        return 1;
    }

    /** Ticks sin regenerar tras disparar. */
    public int regenDelay() {
        return 6;
    }

    /** Dispersión en grados disparando desde la cadera. */
    public double hipSpread() {
        return 0.0;
    }

    /** Dispersión en grados apuntando. */
    public double aimSpread() {
        return hipSpread() * 0.3;
    }

    /** Cuánto se cierra el campo de visión al apuntar (0 = nada, 0,75 = visor de francotirador). */
    public float zoom() {
        return 0.2F;
    }

    /** Si al apuntar se mira por un visor a pantalla completa. */
    public boolean scoped() {
        return false;
    }

    /** Fuerza del retroceso visual en primera persona (0–1). */
    public float kick() {
        return 0.35F;
    }

    /** Si el clic derecho apunta (el BASTIÓN lo usa para cubrirse). */
    public boolean aims() {
        return true;
    }

    /** Ticks de carga completa (armas que disparan al soltar); 0 = dispara al pulsar. */
    public int chargeTicks() {
        return 0;
    }

    /** Distancia de la boca del cañón delante de los ojos. */
    protected double muzzleForward() {
        return 0.95;
    }

    // ------------------------------------------------------------------ disparo

    public static void forget(Player player) {
        NEXT.remove(player.getUUID());
        LAST_EMPTY.remove(player.getUUID());
    }

    /** Si ya pasó la cadencia desde el último disparo de este jugador con esta arma. */
    protected boolean ready(Player player, long now) {
        Ready ready = NEXT.get(player.getUUID());
        return ready == null || ready.item() != this || now >= ready.tick();
    }

    /** Fija la espera hasta el próximo disparo (y la muestra en la barra de acceso rápido si es larga). */
    protected void delay(Player player, long now, int ticks) {
        NEXT.put(player.getUUID(), new Ready(this, now + ticks));
        if (ticks >= 10) player.getCooldowns().addCooldown(this, ticks);
    }

    /** Gasta energía; si no hay, avisa como mucho una vez cada 0,6 s. */
    protected boolean spend(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int amount) {
        if (consume(stack, now, amount, regenDelay())) return true;
        Long last = LAST_EMPTY.get(player.getUUID());
        if (last == null || now - last >= 12L) {
            LAST_EMPTY.put(player.getUUID(), now);
            emptyClick(level, player, "message.proyectox.sin_energia");
        }
        return false;
    }

    @Override
    public void triggerTick(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming) {
        if (player.isSpectator() || !ready(player, now)) return;
        if (!spend(player, stack, level, now, cost())) return;
        delay(player, now, interval());
        Shot shot = Shot.of(player, level, aiming, aiming ? aimSpread() : hipSpread(), muzzleForward());
        fire(shot, stack, heldTicks);
        if (muzzleFlash()) Fx.muzzle(level, player, style(), shot.muzzle(), shot.look());
    }

    /**
     * Dispara el proyectil del arma.
     * @param heldTicks ticks que lleva pulsado el gatillo (para las armas que se calientan)
     */
    protected abstract void fire(Shot shot, ItemStack stack, int heldTicks);

    /** Si cada disparo lleva fogonazo (las armas de chorro y los haces dibujan el suyo). */
    protected boolean muzzleFlash() {
        return true;
    }

    // ------------------------------------------------------------------ comportamiento como objeto

    /** El clic derecho apunta: no hace nada más (el cliente cancela el uso normal del objeto). */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    /** Las armas de fuego no golpean cuerpo a cuerpo. */
    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        return true;
    }

    /** Ni rompen bloques. */
    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, net.minecraft.world.entity.LivingEntity entity, InteractionHand hand) {
        return true;
    }

    // ------------------------------------------------------------------ geometría de un disparo

    /**
     * Un disparo: quién, desde dónde y con qué precisión.
     * Apuntando, la boca del cañón queda centrada bajo la mira.
     */
    public record Shot(ServerPlayer player, ServerLevel level, boolean aiming, Vec3 eye, Vec3 look, Vec3 muzzle, double spread) {
        public static Shot of(ServerPlayer player, ServerLevel level, boolean aiming, double spread, double forward) {
            Vec3 muzzle = aiming ? Aim.muzzle(player, forward, 0.0, 0.13) : Aim.muzzle(player, forward, 0.26, 0.2);
            return new Shot(player, level, aiming, player.getEyePosition(), player.getLookAngle(), muzzle, spread);
        }

        /** Dirección desde la boca del cañón hacia el punto de mira, con la dispersión del arma. */
        public Vec3 aimDir(double range) {
            return Aim.spread(Aim.converge(this.player, this.muzzle, range), this.level.random, this.spread);
        }

        /** Dirección desde los ojos (para los rayos instantáneos), con la dispersión del arma. */
        public Vec3 eyeDir() {
            return Aim.spread(this.look, this.level.random, this.spread);
        }
    }
}
