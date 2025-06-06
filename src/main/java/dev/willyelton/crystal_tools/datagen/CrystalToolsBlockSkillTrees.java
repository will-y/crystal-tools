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
import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.FE_CAPACITY;
import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.FE_GENERATION;
import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.FORTUNE;
import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.FUEL_EFFICIENCY;
import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.FUEL_SLOT_BONUS;
import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.FURNACE_SPEED;
import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.MINING_SPEED;
import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.SLOT_BONUS;
import static dev.willyelton.crystal_tools.utils.constants.BlockEntityResourceLocations.TRASH_FILTER;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.AUTO_OUTPUT;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.AUTO_SPLIT;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.CHUNK_LOADING;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.FOOD_GENERATOR;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.GEM_GENERATOR;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.METAL_GENERATOR;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.REDSTONE_CONTROL;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.SAVE_FUEL;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.SILK_TOUCH;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.expBoost;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.feCapacity;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.feGeneration;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.fortune;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.fuelEfficiency;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.furnaceFuelSlot;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.furnaceSlot;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.furnaceSpeed;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.miningSpeed;
import static dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles.trashFilter;

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
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_BLOCKS,
                Registration.CRYSTAL_QUARRY.getId()), quarry());
        context.register(ResourceKey.create(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_BLOCKS,
                Registration.CRYSTAL_GENERATOR.getId()), generator());
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

    private SkillData quarry() {
        SkillTreeDescriptions desc = new SkillTreeDescriptions("Quarry");

        return SkillData.builder(null)
                .tier()
                    .blockNode(0, miningSpeed(1), desc.miningSpeed(), MINING_SPEED, 1)
                    .energyCost()
                .tier()
                    .blockNode(1, miningSpeed(2), desc.miningSpeed(), MINING_SPEED, 1)
                        .energyCost()
                        .nodeRequirement(0)
                    .blockNode(2, REDSTONE_CONTROL, desc.redstoneControl(), BlockEntityResourceLocations.REDSTONE_CONTROL, 1)
                        .nodeRequirement(0)
                    .blockNode(3, trashFilter(1), desc.trashFilter(), TRASH_FILTER, 1)
                        .nodeRequirement(0)
                .tier()
                    .blockNode(4, miningSpeed(3), desc.miningSpeed(), MINING_SPEED, 1)
                        .energyCost()
                        .nodeRequirement(1)
                .tier()
                    .blockNode(5, miningSpeed(4), desc.miningSpeed(), MINING_SPEED, 1)
                        .energyCost()
                        .nodeRequirement(4)
                    .blockNode(6, AUTO_OUTPUT, desc.autoOutput(), BlockEntityResourceLocations.AUTO_OUTPUT, 1)
                        .nodeRequirement(4)
                    .blockNode(7, trashFilter(2), desc.trashFilter(), TRASH_FILTER, 1)
                        .nodeRequirement(4)
                .tier()
                    .blockNode(8, miningSpeed(5), desc.miningSpeed(), MINING_SPEED, 1)
                        .energyCost()
                        .nodeRequirement(5)
                .tier()
                    .blockNode(9, miningSpeed(6), desc.miningSpeed(), MINING_SPEED, 1)
                        .energyCost()
                        .nodeRequirement(8)
                    .blockNode(10, SILK_TOUCH, desc.quarrySilkTouch(), BlockEntityResourceLocations.SILK_TOUCH, 1)
                        .energyCost()
                        .nodeRequirement(8)
                    .blockNode(11, fortune(3), desc.quarryFortune(), FORTUNE, 3)
                        .energyCost()
                        .nodeRequirement(8)
                    .blockNode(12, trashFilter(3), desc.trashFilter(), TRASH_FILTER, 1)
                        .nodeRequirement(8)
                .tier()
                    .blockNode(13, miningSpeed(7), desc.miningSpeed(), MINING_SPEED, 1)
                        .energyCost()
                        .nodeRequirement(9)
                .tier()
                    .blockNode(14, miningSpeed(8), desc.miningSpeed(), MINING_SPEED, 1)
                        .energyCost()
                        .nodeRequirement(13)
                    .blockNode(15, CHUNK_LOADING, desc.chunkLoading(), BlockEntityResourceLocations.CHUNK_LOADING, 1)
                        .subText("NOTE: Will only tick the chunk that the quarry block is in.\nMake sure to keep your power generation in the same chunk.", "#ABABAB")
                        .nodeRequirement(13)
                .tier()
                    .blockNode(16, miningSpeed(0), desc.miningSpeed(), MINING_SPEED, 0.05F, 0)
                        .energyCost()
                        .nodeRequirement(14)
                .build();
    }

    private SkillData generator() {
        SkillTreeDescriptions desc = new SkillTreeDescriptions("Generator");

        return SkillData.builder(null)
                .tier()
                    .blockNode(0, feGeneration(1), desc.feGeneration(), FE_GENERATION, 1)
                    .blockNode(1, fuelEfficiency(1), desc.fuelEfficiency(), FUEL_EFFICIENCY, 0.1F)
                    .blockNode(2, feCapacity(1), desc.feCapacity(), FE_CAPACITY, 1)
                .tier()
                    .blockNode(3, feGeneration(2), desc.feGeneration(), FE_GENERATION, 1)
                        .nodeRequirement(0)
                    .blockNode(4, fuelEfficiency(2), desc.fuelEfficiency(), FUEL_EFFICIENCY, 0.1F)
                        .nodeRequirement(1)
                    .blockNode(5, feCapacity(2), desc.feCapacity(), FE_CAPACITY, 1)
                        .nodeRequirement(2)
                .tier()
                    .blockNode(6, REDSTONE_CONTROL, desc.redstoneControl(), BlockEntityResourceLocations.REDSTONE_CONTROL, 1)
                        .previousTierOrRequirements()
                    .blockNode(7, METAL_GENERATOR, desc.metalGenerator(), BlockEntityResourceLocations.METAL_GENERATOR, 1)
                        .previousTierOrRequirements()
                .tier()
                    .blockNode(8, feGeneration(3), desc.feGeneration(), FE_GENERATION, 1)
                        .nodeRequirement(3)
                        .previousTierOrRequirements()
                    .blockNode(9, fuelEfficiency(3), desc.fuelEfficiency(), FUEL_EFFICIENCY, 0.1F)
                        .nodeRequirement(4)
                        .previousTierOrRequirements()
                    .blockNode(10, feCapacity(3), desc.feCapacity(), FE_CAPACITY, 1)
                        .nodeRequirement(5)
                        .previousTierOrRequirements()
                .tier()
                    .blockNode(11, feGeneration(4), desc.feGeneration(), FE_GENERATION, 1)
                        .nodeRequirement(8)
                    .blockNode(12, fuelEfficiency(4), desc.fuelEfficiency(), FUEL_EFFICIENCY, 0.1F)
                        .nodeRequirement(9)
                    .blockNode(13, feCapacity(4), desc.feCapacity(), FE_CAPACITY, 1)
                        .nodeRequirement(10)
                .tier()
                    .blockNode(14, SAVE_FUEL, desc.saveFuel(), BlockEntityResourceLocations.SAVE_FUEL, 1)
                        .previousTierOrRequirements()
                    .blockNode(15, FOOD_GENERATOR, desc.foodGenerator(), BlockEntityResourceLocations.FOOD_GENERATOR, 1)
                        .previousTierOrRequirements()
                .tier()
                    .blockNode(16, feGeneration(5), desc.feGeneration(), FE_GENERATION, 1)
                        .nodeRequirement(11)
                        .previousTierOrRequirements()
                    .blockNode(17, fuelEfficiency(5), desc.fuelEfficiency(), FUEL_EFFICIENCY, 0.1F)
                        .nodeRequirement(12)
                        .previousTierOrRequirements()
                    .blockNode(18, feCapacity(5), desc.feCapacity(), FE_CAPACITY, 1)
                        .nodeRequirement(13)
                        .previousTierOrRequirements()
                .tier()
                    .blockNode(19, feGeneration(6), desc.feGeneration(), FE_GENERATION, 1)
                        .nodeRequirement(16)
                    .blockNode(20, fuelEfficiency(6), desc.fuelEfficiency(), FUEL_EFFICIENCY, 0.1F)
                        .nodeRequirement(17)
                    .blockNode(21, feCapacity(6), desc.feCapacity(), FE_CAPACITY, 1)
                        .nodeRequirement(18)
                .tier()
                    .blockNode(22, GEM_GENERATOR, desc.gemGenerator(), BlockEntityResourceLocations.GEM_GENERATOR, 1)
                        .previousTierAndRequirements()
                .tier()
                    .blockNode(23, feGeneration(0), desc.feGeneration(), FE_GENERATION, 0.1F, 0)
                        .nodeRequirement(22)
                    .blockNode(24, fuelEfficiency(0), desc.fuelEfficiency(), FUEL_EFFICIENCY, 0.01F, 0)
                        .nodeRequirement(22)
                    .blockNode(25, feCapacity(0), desc.feCapacity(), FE_CAPACITY, 0.5F, 0)
                        .nodeRequirement(22)
                .build();
    }
}
