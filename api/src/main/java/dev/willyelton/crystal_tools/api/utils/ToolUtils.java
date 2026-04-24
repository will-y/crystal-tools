package dev.willyelton.crystal_tools.api.utils;

import dev.willyelton.crystal_tools.api.common.config.CrystalToolsCoreConfig;
import dev.willyelton.crystal_tools.api.common.datacomponent.DataComponents;
import dev.willyelton.crystal_tools.api.common.datamap.CrystalCoreDataMaps;
import dev.willyelton.crystal_tools.api.common.event.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.api.common.event.intermod.registries.SkillTreeBlackListRegistry;
import dev.willyelton.crystal_tools.api.common.skill.SkillData;
import dev.willyelton.crystal_tools.api.common.skill.SkillPoints;
import dev.willyelton.crystal_tools.api.common.tag.CrystalCoreTags;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ToolUtils {
    public static boolean isBroken(ItemStack stack) {
        int durability = stack.getItem().getMaxDamage(stack) - stack.getDamageValue();
        return durability <= 1;
    }

    public static boolean willBreak(ItemStack stack, int durabilityToUse) {
        return stack.getMaxDamage() - stack.getDamageValue() <= durabilityToUse;
    }

    public static boolean hasSkillTree(ItemStack stack) {
        ResourceKey<Item> key = stack.typeHolder().getKey();
        if (key == null) return false;

        if (stack.typeHolder().getData(CrystalCoreDataMaps.SKILL_TREES) == null) return false;

        return !SkillTreeBlackListRegistry.skillTreeBlackListed(stack);
    }

    public static int getNewCap(int currentCap, int levelIncrease) {
        return (int) Math.min((float) (currentCap * Math.pow(CrystalToolsCoreConfig.EXPERIENCE_MULTIPLIER.get(), levelIncrease)), CrystalToolsCoreConfig.MAX_EXP.get());
    }

    public static void resetPoints(ItemStack stack, Player player) {
        // Drop Items
        if (player instanceof ServerPlayer) {
            List<ItemContainerContents> contents = stack.get(DataComponents.ITEM_INVENTORY);
            if (contents != null) {
                Level level = player.level();
                for (ItemContainerContents item : contents) {
                    item.allItemsCopyStream().forEach(toDrop -> level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), toDrop)));
                }
            }
        }

        // TODO: This will remove other enchantments again. That is probably fine for now?
        ItemStackUtils.removeAllComponents(stack);
        SkillPoints points = stack.getOrDefault(DataComponents.SKILL_POINT_DATA, new SkillPoints());
        int skillPoints = stack.getOrDefault(DataComponents.SKILL_POINTS, 0);

        skillPoints += points.getTotalPoints();

        stack.set(DataComponents.SKILL_POINT_DATA, new SkillPoints());
        stack.set(DataComponents.SKILL_POINTS, skillPoints);
    }

    public static boolean isValidEntity(LivingEntity entity) {
        return !entity.is(CrystalCoreTags.ENTITY_BLACKLIST);
    }

    public static Optional<Holder.Reference<SkillData>> getItemSkillData(RegistryAccess registryAccess, Identifier treeLocation) {
        return getSkillData(registryAccess, treeLocation, DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS);
    }

    public static Optional<Holder.Reference<SkillData>> getEntitySkillData(RegistryAccess registryAccess, Identifier treeLocation) {
        return getSkillData(registryAccess, treeLocation, DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ENTITIES);
    }

    public static Optional<Holder.Reference<SkillData>> getSkillData(RegistryAccess registryAccess, Identifier treeLocation, ResourceKey<Registry<SkillData>> registryKey) {
        if (registryAccess == null) return Optional.empty();

        Optional<Registry<SkillData>> skillDataOptional = registryAccess.lookup(registryKey);

        return skillDataOptional.flatMap(skillData -> skillData.get(treeLocation));
    }

    public static List<ItemStack> getFilterItems(ItemStack stack) {
        ItemContainerContents filterContents = stack.get(DataComponents.FILTER_INVENTORY);

        if (filterContents == null) {
            return Collections.emptyList();
        }

        return filterContents.allItemsCopyStream().filter(stack1 -> !stack1.isEmpty()).toList();
    }

    public static boolean matchesFilter(ItemStack stack, ItemStack toMatch, List<ItemStack> filter) {
        // TODO: matching modes (respect nbt)
        // If there are no filters, default to pickup
        if (stack.getOrDefault(DataComponents.FILTER_CAPACITY, 0) == 0) {
            return true;
        }
        boolean whiteList = stack.getOrDefault(DataComponents.WHITELIST, true);
        for (ItemStack filterStack : filter) {
            if (filterStack.is(toMatch.getItem())) {
                return whiteList;
            }
        }

        return !whiteList;
    }
}
