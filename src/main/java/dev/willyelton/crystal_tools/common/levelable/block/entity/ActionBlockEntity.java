package dev.willyelton.crystal_tools.common.levelable.block.entity;

import com.mojang.serialization.Codec;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.Action;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.ActionParameters;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for any block entity that supports actions
 */
// TODO: Should dynamic actions be saved to itemstack? Probably not
public class ActionBlockEntity extends BlockEntity {
    private static final Codec<Map<Action.ActionType, ActionParameters>> SAVED_ACTIONS_CODEC = Codec.unboundedMap(Action.ActionType.CODEC, ActionParameters.CODEC);

    private final Map<Action.ActionType, Action> actions = new HashMap<>();
    private final Map<Action.ActionType, ActionParameters> savedActions = new HashMap<>();

    public ActionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        getDefaultActions().forEach(action -> actions.put(action.getActionType(), action));
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        for (Action action : getActions()) {
            action.onRemove();
        }
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);

        getActions().forEach(action -> action.load(valueInput));
        savedActions.putAll(valueInput.read("savedActions", SAVED_ACTIONS_CODEC).orElse(Map.of()));
        savedActions.forEach((type, parameters) -> {
            Action action = type.getActionInstance(this, parameters);
            if (action != null) {
                actions.put(type, action);
            } else {
                CrystalTools.LOGGER.warn("Saved action parameters created null action {} {}", type, parameters);
            }
        });
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter componentGetter) {
        super.applyImplicitComponents(componentGetter);

        for (Action action : getActions()) {
            action.applyComponents(componentGetter);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);

        getActions().forEach(action -> action.save(valueOutput));

        valueOutput.store("savedActions", SAVED_ACTIONS_CODEC, savedActions);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        for (Action action : getActions()) {
            action.collectComponents(components);
        }
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        for (Action action : getActions()) {
            action.tick(level, pos, state);
        }
    }

    protected <T extends Action> void addAction(T action, ActionParameters parameters) {
        this.actions.put(action.getActionType(), action);
        this.savedActions.put(action.getActionType(), parameters);
        this.setChanged();
    }

    protected void removeAction(Action.ActionType actionType) {
        this.actions.remove(actionType);
        this.savedActions.remove(actionType);
        this.setChanged();
    }

    protected boolean hasAction(Action.ActionType actionType) {
        return this.actions.containsKey(actionType);
    }

    protected Action getAction(Action.ActionType actionType) {
        return this.actions.get(actionType);
    }

    protected Iterable<Action> getActions() {
        return this.actions.values();
    }

    protected Collection<Action> getDefaultActions() {
        return List.of();
    }
}
