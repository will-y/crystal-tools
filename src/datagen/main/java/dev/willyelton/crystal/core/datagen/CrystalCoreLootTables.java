package dev.willyelton.crystal.core.datagen;

import dev.willyelton.crystal.core.Registration;
import dev.willyelton.crystal.core.common.block.entity.CrystalTorch;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Map;
import java.util.stream.Collectors;

public class CrystalCoreLootTables extends VanillaBlockLoot {
    public CrystalCoreLootTables(HolderLookup.Provider provider) {
        super(provider);
    }

    @Override
    protected void generate() {
        dropSelf(Registration.CRYSTAL_BLOCK.get());
        dropSelf(Registration.CRYSTAL_GEODE.get());
        add(Registration.NETHERITE_INFUSED_CRYSTAL_GEODE.get(), createOreDrop(Registration.NETHERITE_INFUSED_CRYSTAL_GEODE.get(), Registration.NETHERITE_INFUSED_CRYSTAL_SHARD.get()));
        add(Registration.CRYSTAL_ORE.get(), createOreDrop(Registration.CRYSTAL_ORE.get(), Registration.CRYSTAL.get()));
        add(Registration.CRYSTAL_DEEPSLATE_ORE.get(), createOreDrop(Registration.CRYSTAL_DEEPSLATE_ORE.get(), Registration.CRYSTAL.get()));
        createTorchTable(Registration.CRYSTAL_TORCH.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.entrySet().stream()
                .filter(e -> e.getKey().identifier().getNamespace().equals(ApiConstants.CORE_MOD_ID))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
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
