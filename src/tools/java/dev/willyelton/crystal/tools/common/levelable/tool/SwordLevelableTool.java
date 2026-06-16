package dev.willyelton.crystal.tools.common.levelable.tool;

import dev.willyelton.crystal.core.utils.ToolUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SwordLevelableTool extends LevelableTool {
    public SwordLevelableTool(Properties properties) {
        super(properties.sword(CRYSTAL, 3.0F, -2.4F));
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
}
