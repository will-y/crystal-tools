package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.utils.constants.SkillTreeDescriptions;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;

import static dev.willyelton.crystal_tools.datagen.CrystalToolsItemSkillTrees.attr;
import static dev.willyelton.crystal_tools.utils.constants.SkillConstants.DURABILITY;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.durability;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.miningSpeed;

public class MinecraftItemSkillTrees {
    public static void registerSkillTrees(BootstrapContext<SkillData> context) {
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS,
                ResourceLocation.withDefaultNamespace("netherite_pickaxe")), simpleMiningTool("Pickaxe"));
    }

    private static SkillData simpleMiningTool(String name) {
        SkillTreeDescriptions desc = new SkillTreeDescriptions(name);

        return SkillData.builder(EquipmentSlot.MAINHAND)
                .tier()
                    .attributeNode(0, miningSpeed(1), desc.miningSpeed(), attr(Attributes.MINING_EFFICIENCY), 6F)
                    .dataComponentNode(1, durability(1), desc.durability(), DURABILITY, 200)
                .build();
    }
}
