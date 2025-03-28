package dev.willyelton.crystal_tools.common.levelable.block.entity.action;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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
        if (!this.isActive()) return;

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

    public abstract ActionType getActionType();

    public void load(CompoundTag tag, HolderLookup.Provider registries) {}

    public void save(CompoundTag tag, HolderLookup.Provider registries) {}

    public void onRemove() {}

    public void applyComponents(DataComponentGetter componentInput) {}

    public void collectComponents(DataComponentMap.Builder components) {}

    public boolean addToExtra(String key, float value) {
        return false;
    }

    public void resetExtra() {}

    public boolean isActive() {
        return false;
    }

    public enum ActionType {
        AUTO_OUTPUT, CHUNK_LOAD
    }
}
