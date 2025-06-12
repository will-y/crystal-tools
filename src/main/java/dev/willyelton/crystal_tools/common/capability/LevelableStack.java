package dev.willyelton.crystal_tools.common.capability;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.datamap.DataMaps;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Class to do all the actions of a levelable item.
 * This is kindof like a capability. I would rather do it that
 * way but idk if I can do data driven capabilities
 * Level for the context? Idk if there is caching for stacks or if that is a problem
 * This should work ^ can also be different caps for special things (shield maybe ...)
 */
// TODO: Is the datamap a good idea? I think yes
// Would allow fast check if there is a skill tree for tooltips
    // ToolUtils.getLevelableData?
    // Would fail fast with the datamap, then check datacomponent, then as a last resort, check the registry?
    // Level is required then? Should be generally fine
// Otherwise idk how im going to show tooltips
// Crafting event or key press event to put the datacomponent in
// More thoughts: The only expensive place to call is the tooltip
// Why not use a custom tooltip datacomponent?

// Design options
// Data storage:
// 1. Use everything. Datacomponent on stack, datamap for mapping trees,
    // Only registry lookup when you have to
    // + Fast
    // + Small datapacks
    // - Can't change skill trees after getting an item
    // No reason to have it on a datacomponent. Might as well cache the registry lookup
// 2. Just always do a registry lookup
    // + Easy
    // - Slow
// 3. Registry lookup + datamap
    // + Pretty easy
    // - Slowish
// 4. Cache the registry lookup (could apply to all of these, would work better with the datamap probably) ... is registry lookup just a map lookup under the hood?
    // + Fast
    // - Probably useless
// 5. Tooltip datacomponent (only if I don't do the whole tree on a datacomponent)
    // + Required if I don't do 1

// Data access:
// 1. Pseudo capabilities (ToolUtils#getLevelable ...) does something to get the object that actually does the work
    // + Don't have to register cap for all items
    // Could model off of capability with that factory supplier
    // Is this not what I had before?
// 2. Use capabilities <-
    // + Mod Compat
    // + Can register different objects for different items
// 3. ToolUtils everything
public class LevelableStack implements Levelable {

    public static @Nullable LevelableStack of(ItemStack stack, Level level) {
        if (stack.getMaxStackSize() != 1) return null;

        ResourceLocation treeLocation = stack.getItemHolder().getData(DataMaps.SKILL_TREES);

        if (treeLocation == null) return null;

        Optional<Holder.Reference<SkillData>> skillData = ToolUtils.getSkillData(level, treeLocation);

        return skillData.map(skillDataReference -> new LevelableStack(stack, skillDataReference.value(), skillDataReference.key())).orElse(null);
    }

    private final ItemStack stack;
    private final SkillData skillData;
    private final SkillPoints points;
    private final ResourceKey<SkillData> key;

    private LevelableStack(ItemStack stack, SkillData skillData, ResourceKey<SkillData> key) {
        this.stack = stack;
        this.skillData = skillData;
        this.points = stack.getOrDefault(DataComponents.SKILL_POINT_DATA, new SkillPoints());
        this.key = key;
    }

    @Override
    public void addExp(Level level, BlockPos blockPos, LivingEntity livingEntity, int amount) {
        int newExperience = DataComponents.addToComponent(stack, DataComponents.SKILL_EXPERIENCE, amount);
        int experienceCap = getExperienceCap();

        if (newExperience >= experienceCap) {
            // level up
            DataComponents.addToComponent(stack, DataComponents.SKILL_POINTS, 1);
            // play level up sound
            level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 0.8F, 1.0F);
            if (livingEntity instanceof Player player) {
                player.displayClientMessage(Component.literal("\u00A7b" + stack.getItem().getName(stack).getString() + " Leveled Up (" + getSkillPoints() + " Unspent Points)"), true);
            }
            stack.set(DataComponents.SKILL_EXPERIENCE, Math.max(0, newExperience - experienceCap));
            increaseExpCap();
        }
    }

    @Override
    public int getSkillPoints() {
        return stack.getOrDefault(DataComponents.SKILL_POINTS, 0);
    }

    @Override
    public int getExperienceCap() {
        return stack.getOrDefault(DataComponents.EXPERIENCE_CAP, CrystalToolsConfig.BASE_EXPERIENCE_CAP.get());
    }

    @Override
    public SkillData getSkillTree() {
        return skillData;
    }

    @Override
    public SkillPoints getPointData() {
        return points;
    }

    @Override
    public ResourceKey<SkillData> getKey() {
        return key;
    }

    @Override
    public void increaseExpCap(int levelIncrease) {
        int experienceCap = getExperienceCap();
        int newCap = ToolUtils.getNewCap(experienceCap, levelIncrease);
        stack.set(DataComponents.EXPERIENCE_CAP, newCap);
    }
}
