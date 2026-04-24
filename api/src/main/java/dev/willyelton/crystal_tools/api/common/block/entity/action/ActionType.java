package dev.willyelton.crystal_tools.api.common.block.entity.action;

import dev.willyelton.crystal_tools.api.common.block.entity.ActionBlockEntity;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class ActionType<T extends Action, @Nullable S> {
    private final Identifier key;
    private final ActionFactory<T, S> actionFactory;
    private final Class<S> contextClass;

    public ActionType(Identifier key, ActionFactory<T, S> actionFactory, @Nullable Class<S> contextClass) {
        this.key = key;
        this.actionFactory = actionFactory;
        this.contextClass = contextClass;
    }

    public ActionType(Identifier key, ActionFactoryNoContext<T> actionFactory) {
        this(key, (actionBlockEntity, actionParameters, _) -> actionFactory.create(actionBlockEntity, actionParameters), null);
    }

    public Identifier key() {
        return key;
    }

    public String descriptionTranslationKey() {
        return String.format("action.%s.%s", key().getNamespace(), key().getPath());
    }

    public @Nullable Action getActionInstance(ActionBlockEntity blockEntity, @Nullable ActionParameters actionParameters) {
        if (this.contextClass != null && blockEntity.getClass().isAssignableFrom(this.contextClass)) {
            return actionFactory.create(blockEntity, actionParameters, this.contextClass.cast(blockEntity));
        } else {
            return actionFactory.create(blockEntity, actionParameters, null);
        }

    }

    @FunctionalInterface
    public interface ActionFactory<T extends Action, S> {
        @Nullable T create(ActionBlockEntity actionBlockEntity, ActionParameters actionParameters, @Nullable S context);
    }

    @FunctionalInterface
    public interface ActionFactoryNoContext<T extends Action> {
        @Nullable T create(ActionBlockEntity actionBlockEntity, ActionParameters actionParameters);
    }
}
