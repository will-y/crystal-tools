package dev.willyelton.crystal_tools.api.common.block.entity.action;

import dev.willyelton.crystal_tools.api.Registration;
import dev.willyelton.crystal_tools.api.common.block.entity.ActionBlockEntity;
import dev.willyelton.crystal_tools.api.common.datacomponent.DataComponents;
import dev.willyelton.crystal_tools.api.utils.constants.BlockEntityIdentifiers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class AutoOutputAction extends Action {
    private final AutoOutputable autoOutputable;
    private boolean autoOutputEnabled = false;
    // This is different because it isn't persisted, just temporarily disabled
    private boolean disabled = false;

    public AutoOutputAction(ActionBlockEntity blockEntity, ActionParameters params, AutoOutputable autoOutputable) {
        // 100
        super(blockEntity, params);
        this.autoOutputable = autoOutputable;
    }

    public AutoOutputAction(ActionBlockEntity blockEntity, AutoOutputable autoOutputable) {
        this(blockEntity, null, autoOutputable);
    }

    @Override
    public void tickAction(Level level, BlockPos pos, BlockState state) {
        if (this.disabled || !autoOutputEnabled) return;

        for (Integer index : autoOutputable.getOutputStacks().keySet()) {
            ItemStack stack = autoOutputable.getOutputStacks().get(index);
            if (stack.isEmpty()) continue;

            for (Direction dir : autoOutputable.possibleDirections()) {
                ResourceHandler<ItemResource> handler = level.getCapability(Capabilities.Item.BLOCK, pos.relative(dir), dir.getOpposite());

                if (handler != null) {
                    try (Transaction tx = Transaction.open(null)) {
                        int inserted = handler.insert(ItemResource.of(stack), stack.getCount(), tx);
                        stack.shrink(inserted);
                        tx.commit();
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
        if (BlockEntityIdentifiers.AUTO_OUTPUT.toString().equals(key)) {
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
    public Identifier getActionType() {
        return Registration.AUTO_OUTPUT_ACTION.getId();
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
