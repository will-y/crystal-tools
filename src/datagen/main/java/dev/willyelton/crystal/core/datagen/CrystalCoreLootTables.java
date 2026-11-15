package dev.willyelton.crystal.core.datagen;

import dev.willyelton.crystal.core.Registration;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.advancements.predicates.BlockPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.Map;
import java.util.stream.Collectors;

public class CrystalCoreLootTables extends VanillaBlockLoot {
    public CrystalCoreLootTables(LootTableSubProvider.Context context) {
        super(context);
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
    public Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.entrySet().stream()
                .filter(e -> e.getKey().identifier().getNamespace().equals(ApiConstants.CORE_MOD_ID))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private void createTorchTable(Block block) {
        LootPool.Builder builder = LootPool.lootPool()
                .setRolls(ContextIntProviders.exactly(1))
                .when(AnyOfCondition.anyOf(
                        MatchBlock.blockMatches(BlockPredicate.Builder.block().of(this.blocks, block, Registration.CRYSTAL_WALL_TORCH.get()))
                ))
                .add(this.applyExplosionCondition(block, LootItem.lootTableItem(block)));

        add(block, LootTable.lootTable().withPool(builder));
    }
}
