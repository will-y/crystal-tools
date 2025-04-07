package dev.willyelton.crystal_tools.common.levelable.armor;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.events.RegisterKeyBindingsEvent;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.config.CrystalToolsServerConfig;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.function.Consumer;

// TODO (PORTING): Can make this extend levelable item now
public class CrystalElytra extends Item implements LevelableItem {
    private static final ResourceLocation CREATIVE_FLIGHT_ID = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "creative_flight");

    public CrystalElytra(Properties properties) {
        super(properties
                .fireResistant()
                .component(net.minecraft.core.component.DataComponents.GLIDER, Unit.INSTANCE)
                .component(net.minecraft.core.component.DataComponents.EQUIPPABLE,
                        Equippable.builder(EquipmentSlot.CHEST)
                                .setEquipSound(SoundEvents.ARMOR_EQUIP_ELYTRA)
                                .setAsset(CrystalToolsArmorMaterials.CRYSTAL_ELYTRA)
                                .build())
                .repairable(CrystalToolsTags.REPAIRS_CRYSTAL));
    }

    @Override
    public String getItemType() {
        return "crystal_elytra";
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        int bonusDurability = stack.getOrDefault(DataComponents.DURABILITY_BONUS, 0);
        return CRYSTAL.durability() + bonusDurability;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag flag) {
        String modeSwitchKey = RegisterKeyBindingsEvent.MODE_SWITCH.getKey().getDisplayName().getString();

        if (stack.getOrDefault(DataComponents.CREATIVE_FLIGHT, 0) >= CrystalToolsServerConfig.CREATIVE_FLIGHT_POINTS.get()) {
            if (stack.getOrDefault(DataComponents.DISABLE_CREATIVE_FLIGHT, false)) {
                components.accept(Component.literal("\u00A7c\u00A7l" + "Creative Flight Disabled (Ctrl + " + modeSwitchKey + ") To Enable"));
            } else {
                components.accept(Component.literal("\u00A79" + "Ctrl + " + modeSwitchKey + " To Disable Creative Flight"));
            }
        }

        appendLevelableHoverText(stack, components, this, flag);
    }

    @Override
    public  EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.CHEST;
    }

    // TODO (PORTING): This is going to need to be different, going to have to hot-swap the components
//    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
//        return !canUseCreativeFlight(stack) && ElytraItem.isFlyEnabled(stack);
//    }

    // TODO (PORTING): This no longer exists. Will probably have to be a check in normal inventory tick
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

    public static boolean canUseCreativeFlight(ItemStack stack) {
        return !stack.getOrDefault(DataComponents.DISABLE_CREATIVE_FLIGHT, false) &&
                stack.getOrDefault(DataComponents.CREATIVE_FLIGHT, 0) >= CrystalToolsServerConfig.CREATIVE_FLIGHT_POINTS.get();
    }

    public int getDefense(ItemStack stack) {
        return CrystalToolsArmorMaterials.CRYSTAL.defense().get(ArmorType.BODY) + stack.getOrDefault(DataComponents.ARMOR_BONUS, 0);
    }

    public float getToughness(ItemStack stack) {
        return CrystalToolsArmorMaterials.CRYSTAL.toughness() + stack.getOrDefault(DataComponents.TOUGHNESS_BONUS, 0F);
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
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (this.isDisabled()) {
            stack.shrink(1);
        }

        levelableInventoryTick(stack, level, entity, slot, 1);
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_ELYTRA.get();
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        // Just ignore data components for now
        return !newStack.is(oldStack.getItem());
    }
}
