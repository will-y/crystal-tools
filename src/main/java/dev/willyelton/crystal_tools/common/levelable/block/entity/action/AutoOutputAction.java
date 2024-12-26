package dev.willyelton.crystal_tools.common.levelable.block.entity.action;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class AutoOutputAction extends Action {
    private static final Direction[] POSSIBLE_INVENTORIES = new Direction[] {Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    private final AutoOutputable blockEntity;

    public AutoOutputAction(AutoOutputable blockEntity) {
        super(100);
        this.blockEntity = blockEntity;
    }

    @Override
    public void tickAction(Level level, BlockPos pos, BlockState state) {
        if (blockEntity.autoOutputEnabled()) {
            for (Integer index : blockEntity.getOutputStacks().keySet()) {
                ItemStack stack = blockEntity.getOutputStacks().get(index);

                for (Direction dir : POSSIBLE_INVENTORIES) {
                    IItemHandler itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos.relative(dir), dir.getOpposite());

                    if (itemHandler != null) {
                        for (int i = 0; i < itemHandler.getSlots(); i++) {
                            stack = itemHandler.insertItem(i, stack, false);

                            if (stack.isEmpty()) break;
                        }
                    }
                    if (stack.isEmpty()) break;
                }

                blockEntity.setItem(index, stack);
            }
        }
    }
}
