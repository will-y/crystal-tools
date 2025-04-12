package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.utils.constants.SkillTreeDescriptions;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.*;
import static dev.willyelton.crystal_tools.utils.constants.SkillConstants.*;

public class CrystalToolsSkillTrees {
    public static void skillTrees(BootstrapContext<SkillData> context) {
        // Basic Mining Tools
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY,
                ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "crystal_pickaxe")), basicMiningTool("Pickaxe"));
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY,
                ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "crystal_hoe")), basicMiningTool("Hoe"));
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY,
                ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "crystal_shovel")), basicMiningTool("Shovel"));
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY,
                ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "crystal_axe")), basicMiningTool("Axe"));

        // Food
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY,
                ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "crystal_apple")), food("Apple"));
    }

    private static SkillData basicMiningTool(String name) {
        SkillTreeDescriptions desc = new SkillTreeDescriptions(name);

        boolean axe = name.equals("Axe");

        return SkillData.builder()
                .tier()
                    .attributeNode(0, miningSpeed(1), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 6F)
                    .dataComponentNode(1, durability(1), desc.durability(), DURABILITY, 200)
                .tier()
                    .attributeNode(2, miningSpeed(2), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 6F)
                        .nodeRequirement(0)
                    .dataComponentNode(3, durability(2), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(1)
                    .dataComponentNode(4, unbreaking(1), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(5, miningSpeed(3), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 6F)
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
                    .optional(axe)
                        .dataComponentNode(30, LEAF_MINER, desc.leafMiner(), DataComponents.LEAF_MINE.getId(), 1)
                            .previousTierOrRequirements()
                    .endOptional()
                .tier()
                    .attributeNode(11, miningSpeed(4), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 6F)
                        .nodeRequirement(5)
                        .previousTierOrRequirements()
                    .dataComponentNode(12, durability(4), desc.durability(), DURABILITY, 200)
                        .nodeRequirement(6)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(13, miningSpeed(5), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 6F)
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
                    .dataComponentNode(19, !axe ? VEIN_MINING : AXE_VEIN_MINING, desc.veinMining(axe), DataComponents.VEIN_MINER.getId(), 1)
                        .previousTierOrRequirements()
                        .subText(VEIN_MINING_SUBTEXT, "#ABABAB")
                    .dataComponentNode(20, AUTO_SMELTING, desc.autoSmelting(), DataComponents.AUTO_SMELT.getId(), 1)
                        .previousTierOrRequirements()
                .tier()
                    .optional(name.equals("Pickaxe"))
                        .dataComponentNode(21, TORCH, desc.torch(), DataComponents.TORCH.getId(), 1)
                            .previousTierOrRequirements()
                            .subText(TORCH_SUBTEXT, "#ABABAB")
                    .endOptional()
                    .dataComponentNode(22, MODE_SWITCH, desc.mineMode(), DataComponents.MINE_MODE.getId(), 1)
                        .previousTierOrRequirements()
                    .dataComponentNode(23, unbreaking(3), desc.unbreaking(), DataComponents.UNBREAKING.getId(), 0.1F)
                        .previousTierOrRequirements()
                        .nodeRequirement(10)
                    .attributeNode(24, reach(3), desc.reach(), List.of(attr(Attributes.BLOCK_INTERACTION_RANGE), attr(Attributes.ENTITY_INTERACTION_RANGE)), 1)
                        .nodeRequirement(15)
                        .previousTierOrRequirements()
                .tier()
                    .infiniteAttributeNode(25, miningSpeed(0), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 0.2F)
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

    private static SkillData food(String name) {
        SkillTreeDescriptions desc = new SkillTreeDescriptions(name);

        return SkillData.builder()
                .tier()
                    .nutrition(0, 1, 2, desc.nutrition())
                    .saturation(1, 1, 0.4F, desc.saturation())
                    .dataComponentNode(2, eatSpeed(1), desc.eatSpeed(), DataComponents.EAT_SPEED_BONUS.getId(), 6)
                    .dataComponentNode(3, durability(1), desc.durability(), DURABILITY, 25)
                .tier()
                    .nutrition(4, 2, 2, desc.nutrition())
                        .nodeRequirement(0)
                    .saturation(5, 2, 0.4F, desc.saturation())
                        .nodeRequirement(1)
                    .dataComponentNode(6, eatSpeed(2), desc.eatSpeed(), DataComponents.EAT_SPEED_BONUS.getId(), 6)
                        .nodeRequirement(2)
                    .dataComponentNode(7, durability(2), desc.durability(), DURABILITY, 25)
                        .nodeRequirement(3)
                .tier()
                    .infiniteDataComponentNode(8, AUTO_REPAIR, desc.autoRepair(), DataComponents.AUTO_REPAIR.getId(), 1)
                        .previousTierOrRequirements()
                    .alwaysEat(9, desc.alwaysEat())
                        .previousTierOrRequirements()
                .tier()
                    .nutrition(10, 3, 2, desc.nutrition())
                        .nodeRequirement(4)
                    .saturation(11, 3, 0.4F, desc.saturation())
                        .nodeRequirement(5)
                    .dataComponentNode(12, eatSpeed(3), desc.eatSpeed(), DataComponents.EAT_SPEED_BONUS.getId(), 6)
                        .nodeRequirement(6)
                    .dataComponentNode(13, durability(3), desc.durability(), DURABILITY, 25)
                        .nodeRequirement(7)
                .tier()
                    .nutrition(14, 4, 2, desc.nutrition())
                        .nodeRequirement(10)
                    .saturation(15, 4, 0.4F, desc.saturation())
                        .nodeRequirement(11)
                    .dataComponentNode(16, eatSpeed(4), desc.eatSpeed(), DataComponents.EAT_SPEED_BONUS.getId(), 6)
                        .nodeRequirement(12)
                    .dataComponentNode(17, durability(4), desc.durability(), DURABILITY, 25)
                        .nodeRequirement(13)
                .tier()
                    .nutrition(18, 0, 1, desc.nutrition(), 0)
                        .nodeRequirement(14)
                    .saturation(19, 0, 0.1F, desc.saturation(), 0)
                        .nodeRequirement(15)
                    .dataComponentNode(20, eatSpeed(0), desc.eatSpeed(), DataComponents.EAT_SPEED_BONUS.getId(), 1, 7)
                        .nodeRequirement(16)
                    .infiniteDataComponentNode(21, durability(0), desc.durability(), DURABILITY, 10)
                        .nodeRequirement(17)
                .tier()
                    .effect(22, desc, new MobEffectInstance(MobEffects.SPEED, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.SUGAR, Items.GLOWSTONE_DUST)
                    .effect(23, desc, new MobEffectInstance(MobEffects.HASTE, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.GOLD_INGOT, Items.GLOWSTONE_DUST)
                    .effect(24, desc, new MobEffectInstance(MobEffects.STRENGTH, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.BLAZE_POWDER, Items.GLOWSTONE_DUST)
                    .effect(25, desc, new MobEffectInstance(MobEffects.JUMP_BOOST, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.RABBIT_FOOT, Items.GLOWSTONE_DUST)
                    .effect(26, desc, new MobEffectInstance(MobEffects.REGENERATION, 10, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.GHAST_TEAR, Items.GLOWSTONE_DUST)
                    .effect(27, desc, new MobEffectInstance(MobEffects.RESISTANCE, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.IRON_INGOT, Items.GLOWSTONE_DUST)
                    .effect(28, desc, new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.MAGMA_CREAM)
                    .effect(29, desc, new MobEffectInstance(MobEffects.WATER_BREATHING, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.PUFFERFISH)
                    .effect(30, desc, new MobEffectInstance(MobEffects.INVISIBILITY, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.GOLDEN_CARROT, Items.FERMENTED_SPIDER_EYE)
                    .effect(31, desc, new MobEffectInstance(MobEffects.NIGHT_VISION, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.GOLDEN_CARROT)
                    .effect(32, desc, new MobEffectInstance(MobEffects.ABSORPTION, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.GLISTERING_MELON_SLICE, Items.GLOWSTONE_DUST)
                    .effect(33, desc, new MobEffectInstance(MobEffects.SATURATION, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.COOKED_BEEF, Items.GLOWSTONE_DUST)
                    .effect(34, desc, new MobEffectInstance(MobEffects.SLOW_FALLING, 20, 1))
                        .previousTierOrRequirements()
                        .itemRequirement(Items.NETHER_WART, Items.PHANTOM_MEMBRANE)
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
