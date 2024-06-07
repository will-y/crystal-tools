package dev.willyelton.crystal_tools;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(CrystalTools.MODID);
    // I hate this
    public static final Map<String, ResourceLocation> INT_COMPONENTS = new HashMap<>();
    public static final Map<String, ResourceLocation> FLOAT_COMPONENTS = new HashMap<>();
    public static final Map<String, ResourceLocation> BOOLEAN_COMPONENTS = new HashMap<>();

    // Skill Things
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SKILL_POINTS = register("skill_points", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SKILL_EXPERIENCE = register("skill_experience", Codec.INT, ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> EXPERIENCE_CAP = register("experience_cap", Codec.INT, ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Integer>>> POINTS_ARRAY = COMPONENTS.register("points", () -> DataComponentType.<List<Integer>>builder().persistent(Codec.INT.listOf()).networkSynchronized(ByteBufCodecs.INT.apply(ByteBufCodecs.list())).build());

    // All tools
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> REACH = register("reach", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> DURABILITY_BONUS = register("durability_bonus", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> UNBREAKING = register("unbreaking", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> TORCH = register("torch", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);

    // Mining tools
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> MINING_SPEED = register("speed_bonus", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
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
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> AUTO_REPAIR_COUNTER = register("auto_repair_counter", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);

    // Axe
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> LEAF_MINE = register("leaf_mine", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);

    // Hoe
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SHEAR = register("shear", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);

    // AIOT
    // TODO: Can just make this an enum component
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> USE_MODE = register("use_mode", Codec.STRING, ByteBufCodecs.STRING_UTF8);

    // Weapons
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> DAMAGE_BONUS = register("damage_bonus", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> ATTACK_SPEED = register("attack_speed", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> KNOCKBACK = register("knockback", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> KNOCKBACK_RESISTANCE = register("knockback_resistance", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FIRE = register("fire", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LIFESTEAL = register("lifesteal", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);

    // Armor
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> HEALTH_BONUS = register("health_bonus", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> SPEED_BONUS = register("move_speed_bonus", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ARMOR_BONUS = register("armor_bonus", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> TOUGHNESS_BONUS = register("toughness_bonus", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> NIGHT_VISION = register("night_vision", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> NO_FALL_DAMAGE = register("no_fall_damage", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);

    // Bow
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> INFINITY = register("infinity", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> ARROW_DAMAGE = register("arrow_damage", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> ARROW_SPEED = register("arrow_speed_bonus", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ARROW_KNOCKBACK = register("arrow_knockback", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FLAME = register("flame", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> DRAW_SPEED = register("draw_speed", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);

    // Food Things
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> NUTRITION_BONUS = register("nutrition_bonus", Codec.INT, ByteBufCodecs.INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> SATURATION_BONUS = register("saturation_bonus", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ALWAYS_EAT = register("always_eat", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> EAT_SPEED_BONUS = register("eat_speed_bonus", Codec.INT, ByteBufCodecs.INT, SkillType.INT);

    // Firework
    // TODO: Refactor to just use vanilla firework component?
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FLIGHT_TIME = register("flight_time", Codec.INT, ByteBufCodecs.INT, SkillType.INT);

    // Backpack
    // TODO: Should this be one data component? Probably doesn't need to be but could be
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> INVENTORY = register("inventory", ItemContainerContents.CODEC, ItemContainerContents.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> FILTER_INVENTORY = register("filter_inventory", ItemContainerContents.CODEC, ItemContainerContents.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CAPACITY = register("capacity", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FILTER_CAPACITY = register("filter_capacity", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> WHITELIST = register("whitelist", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SORT_ENABLED = register("sort_enabled", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> BACKPACK_AUTO_PICKUP = register("backpack_auto_pickup", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> PICKUP_DISABLED = register("pickup_disabled", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> INVENTORY_STORE = register("inventory_store", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);

    // Trident
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> RIPTIDE = register("riptide", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> RIPTIDE_DISABLED = register("riptide_disabled", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ALWAYS_RIPTIDE = register("always_riptide", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> PROJECTILE_SPEED = register("projectile_speed", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> PROJECTILE_DAMAGE = register("projectile_damage", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);
    // TODO: Should be a byte?
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LOYALTY = register("loyalty", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> INSTANT_LOYALTY = register("instant_loyalty", Codec.BOOL, ByteBufCodecs.BOOL, SkillType.BOOLEAN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CHANNELING = register("channeling", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);

    // Fishing Rod
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LURE = register("lure", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LUCK_OF_THE_SEA = register("luck_of_the_sea", Codec.INT, ByteBufCodecs.VAR_INT, SkillType.INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> DOUBLE_DROPS = register("double_drops", Codec.FLOAT, ByteBufCodecs.FLOAT, SkillType.FLOAT);


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

        // TODO: Not sure if this will work
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

    // TODO: Value should always be a float probably
    public static void addToComponent(ItemStack stack, String componentKey, float value) {
        ResourceLocation resourceLocation = FLOAT_COMPONENTS.get(componentKey);

        if (resourceLocation != null) {
            DataComponentType<Float> dataComponent = (DataComponentType<Float>) COMPONENTS.getRegistry().get().get(resourceLocation);
            if (dataComponent != null) {
                stack.set(dataComponent, stack.getOrDefault(dataComponent, 0F) + value);
            }
        }

        resourceLocation = INT_COMPONENTS.get(componentKey);

        if (resourceLocation != null) {
            DataComponentType<Integer> dataComponent = (DataComponentType<Integer>) COMPONENTS.getRegistry().get().get(resourceLocation);
            if (dataComponent != null) {
                stack.set(dataComponent, stack.getOrDefault(dataComponent, 0) + (int) value);
            }
        }
    }

    public static void setValue(ItemStack stack, String componentKey, float value) {
        ResourceLocation resourceLocation = FLOAT_COMPONENTS.get(componentKey);

        if (resourceLocation != null) {
            DataComponentType<Float> dataComponent = (DataComponentType<Float>) COMPONENTS.getRegistry().get().get(resourceLocation);
            if (dataComponent != null) {
                stack.set(dataComponent, value);
            }
        }

        resourceLocation = INT_COMPONENTS.get(componentKey);

        if (resourceLocation != null) {
            DataComponentType<Integer> dataComponent = (DataComponentType<Integer>) COMPONENTS.getRegistry().get().get(resourceLocation);
            if (dataComponent != null) {
                stack.set(dataComponent, (int) value);
            }
        }

        resourceLocation = BOOLEAN_COMPONENTS.get(componentKey);
        if (resourceLocation != null) {
            DataComponentType<Boolean> dataComponent = (DataComponentType<Boolean>) COMPONENTS.getRegistry().get().get(resourceLocation);
            if (dataComponent != null) {
                stack.set(dataComponent, value > 0);
            }
        }
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
