package dev.willyelton.crystal_tools.api.common.block.entity;

import com.mojang.serialization.Codec;
import dev.willyelton.crystal_tools.api.CrystalToolsApi;
import dev.willyelton.crystal_tools.api.common.block.entity.action.Action;
import dev.willyelton.crystal_tools.api.common.block.entity.action.ActionParameters;
import dev.willyelton.crystal_tools.api.common.block.entity.action.ActionType;
import dev.willyelton.crystal_tools.api.utils.RegistryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.willyelton.crystal_tools.api.utils.constants.ApiConstants.ACTION_TYPE_REGISTRY_KEY;


/**
 * Base class for any block entity that supports actions
 */
// TODO: Should dynamic actions be saved to itemstack? Probably not
public class ActionBlockEntity extends BlockEntity {
    private static final Codec<Map<Identifier, ActionParameters>> SAVED_ACTIONS_CODEC = Codec.unboundedMap(Identifier.CODEC, ActionParameters.CODEC);

    private final Map<Identifier, Action> actions = new HashMap<>();
    private final Map<Identifier, ActionParameters> savedActions = new HashMap<>();

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
            Action action = createAction(type, parameters);
            if (action != null) {
                actions.put(type, action);
            } else {
                CrystalToolsApi.LOGGER.warn("Saved action parameters created null action {} {}", type, parameters);
            }
        });
    }

    protected @Nullable Action createAction(Identifier type, ActionParameters actionParameters) {
        ActionType<?, ?> actionType = RegistryUtils.getFromRegistry(level.registryAccess(), ACTION_TYPE_REGISTRY_KEY, type);
        if (actionType != null) {
            return actionType.getActionInstance(this, actionParameters);
        } else {
            CrystalToolsApi.LOGGER.warn("No action type {}", type);
            return null;
        }
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

    protected void removeAction(Identifier actionType) {
        if (this.actions.containsKey(actionType)) {
            this.actions.get(actionType).onRemove();
            this.actions.remove(actionType);
            this.savedActions.remove(actionType);
            this.setChanged();
        }
    }

    protected boolean hasAction(Identifier actionType) {
        return this.actions.containsKey(actionType);
    }

    protected Action getAction(Identifier actionType) {
        return this.actions.get(actionType);
    }

    protected Iterable<Action> getActions() {
        return this.actions.values();
    }

    protected Collection<Action> getDefaultActions() {
        return List.of();
    }
}
