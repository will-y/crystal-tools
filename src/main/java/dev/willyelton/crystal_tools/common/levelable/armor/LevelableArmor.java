package dev.willyelton.crystal_tools.common.levelable.armor;

import dev.willyelton.crystal_tools.client.events.RegisterKeyBindingsEvent;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.utils.EnchantmentUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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
    protected final String itemType;

    public LevelableArmor(Item.Properties properties, String itemType, ArmorType type) {
        super(properties.fireResistant().durability(CRYSTAL.durability()).humanoidArmor(ARMOR_MATERIAL, type));
        this.itemType = itemType;
    }

    @Override
    public String getItemType() {
        return this.itemType;
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

    // Changing these two to what they should be @minecraft
    @Override
    public int getBarWidth(ItemStack itemStack) {
        return Math.round(13.0F - (float) itemStack.getDamageValue() * 13.0F / (float) itemStack.getMaxDamage());
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        float f = Math.max(0.0F, ((float)itemStack.getMaxDamage() - (float)itemStack.getDamageValue()) / (float) itemStack.getMaxDamage());
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (this.isDisabled()) {
            stack.shrink(1);
            return;
        }

        if (!ToolUtils.isBroken(stack) && entity instanceof LivingEntity livingEntity && stack.getOrDefault(DataComponents.NIGHT_VISION, false)
                && livingEntity.getItemBySlot(EquipmentSlot.HEAD).equals(stack) && !stack.getOrDefault(DataComponents.DISABLE_NIGHT_VISION, false)) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false));
        }

        levelableInventoryTick(stack, level, entity, slot, 1);
    }

    @Override
    public boolean isDisabled() {
        switch (this.itemType) {
            case "helmet" -> {
                return CrystalToolsConfig.DISABLE_HELMET.get();
            }
            case "chestplate" -> {
                return CrystalToolsConfig.DISABLE_CHESTPLATE.get();
            }
            case "leggings" -> {
                return CrystalToolsConfig.DISABLE_LEGGINGS.get();
            }
            case "boots" -> {
                return CrystalToolsConfig.DISABLE_BOOTS.get();
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        // Just ignore data components for now
        return !newStack.is(oldStack.getItem());
    }
}
