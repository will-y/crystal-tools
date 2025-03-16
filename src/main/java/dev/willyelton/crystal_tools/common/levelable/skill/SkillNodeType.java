package dev.willyelton.crystal_tools.common.levelable.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.willyelton.crystal_tools.CrystalTools;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public enum SkillNodeType {
    FLOAT(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "float"), FloatSkillDataNode.CODEC, FloatSkillDataNode.STREAM_CODEC),
    BOOLEAN(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "boolean"), BooleanSkillDataNode.CODEC, BooleanSkillDataNode.STREAM_CODEC);

    private final ResourceLocation type;
    private final MapCodec<? extends SkillDataNode> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, ? extends SkillDataNode> streamCodec;

    SkillNodeType(ResourceLocation type, Codec<? extends SkillDataNode> codec, StreamCodec<RegistryFriendlyByteBuf, ? extends SkillDataNode> streamCodec) {
        this.type = type;
        this.codec = codec.fieldOf("node");
        this.streamCodec = streamCodec;
    }

    public MapCodec<? extends SkillDataNode> codec() {
        return codec;
    }

    public StreamCodec<RegistryFriendlyByteBuf, ? extends SkillDataNode> streamCodec() {
        return streamCodec;
    }

    public ResourceLocation resourceLocation() {
        return type;
    }

    // TODO: Should probably make a map for this but probably doesn't matter
    public static SkillNodeType fromResourceLocation(ResourceLocation type) {
        for (SkillNodeType skillNodeType : values()) {
            if (skillNodeType.type.equals(type)) {
                return skillNodeType;
            }
        }

        throw new IllegalArgumentException("Invalid skill node type: " + type);
    }
}
