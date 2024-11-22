package dev.willyelton.crystal_tools.common.levelable.armor;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.client.events.RegisterKeyBindingsEvent;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.config.CrystalToolsServerConfig;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.List;
import java.util.function.Consumer;

public class CrystalElytra extends ElytraItem implements LevelableItem {
    private static final ResourceLocation CREATIVE_FLIGHT_ID = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "creative_flight");

    public CrystalElytra(Properties pProperties) {
        super(pProperties.fireResistant());
    }

    @Override
    public String getItemType() {
        return "crystal_elytra";
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        int bonusDurability = stack.getOrDefault(DataComponents.DURABILITY_BONUS, 0);
        return INITIAL_TIER.getUses() + bonusDurability;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components, TooltipFlag flag) {
        String modeSwitchKey = RegisterKeyBindingsEvent.MODE_SWITCH.getKey().getDisplayName().getString();

        if (stack.getOrDefault(DataComponents.CREATIVE_FLIGHT, 0) >= CrystalToolsServerConfig.CREATIVE_FLIGHT_POINTS.get()) {
            if (stack.getOrDefault(DataComponents.DISABLE_CREATIVE_FLIGHT, false)) {
                components.add(Component.literal("\u00A7c\u00A7l" + "Creative Flight Disabled (Ctrl + " + modeSwitchKey + ") To Enable"));
            } else {
                components.add(Component.literal("\u00A79" + "Ctrl + " + modeSwitchKey + " To Disable Creative Flight"));
            }
        }

        appendLevelableHoverText(stack, components, this, flag);
    }

    @Override
    public boolean isValidRepairItem(ItemStack tool, ItemStack repairItem) {
        return repairItem.is(Registration.CRYSTAL.get());
    }

    @Override
    public  EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.CHEST;
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return !canUseCreativeFlight(stack) && ElytraItem.isFlyEnabled(stack);
    }

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        if (!entity.level().isClientSide) {
            int nextFlightTick = flightTicks + 1;
            if (nextFlightTick % 10 == 0) {
                if (nextFlightTick % 20 == 0) {
                    float unbreakingLevel = stack.getOrDefault(DataComponents.DURABILITY_BONUS, 0) / 200F + 1;
                    if (1 / unbreakingLevel > Math.random()) {
                        stack.hurtAndBreak(1, entity, EquipmentSlot.CHEST);
                    }

                    this.addExp(stack, entity.level(), entity.getOnPos(), entity);
                }
                entity.gameEvent(GameEvent.ELYTRA_GLIDE);
            }
        }
        return true;
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


    // Armor things
    @Override
    public ItemAttributeModifiers getLevelableAttributeModifiers(ItemStack stack) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        if (!ToolUtils.isBroken(stack)) {
            builder.add(Attributes.ARMOR, new AttributeModifier(LevelableArmor.ARMOR_ID, this.getDefense(stack), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST);
            builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(LevelableArmor.ARMOR_TOUGHNESS_ID, this.getToughness(stack), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST);
            int health = stack.getOrDefault(DataComponents.HEALTH_BONUS, 0);
            if (health > 0) {
                builder.add(Attributes.MAX_HEALTH, new AttributeModifier(LevelableArmor.MAX_HEALTH_ID, health, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST);
            }

            float speedBonus = stack.getOrDefault(DataComponents.SPEED_BONUS, 0F) / 5;
            if (speedBonus > 0) {
                builder.add(Attributes.MOVEMENT_SPEED, new AttributeModifier(LevelableArmor.MOVEMENT_SPEED_ID, speedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.CHEST);
            }

            if (canUseCreativeFlight(stack)) {
                builder.add(NeoForgeMod.CREATIVE_FLIGHT, new AttributeModifier(CREATIVE_FLIGHT_ID, 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST);

            }

            return builder.build();
        } else {
            return ItemAttributeModifiers.builder().build();
        }
    }

    public static boolean canUseCreativeFlight(ItemStack stack) {
        return !stack.getOrDefault(DataComponents.DISABLE_CREATIVE_FLIGHT, false) &&
                stack.getOrDefault(DataComponents.CREATIVE_FLIGHT, 0) >= CrystalToolsServerConfig.CREATIVE_FLIGHT_POINTS.get();
    }

    public int getDefense(ItemStack stack) {
        return ArmorMaterials.NETHERITE.value().getDefense(ArmorItem.Type.CHESTPLATE) + stack.getOrDefault(DataComponents.ARMOR_BONUS, 0);
    }

    public float getToughness(ItemStack stack) {
        return ArmorMaterials.NETHERITE.value().toughness() + stack.getOrDefault(DataComponents.TOUGHNESS_BONUS, 0F);
    }

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
        }

        levelableInventoryTick(stack, level, entity, inventorySlot, inHand, 1);
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_ELYTRA.get();
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get();
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }
}
