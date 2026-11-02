package com.proyectox.item;

import com.proyectox.entity.EnergyBolt;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.FX;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

/**
 * PX-18 BASTIÓN — escudo de energía de asalto. Clic derecho: te cubres y bloquea el daño venga de donde
 * venga (también por los flancos y la espalda). Clic izquierdo: su proyector dispara balas cinéticas
 * de 8 de daño, también mientras te cubres.
 */
public class BastionItem extends GunItem {
    public BastionItem(Properties properties) {
        super("bastion", "PX-18", 0xFFD8B1, Fx.BASTION, properties);
    }

    @Override
    public int maxCharge() {
        return 120;
    }

    @Override
    public int regenTicks() {
        return 2;
    }

    @Override
    public int interval() {
        return 4;
    }

    @Override
    public double hipSpread() {
        return 0.8;
    }

    @Override
    public boolean aims() {
        return false;
    }

    @Override
    public float kick() {
        return 0.3F;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        shot.level().addFreshEntity(EnergyBolt.create(shot.level(), shot.player(), EnergyBolt.BULLET, shot.muzzle(), shot.aimDir(160.0).scale(5.0), 8.0F, 40));
        FX.sound(shot.level(), shot.player(), PXSounds.BASTION_PULSO.get(), 0.7F, 1.5F + shot.level().random.nextFloat() * 0.2F);
    }

    // ------------------------------------------------------------------ escudo

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        return ItemAbilities.DEFAULT_SHIELD_ACTIONS.contains(ability);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    /** Llamado desde el evento de bloqueo: destello del campo de energía donde golpeó el ataque. */
    public void blocked(LivingEntity holder, Vec3 from) {
        if (!(holder.level() instanceof ServerLevel level)) return;
        Vec3 eye = holder.getEyePosition();
        Vec3 dir = from.subtract(eye);
        dir = dir.lengthSqr() < 1.0E-6 ? holder.getLookAngle() : dir.normalize();
        Fx.point(level, Fx.SHIELD, Fx.BASTION, eye.add(dir.scale(0.9)).add(0.0, -0.3, 0.0), dir, 1.0F, holder.getId());
    }
}
