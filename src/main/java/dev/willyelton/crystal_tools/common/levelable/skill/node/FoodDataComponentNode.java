package dev.willyelton.crystal_tools.common.levelable.skill.node;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillSubText;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirement;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirements;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

import static dev.willyelton.crystal_tools.common.levelable.skill.node.SkillNodeType.FOOD_DATA_COMPONENT;

public final class FoodDataComponentNode extends SkillDataNode implements ItemStackNode {
    public static final Codec<FoodDataComponentNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(FoodDataComponentNode::getId),
            Codec.STRING.fieldOf("name").forGetter(FoodDataComponentNode::getName),
            Codec.STRING.fieldOf("description").forGetter(FoodDataComponentNode::getDescription),
            Codec.INT.fieldOf("limit").forGetter(FoodDataComponentNode::getLimit),
            FoodProperties.DIRECT_CODEC.fieldOf("value").forGetter(FoodDataComponentNode::getValue),
            SkillDataRequirements.CODEC.listOf().fieldOf("requirements").forGetter(FoodDataComponentNode::getRequirements),
            SkillSubText.CODEC.optionalFieldOf("subtext").forGetter(n -> Optional.ofNullable(n.getSkillSubText()))
    ).apply(instance, FoodDataComponentNode::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FoodDataComponentNode> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, FoodDataComponentNode::getId,
            ByteBufCodecs.STRING_UTF8, FoodDataComponentNode::getName,
            ByteBufCodecs.STRING_UTF8, FoodDataComponentNode::getDescription,
            ByteBufCodecs.VAR_INT, FoodDataComponentNode::getLimit,
            FoodProperties.DIRECT_STREAM_CODEC, FoodDataComponentNode::getValue,
            SkillDataRequirements.STREAM_CODEC.apply(ByteBufCodecs.list()), FoodDataComponentNode::getRequirements,
            ByteBufCodecs.optional(SkillSubText.STREAM_CODEC), n -> Optional.ofNullable(n.getSkillSubText()),
            FoodDataComponentNode::new);
    private final FoodProperties value;

    public FoodDataComponentNode(int id, String name, String description, int limit, FoodProperties value, List<SkillDataRequirement> requirements, Optional<SkillSubText> skillSubText) {
        super(id, name, description, limit, (ResourceLocation) null, requirements, skillSubText.orElse(null));
        this.value = value;
    }

    @Override
    public SkillNodeType getSkillNodeType() {
        return FOOD_DATA_COMPONENT;
    }

    @Override
    public void processNode(SkillData skillData, ItemStack stack, int pointsToSpend, RegistryAccess registryAccess) {
        FoodProperties existingProperties = stack.getOrDefault(DataComponents.FOOD, new FoodProperties(0, 0, false));

        stack.set(DataComponents.FOOD, add(existingProperties, mult(this.value, pointsToSpend)));
    }

    public FoodProperties getValue() {
        return value;
    }

    private static FoodProperties add(FoodProperties p1, FoodProperties p2) {
        return new FoodProperties(p1.nutrition() + p2.nutrition(), p1.saturation() + p2.saturation(), p1.canAlwaysEat() || p2.canAlwaysEat());
    }

    private static FoodProperties mult(FoodProperties p, int i) {
        return new FoodProperties(p.nutrition() * i, p.saturation() * i, p.canAlwaysEat());
    }
}
