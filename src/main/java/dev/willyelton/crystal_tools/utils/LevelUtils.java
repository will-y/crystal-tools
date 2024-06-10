package dev.willyelton.crystal_tools.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;
import java.util.List;

public class LevelUtils {
    // I stole this from Level.java because it is bad and I need to make it better
    // TODO: Shouldn't ever need this, use break event and BlockDropsEvent
    public static boolean destroyBlock(Level level, BlockPos pos, boolean dropBlock, @Nullable Entity entity, int recursionLeft, ItemStack tool, boolean autoPickup) {
        BlockState blockState = level.getBlockState(pos);
        if (blockState.isAir()) {
            return false;
        } else {
            FluidState fluidstate = level.getFluidState(pos);
            if (!(blockState.getBlock() instanceof BaseFireBlock)) {
                level.levelEvent(2001, pos, Block.getId(blockState));
            }

            if (dropBlock) {
                BlockEntity blockentity = blockState.hasBlockEntity() ? level.getBlockEntity(pos) : null;

                if (entity instanceof ServerPlayer player && level instanceof ServerLevel serverLevel) {
                    List<ItemStack> drops = Block.getDrops(blockState, (ServerLevel) level, pos, blockentity, entity, tool);
                    if (autoPickup) {
                        addToInventoryOrDrop(drops, player, level, pos);
                    } else {
                        Block.dropResources(blockState, level, pos, blockentity, entity, tool);
                    }
                } else {
                    Block.dropResources(blockState, level, pos, blockentity, entity, tool);
                }
            }

            boolean flag = level.setBlock(pos, fluidstate.createLegacyBlock(), 3, recursionLeft);
            if (flag) {
                level.gameEvent(entity, GameEvent.BLOCK_DESTROY, pos);
            }

            return flag;
        }
    }

    public static void addToInventoryOrDrop(List<ItemStack> stacks, ServerPlayer player, Level level, BlockPos pos) {
        boolean pickedUp = false;

        for (ItemStack stack : stacks) {
            if (!player.getInventory().add(stack)) {
                Block.popResource(level, pos, stack);
            } else {
                pickedUp = true;
            }
        }

        if (pickedUp) {
            player.connection.send(new ClientboundSoundPacket(Holder.direct(SoundEvents.ITEM_PICKUP), SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(),  0.2F, (level.random.nextFloat() - level.random.nextFloat()) * 1.4F + 2.0F, level.getRandom().nextLong()));
        }
    }
}
