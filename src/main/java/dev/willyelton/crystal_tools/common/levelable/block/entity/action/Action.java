package dev.willyelton.crystal_tools.common.levelable.block.entity.action;

import com.mojang.serialization.Codec;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.levelable.block.entity.ActionBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalPedestalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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

    public void load(ValueInput valueInput) {}

    public void save(ValueOutput valueOutput) {}

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

    protected ItemStack getItem() {
        if (blockEntity instanceof CrystalPedestalBlockEntity pedestalBlockEntity) {
            return pedestalBlockEntity.getStack() == null ? ItemStack.EMPTY : pedestalBlockEntity.getStack();
        }

        return ItemStack.EMPTY;
    }

    // TODO: Translatable translations
    public enum ActionType implements StringRepresentable {
        AUTO_OUTPUT(AutoOutputAction.class, AutoOutputable.class, "crystal_tools.action.auto_output"),
        CHUNK_LOAD(ChunkLoadingAction.class, ChunkLoader.class, "crystal_tools.action.chunk_load"),
        MAGNET(MagnetAction.class, null, "crystal_tools.action.magnet"),;

        public static final Codec<ActionType> CODEC = StringRepresentable.fromEnum(Action.ActionType::values);

        private final Class<? extends Action> actionClass;
        private final Class<?> contextClass;
        private final String description;

        ActionType(Class<? extends Action> actionClass, @Nullable Class<?> contextClass, String description) {
            this.actionClass = actionClass;
            this.contextClass = contextClass;
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        // There still has to be a better way to do this.
        // Ideas to improve:
        //  1. Generic context on action (problem is how to get context from the pedestal block)
        public @Nullable Action getActionInstance(ActionBlockEntity blockEntity, @Nullable ActionParameters actionParameters) {
            if (this.contextClass == null) {
                Constructor<? extends Action> constructor = getConstructor(ActionBlockEntity.class, ActionParameters.class);

                if (constructor != null) {
                    return getActionFromConstructor(constructor, blockEntity, actionParameters);
                } else {
                    CrystalTools.LOGGER.error("Failed to instantiate action class {}, no matching constructor found", actionClass);
                    return null;
                }
            }

            if (!contextClass.isAssignableFrom(blockEntity.getClass())) {
                CrystalTools.LOGGER.error("Action type {} expects the ActionBlockEntity to implement {}", this, contextClass.getName());
                return null;
            }

            Constructor<? extends Action> constructor = getConstructor(ActionBlockEntity.class, ActionParameters.class, contextClass);

            if (constructor != null) {
                return getActionFromConstructor(constructor, blockEntity, actionParameters, blockEntity);
            } else {
                CrystalTools.LOGGER.error("Failed to instantiate action class {}, no matching constructor found", actionClass);
                return null;
            }
        }

        private @Nullable Constructor<? extends Action> getConstructor(Class<?>... parameters) {
            try {
                return actionClass.getConstructor(parameters);
            } catch (NoSuchMethodException e) {
                return null;
            }
        }

        private @Nullable Action getActionFromConstructor(Constructor<? extends Action> constructor, Object... parameters) {
            try {
                return constructor.newInstance(parameters);
            } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                return null;
            }
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}
