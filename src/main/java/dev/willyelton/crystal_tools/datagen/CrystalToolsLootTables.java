package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.block.CrystalTorch;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
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
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Map;
import java.util.stream.Collectors;

public class CrystalToolsLootTables extends VanillaBlockLoot {
    public CrystalToolsLootTables(HolderLookup.Provider provider) {
        super(provider);
    }

    @Override
    protected void generate() {
        dropSelf(ModRegistration.CRYSTAL_BLOCK.get());
        dropSelf(ModRegistration.CRYSTAL_GEODE.get());
        add(ModRegistration.NETHERITE_INFUSED_CRYSTAL_GEODE.get(), createOreDrop(ModRegistration.NETHERITE_INFUSED_CRYSTAL_GEODE.get(), ModRegistration.NETHERITE_INFUSED_CRYSTAL_SHARD.get()));
        dropSelf(ModRegistration.QUARRY_STABILIZER.get());
        // TODO: Need to drop items
        dropSelf(ModRegistration.CRYSTAL_PEDESTAL.get());
        add(ModRegistration.CRYSTAL_ORE.get(), createOreDrop(ModRegistration.CRYSTAL_ORE.get(), ModRegistration.CRYSTAL.get()));
        add(ModRegistration.CRYSTAL_DEEPSLATE_ORE.get(), createOreDrop(ModRegistration.CRYSTAL_DEEPSLATE_ORE.get(), ModRegistration.CRYSTAL.get()));
        createTorchTable(ModRegistration.CRYSTAL_TORCH.get());
        createComponentSavingTable(ModRegistration.CRYSTAL_FURNACE.get(),
                DataComponents.FURNACE_DATA.get(),
                DataComponents.FURNACE_UPGRADES.get(),
                DataComponents.LEVELABLE_BLOCK_ENTITY_DATA.get(),
                net.minecraft.core.component.DataComponents.CONTAINER,
                DataComponents.AUTO_OUTPUT.get());
        createComponentSavingTable(ModRegistration.CRYSTAL_GENERATOR.get(),
                DataComponents.LEVELABLE_BLOCK_ENTITY_DATA.get(),
                DataComponents.GENERATOR_DATA.get(),
                DataComponents.GENERATOR_UPGRADES.get(),
                net.minecraft.core.component.DataComponents.CONTAINER);
        createComponentSavingTable(ModRegistration.CRYSTAL_QUARRY.get(),
                DataComponents.LEVELABLE_BLOCK_ENTITY_DATA.get(),
                net.minecraft.core.component.DataComponents.CONTAINER,
                DataComponents.QUARRY_BOUNDS.get(),
                DataComponents.QUARRY_DATA.get(),
                DataComponents.QUARRY_UPGRADES.get(),
                DataComponents.QUARRY_SETTINGS.get(),
                DataComponents.FILTER_INVENTORY.get(),
                DataComponents.AUTO_OUTPUT.get(),
                DataComponents.CHUNKLOADING.get());
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

    private void createTorchTable(Block block) {
        LootPool.Builder builder = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(AnyOfCondition.anyOf(
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CrystalTorch.DROP_ITEM, true)),
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModRegistration.CRYSTAL_WALL_TORCH.get())
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CrystalTorch.DROP_ITEM, true))
                ))
                .add(this.applyExplosionCondition(block, LootItem.lootTableItem(block)));

        add(block, LootTable.lootTable().withPool(builder));
    }
}
