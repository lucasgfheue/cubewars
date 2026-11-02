package com.proyectox.item;

import com.proyectox.ProyectoX;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import com.proyectox.util.Hits;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.phys.Vec3;

/**
 * PX-07 TALADRO — perforadora de asedio. Mantén el clic izquierdo sobre una criatura: la broca gira y
 * la perfora, y cuanto más tiempo sigue perforando al mismo objetivo más daño hace
 * (3 → 10 a partir de 1,5 s → 50 a partir de 3 s, cinco veces por segundo). Sobre los bloques excava
 * como un pico y una pala de netherita a la vez.
 */
public class TaladroItem extends MeleeItem implements TriggerWeapon {
    public static final double REACH = 4.5;
    public static final int STAGE_2 = 30;
    public static final int STAGE_3 = 60;
    public static final float[] DAMAGE = {3.0F, 10.0F, 50.0F};

    /** Objetivo que perfora cada jugador y desde cuándo. */
    private record Contact(int target, long since, long last) {}

    private static final Map<UUID, Contact> CONTACT = new ConcurrentHashMap<>();

    public TaladroItem(Properties properties) {
        super("taladro", "PX-07", 0xFFE119, Fx.TALADRO, properties);
    }

    public static ItemAttributeModifiers attributes() {
        return MeleeItem.attributeBuilder(3.0, 1.6, 1.0).add(Attributes.BLOCK_INTERACTION_RANGE,
                new AttributeModifier(ProyectoX.id("alcance_taladro"), 1.0, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND).build();
    }

    public static Tool tool() {
        return new Tool(List.of(
                Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_NETHERITE_TOOL),
                Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, 14.0F),
                Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_SHOVEL, 14.0F)), 1.0F, 1);
    }

    public static void forget(Player player) {
        CONTACT.remove(player.getUUID());
    }

    /** Fase de perforación (0–2) tras {@code ticks} perforando al mismo objetivo. */
    public static int stage(long ticks) {
        return ticks >= STAGE_3 ? 2 : ticks >= STAGE_2 ? 1 : 0;
    }

    @Override
    public void triggerTick(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming) {
        if (player.isSpectator()) return;
        if (heldTicks % 6 == 0) FX.sound(level, player, PXSounds.TALADRO_CARGA.get(), 0.55F, 1.4F + level.random.nextFloat() * 0.1F);
        if (heldTicks % 4 != 0) return;
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Aim.Ray ray = Aim.pierce(level, player, eye, look, REACH, 1);
        if (ray.hits().isEmpty()) return;
        Entity target = ray.hits().get(0);
        Contact c = CONTACT.get(player.getUUID());
        long since = c != null && c.target() == target.getId() && now - c.last() <= 8L ? c.since() : now;
        CONTACT.put(player.getUUID(), new Contact(target.getId(), since, now));
        int stage = stage(now - since);
        Hits.hurt(target, PXDamage.source(level, PXDamage.PERFORACION, player, player), DAMAGE[stage], player);
        Vec3 at = ray.end();
        Fx.point(level, Fx.DRILL, Fx.TALADRO, at, look, stage, player.getId());
        if (stage == 2 && heldTicks % 8 == 0) FX.sound(level, at, PXSounds.TALADRO_EMBESTIDA.get(), 0.7F, 1.5F);
    }

    @Override
    public void triggerRelease(ServerPlayer player, ItemStack stack, ServerLevel level, long now, int heldTicks, boolean aiming) {
        CONTACT.remove(player.getUUID());
    }
}
