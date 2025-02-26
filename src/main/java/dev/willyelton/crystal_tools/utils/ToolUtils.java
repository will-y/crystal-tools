package dev.willyelton.crystal_tools.utils;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillTreeRegistry;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ToolUtils {
    public static boolean isBroken(ItemStack stack) {
        int durability = stack.getItem().getMaxDamage(stack) - stack.getDamageValue();
        return durability <= 1;
    }

    public static void increaseExpCap(ItemStack stack) {
        increaseExpCap(stack, 1);
    }

    public static void increaseExpCap(ItemStack stack, int levelIncrease) {
        if (stack.getItem() instanceof LevelableItem item) {
            int experienceCap = item.getExperienceCap(stack);
            int newCap = getNewCap(experienceCap, levelIncrease);
            stack.set(DataComponents.EXPERIENCE_CAP, newCap);
        }
    }

    public static int getNewCap(int currentCap, int levelIncrease) {
        return (int) Math.min((float) (currentCap * Math.pow(CrystalToolsConfig.EXPERIENCE_MULTIPLIER.get(), levelIncrease)), CrystalToolsConfig.MAX_EXP.get());
    }

    public static void resetPoints(ItemStack stack) {
        List<Integer> points = stack.getOrDefault(DataComponents.POINTS_ARRAY, Collections.emptyList());
        int skillPoints = stack.getOrDefault(DataComponents.SKILL_POINTS, 0);

        List<ResourceLocation> resourceLocations = new ArrayList<>(DataComponents.FLOAT_COMPONENTS.values());
        resourceLocations.addAll(DataComponents.INT_COMPONENTS.values());
        resourceLocations.addAll(DataComponents.BOOLEAN_COMPONENTS.values());
        for (ResourceLocation resourceLocation : resourceLocations) {
            DataComponentType<?> dataComponent = BuiltInRegistries.DATA_COMPONENT_TYPE.get(resourceLocation);
            if (dataComponent == null) {
                continue;
            }
            stack.remove(dataComponent);
        }

        skillPoints += points.stream().reduce(0, Integer::sum);

        stack.set(DataComponents.POINTS_ARRAY, Collections.emptyList());
        stack.set(DataComponents.SKILL_POINTS, skillPoints);
        stack.remove(DataComponents.EFFECTS);
    }

    public static SkillData getSkillData(ItemStack stack) {
        List<Integer> points = stack.getOrDefault(DataComponents.POINTS_ARRAY, Collections.emptyList());
        if (stack.getItem() instanceof LevelableItem) {
            String toolType = ((LevelableItem) stack.getItem()).getItemType();
            SkillData data = SkillTreeRegistry.SKILL_TREES.get(toolType);
            data.applyPoints(points);
            return data;
        } else {
            return null;
        }
    }

    public static boolean isValidEntity(LivingEntity entity) {
        return !entity.getType().is(CrystalToolsTags.ENTITY_BLACKLIST);
    }
}
