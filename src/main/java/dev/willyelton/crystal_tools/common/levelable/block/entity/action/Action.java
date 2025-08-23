package dev.willyelton.crystal_tools.common.levelable.block.entity.action;

import dev.willyelton.crystal_tools.common.levelable.block.entity.ActionBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalPedestalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Action {
    protected ActionBlockEntity blockEntity;
    protected ActionParameters params;
    protected int tickCounter = 0;

    public Action(ActionBlockEntity blockEntity, @Nullable ActionParameters params) {
        this.blockEntity = blockEntity;
        this.params = params == null ? getDefaultParameters() : params;
    }

    /**
     * Called every server tick, will call {@link Action#tickAction(Level, BlockPos, BlockState)}
     * after the action's tick cooldown is reached
     * @param level Level reference
     * @param pos The position of the block entity
     * @param state The state of the block entity's block
     */
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!this.isActive() || params.maxTickCounter() == -1) return;

        if (++tickCounter >= params.maxTickCounter()) {
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
    public void tickAction(@NotNull Level level, BlockPos pos, BlockState state) {}

    public abstract ActionType getActionType();

    public abstract ActionParameters getDefaultParameters();

    public void load(CompoundTag tag, HolderLookup.Provider registries) {}

    public void save(CompoundTag tag, HolderLookup.Provider registries) {}

    public void onRemove() {}

    public void applyComponents(BlockEntity.DataComponentInput componentInput) {}

    public void collectComponents(DataComponentMap.Builder components) {}

    public boolean addToExtra(String key, float value) {
        return false;
    }

    public void resetExtra() {}

    public boolean isActive() {
        return false;
    }

    protected ItemStack getItem() {
        if (blockEntity instanceof CrystalPedestalBlockEntity pedestalBlockEntity) {
            return pedestalBlockEntity.getStack() == null ? ItemStack.EMPTY : pedestalBlockEntity.getStack();
        }

        return ItemStack.EMPTY;
    }
}
