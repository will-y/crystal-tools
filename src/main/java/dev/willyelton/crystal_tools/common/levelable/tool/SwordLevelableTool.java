package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;

public class SwordLevelableTool extends LevelableTool {
    public SwordLevelableTool(Item.Properties properties) {
        this(properties, "sword", 3, -2.4f);
    }

    public SwordLevelableTool(Item.Properties properties, String itemType, float attackDamageModifier, float attackSpeedModifier) {
        super(properties, null, itemType, attackDamageModifier, attackSpeedModifier);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(Blocks.COBWEB) && !ToolUtils.isBroken(stack)) {
            return 15.0F;
        } else {
            return state.is(BlockTags.SWORD_EFFICIENT) ? 1.5F : 1.0F;
        }
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.is(Blocks.COBWEB);
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_SWORD.get();
    }

    @Override
    protected double getAttackExperienceBoost() {
        return CrystalToolsConfig.SWORD_EXPERIENCE_BOOST.get();
    }
}
