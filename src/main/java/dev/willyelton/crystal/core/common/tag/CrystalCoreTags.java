package dev.willyelton.crystal.core.common.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import static dev.willyelton.crystal.core.utils.constants.ApiConstants.baseRl;

public class CrystalCoreTags {
    // Item
    public static final TagKey<Item> REPAIRS_CRYSTAL = ItemTags.create(baseRl("repairs_crystal"));
    public static final TagKey<Item> RODS_METAL = ItemTags.create(Identifier.fromNamespaceAndPath("c", "rods/all_metal"));
    public static final TagKey<Item> RODS_METAL_NETHERITE = ItemTags.create(Identifier.fromNamespaceAndPath("c", "rods/netherite"));

    // Entity Type
    public static final TagKey<EntityType<?>> ENTITY_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, baseRl("entity_blacklist"));
}
