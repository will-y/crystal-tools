package dev.willyelton.crystal_tools.common.levelable.skill.node;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.levelable.block.entity.LevelableBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillSubText;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirement;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirements;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public class BlockEntityNbtNode extends SkillDataNode implements BlockEntityNode {
    public static final Codec<BlockEntityNbtNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(BlockEntityNbtNode::getId),
            Codec.STRING.fieldOf("name").forGetter(BlockEntityNbtNode::getName),
            Codec.STRING.fieldOf("description").forGetter(BlockEntityNbtNode::getDescription),
            Codec.INT.fieldOf("limit").forGetter(BlockEntityNbtNode::getLimit),
            ResourceLocation.CODEC.listOf().fieldOf("key").forGetter(BlockEntityNbtNode::getKeys),
            Codec.FLOAT.fieldOf("value").forGetter(BlockEntityNbtNode::getValue),
            SkillDataRequirements.CODEC.listOf().fieldOf("requirements").forGetter(BlockEntityNbtNode::getRequirements),
            SkillSubText.CODEC.optionalFieldOf("subtext").forGetter(n -> Optional.ofNullable(n.getSkillSubText()))
    ).apply(instance, BlockEntityNbtNode::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockEntityNbtNode> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, BlockEntityNbtNode::getId,
            ByteBufCodecs.STRING_UTF8, BlockEntityNbtNode::getName,
            ByteBufCodecs.STRING_UTF8, BlockEntityNbtNode::getDescription,
            ByteBufCodecs.VAR_INT, BlockEntityNbtNode::getLimit,
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), BlockEntityNbtNode::getKeys,
            ByteBufCodecs.FLOAT, BlockEntityNbtNode::getValue,
            SkillDataRequirements.STREAM_CODEC.apply(ByteBufCodecs.list()), BlockEntityNbtNode::getRequirements,
            ByteBufCodecs.optional(SkillSubText.STREAM_CODEC), n -> Optional.ofNullable(n.getSkillSubText()),
            BlockEntityNbtNode::new);

    private final float value;
    
    public BlockEntityNbtNode(int id, String name, String description, int limit, List<ResourceLocation> keys, float value, List<SkillDataRequirement> requirements, Optional<SkillSubText> skillSubText) {
        super(id, name, description, limit, keys, requirements, skillSubText.orElse(null));
        this.value = value;
    }

    @Override
    public SkillNodeType getSkillNodeType() {
        return SkillNodeType.BLOCK_ENTITY_NBT;
    }

    @Override
    public void processNode(SkillData skillData, LevelableBlockEntity blockEntity, int pointsToSpend, RegistryAccess registryAccess) {
        for (ResourceLocation key : getKeys()) {
            blockEntity.addToData(key.toString(), this.value * pointsToSpend);
            blockEntity.addToPoints(this.getId(), pointsToSpend);
        }
    }

    public float getValue() {
        return value;
    }
}
