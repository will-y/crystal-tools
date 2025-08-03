package dev.willyelton.crystal_tools.common.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.Action;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.ActionParameters;

import java.util.Optional;

public record ActionData(Action.ActionType type, ActionParameters params) {
    public static Codec<ActionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Action.ActionType.CODEC.fieldOf("type").forGetter(ActionData::type),
            ActionParameters.CODEC.optionalFieldOf("params").forGetter(actionData -> Optional.ofNullable(actionData.params()))
    ).apply(instance, ActionData::new));

    public ActionData(Action.ActionType type, Optional<ActionParameters> params) {
        this(type, params.orElse(null));
    }
}
