package dev.willyelton.crystal_tools.common.levelable.block.entity.action;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class Action {
    protected int maxTickCounter;
    protected int tickCounter = 0;

    public Action(int maxTickCounter) {
        this.maxTickCounter = maxTickCounter;
    }

    /**
     * Called every server tick, will call {@link Action#tickAction(Level, BlockPos, BlockState)}
     * after the action's tick cooldown is reached
     * @param level Level reference
     * @param pos The position of the block entity
     * @param state The state of the block entity's block
     */
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (tickCounter++ >= maxTickCounter) {
            if (level != null) {
                tickAction(level, pos, state);
                tickCounter = 0;
            }
        }
    }

    /**
     * The action this will perform. Will be called every {@link Action#tickCounter} ticks.
     * Can also be called manually
     * @param level Level reference
     * @param pos The position of the block entity
     * @param state The state of the block entity's block
     */
    public abstract void tickAction(@NotNull Level level, BlockPos pos, BlockState state);
}
