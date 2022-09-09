package dev.willyelton.crystal_tools.levelable.block;

import dev.willyelton.crystal_tools.levelable.block.entity.CrystalFurnaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CrystalFurnaceBlock extends FurnaceBlock {
    public CrystalFurnaceBlock(Properties properties) {
        super(properties);
    }

    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new CrystalFurnaceBlockEntity(pPos, pState);
    }

}
