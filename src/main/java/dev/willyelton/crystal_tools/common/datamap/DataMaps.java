package dev.willyelton.crystal_tools.common.datamap;

import net.minecraft.core.registries.Registries;
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

    public static final DataMapType<Item, ActionData> PEDESTAL_ACTIONS = DataMapType.builder(
                    rl("pedestal_actions"),
                    Registries.ITEM,
                    ActionData.CODEC)
            .synced(ActionData.CODEC, true)
            .build();
}
