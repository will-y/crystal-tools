package dev.willyelton.crystal.core.common.datacomponent;

import com.mojang.serialization.Codec;
import dev.willyelton.crystal.core.common.block.entity.model.SideConfigOption;
import dev.willyelton.crystal.core.common.levelable.LevelableTooltip;
import dev.willyelton.crystal.core.common.skill.SkillPoints;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Map;

public class DataComponents {
    private DataComponents() {}

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ApiConstants.CORE_MOD_ID);

    // Skill Things
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SKILL_POINTS = register("skill_points", Codec.INT, ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SKILL_EXPERIENCE = register("skill_experience", Codec.INT, ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> EXPERIENCE_CAP = register("experience_cap", Codec.INT, ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SkillPoints>> SKILL_POINT_DATA = COMPONENTS.register("skill_point_data", () -> DataComponentType.<SkillPoints>builder().persistent(SkillPoints.CODEC).networkSynchronized(SkillPoints.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LevelableTooltip>> SKILL_TOOLTIP = COMPONENTS.register("skill_tooltip", () -> DataComponentType.<LevelableTooltip>builder().persistent(LevelableTooltip.CODEC).networkSynchronized(LevelableTooltip.STREAM_CODEC).build());

    // Actions
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> AUTO_OUTPUT = register("auto_output", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> CHUNKLOADING = register("chunkloading", Codec.BOOL, ByteBufCodecs.BOOL);

    // Enchantments
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SILK_TOUCH_BONUS = register("silk_touch_bonus", Codec.BOOL, ByteBufCodecs.BOOL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FORTUNE_BONUS = register("fortune_bonus", Codec.INT, ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FROST_WALKER = register("frost_walker", Codec.BOOL, ByteBufCodecs.BOOL);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> CREATIVE_FLIGHT = register("creative_flight", Codec.BOOL, ByteBufCodecs.BOOL);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LevelableBlockEntityData>> LEVELABLE_BLOCK_ENTITY_DATA = register("levelable_block_entity_data", LevelableBlockEntityData.CODEC, LevelableBlockEntityData.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Map<Direction, SideConfigOption>>> SIDE_CONFIG_OPTIONS = register("side_config_options", SideConfigOption.DIRECTION_MAP_CODEC, SideConfigOption.DIRECTION_MAP_STREAM_CODEC);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ItemContainerContents>>> ITEM_INVENTORY = register("item_inventory", ItemContainerContents.CODEC.listOf(), ItemContainerContents.STREAM_CODEC.apply(ByteBufCodecs.list()));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> FILTER_INVENTORY = register("filter_inventory", ItemContainerContents.CODEC, ItemContainerContents.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FILTER_CAPACITY = register("filter_capacity", Codec.INT, ByteBufCodecs.VAR_INT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> WHITELIST = register("whitelist", Codec.BOOL, ByteBufCodecs.BOOL);

    // Effects
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<MobEffectInstance>>> EFFECTS = register("effects", MobEffectInstance.CODEC.listOf(), MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list()));

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String key, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        DeferredHolder<DataComponentType<?>, DataComponentType<T>> result;
        if (streamCodec == null) {
            result = COMPONENTS.register(key, () -> DataComponentType.<T>builder().persistent(codec).build());
        } else {
            result = COMPONENTS.register(key, () -> DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec).build());
        }

        return result;
    }

    public static int addToComponent(ItemStack stack, DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> component, int i) {
        int newValue = stack.getOrDefault(component, 0) + i;
        stack.set(component, newValue);
        return newValue;
    }
}
