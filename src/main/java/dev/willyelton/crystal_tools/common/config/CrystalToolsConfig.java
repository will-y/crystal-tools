package dev.willyelton.crystal_tools.common.config;

import dev.willyelton.crystal_tools.common.inventory.CrystalBackpackInventory;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CrystalToolsConfig {
    public static final ModConfigSpec COMMON_CONFIG;

    // Config things, basic tool things
    public static ModConfigSpec.IntValue BASE_EXPERIENCE_CAP;
    public static ModConfigSpec.IntValue MAX_EXP;
    public static ModConfigSpec.DoubleValue EXPERIENCE_MULTIPLIER;

    // Exp Boosts
    public static ModConfigSpec.DoubleValue FURNACE_EXPERIENCE_BOOST;
    public static ModConfigSpec.IntValue FISHING_ROD_EXP;

    // Repair
    public static ModConfigSpec.IntValue TOOL_REPAIR_COOLDOWN;
    public static ModConfigSpec.BooleanValue REPAIR_IN_HAND;
    public static ModConfigSpec.DoubleValue ROCKET_REPAIR_MODIFIER;
    public static ModConfigSpec.DoubleValue APPLE_REPAIR_MODIFIER;

    // Vein Mining
    public static ModConfigSpec.IntValue VEIN_MINER_DEFAULT_RANGE;
    public static ModConfigSpec.IntValue AXE_VEIN_MINER_DEFAULT_RANGE;

    // Misc
    public static ModConfigSpec.BooleanValue ENABLE_ITEM_REQUIREMENTS;
    public static ModConfigSpec.BooleanValue REQUIRE_CRYSTAL_FOR_RESET;

    public static ModConfigSpec.BooleanValue ENCHANT_TOOLS;
    public static ModConfigSpec.IntValue EXPERIENCE_LEVELING_SCALING;
    public static ModConfigSpec.IntValue EXPERIENCE_PER_SKILL_LEVEL;

    // Furnace
    public static ModConfigSpec.IntValue FUEL_EFFICIENCY_ADDED_TICKS;
    public static ModConfigSpec.IntValue SPEED_UPGRADE_SUBTRACT_TICKS;
    public static ModConfigSpec.DoubleValue EXPERIENCE_BOOST_PERCENTAGE;

    // Generator
    public static ModConfigSpec.IntValue BASE_FE_GENERATION;
    public static ModConfigSpec.IntValue BASE_FE_STORAGE;
    public static ModConfigSpec.IntValue BASE_FE_TRANSFER;
    public static ModConfigSpec.IntValue FE_GENERATION_PER_LEVEL;
    public static ModConfigSpec.IntValue FE_STORAGE_PER_LEVEL;
    public static ModConfigSpec.DoubleValue FOOD_BURN_TIME_MULTIPLIER;
    public static ModConfigSpec.DoubleValue SKILL_POINTS_PER_BURN_TIME;
    public static ModConfigSpec.IntValue GENERATOR_BASE_EXPERIENCE_CAP;

    // Backpack
    public static ModConfigSpec.EnumValue<CrystalBackpackInventory.SortType> BACKPACK_SORT_TYPE;
    public static ModConfigSpec.IntValue MAX_COMPRESSION_SLOT_ROWS;

    // Trident
    public static ModConfigSpec.BooleanValue ALWAYS_CHANNEL;

    // Quarry
    public static ModConfigSpec.IntValue QUARRY_BASE_ENERGY_COST;
    public static ModConfigSpec.IntValue QUARRY_SPEED_COST_INCREASE;
    public static ModConfigSpec.IntValue QUARRY_SILK_TOUCH_COST_INCREASE;
    public static ModConfigSpec.IntValue QUARRY_FORTUNE_COST_INCREASE;
    public static ModConfigSpec.DoubleValue QUARRY_INITIAL_POINT_MULTIPLIER;
    public static ModConfigSpec.IntValue QUARRY_BASE_EXPERIENCE_CAP;
    public static ModConfigSpec.DoubleValue QUARRY_SPEED_UPGRADE_MULTIPLIER;
    public static ModConfigSpec.IntValue QUARRY_MAX_SIZE;

    // Magnet
    public static ModConfigSpec.DoubleValue MAGNET_BASE_RANGE;

    static {
        ModConfigSpec.Builder configBuilder = new ModConfigSpec.Builder();
        setupConfig(configBuilder);
        COMMON_CONFIG = configBuilder.build();
    }

    private static void setupConfig(ModConfigSpec.Builder builder) {
        // EXP
        builder.comment("Change tool experience settings").push("experience");
        BASE_EXPERIENCE_CAP = builder.comment("Starting EXP Value for Tools and Armor")
                .defineInRange("base_experience_cap", 75, 1, 10000);
        MAX_EXP = builder.comment("Max exp that can be required to get to the next duration")
                .defineInRange("max_exp", 1000, 1, 100000);
        EXPERIENCE_MULTIPLIER = builder.comment("Multiplier for the experience to the next duration")
                .defineInRange("experience_multiplier", 1.1D, 1D, 5);
        FURNACE_EXPERIENCE_BOOST = builder.comment("Multiplies how much experience Furnaces get.")
                .defineInRange("furnace_experience_boost", 1D, 1D, 1000D);
        FISHING_ROD_EXP = builder.comment("Determines how much experience you get for fish caught")
                .defineInRange("fishing_rod_exp", 10, 1, 1000);
        EXPERIENCE_LEVELING_SCALING = builder.comment("Number of levels in a tool before the experience duration costs increases. Set to 0 to disable scaling")
                .defineInRange("experience_leveling_scaling", 10, 0, 100);
        EXPERIENCE_PER_SKILL_LEVEL = builder.comment("Determines the number of experience levels you need to gain a duration on a tool. Set to 0 to disable")
                .defineInRange("experience_per_skill_level", 10, 0, 100);
        builder.pop();

        // Tool Settings
        builder.comment("Configure different tool options").push("tool");
        TOOL_REPAIR_COOLDOWN = builder.comment("Determines the cooldown between tool auto repairs")
                .defineInRange("tool_repair_cooldown", 300, 1, 10000);
        REPAIR_IN_HAND = builder.comment("If true, tools will auto repair while you are holding them")
                .define("repair_in_hand", false);
        ROCKET_REPAIR_MODIFIER = builder.comment("Increases the repair cooldown for the rocket")
                .defineInRange("rocket_repair_modifier", 10D, 1D, 10000D);
        APPLE_REPAIR_MODIFIER = builder.comment("Increases the repair cooldown for the apple")
                .defineInRange("apple_repair_modifier", 50D, 1D, 10000D);
        VEIN_MINER_DEFAULT_RANGE = builder.comment("Determines the range of the vein miner on the shovel, pickaxe, and AIOT. It will mine blocks up to this range away from the ore broken.")
                .defineInRange("vein_miner_range", 4, 1, 100);
        AXE_VEIN_MINER_DEFAULT_RANGE = builder.comment("Determines the range of the tree chopper and tree stripper upgrade on the axe. It will mine logs up to this range away from the log broken.")
                .defineInRange("tree_chopper_range", 10, 1, 100);
        ALWAYS_CHANNEL = builder.comment("If true, channeling Crystal Tridents will summon lightning even if they don't hit an entity")
                .define("always_channel", true);
        builder.pop();

        // Furnace
        builder.comment("Crystal furnace options").push("furnace");
        FUEL_EFFICIENCY_ADDED_TICKS = builder.comment("Ticks added to fuel sources per duration of fuel efficiency")
                .defineInRange("fuel_efficiency_added_ticks", 100, 1, 1000);
        SPEED_UPGRADE_SUBTRACT_TICKS = builder.comment("Ticks subtracted from the smelting time per duration of furnace speed")
                .defineInRange("speed_upgrade_subtract_ticks", 10, 1, 1000);
        EXPERIENCE_BOOST_PERCENTAGE = builder.comment("Percentage increase for experience gained from smelting items. EXP_GAINED = EXP_IN_FURNACE * (1 + EXPERIENCE_BOOST_PERCENTAGE * EXPERIENCE BOOST LEVELS)")
                .defineInRange("experience_boost_percentage", 0.1D, 0, 1000);
        builder.pop();

        // Generator
        builder.comment("Crystal generator options").push("generator");
        BASE_FE_GENERATION = builder.comment("Base FE generation per tick")
                .defineInRange("base_fe_generation", 40, 1, 1000000);
        BASE_FE_STORAGE = builder.comment("Base FE the crystal generator can store")
                .defineInRange("base_fe_storage", 10000, 1, 1000000);
        BASE_FE_TRANSFER = builder.comment("Base FE transfer per tick")
                .defineInRange("base_fe_transfer", 80, 1, 1000000);
        FE_GENERATION_PER_LEVEL = builder.comment("FE generation gained per level of FE Generation")
                .defineInRange("fe_generation_per_level", 10, 1, 1000000);
        FE_STORAGE_PER_LEVEL = builder.comment("FE gained per level of FE Capacity")
                .defineInRange("fe_storage_per_level", 2000, 1, 1000000);
        FOOD_BURN_TIME_MULTIPLIER = builder.comment("The formula for food burn time is: (Nutrition + Saturation) * FOOD_BURN_TIME_MULTIPLIER")
                .defineInRange("food_burn_time_multiplier", 20D, 0.01D, 10000F);
        SKILL_POINTS_PER_BURN_TIME = builder.comment("Determines how much skill experience you get from burning items. Skill Exp = Fuel Burn Time * SKILL_POINTS_PER_BURN_TIME (Rounded Up)")
                .defineInRange("skill_points_per_burn_time", 0.00625D, 0D, 1D);
        GENERATOR_BASE_EXPERIENCE_CAP = builder.comment("Starting EXP Cap for the generator")
                .defineInRange("generator_base_experience_cap", 50, 1, 100000);
        builder.pop();

        // Backpack
        builder.comment("Crystal backpack settings").push("backpack");
        BACKPACK_SORT_TYPE = builder.comment("Method used for sorting the Crystal Backpack")
                .defineEnum("backpack_sort_type", CrystalBackpackInventory.SortType.QUANTITY);
        MAX_COMPRESSION_SLOT_ROWS = builder.comment("Maximum number of rows of compression slots. These slots will not currently scroll so don't set it to larger than your gui scale can render")
                .defineInRange("max_compression_slot_rows", 6, 1, 20);
        builder.pop();

        // Quarry
        builder.comment("Crystal quarry settings").push("quarry");
        QUARRY_BASE_ENERGY_COST = builder.comment("Quarry base RF/tick")
                .defineInRange("quarry_base_energy_cost", 40, 0, 100000);
        QUARRY_SPEED_COST_INCREASE = builder.comment("How much the quarry speed upgrade increases the RF/tick")
                .defineInRange("quarry_speed_cost_increase", 10, 0, 100000);
        QUARRY_SILK_TOUCH_COST_INCREASE = builder.comment("How much the quarry silk touch upgrade increases the RF/tick")
                .defineInRange("quarry_silk_touch_cost_increase", 40, 0, 100000);
        QUARRY_FORTUNE_COST_INCREASE = builder.comment("How much the quarry fortune upgrade increases the RF/tick")
                .defineInRange("quarry_fortune_cost_increase", 40, 0, 100000);
        QUARRY_INITIAL_POINT_MULTIPLIER = builder.comment("What percentage of the points from an AIOT the quarry gets when crafted")
                .defineInRange("quarry_initial_point_multiplier", 0.1D, 0D, 1D);
        QUARRY_BASE_EXPERIENCE_CAP = builder.comment("Starting EXP Cap for the quarry")
                .defineInRange("quarry_base_experience_cap", 500, 1, 100000);
        QUARRY_SPEED_UPGRADE_MULTIPLIER = builder.comment("Multiplier for the speed upgrade of the quarry")
                        .defineInRange("quarry_speed_upgrade_multiplier", 20D, 0.1D, 100000D);
        QUARRY_MAX_SIZE = builder.comment("Max size of the quarry")
                        .defineInRange("quarry_max_size", 64, 2, 256);
        builder.pop();

        // Magnet
        builder.comment("Crystal magnet settings").push("magnet");
        MAGNET_BASE_RANGE = builder.comment("Magnet Base Range (in blocks)")
                        .defineInRange("magnet_base_range", 5.0, 0, 100);
        builder.pop();

        // MISC
        builder.comment("Change miscellaneous options").push("miscellaneous");
        ENABLE_ITEM_REQUIREMENTS = builder.comment("Enables or disables item requirements for certain upgrades")
                .define("enable_item_requirements", true);
        REQUIRE_CRYSTAL_FOR_RESET = builder.comment("Require a crystal item in your inventory for resetting skill points")
                .define("require_crystal_for_reset", true);
        ENCHANT_TOOLS = builder.comment("If true, Crystal Tools will be enchantable. Note: There could be some weird interactions / it might break some things")
                .define("enchant_tools", false);
        builder.pop();
    }
}
