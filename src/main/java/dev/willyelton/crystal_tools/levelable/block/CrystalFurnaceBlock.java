package dev.willyelton.crystal_tools.levelable.block;

import dev.willyelton.crystal_tools.levelable.block.entity.CrystalFurnaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CrystalFurnaceBlock extends AbstractFurnaceBlock {
    public CrystalFurnaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void openContainer(Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof CrystalFurnaceBlockEntity) {
            pPlayer.openMenu((MenuProvider)blockentity);
        }
    }

    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new CrystalFurnaceBlockEntity(pPos, pState);
    }

}
