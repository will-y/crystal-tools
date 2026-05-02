package dev.willyelton.crystal.tools.utils.constants;

import net.minecraft.resources.Identifier;

import static dev.willyelton.crystal.tools.CrystalTools.rl;

// These will hopefully get replaced with datacomponents soon
public class BlockEntityResourceLocations {
    // Furnace
    public static final Identifier FURNACE_SPEED = rl("furnace_speed");
    public static final Identifier FUEL_EFFICIENCY = rl("fuel_efficiency");
    public static final Identifier SLOT_BONUS = rl("slot_bonus");
    public static final Identifier FUEL_SLOT_BONUS = rl("fuel_slot_bonus");
    public static final Identifier AUTO_BALANCE = rl("auto_balance");
    public static final Identifier EXP_BOOST = rl("exp_boost");
    public static final Identifier SAVE_FUEL = rl("save_fuel");

    // Quarry
    public static final Identifier MINING_SPEED = rl("mining_speed");
    public static final Identifier REDSTONE_CONTROL = rl("redstone_control");
    public static final Identifier TRASH_FILTER = rl("trash_filter");
    public static final Identifier SILK_TOUCH = rl("silk_touch");
    public static final Identifier FORTUNE = rl("fortune");

    // Generator
    public static final Identifier FE_GENERATION = rl("fe_generation");
    public static final Identifier FE_CAPACITY = rl("fe_capacity");
    public static final Identifier METAL_GENERATOR = rl("metal_generator");
    public static final Identifier FOOD_GENERATOR = rl("food_generator");
    public static final Identifier GEM_GENERATOR = rl("gem_generator");
}
