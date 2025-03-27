package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.utils.constants.SkillTreeDescriptions;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.*;
import static dev.willyelton.crystal_tools.utils.constants.SkillConstants.*;

public class CrystalToolsSkillTrees {
    public static void skillTrees(BootstrapContext<SkillData> context) {
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY,
                ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "crystal_pickaxe")), pickaxe(context));
    }

    private static SkillData pickaxe(BootstrapContext<SkillData> context) {
        SkillTreeDescriptions desc = new SkillTreeDescriptions("Pickaxe");

        return SkillData.builder()
                .tier()
                    .attributeNode(0, miningSpeed(1), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 0.25F)
                    .dataComponentNode(1, durability(1), desc.durability(), DURABILITY, 200)
                .tier()
                    .attributeNode(2, miningSpeed(2), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 0.25F)
                        .nodeRequirement(0)
                    .dataComponentNode(3, durability(2), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(1)
                .dataComponentNode(4, unbreaking(1), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                    .previousTierOrRequirements()
                .tier()
                    .attributeNode(5, miningSpeed(3), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 0.25F)
                        .nodeRequirement(2)
                    .dataComponentNode(6, durability(3), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(3)
                    .attributeNode(7, reach(1), desc.reach(), List.of(attr(Attributes.BLOCK_INTERACTION_RANGE), attr(Attributes.ENTITY_INTERACTION_RANGE)), 1)
                        .previousTierOrRequirements()
                .tier()
                    .enchantmentNode(8, SILK_TOUCH, desc.silkTouch(), Enchantments.SILK_TOUCH, 1)
                        .previousTierOrRequirements()
                        .notNodeRequirement(9, 21)
                    .enchantmentNode(9, fortune(3), desc.fortune(), Enchantments.FORTUNE, 3)
                        .previousTierOrRequirements()
                        .notNodeRequirement(8, 21)
                    .dataComponentNode(10, unbreaking(2), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .previousTierOrRequirements()
                .build();
    }

    private static ResourceLocation attr(Holder<Attribute> attribute) {
        ResourceKey<?> key = attribute.getKey();
        if (key == null) {
            throw new IllegalArgumentException("Invalid attribute " + attribute);
        }

        return key.location();
    }
}
