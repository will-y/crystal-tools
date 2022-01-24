package dev.willyelton.crystal_tools.tool;

import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PickaxeLevelableTool extends LevelableTool {
    public PickaxeLevelableTool() {
        super(new Item.Properties(), BlockTags.MINEABLE_WITH_PICKAXE, "pickaxe");
    }

    public InteractionResult useOn(UseOnContext context) {
        ItemStack tool = context.getItemInHand();

        if (NBTUtils.getFloatOrAddKey(tool, "torch") > 0) {
            Level level = context.getLevel();
            BlockPos position = context.getClickedPos();
            BlockState state = level.getBlockState(position);
            Direction direction = context.getClickedFace();
            position = position.relative(direction);

            BlockState torchBlockState;

            if (direction.equals(Direction.UP)) {
                torchBlockState = Blocks.TORCH.defaultBlockState();
            } else if (direction.equals(Direction.DOWN)) {
                return InteractionResult.PASS;
            } else {
                torchBlockState = Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, direction);
            }

            if (level.getBlockState(position).is(Blocks.AIR) && state.isFaceSturdy(level, position, direction)) {
                level.setBlock(position, torchBlockState, 0);
            } else {
                return InteractionResult.FAIL;
            }

            if (!level.isClientSide && context.getPlayer() != null) {
                tool.hurtAndBreak(10, context.getPlayer(), (player) -> {
                    player.broadcastBreakEvent(EquipmentSlot.MAINHAND);
                });
            }
        }

        return InteractionResult.FAIL;
    }
}
