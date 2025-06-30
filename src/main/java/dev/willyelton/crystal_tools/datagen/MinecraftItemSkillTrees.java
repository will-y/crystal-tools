package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.utils.constants.SkillTreeDescriptions;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.List;

import static dev.willyelton.crystal_tools.CrystalTools.rl;
import static dev.willyelton.crystal_tools.datagen.CrystalToolsItemSkillTrees.attr;
import static dev.willyelton.crystal_tools.utils.constants.SkillConstants.DURABILITY;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.attackDamage;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.attackSpeed;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.durability;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.miningSpeed;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.reach;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.unbreaking;

public class MinecraftItemSkillTrees {
    public static void registerSkillTrees(BootstrapContext<SkillData> context) {
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                rl("simple_pickaxe")), simpleMiningTool("Pickaxe"));
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                rl("simple_axe")), simpleMiningTool("Axe"));
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                rl("simple_hoe")), simpleMiningTool("Hoe"));
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                rl("simple_shovel")), simpleMiningTool("Shovel"));
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                rl("simple_sword")), simpleWeapon("Sword"));
    }

    private static SkillData simpleMiningTool(String name) {
        SkillTreeDescriptions desc = new SkillTreeDescriptions(name);

        return SkillData.builder(EquipmentSlot.MAINHAND)
                .tier()
                    .attributeNode(0, miningSpeed(1), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 2F)
                    .dataComponentNode(1, durability(1), desc.durability(), DURABILITY, 50)
                .tier()
                    .attributeNode(2, miningSpeed(2), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 2F)
                        .nodeRequirement(0)
                    .dataComponentNode(3, durability(2), desc.durability(), DURABILITY, 50)
                        .nodeRequirement(1)
                .tier()
                    .attributeNode(4, reach(1), desc.reach(), List.of(attr(Attributes.BLOCK_INTERACTION_RANGE), attr(Attributes.ENTITY_INTERACTION_RANGE)), 0.5F)
                        .previousTierOrRequirements()
                    .dataComponentNode(5, unbreaking(1), desc.unbreaking(5), DataComponents.UNBREAKING.getId(), 0.05F)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(6, miningSpeed(3), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 2F)
                        .nodeRequirement(2)
                        .previousTierOrRequirements()
                    .dataComponentNode(7, durability(3), desc.durability(), DURABILITY, 50)
                        .nodeRequirement(3)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(8, miningSpeed(4), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 2F)
                        .nodeRequirement(6)
                    .dataComponentNode(9, durability(4), desc.durability(), DURABILITY, 50)
                        .nodeRequirement(7)
                .tier()
                    .attributeNode(10, reach(2), desc.reach(), List.of(attr(Attributes.BLOCK_INTERACTION_RANGE), attr(Attributes.ENTITY_INTERACTION_RANGE)), 0.25F)
                        .nodeRequirement(4)
                        .previousTierOrRequirements()
                    .dataComponentNode(11, unbreaking(2), desc.unbreaking(5), DataComponents.UNBREAKING.getId(), 0.05F)
                        .nodeRequirement(5)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(12, miningSpeed(5), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 2F)
                        .nodeRequirement(8)
                        .previousTierOrRequirements()
                    .dataComponentNode(13, durability(5), desc.durability(), DURABILITY, 50)
                        .nodeRequirement(9)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(14, miningSpeed(5), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 2F)
                        .nodeRequirement(12)
                    .dataComponentNode(15, durability(5), desc.durability(), DURABILITY, 50)
                        .nodeRequirement(13)
                .tier()
                    .attributeNode(16, reach(3), desc.reach(), List.of(attr(Attributes.BLOCK_INTERACTION_RANGE), attr(Attributes.ENTITY_INTERACTION_RANGE)), 0.25F)
                        .nodeRequirement(10)
                        .previousTierOrRequirements()
                    .dataComponentNode(17, unbreaking(3), desc.unbreaking(5), DataComponents.UNBREAKING.getId(), 0.05F)
                        .nodeRequirement(11)
                        .previousTierOrRequirements()
                .build();
    }

    private static SkillData simpleWeapon(String name) {
        SkillTreeDescriptions desc = new SkillTreeDescriptions(name);

        return SkillData.builder(EquipmentSlot.MAINHAND)
                .tier()
                    .attributeNode(0, attackDamage(1), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 0.25F)
                    .attributeNode(1, attackSpeed(1), desc.attackSpeed(), attr(Attributes.ATTACK_SPEED), 0.25F)
                .tier()
                    .attributeNode(2, attackDamage(2), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 0.25F)
                        .nodeRequirement(0)
                    .attributeNode(3, attackSpeed(2), desc.attackSpeed(), attr(Attributes.ATTACK_SPEED), 0.25F)
                        .nodeRequirement(1)
                .tier()
                    .attributeNode(4, reach(1), desc.reach(), List.of(attr(Attributes.BLOCK_INTERACTION_RANGE), attr(Attributes.ENTITY_INTERACTION_RANGE)), 0.5F)
                        .previousTierOrRequirements()
                    .dataComponentNode(5, unbreaking(1), desc.unbreaking(5), DataComponents.UNBREAKING.getId(), 0.05F)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(6, attackDamage(3), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 0.25F)
                        .nodeRequirement(2)
                        .previousTierOrRequirements()
                    .attributeNode(7, attackSpeed(3), desc.attackSpeed(), attr(Attributes.ATTACK_SPEED), 0.25F)
                        .nodeRequirement(3)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(8, attackDamage(4), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 0.25F)
                        .nodeRequirement(6)
                    .attributeNode(9, attackSpeed(4), desc.attackSpeed(), attr(Attributes.ATTACK_SPEED), 0.25F)
                        .nodeRequirement(7)
                .tier()
                    .attributeNode(10, reach(2), desc.reach(), List.of(attr(Attributes.BLOCK_INTERACTION_RANGE), attr(Attributes.ENTITY_INTERACTION_RANGE)), 0.25F)
                        .nodeRequirement(4)
                        .previousTierOrRequirements()
                    .dataComponentNode(11, unbreaking(2), desc.unbreaking(5), DataComponents.UNBREAKING.getId(), 0.05F)
                        .nodeRequirement(5)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(12, attackDamage(5), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 0.25F)
                        .nodeRequirement(8)
                        .previousTierOrRequirements()
                    .attributeNode(13, attackSpeed(5), desc.attackSpeed(), attr(Attributes.ATTACK_SPEED), 0.25F)
                        .nodeRequirement(9)
                        .previousTierOrRequirements()
                .tier()
                    .attributeNode(14, attackDamage(5), desc.attackDamage(), attr(Attributes.ATTACK_DAMAGE), 0.25F)
                        .nodeRequirement(12)
                    .dataComponentNode(15, attackSpeed(5), desc.attackSpeed(), attr(Attributes.ATTACK_SPEED), 0.25F)
                        .nodeRequirement(13)
                .tier()
                    .attributeNode(16, reach(3), desc.reach(), List.of(attr(Attributes.BLOCK_INTERACTION_RANGE), attr(Attributes.ENTITY_INTERACTION_RANGE)), 0.25F)
                        .nodeRequirement(10)
                        .previousTierOrRequirements()
                    .dataComponentNode(17, unbreaking(3), desc.unbreaking(5), DataComponents.UNBREAKING.getId(), 0.05F)
                        .nodeRequirement(11)
                        .previousTierOrRequirements()
                .build();
    }
}
