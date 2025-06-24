package dev.willyelton.crystal_tools.utils;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.config.CrystalToolsServerConfig;
import dev.willyelton.crystal_tools.common.datamap.DataMaps;
import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class ToolUtils {
    public static boolean isBroken(ItemStack stack) {
        int durability = stack.getItem().getMaxDamage(stack) - stack.getDamageValue();
        return durability <= 1;
    }

    public static boolean hasSkillTree(ItemStack stack) {
        ResourceKey<Item> key = stack.getItemHolder().getKey();
        if (key == null) return false;

        if (stack.getItemHolder().getData(DataMaps.SKILL_TREES) == null) return false;

        ResourceLocation location = key.location();

        if ("minecraft".equalsIgnoreCase(location.getNamespace())) {
            return switch (CrystalToolsServerConfig.VANILLA_SKILL_TREES.get()) {
                case ALL -> true;
                case NONE -> false;
                case NETHERITE -> location.getPath().contains("netherite");
                case DIAMOND_NETHERITE -> location.getPath().contains("netherite") || location.getPath().contains("diamond");
            };
        }

        return true;
    }

    @Deprecated
    // TODO: remove from recipes. Might have to change the capability to registryaccess :(
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
        // TODO: This will remove other enchantments again. That is probably fine for now?
        stack.applyComponents(stack.getItem().components());
        SkillPoints points = stack.getOrDefault(DataComponents.SKILL_POINT_DATA, new SkillPoints());
        int skillPoints = stack.getOrDefault(DataComponents.SKILL_POINTS, 0);

        skillPoints += points.getTotalPoints();

        stack.set(DataComponents.SKILL_POINT_DATA, new SkillPoints());
        stack.set(DataComponents.SKILL_POINTS, skillPoints);
    }

    public static boolean isValidEntity(LivingEntity entity) {
        return !entity.getType().is(CrystalToolsTags.ENTITY_BLACKLIST);
    }

    public static Optional<Holder.Reference<SkillData>> getSkillData(RegistryAccess registryAccess, ResourceLocation treeLocation) {
        if (registryAccess == null) return Optional.empty();

        Optional<Registry<SkillData>> skillDataOptional = registryAccess.lookup(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS);

        return skillDataOptional.flatMap(skillData -> skillData.get(treeLocation));
    }
}
