package dev.willyelton.crystal_tools.common.levelable.skill.node;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillSubText;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirement;
import dev.willyelton.crystal_tools.utils.EnchantmentUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;
import java.util.Optional;

public final class EnchantmentDataNode extends SkillDataNode {
    public static final Codec<EnchantmentDataNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(EnchantmentDataNode::getId),
            Codec.STRING.fieldOf("name").forGetter(EnchantmentDataNode::getName),
            Codec.STRING.fieldOf("description").forGetter(EnchantmentDataNode::getDescription),
            ResourceLocation.CODEC.fieldOf("enchantment").forGetter(EnchantmentDataNode::getEnchantment),
            Codec.INT.fieldOf("level").forGetter(EnchantmentDataNode::getLevel),
            SkillDataRequirement.CODEC.listOf().fieldOf("requirements").forGetter(EnchantmentDataNode::getRequirements),
            SkillSubText.CODEC.optionalFieldOf("subtext").forGetter(n -> Optional.ofNullable(n.getSkillSubText()))
    ).apply(instance, EnchantmentDataNode::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentDataNode> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EnchantmentDataNode::getId,
            ByteBufCodecs.STRING_UTF8, EnchantmentDataNode::getName,
            ByteBufCodecs.STRING_UTF8, EnchantmentDataNode::getDescription,
            ResourceLocation.STREAM_CODEC, EnchantmentDataNode::getEnchantment,
            ByteBufCodecs.VAR_INT, EnchantmentDataNode::getLevel,
            ByteBufCodecs.fromCodec(SkillDataRequirement.CODEC.listOf().fieldOf("requirements").codec()), EnchantmentDataNode::getRequirements, // TODO
            ByteBufCodecs.fromCodec(SkillSubText.CODEC.optionalFieldOf("subtext").codec()), n -> Optional.ofNullable(n.getSkillSubText()), // TODO
            EnchantmentDataNode::new);

    private final ResourceLocation enchantment;
    private final int level;

    public EnchantmentDataNode(int id, String name, String description, ResourceLocation enchantment, int level,
                               List<SkillDataRequirement> requirements, Optional<SkillSubText> subText) {
        super(id, name, description, 1, List.of(enchantment), requirements, subText.orElse(null));

        this.enchantment = enchantment;
        this.level = level;
    }

    public ResourceLocation getEnchantment() {
        return enchantment;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public SkillNodeType getSkillNodeType() {
        return SkillNodeType.ENCHANTMENT;
    }

    @Override
    public void processNode(ItemStack stack, int pointsToSpend, RegistryAccess registryAccess) {
        Registry<Enchantment> enchantmentRegistry = registryAccess.lookupOrThrow(Registries.ENCHANTMENT);

        for (ResourceLocation key : this.getKeys()) {
            Optional<Holder.Reference<Enchantment>> enchantmentOptional = enchantmentRegistry.get(key);
            if (key.equals(Enchantments.SILK_TOUCH.location())) {
                stack.set(DataComponents.SILK_TOUCH_BONUS, true);
                if (EnchantmentUtils.hasEnchantment(stack, Enchantments.FORTUNE)) {
                    return;
                }
            } else if (key.equals(Enchantments.FORTUNE.location())) {
                stack.set(DataComponents.FORTUNE_BONUS, this.level);
                if (EnchantmentUtils.hasEnchantment(stack, Enchantments.SILK_TOUCH)) {
                    return;
                }
            }

            enchantmentOptional.ifPresent(enchantmentReference -> stack.enchant(enchantmentReference, this.level));
        }
    }
}
