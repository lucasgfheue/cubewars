package com.proyectox.item;

import com.proyectox.ProyectoX;
import com.proyectox.fx.Fx;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.phys.Vec3;

/** Arma cuerpo a cuerpo: golpe con el clic izquierdo, indestructible y con efecto de impacto propio. */
public abstract class MeleeItem extends PXWeapon {
    protected MeleeItem(String key, String code, int color, int style, Properties properties) {
        super(key, code, color, style, properties);
    }

    /** Propiedades comunes: indestructible (conserva la durabilidad para poder encantarse). */
    public static Properties meleeProperties(int durability) {
        return baseProperties().durability(durability).component(DataComponents.UNBREAKABLE, new Unbreakable(true));
    }

    /**
     * Atributos de la mano principal.
     * @param damage daño total por golpe
     * @param speed  golpes por segundo
     * @param reach  bloques extra de alcance (0 = normal)
     */
    public static ItemAttributeModifiers attributes(double damage, double speed, double reach) {
        return attributeBuilder(damage, speed, reach).build();
    }

    public static ItemAttributeModifiers.Builder attributeBuilder(double damage, double speed, double reach) {
        ItemAttributeModifiers.Builder b = ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, damage - 1.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, speed - 4.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND);
        if (reach > 0.0) {
            b.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(ProyectoX.id("alcance_arma"), reach, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND);
        }
        return b;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.level() instanceof ServerLevel level) {
            Vec3 dir = target.getBoundingBox().getCenter().subtract(attacker.getEyePosition());
            Fx.point(level, Fx.MELEE, style(), target.getBoundingBox().getCenter(), dir.lengthSqr() < 1.0E-6 ? attacker.getLookAngle() : dir.normalize(),
                    1.0F, attacker.getId());
            onHit(level, stack, target, attacker);
        }
        return true;
    }

    /** Efecto extra al golpear (servidor). */
    protected void onHit(ServerLevel level, ItemStack stack, LivingEntity target, LivingEntity attacker) {}

    @Override
    public int getEnchantmentValue() {
        return 15;
    }
}
