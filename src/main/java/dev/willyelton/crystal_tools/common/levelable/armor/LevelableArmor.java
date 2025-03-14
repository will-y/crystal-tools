package dev.willyelton.crystal_tools.common.levelable.armor;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.events.RegisterKeyBindingsEvent;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.utils.EnchantmentUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

// TODO (PORTING): Check if we can no longer extend ArmorItem (look at uses)
public class LevelableArmor extends ArmorItem implements LevelableItem {
    public static final ResourceLocation ARMOR_ID = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "armor");
    public static final ResourceLocation ARMOR_TOUGHNESS_ID = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "armor_toughness");
    public static final ResourceLocation MAX_HEALTH_ID = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "max_health");
    public static final ResourceLocation MOVEMENT_SPEED_ID = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "movement_speed");

    private static final ArmorMaterial ARMOR_MATERIAL = CrystalToolsArmorMaterials.CRYSTAL;
    protected final String itemType;

    public LevelableArmor(Item.Properties properties, String itemType, ArmorType type) {
        super(ARMOR_MATERIAL, type, ARMOR_MATERIAL.humanoidProperties(properties.fireResistant().durability(INITIAL_TIER.durability()), type));
        this.itemType = itemType;
    }

    @Override
    public ItemAttributeModifiers getLevelableAttributeModifiers(ItemStack stack) {
        // TODO (PORTING): This might be wrong now: getEquipmentSlot
        // Most of this probably go away with my skill tree refactors
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        if (!ToolUtils.isBroken(stack)) {
            builder.add(Attributes.ARMOR, new AttributeModifier(ARMOR_ID, this.getDefense(stack), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(getEquipmentSlot(stack)));
            builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ARMOR_TOUGHNESS_ID, this.getToughness(stack), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(getEquipmentSlot(stack)));
            int health = stack.getOrDefault(DataComponents.HEALTH_BONUS, 0);
            if (health > 0) {
                builder.add(Attributes.MAX_HEALTH, new AttributeModifier(MAX_HEALTH_ID, health, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(getEquipmentSlot(stack)));
            }

            float speedBonus = stack.getOrDefault(DataComponents.SPEED_BONUS, 0F) / 5;
            if (speedBonus > 0) {
                builder.add(Attributes.MOVEMENT_SPEED, new AttributeModifier(MOVEMENT_SPEED_ID, speedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.bySlot(getEquipmentSlot(stack)));
            }

            return builder.build();
        } else {
            return ItemAttributeModifiers.builder().build();
        }
    }

    // Attributes
    public int getDefense(ItemStack stack) {
        return stack.getOrDefault(DataComponents.ARMOR_BONUS, 0);
    }

    public float getToughness(ItemStack stack) {
        return stack.getOrDefault(DataComponents.TOUGHNESS_BONUS, 0F);
    }

    @Override
    public String getItemType() {
        return this.itemType;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components, TooltipFlag flag) {
        String modeSwitchKey = RegisterKeyBindingsEvent.MODE_SWITCH.getKey().getDisplayName().getString();
        if (stack.getOrDefault(DataComponents.NIGHT_VISION, false)) {
            if (stack.getOrDefault(DataComponents.DISABLE_NIGHT_VISION, false)) {
                components.add(Component.literal("\u00A7c\u00A7l" + "Night Vision Disabled (" + modeSwitchKey + ") To Enable"));
            } else {
                components.add(Component.literal("\u00A79" + modeSwitchKey + " To Disable Night Vision"));
            }
        }

        if (stack.getOrDefault(DataComponents.FROST_WALKER, false)) {
            if (EnchantmentUtils.hasEnchantment(stack, Enchantments.FROST_WALKER)) {
                components.add(Component.literal("\u00A79" + "Shift + " + modeSwitchKey + " To Disable Frost Walker"));
            } else {
                components.add(Component.literal("\u00A7c\u00A7l" + "Frost Walker Disabled (Shift + " + modeSwitchKey + ") To Enable"));
            }
        }

        appendLevelableHoverText(stack, components, this, flag);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        int bonusDurability = stack.getOrDefault(DataComponents.DURABILITY_BONUS, 0);
        return INITIAL_TIER.durability() + bonusDurability;
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
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int inventorySlot, boolean inHand) {
        if (this.isDisabled()) {
            stack.shrink(1);
            return;
        }

        if (!ToolUtils.isBroken(stack) && entity instanceof LivingEntity livingEntity && stack.getOrDefault(DataComponents.NIGHT_VISION, false)
                && livingEntity.getItemBySlot(EquipmentSlot.HEAD).equals(stack) && !stack.getOrDefault(DataComponents.DISABLE_NIGHT_VISION, false)) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false));
        }

        levelableInventoryTick(stack, level, entity, inventorySlot, inHand, 1);
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
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get();
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        // Just ignore data components for now
        return !newStack.is(oldStack.getItem());
    }
}
