package dev.willyelton.crystal_tools.common.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.api.common.block.entity.action.ActionParameters;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record ActionData(Identifier actionType, ActionParameters params) {
    public static Codec<ActionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("type").forGetter(ActionData::actionType),
            ActionParameters.CODEC.optionalFieldOf("params").forGetter(actionData -> Optional.ofNullable(actionData.params()))
    ).apply(instance, ActionData::new));

    public ActionData(Identifier type, Optional<ActionParameters> params) {
        this(type, params.orElse(null));
    }
}
