package dev.willyelton.crystal_tools.common.components;

import com.mojang.serialization.Codec;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.levelable.LevelableTooltip;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.common.levelable.tool.UseMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CrystalTools.MODID);

    public static final Map<String, ResourceLocation> INT_COMPONENTS = new HashMap<>();
    public static final Map<String, ResourceLocation> FLOAT_COMPONENTS = new HashMap<>();
    public static final Map<String, ResourceLocation> BOOLEAN_COMPONENTS = new HashMap<>();

    // Skill Things
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SKILL_POINTS = register("skill_points", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SKILL_EXPERIENCE = register("skill_experience", Codec.INT, ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> EXPERIENCE_CAP = register("experience_cap", Codec.INT, ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SkillPoints>> SKILL_POINT_DATA = COMPONENTS.register("skill_point_data", () -> DataComponentType.<SkillPoints>builder().persistent(SkillPoints.CODEC).networkSynchronized(SkillPoints.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SkillData>> SKILL_DATA = COMPONENTS.register("skill_data", () -> DataComponentType.<SkillData>builder().persistent(SkillData.CODEC).networkSynchronized(SkillData.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LevelableTooltip>> SKILL_TOOLTIP = COMPONENTS.register("skill_tooltip", () -> DataComponentType.<LevelableTooltip>builder().persistent(LevelableTooltip.CODEC).networkSynchronized(LevelableTooltip.STREAM_CODEC).build());

    // All tools
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> UNBREAKING = register("unbreaking", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> TORCH = register("torch", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);

    // Mining tools
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> AUTO_PICKUP = register("auto_pickup", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> AUTO_SMELT = register("auto_smelt", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DISABLE_AUTO_SMELT = register("disable_auto_smelt", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> VEIN_MINER = register("vein_miner", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> HAS_3x3 = register("3x3", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DISABLE_3x3 = register("disable_3x3", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> MINE_MODE = register("mine_mode", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SILK_TOUCH_BONUS = register("silk_touch_bonus", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FORTUNE_BONUS = register("fortune_bonus", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> AUTO_REPAIR = register("auto_repair", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> AUTO_REPAIR_GAME_TIME = register("auto_repair_game_time", Codec.LONG, ByteBufCodecs.VAR_LONG);

    // Axe
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> LEAF_MINE = register("leaf_mine", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);

    // Hoe
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SHEAR = register("shear", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);

    // AIOT
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UseMode>> USE_MODE = register("use_mode", UseMode.CODEC, UseMode.STREAM_CODEC);

    // Weapons
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FIRE = register("fire", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LIFESTEAL = register("lifesteal", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> BEHEADING = register("beheading", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> CAPTURING = register("capture", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);

    // Armor
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> NIGHT_VISION = register("night_vision", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DISABLE_NIGHT_VISION = register("disable_night_vision", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> NO_FALL_DAMAGE = register("no_fall_damage", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> CREATIVE_FLIGHT = register("creative_flight", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DISABLE_CREATIVE_FLIGHT = register("disable_creative_flight", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FROST_WALKER = register("frost_walker", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);

    // Bow
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> ARROW_DAMAGE = register("arrow_damage", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> ARROW_SPEED = register("arrow_speed_bonus", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> DRAW_SPEED = register("draw_speed", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);

    // Food Things
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> EAT_SPEED_BONUS = register("eat_speed_bonus", Codec.INT, ByteBufCodecs.INT, SkillType.INT);

    // Effects
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<MobEffectInstance>>> EFFECTS = register("effects", MobEffectInstance.CODEC.listOf(), MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list()));

    // Firework
    // At some point could use vanilla component if I ever have a better way to use not primitive types
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FLIGHT_TIME = register("flight_time", Codec.INT, ByteBufCodecs.INT, SkillType.INT);

    // Backpack
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ItemContainerContents>>> BACKPACK_INVENTORY = register("backpack_inventory", ItemContainerContents.CODEC.listOf(), ItemContainerContents.STREAM_CODEC.apply(ByteBufCodecs.list()));
    // Also used for quarry
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> FILTER_INVENTORY = register("filter_inventory", ItemContainerContents.CODEC, ItemContainerContents.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> COMPRESSION_INVENTORY = register("compression_inventory", ItemContainerContents.CODEC, ItemContainerContents.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Integer>>> COMPRESSION_MODES = register("compression_modes", Codec.INT.listOf(), ByteBufCodecs.INT.apply(ByteBufCodecs.list()));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CAPACITY = register("capacity", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FILTER_CAPACITY = register("filter_capacity", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> WHITELIST = register("whitelist", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SORT_ENABLED = register("sort_enabled", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> COMPRESSION_ENABLED = register("compression_enabled", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> BACKPACK_AUTO_PICKUP = register("backpack_auto_pickup", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> PICKUP_DISABLED = register("pickup_disabled", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> INVENTORY_STORE = register("inventory_store", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);

    // Trident
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> RIPTIDE = register("riptide", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> RIPTIDE_DISABLED = register("riptide_disabled", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ALWAYS_RIPTIDE = register("always_riptide", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> PROJECTILE_SPEED = register("projectile_speed", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> PROJECTILE_DAMAGE = register("projectile_damage", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> INSTANT_LOYALTY = register("instant_loyalty", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CHANNELING = register("channeling", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);

    // Fishing Rod
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> DOUBLE_DROPS = register("double_drops", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);

    // Magnet
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DISABLED = register("disabled", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> ITEM_SPEED = register("item_speed", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> MAGNET_RANGE = register("magnet_range", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> PULL_XP = register("pull_xp", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> PULL_MOBS = register("pull_mobs", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> INSTANT_PICKUP = register("instant_pickup", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DISABLE_MOB_PULL = register("disable_mob_pull", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);

    // Block Entities
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LevelableBlockEntityData>> LEVELABLE_BLOCK_ENTITY_DATA = register("levelable_block_entity_data", LevelableBlockEntityData.CODEC, LevelableBlockEntityData.STREAM_CODEC);

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

    // Actions
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> AUTO_OUTPUT = register("auto_output", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> CHUNKLOADING = register("chunkloading", Codec.BOOL, ByteBufCodecs.BOOL);

    // Auto Targeting
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> AUTO_TARGET = register("auto_target", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENTITY_TARGET = register("entity_target", Codec.INT, ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DISABLE_AUTO_TARGET = register("disable_auto_target", Codec.BOOL, ByteBufCodecs.BOOL);

    // Shield
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FLAMING_SHIELD = register("flaming_shield", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SHIELD_THORNS = register("shield_thorns", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SHIELD_KNOCKBACK = register("shield_knockback", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TOTEM_SLOTS = register("totem_slots", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FILLED_TOTEM_SLOTS = register("filled_totem_slots", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);


    // Utilities
    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String key, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return register(key, codec, streamCodec, SkillType.NONE);
    }

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String key, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, SkillType skillType) {
        DeferredHolder<DataComponentType<?>, DataComponentType<T>> result;
        if (streamCodec == null) {
            result = COMPONENTS.register(key, () -> DataComponentType.<T>builder().persistent(codec).build());
        } else {
            result = COMPONENTS.register(key, () -> DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec).build());
        }

        switch (skillType) {
            case INT:
                INT_COMPONENTS.put(key, result.getId());
                break;
            case FLOAT:
                FLOAT_COMPONENTS.put(key, result.getId());
                break;
            case BOOLEAN:
                BOOLEAN_COMPONENTS.put(key, result.getId());
                break;
        }

        return result;
    }

    public static float addToComponent(ItemStack stack, DeferredHolder<DataComponentType<?>, DataComponentType<Float>> component, float f) {
        float newValue = stack.getOrDefault(component, 0F) + f;
        stack.set(component, newValue);
        return newValue;
    }

    public static int addToComponent(ItemStack stack, DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> component, int i) {
        int newValue = stack.getOrDefault(component, 0) + i;
        stack.set(component, newValue);
        return newValue;
    }

    public static void addToComponent(ItemStack stack, String componentKey, float value) {
        ResourceLocation resourceLocation = FLOAT_COMPONENTS.get(componentKey);

        if (resourceLocation != null) {
            DataComponentType<Float> dataComponent = (DataComponentType<Float>) COMPONENTS.getRegistry().get().getValue(resourceLocation);
            if (dataComponent != null) {
                stack.set(dataComponent, stack.getOrDefault(dataComponent, 0F) + value);
                return;
            }
        }

        resourceLocation = INT_COMPONENTS.get(componentKey);

        if (resourceLocation != null) {
            DataComponentType<Integer> dataComponent = (DataComponentType<Integer>) COMPONENTS.getRegistry().get().getValue(resourceLocation);
            if (dataComponent != null) {
                stack.set(dataComponent, stack.getOrDefault(dataComponent, 0) + (int) value);
                return;
            }
        }

        resourceLocation = BOOLEAN_COMPONENTS.get(componentKey);

        if (resourceLocation != null) {
            DataComponentType<Boolean> dataComponent = (DataComponentType<Boolean>) COMPONENTS.getRegistry().get().getValue(resourceLocation);
            if (dataComponent != null && value > 0) {
                stack.set(dataComponent, true);
                return;
            }
        }

        throw new IllegalArgumentException("Skill " + componentKey + " not registered correctly");
    }

    public static void setValue(ItemStack stack, String componentKey, float value) {
        ResourceLocation resourceLocation = FLOAT_COMPONENTS.get(componentKey);

        if (resourceLocation != null) {
            DataComponentType<Float> dataComponent = (DataComponentType<Float>) COMPONENTS.getRegistry().get().getValue(resourceLocation);
            if (dataComponent != null) {
                stack.set(dataComponent, value);
                return;
            }
        }

        resourceLocation = INT_COMPONENTS.get(componentKey);

        if (resourceLocation != null) {
            DataComponentType<Integer> dataComponent = (DataComponentType<Integer>) COMPONENTS.getRegistry().get().getValue(resourceLocation);
            if (dataComponent != null) {
                stack.set(dataComponent, (int) value);
                return;
            }
        }

        resourceLocation = BOOLEAN_COMPONENTS.get(componentKey);
        if (resourceLocation != null) {
            DataComponentType<Boolean> dataComponent = (DataComponentType<Boolean>) COMPONENTS.getRegistry().get().getValue(resourceLocation);
            if (dataComponent != null) {
                stack.set(dataComponent, value > 0);
                return;
            }
        }

        throw new IllegalArgumentException("Skill " + componentKey + " not registered correctly");
    }

    public static void addValueToArray(ItemStack stack,  DeferredHolder<DataComponentType<?>, DataComponentType<List<Integer>>> component, int index, int value) {
        List<Integer> points = new ArrayList<>(stack.getOrDefault(component, new ArrayList<>()));

        if (points.size() > index) {
            points.set(index, points.get(index) + value);
        } else {
            while (points.size() < index) {
                points.add(0);
            }
            points.add(value);
        }

        stack.set(component, points);
    }

    private enum SkillType {
        INT, FLOAT, BOOLEAN, NONE
    }
}
