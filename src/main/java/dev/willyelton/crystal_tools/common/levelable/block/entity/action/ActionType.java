package dev.willyelton.crystal_tools.common.levelable.block.entity.action;

import com.mojang.serialization.Codec;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.levelable.block.entity.ActionBlockEntity;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum ActionType implements StringRepresentable {
    AUTO_OUTPUT(AutoOutputAction.class, AutoOutputable.class, "crystal_tools.action.auto_output"),
    CHUNK_LOAD(ChunkLoadingAction.class, ChunkLoader.class, "crystal_tools.action.chunk_load"),
    MAGNET(MagnetAction.class, null, "crystal_tools.action.magnet"),
    BLOCK_BREAK(BlockBreakAction.class, null, "crystal_tools.action.block_break");

    public static final Codec<ActionType> CODEC = StringRepresentable.fromEnum(ActionType::values);

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