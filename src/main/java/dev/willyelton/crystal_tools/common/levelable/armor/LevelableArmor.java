package dev.willyelton.crystal_tools.common.levelable.armor;

import dev.willyelton.crystal_tools.client.events.RegisterKeyBindingsEvent;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import dev.willyelton.crystal_tools.utils.EnchantmentUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.function.Consumer;

public class LevelableArmor extends Item implements LevelableItem {
    private static final ArmorMaterial ARMOR_MATERIAL = CrystalToolsArmorMaterials.CRYSTAL;

    public LevelableArmor(Item.Properties properties, ArmorType type) {
        super(properties.fireResistant()
                .durability(CRYSTAL.durability())
                .repairable(CrystalToolsTags.REPAIRS_CRYSTAL)
                .humanoidArmor(ARMOR_MATERIAL, type));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag flag) {
        String modeSwitchKey = RegisterKeyBindingsEvent.MODE_SWITCH.getKey().getDisplayName().getString();
        if (stack.getOrDefault(DataComponents.NIGHT_VISION, false)) {
            if (stack.getOrDefault(DataComponents.DISABLE_NIGHT_VISION, false)) {
                consumer.accept(Component.literal("\u00A7c\u00A7l" + "Night Vision Disabled (" + modeSwitchKey + ") To Enable"));
            } else {
                consumer.accept(Component.literal("\u00A79" + modeSwitchKey + " To Disable Night Vision"));
            }
        }

        if (stack.getOrDefault(DataComponents.FROST_WALKER, false)) {
            if (EnchantmentUtils.hasEnchantment(stack, Enchantments.FROST_WALKER)) {
                consumer.accept(Component.literal("\u00A79" + "Shift + " + modeSwitchKey + " To Disable Frost Walker"));
            } else {
                consumer.accept(Component.literal("\u00A7c\u00A7l" + "Frost Walker Disabled (Shift + " + modeSwitchKey + ") To Enable"));
            }
        }
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
        int durability = this.getMaxDamage(stack) - stack.getDamageValue();

        if (durability - amount <= 0) {
            return 0;
        } else {
            return amount;
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (!ToolUtils.isBroken(stack) && entity instanceof LivingEntity livingEntity && stack.getOrDefault(DataComponents.NIGHT_VISION, false)
                && livingEntity.getItemBySlot(EquipmentSlot.HEAD).equals(stack) && !stack.getOrDefault(DataComponents.DISABLE_NIGHT_VISION, false)) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false));
        }

        levelableInventoryTick(stack, level, entity, slot, 1);
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        // Just ignore data components for now
        return !newStack.is(oldStack.getItem());
    }
}
