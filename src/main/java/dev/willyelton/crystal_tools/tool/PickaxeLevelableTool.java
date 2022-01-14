package dev.willyelton.crystal_tools.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PickaxeLevelableTool extends LevelableTool {
    public PickaxeLevelableTool(Properties properties, String toolType) {
        super(properties, BlockTags.MINEABLE_WITH_PICKAXE, toolType);
    }

    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos position = context.getClickedPos();
        Direction direction = context.getClickedFace();
        position = position.relative(direction);

        if (direction.equals(Direction.UP)) {
            BlockState blockState = Blocks.TORCH.defaultBlockState();
            level.setBlock(position, blockState, 0);
        } else if (!direction.equals(Direction.DOWN)) {
            BlockState torchBlockState = Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, direction);

            level.setBlock(position, torchBlockState, 0);
        }



        return InteractionResult.FAIL;
    }
}
