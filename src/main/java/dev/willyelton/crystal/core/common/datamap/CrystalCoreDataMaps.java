package dev.willyelton.crystal.core.common.datamap;

import dev.willyelton.crystal.core.common.skill.SkillTreeData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import static dev.willyelton.crystal.core.utils.constants.ApiConstants.baseRl;

public class CrystalCoreDataMaps {
    private CrystalCoreDataMaps() {}

    public static final DataMapType<EntityType<?>, Item> MOB_HEADS = DataMapType.builder(
                    baseRl("mob_heads"),
                    Registries.ENTITY_TYPE,
                    BuiltInRegistries.ITEM.byNameCodec())
            .synced(BuiltInRegistries.ITEM.byNameCodec(), false)
            .build();

    public static final DataMapType<Item, SkillTreeData> SKILL_TREES = DataMapType.builder(
                    baseRl("skill_trees"),
                    Registries.ITEM,
                    SkillTreeData.CODEC)
            .synced(SkillTreeData.CODEC, true)
            .build();

    public static final DataMapType<EntityType<?>, SkillTreeData> ENTITY_SKILL_TREES = DataMapType.builder(
                    baseRl("entity_skill_trees"),
                    Registries.ENTITY_TYPE,
                    SkillTreeData.CODEC)
            .synced(SkillTreeData.CODEC, true)
            .build();
}
