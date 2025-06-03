package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations;
import dev.willyelton.crystal_tools.utils.constants.SkillTreeDescriptions;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.AUTO_BALANCE;
import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.EXP_BOOST;
import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.FUEL_EFFICIENCY;
import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.FUEL_SLOT_BONUS;
import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.FURNACE_SPEED;
import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.SLOT_BONUS;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.AUTO_OUTPUT;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.AUTO_SPLIT;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.SAVE_FUEL;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.expBoost;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.fuelEfficiency;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.furnaceFuelSlot;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.furnaceSlot;
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
                .tier()
                    .blockNode(3, furnaceSpeed(2), desc.furnaceSpeed(), FURNACE_SPEED, 1)
                        .nodeRequirement(0)
                    .blockNode(4, fuelEfficiency(2), desc.fuelEfficiency(), FUEL_EFFICIENCY, 1)
                        .nodeRequirement(1)
                    .blockNode(5, expBoost(2), desc.expBoost(), EXP_BOOST, 1)
                        .nodeRequirement(2)
                .tier()
                    .blockNode(6, furnaceSlot(1), desc.furnaceSlot(), SLOT_BONUS, 1)
                        .previousTierOrRequirements()
                    .blockNode(7, furnaceFuelSlot(1), desc.furnaceFuelSlot(), FUEL_SLOT_BONUS, 1)
                        .previousTierOrRequirements()
                .tier()
                    .blockNode(8, furnaceSpeed(3), desc.furnaceSpeed(), FURNACE_SPEED, 1)
                        .nodeRequirement(3)
                        .previousTierOrRequirements()
                    .blockNode(9, fuelEfficiency(3), desc.fuelEfficiency(), FUEL_EFFICIENCY, 1)
                        .nodeRequirement(4)
                        .previousTierOrRequirements()
                    .blockNode(10, expBoost(3), desc.expBoost(), EXP_BOOST, 1)
                        .nodeRequirement(5)
                        .previousTierOrRequirements()
                .tier()
                    .blockNode(11, furnaceSlot(2), desc.furnaceFuelSlot(), SLOT_BONUS, 1)
                        .nodeRequirement(6)
                        .previousTierOrRequirements()
                    .blockNode(12, AUTO_SPLIT, desc.autoSplit(), AUTO_BALANCE, 1)
                        .previousTierOrRequirements()
                .tier()
                    .blockNode(13, furnaceSpeed(4), desc.furnaceSpeed(), FURNACE_SPEED, 1)
                        .nodeRequirement(8)
                        .previousTierOrRequirements()
                    .blockNode(14, fuelEfficiency(4), desc.fuelEfficiency(), FUEL_EFFICIENCY, 1)
                        .nodeRequirement(9)
                        .previousTierOrRequirements()
                    .blockNode(15, expBoost(4), desc.expBoost(), EXP_BOOST, 1)
                        .nodeRequirement(10)
                        .previousTierOrRequirements()
                .tier()
                    .blockNode(16, furnaceSlot(3), desc.furnaceSlot(), SLOT_BONUS, 1)
                        .nodeRequirement(11)
                        .previousTierOrRequirements()
                    .blockNode(17, furnaceFuelSlot(2), desc.furnaceFuelSlot(), FUEL_SLOT_BONUS, 1)
                        .nodeRequirement(7)
                        .previousTierOrRequirements()
                .tier()
                    .blockNode(18, furnaceSpeed(5), desc.furnaceSpeed(), FURNACE_SPEED, 1)
                        .nodeRequirement(13)
                        .previousTierOrRequirements()
                    .blockNode(19, fuelEfficiency(5), desc.fuelEfficiency(), FUEL_EFFICIENCY, 1)
                        .nodeRequirement(14)
                        .previousTierOrRequirements()
                    .blockNode(20, expBoost(5), desc.expBoost(), EXP_BOOST, 1)
                        .nodeRequirement(15)
                        .previousTierOrRequirements()
                .tier()
                    .blockNode(21, furnaceSlot(4), desc.furnaceSlot(), SLOT_BONUS, 1)
                        .nodeRequirement(16)
                        .previousTierOrRequirements()
                    .blockNode(22, AUTO_OUTPUT, desc.autoOutput(), BlockEntityResourceLocations.AUTO_OUTPUT, 1)
                        .previousTierOrRequirements()
                    .blockNode(23, SAVE_FUEL, desc.saveFuel(), BlockEntityResourceLocations.SAVE_FUEL, 1)
                        .previousTierOrRequirements()
                .tier()
                    .blockNode(24, furnaceSpeed(0), desc.furnaceSpeed(), FURNACE_SPEED, 0.25F, 0)
                        .nodeRequirement(18)
                        .previousTierOrRequirements()
                    .blockNode(25, fuelEfficiency(0), desc.fuelEfficiency(), FUEL_EFFICIENCY, 0.25F, 0)
                        .nodeRequirement(19)
                        .previousTierOrRequirements()
                    .blockNode(26, expBoost(0), desc.expBoost(), EXP_BOOST, 0.25F, 0)
                        .nodeRequirement(20)
                        .previousTierOrRequirements()
                .build();
    }

}
