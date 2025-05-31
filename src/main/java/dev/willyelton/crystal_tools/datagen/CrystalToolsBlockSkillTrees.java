package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.utils.constants.SkillTreeDescriptions;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.EXP_BOOST;
import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.FUEL_EFFICIENCY;
import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.FURNACE_SPEED;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.expBoost;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.fuelEfficiency;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.furnaceSpeed;

public class CrystalToolsBlockSkillTrees {
    private final BootstrapContext<SkillData> context;

    public static void register(BootstrapContext<SkillData> context) {
        CrystalToolsBlockSkillTrees skillTrees = new CrystalToolsBlockSkillTrees(context);
        skillTrees.registerSkillTrees();
    }

    public CrystalToolsBlockSkillTrees(BootstrapContext<SkillData> context) {
        this.context = context;
    }

    public void registerSkillTrees() {
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_BLOCKS,
                Registration.CRYSTAL_FURNACE.getId()), furnace());
    }

    private SkillData furnace() {
        SkillTreeDescriptions desc = new SkillTreeDescriptions("Furnace");

        return SkillData.builder(null)
                .tier()
                    .blockNode(0, furnaceSpeed(1), desc.furnaceSpeed(), FURNACE_SPEED, 1)
                    .blockNode(1, fuelEfficiency(1), desc.fuelEfficiency(), FUEL_EFFICIENCY, 1)
                    .blockNode(2, expBoost(1), desc.expBoost(), EXP_BOOST, 1)
                .build();
    }

}
