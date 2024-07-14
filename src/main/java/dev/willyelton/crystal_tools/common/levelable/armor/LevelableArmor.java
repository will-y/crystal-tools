package dev.willyelton.crystal_tools.common.levelable.armor;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
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
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class LevelableArmor extends ArmorItem implements LevelableItem, Equipable {
    protected final String itemType;

    public LevelableArmor(String itemType, ArmorItem.Type type) {
        super(ArmorMaterials.NETHERITE, type, new Properties().fireResistant().durability(INITIAL_TIER.getUses()));
        this.itemType = itemType;
    }

    @Override
    public ItemAttributeModifiers getLevelableAttributeModifiers(ItemStack stack) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        if (!ToolUtils.isBroken(stack)) {
            ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "armor." + getItemType());
            builder.add(Attributes.ARMOR, new AttributeModifier(resourceLocation, this.getDefense(stack), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(getEquipmentSlot()));
            builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(resourceLocation, this.getToughness(stack), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(getEquipmentSlot()));
            int health = stack.getOrDefault(DataComponents.HEALTH_BONUS, 0);
            if (health > 0) {
                builder.add(Attributes.MAX_HEALTH, new AttributeModifier(resourceLocation, health, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(getEquipmentSlot()));
            }

            float speedBonus = stack.getOrDefault(DataComponents.SPEED_BONUS, 0F) / 5;
            if (speedBonus > 0) {
                builder.add(Attributes.MOVEMENT_SPEED, new AttributeModifier(resourceLocation, speedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.bySlot(getEquipmentSlot()));
            }

            return builder.build();
        } else {
            return ItemAttributeModifiers.builder().build();
        }
    }

    // Attributes
    public int getDefense(ItemStack stack) {
        return this.getDefense() + stack.getOrDefault(DataComponents.ARMOR_BONUS, 0);
    }

    public float getToughness(ItemStack stack) {
        return this.getToughness() + stack.getOrDefault(DataComponents.TOUGHNESS_BONUS, 0F);
    }

    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        return ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, String.format("textures/models/armor/crystal_layer_%d.png", (slot == EquipmentSlot.LEGS ? 2 : 1)));
    }

    @Override
    public String getItemType() {
        return this.itemType;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, List<Component> components, TooltipFlag flag) {
        appendLevelableHoverText(itemStack, components, this);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        int bonusDurability = stack.getOrDefault(DataComponents.DURABILITY_BONUS, 0);
        return INITIAL_TIER.getUses() + bonusDurability;
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
    public boolean isFoil(@NotNull ItemStack stack) {
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
                && livingEntity.getItemBySlot(EquipmentSlot.HEAD).equals(stack)) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false));
        }

        levelableInventoryTick(stack, level, entity, inventorySlot, inHand, 1);
    }

    @Override
    public boolean isValidRepairItem(ItemStack tool, ItemStack repairItem) {
        return repairItem.is(Registration.CRYSTAL.get());
    }

    @Override
    public int getEnchantmentValue() {
        return INITIAL_TIER.getEnchantmentValue();
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
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get();
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get();
    }
}
