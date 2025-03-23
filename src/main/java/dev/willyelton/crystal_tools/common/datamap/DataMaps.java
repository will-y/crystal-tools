package dev.willyelton.crystal_tools.common.datamap;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public class DataMaps {
    public static final DataMapType<Item, GeneratorFuelData> GENERATOR_METALS = DataMapType.builder(
                    ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "generator_metals"),
                    Registries.ITEM,
                    GeneratorFuelData.CODEC)
            .synced(GeneratorFuelData.CODEC, false)
            .build();

    public static final DataMapType<Item, GeneratorFuelData> GENERATOR_GEMS = DataMapType.builder(
                    ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "generator_gems"),
                    Registries.ITEM,
                    GeneratorFuelData.CODEC)
            .synced(GeneratorFuelData.CODEC, false)
            .build();

    public static final DataMapType<EntityType<?>, Item> MOB_SKULLS = DataMapType.builder(
                    ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "mob_skills"),
                    Registries.ENTITY_TYPE,
                    BuiltInRegistries.ITEM.byNameCodec())
            .synced(BuiltInRegistries.ITEM.byNameCodec(), false)
            .build();
}
