package dev.willyelton.crystal.core.datagen;

import dev.willyelton.crystal.core.common.datamap.CrystalCoreDataMaps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class CrystalCoreDataMapGenerator extends DataMapProvider {
    public CrystalCoreDataMapGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(CrystalCoreDataMaps.MOB_HEADS)
                .add(EntityType.ZOMBIE.builtInRegistryHolder(), Items.ZOMBIE_HEAD, false)
                .add(EntityType.CREEPER.builtInRegistryHolder(), Items.CREEPER_HEAD, false)
                .add(EntityType.PIGLIN.builtInRegistryHolder(), Items.PIGLIN_HEAD, false)
                .add(EntityType.PIGLIN_BRUTE.builtInRegistryHolder(), Items.PIGLIN_HEAD, false)
                .add(EntityType.ENDER_DRAGON.builtInRegistryHolder(), Items.DRAGON_HEAD, false)
                .add(EntityType.SKELETON.builtInRegistryHolder(), Items.SKELETON_SKULL, false)
                .add(EntityType.WITHER_SKELETON.builtInRegistryHolder(), Items.WITHER_SKELETON_SKULL, false)
                .add(EntityType.PLAYER.builtInRegistryHolder(), Items.PLAYER_HEAD, false);
    }
}
