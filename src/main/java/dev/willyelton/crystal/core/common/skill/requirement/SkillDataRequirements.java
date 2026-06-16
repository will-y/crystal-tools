package dev.willyelton.crystal.core.common.skill.requirement;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.willyelton.crystal.core.common.skill.node.SkillDataNode;
import dev.willyelton.crystal.core.utils.CodecUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

import java.util.function.Function;

import static dev.willyelton.crystal.core.utils.constants.ApiConstants.baseRl;

/**
 * Class to handle codecs for SkillDataRequirements.
 * <p>
 * Modeled after {@link net.minecraft.client.renderer.texture.atlas.SpriteSources}.
 * <p>
 * Should probably do something similar with {@link SkillDataNode}s,
 * then it is easier to add other types.
 */
public class SkillDataRequirements {
    private static final BiMap<Identifier, StreamCodec<RegistryFriendlyByteBuf, ? extends SkillDataRequirement>> STREAM_CODEC_BI_MAP = HashBiMap.create();

    private static final ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends SkillDataRequirement>> ID_MAPPER = new ExtraCodecs.LateBoundIdMapper<>();

    public static final Codec<SkillDataRequirement> CODEC = ID_MAPPER.codec(Identifier.CODEC).dispatch(SkillDataRequirement::codec, Function.identity());
    public static final StreamCodec<RegistryFriendlyByteBuf, SkillDataRequirement> STREAM_CODEC = CodecUtils.RESOURCE_LOCATION_STREAM_CODEC
            .dispatch(STREAM_CODEC_BI_MAP.inverse()::get, STREAM_CODEC_BI_MAP::get);

    public static void bootstrap() {
        ID_MAPPER.put(baseRl("node_requirement"), NodeSkillDataRequirement.MAP_CODEC);
        ID_MAPPER.put(baseRl("or_node_requirement"), NodeOrSkillDataRequirement.MAP_CODEC);
        ID_MAPPER.put(baseRl("not_node_requirement"), NotNodeSkillDataRequirement.MAP_CODEC);
        ID_MAPPER.put(baseRl("item_requirement"), SkillItemRequirement.MAP_CODEC);

        STREAM_CODEC_BI_MAP.put(baseRl("node_requirement"), NodeSkillDataRequirement.STREAM_CODEC);
        STREAM_CODEC_BI_MAP.put(baseRl("or_node_requirement"), NodeOrSkillDataRequirement.STREAM_CODEC);
        STREAM_CODEC_BI_MAP.put(baseRl("not_node_requirement"), NotNodeSkillDataRequirement.STREAM_CODEC);
        STREAM_CODEC_BI_MAP.put(baseRl("item_requirement"), SkillItemRequirement.STREAM_CODEC);
    }
}
