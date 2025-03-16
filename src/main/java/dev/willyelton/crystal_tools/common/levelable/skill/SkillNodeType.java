package dev.willyelton.crystal_tools.common.levelable.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.willyelton.crystal_tools.CrystalTools;
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
        this.codec = codec.fieldOf("what");
        this.streamCodec = streamCodec;
    }

    public MapCodec<? extends SkillDataNode> getCodec() {
        return codec;
    }



    public static <T extends SkillNodeType> T fromResourceLocation(ResourceLocation type) {
        for (SkillNodeType skillNodeType : values()) {
            if (skillNodeType.type.equals(type)) {
                return skillNodeType;
            }
        }

        throw new IllegalArgumentException("Invalid skill node type: " + type);
    }

    public static <T extends SkillNodeType> ResourceLocation toResourceLocation(T type) {

    }


    // TODO: use the bimap, maybe something in there makes the generics happy
    public static Codec<SkillNodeType> CODEC = ResourceLocation.CODEC.xmap(SkillNodeType::fromResourceLocation, SkillNodeType::toResourceLocation)
            .dispatch(SkillDataNode::getSkillNodeType, SkillNodeType::getCodec);
}
