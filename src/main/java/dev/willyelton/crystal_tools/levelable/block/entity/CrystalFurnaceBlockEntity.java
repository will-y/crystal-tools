package dev.willyelton.crystal_tools.levelable.block.entity;

import dev.willyelton.crystal_tools.levelable.block.ModBlocks;
import dev.willyelton.crystal_tools.levelable.block.container.CrystalFurnaceContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CrystalFurnaceBlockEntity extends BlockEntity {
    public CrystalFurnaceBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlocks.CRYSTAL_FURNACE_BLOCK_ENTITY.get(), pPos, pBlockState);
    }


}

