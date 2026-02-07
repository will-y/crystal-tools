package dev.willyelton.crystal_tools.common.datamap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

public class DataMaps {
    public static final DataMapType<Item, GeneratorFuelData> GENERATOR_METALS = DataMapType.builder(
                    rl("generator_metals"),
                    Registries.ITEM,
                    GeneratorFuelData.CODEC)
            .synced(GeneratorFuelData.CODEC, false)
            .build();

    public static final DataMapType<Item, GeneratorFuelData> GENERATOR_GEMS = DataMapType.builder(
                    rl("generator_gems"),
                    Registries.ITEM,
                    GeneratorFuelData.CODEC)
            .synced(GeneratorFuelData.CODEC, false)
            .build();

    public static final DataMapType<EntityType<?>, Item> MOB_HEADS = DataMapType.builder(
                    rl("mob_heads"),
                    Registries.ENTITY_TYPE,
                    BuiltInRegistries.ITEM.byNameCodec())
            .synced(BuiltInRegistries.ITEM.byNameCodec(), false)
            .build();

    public static final DataMapType<Item, SkillTreeData> SKILL_TREES = DataMapType.builder(
                    rl("skill_trees"),
                    Registries.ITEM,
                    SkillTreeData.CODEC)
            .synced(SkillTreeData.CODEC, true)
            .build();

    public static final DataMapType<EntityType<?>, SkillTreeData> ENTITY_SKILL_TREES = DataMapType.builder(
                    rl("entity_skill_trees"),
                    Registries.ENTITY_TYPE,
                    SkillTreeData.CODEC)
            .synced(SkillTreeData.CODEC, true)
            .build();

    public static final DataMapType<Item, ActionData> PEDESTAL_ACTIONS = DataMapType.builder(
                    rl("pedestal_actions"),
                    Registries.ITEM,
                    ActionData.CODEC)
            .synced(ActionData.CODEC, true)
            .build();
}
