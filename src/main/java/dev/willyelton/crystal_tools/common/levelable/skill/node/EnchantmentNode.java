package dev.willyelton.crystal_tools.common.levelable.skill.node;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillSubText;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirement;
import dev.willyelton.crystal_tools.utils.EnchantmentUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class EnchantmentNode extends SkillDataNode {
    public static final Codec<EnchantmentNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(EnchantmentNode::getId),
            Codec.STRING.fieldOf("name").forGetter(EnchantmentNode::getName),
            Codec.STRING.fieldOf("description").forGetter(EnchantmentNode::getDescription),
            ResourceLocation.CODEC.fieldOf("enchantment").forGetter(EnchantmentNode::getEnchantment),
            Codec.INT.fieldOf("level").forGetter(EnchantmentNode::getLevel),
            SkillDataRequirement.CODEC.listOf().fieldOf("requirements").forGetter(EnchantmentNode::getRequirements),
            SkillSubText.CODEC.optionalFieldOf("subtext").forGetter(n -> Optional.ofNullable(n.getSkillSubText()))
    ).apply(instance, EnchantmentNode::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentNode> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EnchantmentNode::getId,
            ByteBufCodecs.STRING_UTF8, EnchantmentNode::getName,
            ByteBufCodecs.STRING_UTF8, EnchantmentNode::getDescription,
            ResourceLocation.STREAM_CODEC, EnchantmentNode::getEnchantment,
            ByteBufCodecs.VAR_INT, EnchantmentNode::getLevel,
            ByteBufCodecs.fromCodec(SkillDataRequirement.CODEC.listOf().fieldOf("requirements").codec()), EnchantmentNode::getRequirements, // TODO
            ByteBufCodecs.fromCodec(SkillSubText.CODEC.optionalFieldOf("subtext").codec()), n -> Optional.ofNullable(n.getSkillSubText()), // TODO
            EnchantmentNode::new);

    public static final Map<ResourceKey<Enchantment>, DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>>> TOGGLE_ENCHANTMENTS =
            Map.of(Enchantments.FROST_WALKER, DataComponents.FROST_WALKER);

    private final ResourceLocation enchantment;
    private final int level;

    public EnchantmentNode(int id, String name, String description, ResourceLocation enchantment, int level,
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
    public void processNode(SkillData skillData, ItemStack stack, int pointsToSpend, RegistryAccess registryAccess) {
        Registry<Enchantment> enchantmentRegistry = registryAccess.lookupOrThrow(Registries.ENCHANTMENT);

        for (ResourceLocation key : this.getKeys()) {
            Optional<Holder.Reference<Enchantment>> enchantmentOptional = enchantmentRegistry.get(key);
            if (enchantmentOptional.isPresent()) {
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
                } else if (TOGGLE_ENCHANTMENTS.containsKey(enchantmentOptional.get().getKey())) {
                    stack.set(TOGGLE_ENCHANTMENTS.get(enchantmentOptional.get().getKey()), true);
                }

                enchantmentOptional.ifPresent(enchantmentReference -> stack.enchant(enchantmentReference, this.level));
            }
        }
    }
}
