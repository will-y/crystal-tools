package dev.willyelton.crystal_tools.common.levelable.skill.node;

import com.mojang.serialization.Codec;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillSubText;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirement;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public sealed abstract class SkillDataNode permits DataComponentSkillNode, EnchantmentDataNode, AttributeSkillDataNode {
    private final int id;
    private final String name;
    private final String description;
    // If 0 = infinite, 1 = normal, 2+ = amount of points you can put in
    private final int limit;
    private final List<SkillDataRequirement> requirements;
    private final List<ResourceLocation> keys;
    private SkillSubText skillSubText;

    public SkillDataNode(int id, String name, String description, int limit, ResourceLocation key,
                         List<SkillDataRequirement> requirements, SkillSubText skillSubText) {
        this(id, name, description, limit, List.of(key), requirements, skillSubText);
    }

    public SkillDataNode(int id, String name, String description, int limit, List<ResourceLocation> keys,
                         List<SkillDataRequirement> requirements, SkillSubText skillSubText) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.limit = limit;
        this.keys = keys;
        this.requirements = requirements;
        this.skillSubText = skillSubText;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getLimit() {
        return limit;
    }

    public List<SkillDataRequirement> getRequirements() {
        return requirements;
    }

    public void addRequirement(SkillDataRequirement requirement) {
        requirements.add(requirement);
    }

    public List<ResourceLocation> getKeys() {
        return this.keys;
    }

    public SkillSubText getSkillSubText() {
        return this.skillSubText;
    }

    public void setSubtext(SkillSubText subtext) {
        this.skillSubText = subtext;
    }

    /**
     * Returns true if all of this node's requirements are met, and it can be leveled
     */
    public boolean canLevel(SkillPoints points, Player player) {
        for (SkillDataRequirement requirement : this.requirements) {
            if (!requirement.canLevel(points, player)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "SkillDataNode{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", limit=" + limit +
                ", requirements=" + requirements +
                ", key='" + keys.stream().map(ResourceLocation::toString).collect(Collectors.joining(", ")) + '\'' +
                '}';
    }

    public abstract SkillNodeType getSkillNodeType();

    public abstract void processNode(ItemStack stack, int pointsToSpend, RegistryAccess registryAccess);

    public static Codec<SkillDataNode> CODEC = ResourceLocation.CODEC.xmap(SkillNodeType::fromResourceLocation, SkillNodeType::resourceLocation)
            .dispatch(SkillDataNode::getSkillNodeType, SkillNodeType::codec);

    public static StreamCodec<RegistryFriendlyByteBuf, SkillDataNode> STREAM_CODEC = StreamCodec.of(RegistryFriendlyByteBuf::writeResourceLocation, RegistryFriendlyByteBuf::readResourceLocation)
            .map(SkillNodeType::fromResourceLocation, SkillNodeType::resourceLocation)
            .dispatch(SkillDataNode::getSkillNodeType, SkillNodeType::streamCodec);
}
