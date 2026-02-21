package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.utils.constants.EntitySkills;
import dev.willyelton.crystal_tools.utils.constants.SkillTreeDescriptions;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;

import static dev.willyelton.crystal_tools.datagen.CrystalToolsItemSkillTrees.attr;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.*;

public class CrystalToolsEntitySkillTrees {
    public static void register(BootstrapContext<SkillData> context) {
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ENTITIES,
                EntityType.WOLF.builtInRegistryHolder().key().identifier()), wolf());
    }

    private static SkillData wolf() {
        SkillTreeDescriptions desc = new SkillTreeDescriptions("wolf");
        return SkillData.builder(null)
                .tier()
                    .entityAttributeNode(0, entityHealth(1), desc.entityHealth(), attr(Attributes.MAX_HEALTH), 1)
                    .entityAttributeNode(1, entityDamage(1), desc.entityDamage(), attr(Attributes.ATTACK_DAMAGE), 1)
                    .entityAttributeNode(2, entityArmor(1), desc.entityArmor(), attr(Attributes.ARMOR), 1)
                    .entityAttributeNode(3, entitySpeed(1), desc.entitySpeed(), attr(Attributes.MOVEMENT_SPEED), 0.025F)
                .tier()
                    .entityAttributeNode(4, entityHealth(2), desc.entityHealth(), attr(Attributes.MAX_HEALTH), 1)
                        .nodeRequirement(0)
                    .entityAttributeNode(5, entityDamage(2), desc.entityDamage(), attr(Attributes.ATTACK_DAMAGE), 1)
                        .nodeRequirement(1)
                    .entityAttributeNode(6, entityArmor(2), desc.entityArmor(), attr(Attributes.ARMOR), 1)
                        .nodeRequirement(2)
                    .entityAttributeNode(7, entitySpeed(2), desc.entitySpeed(), attr(Attributes.MOVEMENT_SPEED), 0.025F)
                        .nodeRequirement(3)
                .tier()
                    .entityDataNode(8, lifesteal(1), desc.entityLifesteal(), EntitySkills.LIFESTEAL, 0.25F)
                        .previousTierOrRequirements()
                .tier()
                    .entityAttributeNode(9, entityHealth(3), desc.entityHealth(), attr(Attributes.MAX_HEALTH), 1)
                        .nodeRequirement(4)
                        .previousTierOrRequirements()
                    .entityAttributeNode(10, entityDamage(3), desc.entityDamage(), attr(Attributes.ATTACK_DAMAGE), 1)
                        .nodeRequirement(5)
                        .previousTierOrRequirements()
                    .entityAttributeNode(11, entityArmor(3), desc.entityArmor(), attr(Attributes.ARMOR), 1)
                        .nodeRequirement(6)
                        .previousTierOrRequirements()
                    .entityAttributeNode(12, entitySpeed(3), desc.entitySpeed(), attr(Attributes.MOVEMENT_SPEED), 0.025F)
                        .nodeRequirement(7)
                        .previousTierOrRequirements()
                .tier()
                    .entityAttributeNode(13, entityHealth(4), desc.entityHealth(), attr(Attributes.MAX_HEALTH), 1)
                        .nodeRequirement(9)
                    .entityAttributeNode(14, entityDamage(4), desc.entityDamage(), attr(Attributes.ATTACK_DAMAGE), 1)
                        .nodeRequirement(10)
                    .entityAttributeNode(15, entityArmor(4), desc.entityArmor(), attr(Attributes.ARMOR), 1)
                        .nodeRequirement(11)
                    .entityAttributeNode(16, entitySpeed(4), desc.entitySpeed(), attr(Attributes.MOVEMENT_SPEED), 0.025F)
                        .nodeRequirement(12)
                .tier()
                    .entityDataNode(17, lifesteal(2), desc.entityLifesteal(), EntitySkills.LIFESTEAL, 0.25F)
                        .previousTierOrRequirements()
                    .entityDataNode(18, CRATE_TRAINING, desc.crateTraining(), EntitySkills.CRATE_TRAINING, 1)
                        .previousTierOrRequirements()
                .tier()
                    .entityAttributeNode(19, entityHealth(5), desc.entityHealth(), attr(Attributes.MAX_HEALTH), 1)
                        .nodeRequirement(13)
                        .previousTierOrRequirements()
                    .entityAttributeNode(20, entityDamage(5), desc.entityDamage(), attr(Attributes.ATTACK_DAMAGE), 1)
                        .nodeRequirement(14)
                        .previousTierOrRequirements()
                    .entityAttributeNode(21, entityArmor(5), desc.entityArmor(), attr(Attributes.ARMOR), 1)
                        .nodeRequirement(15)
                        .previousTierOrRequirements()
                    .entityAttributeNode(22, entitySpeed(5), desc.entitySpeed(), attr(Attributes.MOVEMENT_SPEED), 0.025F)
                        .nodeRequirement(16)
                        .previousTierOrRequirements()
                .tier()
                    .entityDataNode(23, IMMORTALITY, desc.immortality(), EntitySkills.IMMORTALITY, 1)
                        .previousTierAndRequirements()
                        .itemRequirement(ModRegistration.CRYSTAL_DOG_CAGE.get())
                .build();
    }
}
