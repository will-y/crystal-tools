package dev.willyelton.crystal.tools.common.components;

import com.mojang.serialization.Codec;
import dev.willyelton.crystal.tools.CrystalTools;
import dev.willyelton.crystal.tools.common.datamap.GeneratorFuelData;
import dev.willyelton.crystal.tools.common.levelable.tool.UseMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class DataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CrystalTools.MODID);

    // All tools
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> UNBREAKING = register("unbreaking", Codec.FLOAT, ByteBufCodecs.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> TORCH = register("torch", Codec.BOOL, ByteBufCodecs.BOOL);

    // Mining tools
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> AUTO_PICKUP = register("auto_pickup", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> AUTO_SMELT = register("auto_smelt", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DISABLE_AUTO_SMELT = register("disable_auto_smelt", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> VEIN_MINER = register("vein_miner", Codec.INT, ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> HAS_3x3 = register("3x3", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DISABLE_3x3 = register("disable_3x3", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> MINE_MODE = register("mine_mode", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> AUTO_REPAIR = register("auto_repair", Codec.INT, ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> AUTO_REPAIR_GAME_TIME = register("auto_repair_game_time", Codec.LONG, ByteBufCodecs.VAR_LONG);

    // Axe
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> LEAF_MINE = register("leaf_mine", Codec.BOOL, ByteBufCodecs.BOOL);

    // Hoe
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SHEAR = register("shear", Codec.BOOL, ByteBufCodecs.BOOL);

    // AIOT
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UseMode>> USE_MODE = register("use_mode", UseMode.CODEC, UseMode.STREAM_CODEC);

    // Weapons
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FIRE = register("fire", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LIFESTEAL = register("lifesteal", Codec.INT, ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> BEHEADING = register("beheading", Codec.FLOAT, ByteBufCodecs.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> CAPTURING = register("capture", Codec.FLOAT, ByteBufCodecs.FLOAT);

    // Armor
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> NIGHT_VISION = register("night_vision", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DISABLE_NIGHT_VISION = register("disable_night_vision", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> NO_FALL_DAMAGE = register("no_fall_damage", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DISABLE_CREATIVE_FLIGHT = register("disable_creative_flight", Codec.BOOL, ByteBufCodecs.BOOL);

    // Bow
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> ARROW_DAMAGE = register("arrow_damage", Codec.FLOAT, ByteBufCodecs.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> ARROW_SPEED = register("arrow_speed_bonus", Codec.FLOAT, ByteBufCodecs.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> DRAW_SPEED = register("draw_speed", Codec.FLOAT, ByteBufCodecs.FLOAT);

    // Food Things
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> EAT_SPEED_BONUS = register("eat_speed_bonus", Codec.INT, ByteBufCodecs.INT);

    // Firework
    // At some point could use vanilla component if I ever have a better way to use not primitive types
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FLIGHT_TIME = register("flight_time", Codec.INT, ByteBufCodecs.INT);

    // Also used for quarry
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> COMPRESSION_INVENTORY = register("compression_inventory", ItemContainerContents.CODEC, ItemContainerContents.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Integer>>> COMPRESSION_MODES = register("compression_modes", Codec.INT.listOf(), ByteBufCodecs.INT.apply(ByteBufCodecs.list()));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CAPACITY = register("capacity", Codec.INT, ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SORT_ENABLED = register("sort_enabled", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> COMPRESSION_ENABLED = register("compression_enabled", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> BACKPACK_AUTO_PICKUP = register("backpack_auto_pickup", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> PICKUP_DISABLED = register("pickup_disabled", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> INVENTORY_STORE = register("inventory_store", Codec.BOOL, ByteBufCodecs.BOOL);

    // Trident
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> RIPTIDE = register("riptide", Codec.INT, ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> RIPTIDE_DISABLED = register("riptide_disabled", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ALWAYS_RIPTIDE = register("always_riptide", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> PROJECTILE_SPEED = register("projectile_speed", Codec.FLOAT, ByteBufCodecs.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> PROJECTILE_DAMAGE = register("projectile_damage", Codec.FLOAT, ByteBufCodecs.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> INSTANT_LOYALTY = register("instant_loyalty", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CHANNELING = register("channeling", Codec.INT, ByteBufCodecs.VAR_INT);

    // Fishing Rod
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> DOUBLE_DROPS = register("double_drops", Codec.FLOAT, ByteBufCodecs.FLOAT);

    // Magnet
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DISABLED = register("disabled", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> ITEM_SPEED = register("item_speed", Codec.FLOAT, ByteBufCodecs.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> MAGNET_RANGE = register("magnet_range", Codec.FLOAT, ByteBufCodecs.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> PULL_XP = register("pull_xp", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> PULL_MOBS = register("pull_mobs", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> INSTANT_PICKUP = register("instant_pickup", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DISABLE_MOB_PULL = register("disable_mob_pull", Codec.BOOL, ByteBufCodecs.BOOL);

    // Portable Generator
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FE_GENERATION = register("fe_generation", Codec.INT, ByteBufCodecs.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> FUEL_EFFICIENCY = register("fuel_efficiency", Codec.FLOAT, ByteBufCodecs.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FE_CAPACITY = register("fe_capacity", Codec.INT, ByteBufCodecs.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FE_STORED = register("fe_stored", Codec.INT, ByteBufCodecs.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> METAL_GENERATOR = register("metal_generator", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FOOD_GENERATOR = register("food_generator", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> GEM_GENERATOR = register("gem_generator", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GeneratorFuelData>> BURNING_ITEM_DATA = register("burning_item_data", GeneratorFuelData.CODEC, GeneratorFuelData.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PORTABLE_GENERATOR_SLOTS = register("portable_generator_slots", Codec.INT, ByteBufCodecs.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAX_BURN_TIME = register("max_burn_time", Codec.INT, ByteBufCodecs.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> GENERATED_LAST_TICK = register("generated_last_tick", Codec.INT, ByteBufCodecs.INT);

    // Furnace
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FurnaceData>> FURNACE_DATA = register("furnace_data", FurnaceData.CODEC, FurnaceData.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FurnaceUpgrades>> FURNACE_UPGRADES = register("furnace_upgrades", FurnaceUpgrades.CODEC, FurnaceUpgrades.STREAM_CODEC);

    // Generator
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GeneratorData>> GENERATOR_DATA = register("generator_data", GeneratorData.CODEC, GeneratorData.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GeneratorUpgrades>> GENERATOR_UPGRADES = register("generator_upgrades", GeneratorUpgrades.CODEC, GeneratorUpgrades.STREAM_CODEC);

    // Quarry
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<BlockPos>>> QUARRY_BOUNDS = register("quarry_bounds", BlockPos.CODEC.listOf(), BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<QuarryData>> QUARRY_DATA = register("quarry_data", QuarryData.CODEC, QuarryData.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<QuarryUpgrades>> QUARRY_UPGRADES = register("quarry_upgrades", QuarryUpgrades.CODEC, QuarryUpgrades.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<QuarrySettings>> QUARRY_SETTINGS = register("quarry_settings", QuarrySettings.CODEC, QuarrySettings.STREAM_CODEC);

    // Auto Targeting
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> AUTO_TARGET = register("auto_target", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENTITY_TARGET = register("entity_target", Codec.INT, ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DISABLE_AUTO_TARGET = register("disable_auto_target", Codec.BOOL, ByteBufCodecs.BOOL);

    // Shield
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FLAMING_SHIELD = register("flaming_shield", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SHIELD_THORNS = register("shield_thorns", Codec.INT, ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SHIELD_KNOCKBACK = register("shield_knockback", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TOTEM_SLOTS = register("totem_slots", Codec.INT, ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FILLED_TOTEM_SLOTS = register("filled_totem_slots", Codec.INT, ByteBufCodecs.VAR_INT);

    // Entities
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> CAPTURED_ENTITY_TOOLTIP = register("captured_entity_tooltip", Codec.STRING, ByteBufCodecs.STRING_UTF8);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> BREAK_CAGE_ON_USE = register("break_cage_on_use", Codec.BOOL, ByteBufCodecs.BOOL);

    // Utilities
    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String key, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        DeferredHolder<DataComponentType<?>, DataComponentType<T>> result;
        if (streamCodec == null) {
            result = COMPONENTS.register(key, () -> DataComponentType.<T>builder().persistent(codec).build());
        } else {
            result = COMPONENTS.register(key, () -> DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec).build());
        }

        return result;
    }


}
