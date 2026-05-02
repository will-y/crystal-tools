package dev.willyelton.crystal.tools.common.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static dev.willyelton.crystal.tools.CrystalTools.rl;

public class CrystalToolsTags {
    // Item
    public static final TagKey<Item> REPAIRS_CRYSTAL = ItemTags.create(rl("repairs_crystal"));
    public static final TagKey<Item> CRYSTAL_TOOL = ItemTags.create(rl("crystal_tool"));

    // Block
    public static final TagKey<Block> MINABLE_WITH_AIOT = BlockTags.create(rl("mineable/pickaxe"));

    // Entity Type
    public static final TagKey<EntityType<?>> PICKUP_ENTITY_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, rl("pickup_entity_blacklist"));
}
