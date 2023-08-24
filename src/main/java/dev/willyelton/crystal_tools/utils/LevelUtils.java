package dev.willyelton.crystal_tools.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;

public class LevelUtils {
    // I stole this from Level.java because it is bad and I need to make it better
    public static boolean destroyBlock(Level level, BlockPos pPos, boolean pDropBlock, @Nullable Entity pEntity, int pRecursionLeft, ItemStack tool) {
        BlockState blockstate = level.getBlockState(pPos);
        if (blockstate.isAir()) {
            return false;
        } else {
            FluidState fluidstate = level.getFluidState(pPos);
            if (!(blockstate.getBlock() instanceof BaseFireBlock)) {
                level.levelEvent(2001, pPos, Block.getId(blockstate));
            }

            if (pDropBlock) {
                BlockEntity blockentity = blockstate.hasBlockEntity() ? level.getBlockEntity(pPos) : null;
                // Change to tool
                if (pEntity != null) {
                    Block.dropResources(blockstate, level, pPos, blockentity, pEntity, tool);

                    if (pEntity instanceof ServerPlayer player && level instanceof ServerLevel serverLevel) {
                        int exp = net.minecraftforge.common.ForgeHooks.onBlockBreakEvent(level, GameType.SURVIVAL, player, pPos);

                        if (exp > 0) {
                            blockstate.getBlock().popExperience(serverLevel, pPos, exp);
                        }
                    }
                } else {
                    Block.dropResources(blockstate, level, pPos, blockentity, pEntity, tool);
                }
            }

            boolean flag = level.setBlock(pPos, fluidstate.createLegacyBlock(), 3, pRecursionLeft);
            if (flag) {
                level.gameEvent(pEntity, GameEvent.BLOCK_DESTROY, pPos);
            }

            return flag;
        }
    }
}
