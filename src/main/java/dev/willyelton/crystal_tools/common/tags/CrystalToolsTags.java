package dev.willyelton.crystal_tools.common.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

public class CrystalToolsTags {
    // Item
    public static final TagKey<Item> RODS_METAL = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "rods/all_metal"));
    public static final TagKey<Item> RODS_METAL_NETHERITE = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "rods/netherite"));
    public static final TagKey<Item> REPAIRS_CRYSTAL = ItemTags.create(rl("repairs_crystal"));
    public static final TagKey<Item> CRYSTAL_TOOL = ItemTags.create(rl("crystal_tool"));

    // Block
    public static final TagKey<Block> AUTO_OUTPUT_BLACKLIST = BlockTags.create(rl("auto_output_blacklist"));
    public static final TagKey<Block> MINABLE_WITH_AIOT = BlockTags.create(rl("mineable/pickaxe"));

    // Entity Type
    public static final TagKey<EntityType<?>> ENTITY_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, rl("entity_blacklist"));
    public static final TagKey<EntityType<?>> PICKUP_ENTITY_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, rl("pickup_entity_blacklist"));
}
