package dev.willyelton.crystal.tools.datagen;

import dev.willyelton.crystal.tools.CrystalTools;
import dev.willyelton.crystal.tools.ModRegistration;
import dev.willyelton.crystal.tools.common.components.DataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Map;
import java.util.stream.Collectors;

public class CrystalToolsLootTables extends VanillaBlockLoot {
    public CrystalToolsLootTables(HolderLookup.Provider provider) {
        super(provider);
    }

    @Override
    protected void generate() {

        dropSelf(ModRegistration.QUARRY_STABILIZER.get());
        // TODO: Need to drop items
        dropSelf(ModRegistration.CRYSTAL_PEDESTAL.get());
        createComponentSavingTable(ModRegistration.CRYSTAL_FURNACE.get(),
                DataComponents.FURNACE_DATA.get(),
                DataComponents.FURNACE_UPGRADES.get(),
                dev.willyelton.crystal.core.common.datacomponent.DataComponents.LEVELABLE_BLOCK_ENTITY_DATA.get(),
                net.minecraft.core.component.DataComponents.CONTAINER,
                dev.willyelton.crystal.core.common.datacomponent.DataComponents.AUTO_OUTPUT.get());
        createComponentSavingTable(ModRegistration.CRYSTAL_GENERATOR.get(),
                dev.willyelton.crystal.core.common.datacomponent.DataComponents.LEVELABLE_BLOCK_ENTITY_DATA.get(),
                DataComponents.GENERATOR_DATA.get(),
                DataComponents.GENERATOR_UPGRADES.get(),
                net.minecraft.core.component.DataComponents.CONTAINER);
        createComponentSavingTable(ModRegistration.CRYSTAL_QUARRY.get(),
                dev.willyelton.crystal.core.common.datacomponent.DataComponents.LEVELABLE_BLOCK_ENTITY_DATA.get(),
                net.minecraft.core.component.DataComponents.CONTAINER,
                DataComponents.QUARRY_BOUNDS.get(),
                DataComponents.QUARRY_DATA.get(),
                DataComponents.QUARRY_UPGRADES.get(),
                DataComponents.QUARRY_SETTINGS.get(),
                dev.willyelton.crystal.core.common.datacomponent.DataComponents.FILTER_INVENTORY.get(),
                dev.willyelton.crystal.core.common.datacomponent.DataComponents.AUTO_OUTPUT.get(),
                dev.willyelton.crystal.core.common.datacomponent.DataComponents.CHUNKLOADING.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.entrySet().stream()
                .filter(e -> e.getKey().identifier().getNamespace().equals(CrystalTools.MODID))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private void createComponentSavingTable(Block block, DataComponentType<?>... dataComponents) {
        LootPoolSingletonContainer.Builder<?> lti = LootItem.lootTableItem(block);
        lti.apply(CopyNameFunction.copyName(LootContext.BlockEntityTarget.BLOCK_ENTITY));

        CopyComponentsFunction.Builder copyComponentsFunctionBuilder = CopyComponentsFunction.copyComponentsFromBlockEntity(LootContext.BlockEntityTarget.BLOCK_ENTITY.contextParam());

        for (DataComponentType<?> dataComponent : dataComponents) {
            copyComponentsFunctionBuilder.include(dataComponent);
        }
        lti.apply(copyComponentsFunctionBuilder);

        LootPool.Builder builder = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(lti);
        add(block, LootTable.lootTable().withPool(builder));
    }
}
