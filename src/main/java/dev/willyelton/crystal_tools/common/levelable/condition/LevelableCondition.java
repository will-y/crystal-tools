package dev.willyelton.crystal_tools.common.levelable.condition;

import com.mojang.serialization.Codec;
import dev.willyelton.crystal_tools.utils.CodecUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public interface LevelableCondition {
    Codec<LevelableCondition> CODEC = Identifier.CODEC.xmap(LevelableConditions::getCondition, LevelableCondition::id);
    StreamCodec<RegistryFriendlyByteBuf, LevelableCondition> STREAM_CODEC = CodecUtils.RESOURCE_LOCATION_STREAM_CODEC
            .map(LevelableConditions::getCondition, LevelableCondition::id);

    Identifier id();
}
