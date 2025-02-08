package dev.willyelton.crystal_tools.common.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class CrystalToolsTags {
    public static final TagKey<Item> RODS_METAL = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "rods/all_metal"));
    public static final TagKey<Item> RODS_METAL_NETHERITE = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "rods/netherite"));
}
