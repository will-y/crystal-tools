package dev.willyelton.crystal_tools.common.levelable.armor;

import dev.willyelton.crystal_tools.client.events.RegisterKeyBindingsEvent;
import dev.willyelton.crystal_tools.common.capability.Capabilities;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.common.levelable.tool.LevelableTool;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.Equippable;

import java.util.function.Consumer;

public class CrystalElytra extends LevelableTool implements LevelableItem {
    public CrystalElytra(Properties properties) {
        super(properties
                .fireResistant()
                .durability(CRYSTAL.durability())
                .component(net.minecraft.core.component.DataComponents.GLIDER, Unit.INSTANCE)
                .component(net.minecraft.core.component.DataComponents.EQUIPPABLE,
                        Equippable.builder(EquipmentSlot.CHEST)
                                .setEquipSound(SoundEvents.ARMOR_EQUIP_ELYTRA)
                                .setAsset(CrystalToolsArmorMaterials.CRYSTAL_ELYTRA)
                                .build())
                .repairable(CrystalToolsTags.REPAIRS_CRYSTAL));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag flag) {
        String modeSwitchKey = RegisterKeyBindingsEvent.MODE_SWITCH.getKey().getDisplayName().getString();

        if (stack.getOrDefault(DataComponents.CREATIVE_FLIGHT, false)) {
            if (stack.getOrDefault(DataComponents.DISABLE_CREATIVE_FLIGHT, false)) {
                components.accept(Component.literal("\u00A7c\u00A7l" + "Creative Flight Disabled (Ctrl + " + modeSwitchKey + ") To Enable"));
            } else {
                components.accept(Component.literal("\u00A79" + "Ctrl + " + modeSwitchKey + " To Disable Creative Flight"));
            }
        }
    }

    @Override
    public  EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.CHEST;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);

        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.isFallFlying()) {
                if (livingEntity.getFallFlyingTicks() % 20 == 0) {
                    Levelable levelable = stack.getCapability(Capabilities.ITEM_SKILL, level.registryAccess());
                    if (levelable != null) {
                        levelable.addExp(entity.level(), entity.getOnPos(), livingEntity);
                    }
                }
            }
        }
    }
}
