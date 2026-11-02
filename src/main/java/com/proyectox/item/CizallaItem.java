package com.proyectox.item;

import com.proyectox.entity.SlashWave;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * PX-06 CIZALLA — hoja de vibroenergía: 10 de daño por golpe. Clic derecho: lanza su tajo de media luna,
 * que atraviesa a todos los enemigos en 50 bloques (8 de daño).
 */
public class CizallaItem extends MeleeItem {
    /** Daño cuerpo a cuerpo. */
    public static final double DAMAGE = 10.0;
    public static final float WAVE_DAMAGE = 8.0F;

    public CizallaItem(Properties properties) {
        super("cizalla", "PX-06", 0xFF2E7E, Fx.CIZALLA, properties);
    }

    @Override
    public int maxCharge() {
        return 10;
    }

    @Override
    public int regenTicks() {
        return 10;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        long now = level.getGameTime();
        if (charge(stack, now) <= 0) {
            emptyClick(level, player, "message.proyectox.sin_energia");
            return InteractionResultHolder.fail(stack);
        }
        if (level instanceof ServerLevel server) {
            consume(stack, now, 1, 6);
            Vec3 muzzle = Aim.muzzle(player, 1.0, 0.0, 0.3);
            Vec3 dir = Aim.converge(player, muzzle, 64.0);
            server.addFreshEntity(SlashWave.create(server, player, muzzle, dir.scale(2.2), WAVE_DAMAGE));
            Fx.muzzle(server, player, Fx.CIZALLA, muzzle, dir);
            FX.sound(server, player, PXSounds.CIZALLA_TAJO.get(), 1.2F, 0.9F + server.random.nextFloat() * 0.2F);
        }
        player.getCooldowns().addCooldown(this, 6);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
