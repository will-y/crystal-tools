package dev.willyelton.crystal_tools.common.levelable.skill.node;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillSubText;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirement;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public final class DataComponentSkillNode extends SkillDataNode {
    public static final Codec<DataComponentSkillNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(DataComponentSkillNode::getId),
            Codec.STRING.fieldOf("name").forGetter(DataComponentSkillNode::getName),
            Codec.STRING.fieldOf("description").forGetter(DataComponentSkillNode::getDescription),
            Codec.INT.fieldOf("limit").forGetter(DataComponentSkillNode::getLimit),
            ResourceLocation.CODEC.listOf().fieldOf("key").forGetter(DataComponentSkillNode::getKeys),
            Codec.FLOAT.fieldOf("value").forGetter(DataComponentSkillNode::getValue),
            SkillDataRequirement.CODEC.listOf().fieldOf("requirements").forGetter(DataComponentSkillNode::getRequirements),
            SkillSubText.CODEC.optionalFieldOf("subtext").forGetter(n -> Optional.ofNullable(n.getSkillSubText()))
    ).apply(instance, DataComponentSkillNode::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DataComponentSkillNode> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, DataComponentSkillNode::getId,
            ByteBufCodecs.STRING_UTF8, DataComponentSkillNode::getName,
            ByteBufCodecs.STRING_UTF8, DataComponentSkillNode::getDescription,
            ByteBufCodecs.VAR_INT, DataComponentSkillNode::getLimit,
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), DataComponentSkillNode::getKeys,
            ByteBufCodecs.FLOAT, DataComponentSkillNode::getValue,
            ByteBufCodecs.fromCodec(SkillDataRequirement.CODEC.listOf().fieldOf("requirements").codec()), DataComponentSkillNode::getRequirements, // TODO
            ByteBufCodecs.fromCodec(SkillSubText.CODEC.optionalFieldOf("subtext").codec()), n -> Optional.ofNullable(n.getSkillSubText()), // TODO
            DataComponentSkillNode::new);

    private final float value;

    public DataComponentSkillNode(int id, String name, String description, int limit, List<ResourceLocation> keys, float value, List<SkillDataRequirement> requirements, Optional<SkillSubText> skillSubText) {
        super(id, name, description, limit, keys, requirements, skillSubText.orElse(null));
        this.value = value;
    }

    public DataComponentSkillNode(int id, String name, String description, int limit, ResourceLocation key, float value, List<SkillDataRequirement> requirements, Optional<SkillSubText> skillSubText) {
        this(id, name, description, limit, List.of(key), value, requirements, skillSubText);
    }

    public float getValue() {
        return value;
    }

    @Override
    public SkillNodeType getSkillNodeType() {
        return SkillNodeType.DATA_COMPONENT;
    }

    @Override
    public void processNode(ItemStack stack, int pointsToSpend, RegistryAccess registryAccess) {
        Registry<DataComponentType<?>> dataComponents = registryAccess.lookupOrThrow(Registries.DATA_COMPONENT_TYPE);

        for (ResourceLocation key : this.getKeys()) {
            Optional<Holder.Reference<DataComponentType<?>>> dataComponentOptional = dataComponents.get(key);

            if (dataComponentOptional.isPresent()) {
                DataComponentType<?> dataComponent = dataComponentOptional.get().value();
                Object value = stack.get(dataComponent);

                // TODO: There still has to be something better ...
                if (dataComponent.codec() != null) {
                    if (Codec.BOOL.equals(dataComponent.codec())) {
                        stack.set((DataComponentType<Boolean>) dataComponent, true);
                    } else if (Codec.INT.equals(dataComponent.codec()) || ExtraCodecs.POSITIVE_INT.equals(dataComponent.codec()) || ExtraCodecs.NON_NEGATIVE_INT.equals(dataComponent.codec())) {
                        int intValue = value == null ? 0 : (int) value;
                        stack.set((DataComponentType<Integer>) dataComponent, intValue + (int) this.value * pointsToSpend);
                    } else if (Codec.FLOAT.equals(dataComponent.codec())) {
                        float floatValue = value == null ? 0.0F : (float) value;
                        stack.set((DataComponentType<Float>) dataComponent, floatValue + this.value * pointsToSpend);
                    } else {
                        throw new IllegalStateException("Unexpected skill datacomponent type: " + value);
                    }
                }
            }
        }
    }
}
