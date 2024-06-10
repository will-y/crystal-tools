package dev.willyelton.crystal_tools.common.levelable.armor;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.Util;
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

import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

public class LevelableArmor extends ArmorItem implements LevelableItem, Equipable {
    protected final String itemType;
    private static final EnumMap<Type, UUID> ARMOR_MODIFIER_UUID_PER_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
        map.put(ArmorItem.Type.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
        map.put(ArmorItem.Type.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
        map.put(ArmorItem.Type.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
        map.put(ArmorItem.Type.BODY, UUID.fromString("C1C72771-8B8E-BA4A-ACE0-81A93C8928B2"));
    });

    public LevelableArmor(String itemType, ArmorItem.Type type) {
        super(ArmorMaterials.NETHERITE, type, new Properties().fireResistant().durability(INITIAL_TIER.getUses()));
        this.itemType = itemType;
    }

    @Override
    public ItemAttributeModifiers getLevelableAttributeModifiers(ItemStack stack) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        if (!ToolUtils.isBroken(stack)) {
            UUID uuid = ARMOR_MODIFIER_UUID_PER_TYPE.get(getType());
            builder.add(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", this.getDefense(stack), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST);
            builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", this.getToughness(stack), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST);
            int health = stack.getOrDefault(DataComponents.HEALTH_BONUS, 0);
            if (health > 0) {
                builder.add(Attributes.MAX_HEALTH, new AttributeModifier(uuid, "Health modifier", health, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST);
            }

            float speedBonus = stack.getOrDefault(DataComponents.SPEED_BONUS, 0F) / 5;
            if (speedBonus > 0) {
                builder.add(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, "Speed modifier", speedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.CHEST);
            }

            return builder.build();
        } else {
            return super.getAttributeModifiers(stack);
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
        // TODO: Do I need the type thing too? Maybe
        return new ResourceLocation(CrystalTools.MODID, String.format("textures/models/armor/crystal_layer_%d.png", (slot == EquipmentSlot.LEGS ? 2 : 1)));
//        return String.format("crystal_tools:textures/models/armor/%s_layer_%d%s.png", "crystal", (slot == EquipmentSlot.LEGS ? 2 : 1), type == null ? "" : String.format("_%s", type));
    }

    @Override
    public String getItemType() {
        return this.itemType;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, List<Component> components, TooltipFlag flag) {
        ToolUtils.appendHoverText(itemStack, components, flag, this);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        int bonusDurability = stack.getOrDefault(DataComponents.DURABILITY_BONUS, 0);
        return INITIAL_TIER.getUses() + bonusDurability;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Runnable onBroken) {
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

        if (!ToolUtils.isBroken(stack) && entity instanceof LivingEntity livingEntity && stack.getOrDefault(DataComponents.NIGHT_VISION, false)) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false));
        }

        ToolUtils.inventoryTick(stack, level, entity, inventorySlot, inHand);
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
