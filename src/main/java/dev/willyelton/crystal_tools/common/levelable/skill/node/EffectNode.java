package dev.willyelton.crystal_tools.common.levelable.skill.node;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillSubText;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirement;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class EffectNode extends SkillDataNode implements ItemStackNode {
    public static final Codec<EffectNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(EffectNode::getId),
            Codec.STRING.fieldOf("name").forGetter(EffectNode::getName),
            Codec.STRING.fieldOf("description").forGetter(EffectNode::getDescription),
            Codec.INT.fieldOf("limit").forGetter(EffectNode::getLimit),
            SkillDataRequirement.CODEC.listOf().fieldOf("requirements").forGetter(EffectNode::getRequirements),
            SkillSubText.CODEC.optionalFieldOf("subtext").forGetter(n -> Optional.ofNullable(n.getSkillSubText())),
            MobEffectInstance.CODEC.listOf().fieldOf("effects").forGetter(EffectNode::getEffects)
    ).apply(instance, EffectNode::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EffectNode> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EffectNode::getId,
            ByteBufCodecs.STRING_UTF8, EffectNode::getName,
            ByteBufCodecs.STRING_UTF8, EffectNode::getDescription,
            ByteBufCodecs.VAR_INT, EffectNode::getLimit,
            ByteBufCodecs.fromCodec(SkillDataRequirement.CODEC.listOf().fieldOf("requirements").codec()), EffectNode::getRequirements, // TODO
            ByteBufCodecs.fromCodec(SkillSubText.CODEC.optionalFieldOf("subtext").codec()), n -> Optional.ofNullable(n.getSkillSubText()), // TODO
            MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list()), EffectNode::getEffects,
            EffectNode::new);

    private final List<MobEffectInstance> effects;

    public EffectNode(int id, String name, String description, int limit, List<SkillDataRequirement> requirements, Optional<SkillSubText> skillSubText, MobEffectInstance effect) {
        this(id, name, description, limit, requirements, skillSubText, List.of(effect));
    }

    public EffectNode(int id, String name, String description, int limit, List<SkillDataRequirement> requirements, Optional<SkillSubText> skillSubText, List<MobEffectInstance> effects) {
        super(id, name, description, limit, (ResourceLocation) null, requirements, skillSubText.orElse(null));
        this.effects = effects;
    }

    @Override
    public SkillNodeType getSkillNodeType() {
        return SkillNodeType.EFFECT;
    }

    @Override
    public void processNode(SkillData skillData, ItemStack stack, int pointsToSpend, RegistryAccess registryAccess) {
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);

        if (consumable != null) {
            stack.set(DataComponents.CONSUMABLE, withEffects(consumable, this.effects));
        } else {
            List<MobEffectInstance> instances = new ArrayList<>(stack.getOrDefault(dev.willyelton.crystal_tools.common.components.DataComponents.EFFECTS, Collections.emptyList()));
            List<MobEffectInstance> newEffects = addEffects(instances, new ArrayList<>(this.effects));
            stack.set(dev.willyelton.crystal_tools.common.components.DataComponents.EFFECTS, newEffects);
        }
    }

    public List<MobEffectInstance> getEffects() {
        return effects;
    }

    private Consumable withEffects(Consumable c, List<MobEffectInstance> effects) {
        List<ConsumeEffect> consumeEffects = new ArrayList<>();
        boolean foundConsumeEffect = false;
        for (ConsumeEffect consumeEffect : c.onConsumeEffects()) {
            if (consumeEffect instanceof ApplyStatusEffectsConsumeEffect applyStatusEffect) {
                foundConsumeEffect = true;
                List<MobEffectInstance> newEffects = new ArrayList<>();
                for (MobEffectInstance newEffect : effects) {
                    boolean found = false;
                    for (MobEffectInstance existingEffect : applyStatusEffect.effects()) {
                        if (existingEffect.getEffect().equals(newEffect.getEffect())) {
                            newEffects.add(combine(existingEffect, newEffect));
                            found = true;
                            break;
                        } else {
                            newEffects.add(existingEffect);
                        }
                    }

                    if (!found) {
                        newEffects.add(newEffect);
                    }
                }

                consumeEffects.add(new ApplyStatusEffectsConsumeEffect(newEffects));
            } else {
                consumeEffects.add(consumeEffect);
            }
        }

        if (!foundConsumeEffect) {
            consumeEffects.add(new ApplyStatusEffectsConsumeEffect(effects));
        }

        return new Consumable(c.consumeSeconds(), c.animation(), c.sound(), c.hasConsumeParticles(), consumeEffects);
    }

    private List<MobEffectInstance> addEffects(List<MobEffectInstance> initialEffects, List<MobEffectInstance> newEffects) {
        List<MobEffectInstance> effects = new ArrayList<>(initialEffects);

        for (MobEffectInstance initialEffect : initialEffects) {
            boolean matched = false;
            for (int i = 0; i < newEffects.size(); i++) {
                MobEffectInstance newEffect = newEffects.get(i);
                if (initialEffect.getEffect().equals(newEffect.getEffect())) {
                    effects.add(combine(initialEffect, newEffect));
                    newEffects.remove(i);
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                effects.add(initialEffect);
            }
        }

        effects.addAll(newEffects);

        return effects;
    }

    private MobEffectInstance combine(MobEffectInstance effect1, MobEffectInstance effect2) {
        // TODO: Do I want to let more things change than duration somehow?
        return new MobEffectInstance(effect1.getEffect(), effect1.getDuration() + effect2.getDuration(),
                effect1.getAmplifier(), effect1.isAmbient(), effect1.isVisible(), effect1.showIcon());
    }
}
