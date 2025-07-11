package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.common.capability.Capabilities;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

public class CrystalApple extends LevelableTool {
    private static final int BASE_EAT_SPEED = 32;

    public CrystalApple(Item.Properties properties) {
        super(properties
                .durability(50)
                .food(new FoodProperties(2, 0.4F, false)));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity player) {
        if (!(player instanceof Player) || !((Player) player).getAbilities().instabuild) {
            stack.grow(1);
        }

        Levelable levelable = stack.getCapability(Capabilities.ITEM_SKILL, level.registryAccess());
        if (levelable != null) {
            FoodProperties food = stack.getOrDefault(net.minecraft.core.component.DataComponents.FOOD, new FoodProperties(0, 0, false));
            int effectiveHunger = (int) (food.nutrition() * food.saturation() * 2) + food.nutrition();
            levelable.addExp(level, player.getOnPos(), player, effectiveHunger);
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }

        return super.finishUsingItem(stack, level, player);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        levelableInventoryTick(itemStack, level, entity, slot, CrystalToolsConfig.APPLE_REPAIR_MODIFIER.get());
    }

    // TODO: This should really use the component but can't think of a way to do that right now
    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return BASE_EAT_SPEED - stack.getOrDefault(DataComponents.EAT_SPEED_BONUS, 0);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.EAT;
    }
}
