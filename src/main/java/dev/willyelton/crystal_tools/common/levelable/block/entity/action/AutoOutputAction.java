package dev.willyelton.crystal_tools.common.levelable.block.entity.action;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.block.entity.ActionBlockEntity;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.AUTO_OUTPUT;

public class AutoOutputAction extends Action {
    private static final Direction[] POSSIBLE_INVENTORIES = new Direction[] {Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    private final AutoOutputable autoOutputable;
    private boolean autoOutputEnabled = false;
    // This is different because it isn't persisted, just temporarily disabled
    private boolean disabled = false;

    public AutoOutputAction(ActionBlockEntity blockEntity, ActionParameters params, AutoOutputable autoOutputEnabled) {
        // 100
        super(blockEntity, params);
        this.autoOutputable = autoOutputEnabled;
    }

    public AutoOutputAction(ActionBlockEntity blockEntity, AutoOutputable autoOutputEnabled) {
        this(blockEntity, null, autoOutputEnabled);
    }

    @Override
    public void tickAction(Level level, BlockPos pos, BlockState state) {
        if (this.disabled || !autoOutputEnabled) return;

        for (Integer index : autoOutputable.getOutputStacks().keySet()) {
            ItemStack stack = autoOutputable.getOutputStacks().get(index);

            for (Direction dir : POSSIBLE_INVENTORIES) {
                IItemHandler itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos.relative(dir), dir.getOpposite());
                BlockState invState = level.getBlockState(pos.relative(dir));

                if (itemHandler != null && !invState.is(CrystalToolsTags.AUTO_OUTPUT_BLACKLIST)) {
                    for (int i = 0; i < itemHandler.getSlots(); i++) {
                        stack = itemHandler.insertItem(i, stack, false);

                        if (stack.isEmpty()) break;
                    }
                }
                if (stack.isEmpty()) break;
            }

            autoOutputable.setItem(index, stack);
        }
    }

    @Override
    public void load(ValueInput valueInput) {
        this.autoOutputEnabled = valueInput.getBooleanOr("AutoOutput", false);
    }

    @Override
    public void save(ValueOutput valueOutput) {
        valueOutput.putBoolean("AutoOutput", this.autoOutputEnabled);
    }

    @Override
    public boolean addToExtra(String key, float value) {
        if (AUTO_OUTPUT.toString().equals(key)) {
            this.autoOutputEnabled = true;
            return true;
        }
        return false;
    }

    @Override
    public void resetExtra() {
        this.autoOutputEnabled = false;
    }

    @Override
    public boolean isActive() {
        return autoOutputEnabled;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.AUTO_OUTPUT;
    }

    @Override
    public ActionParameters getDefaultParameters() {
        return new ActionParameters(100);
    }

    @Override
    public void applyComponents(DataComponentGetter componentInput) {
        super.applyComponents(componentInput);
        this.autoOutputEnabled = componentInput.getOrDefault(DataComponents.AUTO_OUTPUT, false);
    }

    @Override
    public void collectComponents(DataComponentMap.Builder components) {
        super.collectComponents(components);
        components.set(DataComponents.AUTO_OUTPUT, this.autoOutputEnabled);
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
