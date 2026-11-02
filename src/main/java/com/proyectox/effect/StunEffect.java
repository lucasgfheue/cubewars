package com.proyectox.effect;

import com.proyectox.ProyectoX;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

/**
 * Parálisis (MARTINETE y PULSAR): mientras dura, la criatura no puede andar, saltar ni atacar. Puede caer si está en
 * el aire. A los jugadores les quita la velocidad y el salto; a los monstruos, además, la ruta y el objetivo.
 */
public class StunEffect extends MobEffect {
    public StunEffect() {
        super(MobEffectCategory.HARMFUL, 0xF2E35A);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ProyectoX.id("paralisis_movimiento"), -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.JUMP_STRENGTH, ProyectoX.id("paralisis_salto"), -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        Vec3 v = entity.getDeltaMovement();
        entity.setDeltaMovement(0.0, Math.min(v.y, 0.0), 0.0);
        if (entity instanceof Mob mob) {
            mob.getNavigation().stop();
            mob.setTarget(null);
        }
        return true;
    }
}
