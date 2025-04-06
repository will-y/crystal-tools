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
                        .notNodeRequirement(9, 22)
                    .enchantmentNode(9, fortune(3), desc.fortune(), Enchantments.FORTUNE, 3)
                        .previousTierOrRequirements()
                        .notNodeRequirement(8, 22)
                    .dataComponentNode(10, unbreaking(2), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .previousTierOrRequirements()
                        .nodeRequirement(4)
                .tier()
                    .attributeNode(11, miningSpeed(4), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 0.25F)
                        .nodeRequirement(5)
                        .previousTierOrRequirements()
                    .dataComponentNode(12, durability(4), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(6)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(13, miningSpeed(5), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 0.25F)
                        .nodeRequirement(11)
                    .dataComponentNode(14, durability(5), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(12)
                    .attributeNode(15, reach(2), desc.reach(), List.of(attr(Attributes.BLOCK_INTERACTION_RANGE), attr(Attributes.ENTITY_INTERACTION_RANGE)), 1)
                        .nodeRequirement(7)
                        .previousTierOrRequirements()
                    .dataComponentNode(16, AUTO_PICKUP, desc.autoPickup(), DataComponents.AUTO_PICKUP.getId(), 1)
                        .previousTierOrRequirements()
                .tier()
                    .infiniteDataComponentNode(17, AUTO_REPAIR, desc.autoRepair(), DataComponents.AUTO_REPAIR.getId(), 1)
                        .previousTierOrRequirements()
                    .dataComponentNode(18, MINING_3x3, desc.mining3x3(), DataComponents.HAS_3x3.getId(), 1)
                        .previousTierOrRequirements()
                    .dataComponentNode(19, VEIN_MINING, desc.veinMining(), DataComponents.VEIN_MINER.getId(), 1)
                        .previousTierOrRequirements()
                        .subText(VEIN_MINING_SUBTEXT, "#ABABAB")
                    .dataComponentNode(20, AUTO_SMELTING, desc.autoSmelting(), DataComponents.AUTO_SMELT.getId(), 1)
                        .previousTierOrRequirements()
                .tier()
                    .dataComponentNode(21, TORCH, desc.torch(), DataComponents.TORCH.getId(), 1)
                        .previousTierOrRequirements()
                        .subText(TORCH_SUBTEXT, "#ABABAB")
                    .dataComponentNode(22, MODE_SWITCH, desc.mineMode(), DataComponents.MINE_MODE.getId(), 1)
                        .previousTierOrRequirements()
                    .dataComponentNode(23, unbreaking(3), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .previousTierOrRequirements()
                        .nodeRequirement(10)
                    .attributeNode(24, reach(3), desc.reach(), List.of(attr(Attributes.BLOCK_INTERACTION_RANGE), attr(Attributes.ENTITY_INTERACTION_RANGE)), 1)
                        .nodeRequirement(15)
                        .previousTierOrRequirements()
                .tier()
                    .infiniteAttributeNode(25, miningSpeed(0), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 0.1F)
                        .nodeRequirement(13)
                        .previousTierOrRequirements()
                    .infiniteDataComponentNode(26, durability(0), desc.durability(), DURABILITY, 50)
                        .nodeRequirement(14)
                        .previousTierOrRequirements()
                    .dataComponentNode(27, unbreaking(0), desc.unbreaking(), DURABILITY, 50, 70)
                        .nodeRequirement(23)
                        .previousTierOrRequirements()
                    .infiniteAttributeNode(28, reach(0), desc.reach(), List.of(attr(Attributes.BLOCK_INTERACTION_RANGE), attr(Attributes.ENTITY_INTERACTION_RANGE)), 0.1F)
                        .nodeRequirement(24)
                        .previousTierOrRequirements()
                .tier()
                    .enchantmentNode(29, fortune(5), desc.fortune(), Enchantments.FORTUNE, 5)
                        .nodeRequirement(9, 25, 26, 27, 28)
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
