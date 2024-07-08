package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.block.CrystalTorch;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalFurnaceBlockEntity;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;

public class CrystalToolsLootTables extends VanillaBlockLoot {
    public CrystalToolsLootTables(HolderLookup.Provider provider) {
        super(provider);
    }

    @Override
    protected void generate() {
        dropSelf(Registration.CRYSTAL_BLOCK.get());
        add(Registration.CRYSTAL_ORE.get(), createOreDrop(Registration.CRYSTAL_ORE.get(), Registration.CRYSTAL.get()));
        add(Registration.CRYSTAL_DEEPSLATE_ORE.get(), createOreDrop(Registration.CRYSTAL_DEEPSLATE_ORE.get(), Registration.CRYSTAL.get()));
        createTorchTable(Registration.CRYSTAL_TORCH.get());
        createStandardTable(Registration.CRYSTAL_FURNACE.get(), Registration.CRYSTAL_FURNACE_BLOCK_ENTITY.get(), CrystalFurnaceBlockEntity.NBT_TAGS.toArray(new String[0]));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.entrySet().stream()
                .filter(e -> e.getKey().location().getNamespace().equals(CrystalTools.MODID))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private void createStandardTable(Block block, BlockEntityType<?> type, String... tags) {
        LootPoolSingletonContainer.Builder<?> lti = LootItem.lootTableItem(block);
        lti.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY));
        lti.apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                .include(DataComponents.FURNACE_DATA.get())
                .include(DataComponents.FURNACE_UPGRADES.get())
                .include(DataComponents.LEVELABLE_BLOCK_ENTITY_DATA.get())
                .include(DataComponents.FURNACE_INVENTORY.get()));

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
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(Registration.CRYSTAL_WALL_TORCH.get())
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CrystalTorch.DROP_ITEM, true))
                ))
                .add(this.applyExplosionCondition(block, LootItem.lootTableItem(block)));

        add(block, LootTable.lootTable().withPool(builder));
    }
}
