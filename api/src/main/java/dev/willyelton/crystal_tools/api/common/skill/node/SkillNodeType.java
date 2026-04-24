package dev.willyelton.crystal_tools.api.common.skill.node;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.willyelton.crystal_tools.api.utils.constants.ApiConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public enum SkillNodeType {
    ATTRIBUTE(Identifier.fromNamespaceAndPath(ApiConstants.CORE_MOD_ID, "attribute"), AttributeNode.CODEC, AttributeNode.STREAM_CODEC),
    DATA_COMPONENT(Identifier.fromNamespaceAndPath(ApiConstants.CORE_MOD_ID, "data_component"), DataComponentNode.CODEC, DataComponentNode.STREAM_CODEC),
    ENCHANTMENT(Identifier.fromNamespaceAndPath(ApiConstants.CORE_MOD_ID, "enchantment"), EnchantmentNode.CODEC, EnchantmentNode.STREAM_CODEC),
    FOOD_DATA_COMPONENT(Identifier.fromNamespaceAndPath(ApiConstants.CORE_MOD_ID, "food_data_component"), FoodDataComponentNode.CODEC, FoodDataComponentNode.STREAM_CODEC),
    EFFECT(Identifier.fromNamespaceAndPath(ApiConstants.CORE_MOD_ID, "effect"), EffectNode.CODEC, EffectNode.STREAM_CODEC),
    BLOCK_ENTITY_NBT(Identifier.fromNamespaceAndPath(ApiConstants.CORE_MOD_ID, "block_entity_nbt"), BlockEntityNbtNode.CODEC, BlockEntityNbtNode.STREAM_CODEC),
    ENTITY_ATTRIBUTE(Identifier.fromNamespaceAndPath(ApiConstants.CORE_MOD_ID, "entity_attribute"), EntityAttributeNode.CODEC, EntityAttributeNode.STREAM_CODEC),
    ENTITY_DATA(Identifier.fromNamespaceAndPath(ApiConstants.CORE_MOD_ID, "entity_data"), EntityDataNode.CODEC, EntityDataNode.STREAM_CODEC);

    private final Identifier type;
    private final MapCodec<? extends SkillDataNode> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, ? extends SkillDataNode> streamCodec;

    SkillNodeType(Identifier type, Codec<? extends SkillDataNode> codec, StreamCodec<RegistryFriendlyByteBuf, ? extends SkillDataNode> streamCodec) {
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

    public Identifier Identifier() {
        return type;
    }

    public static SkillNodeType fromIdentifier(Identifier type) {
        for (SkillNodeType skillNodeType : values()) {
            if (skillNodeType.type.equals(type)) {
                return skillNodeType;
            }
        }

        throw new IllegalArgumentException("Invalid skill node type: " + type);
    }
}
