package dev.willyelton.crystal.tools.common.levelable.block.entity;

import dev.willyelton.crystal.core.common.block.entity.BaseContainerBlockEntity;
import dev.willyelton.crystal.core.common.inventory.container.BaseContainerMenu;
import dev.willyelton.crystal.core.common.inventory.container.CrystalContainerMenu;
import dev.willyelton.crystal.tools.ModRegistration;
import dev.willyelton.crystal.tools.common.levelable.block.CrystalChestBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class CrystalChestBlockEntity extends BaseContainerBlockEntity implements LidBlockEntity {

    public static final int WIDTH = 18;
    public static final int HEIGHT = 9;

    private final ChestLidController chestLidController = new ChestLidController();
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState blockState) {
            if (blockState.getBlock() instanceof CrystalChestBlock chestBlock) {
                CrystalChestBlockEntity.playSound(level, pos, chestBlock.getOpenChestSound());
            }
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState blockState) {
            if (blockState.getBlock() instanceof CrystalChestBlock chestBlock) {
                CrystalChestBlockEntity.playSound(level, pos, chestBlock.getCloseChestSound());
            }
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState blockState, int previous, int current) {
            CrystalChestBlockEntity.this.signalOpenCount(level, pos, blockState, previous, current);
        }

        @Override
        public boolean isOwnContainer(Player player) {
            if (player.containerMenu instanceof CrystalContainerMenu crystalContainerMenu) {
                return crystalContainerMenu.blockEntity() == CrystalChestBlockEntity.this;
            }

            return false;
        }
    };

    public CrystalChestBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModRegistration.CRYSTAL_CHEST_BLOCK_ENTITY.get(), worldPosition, blockState,
                Component.translatable("container.crystal_tools.crystal_chest"),
                WIDTH, HEIGHT);
    }

    public ResourceHandler<ItemResource> getItemHandlerCapForSide(Direction side) {
        return getItemHandler();
    }

    @Override
    public float getOpenNess(float a) {
        return this.chestLidController.getOpenness(a);
    }

    public void lidAnimateTick(Level level, BlockPos pos, BlockState state, ChestBlockEntity entity) {
        chestLidController.tickLid();
    }

    public void startOpen(BlockState blockState) {
        if (blockState.getBlock() instanceof CrystalChestBlock) {

        }
    }

    private static void playSound(Level level, BlockPos pos, SoundEvent event) {
        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), event, SoundSource.BLOCKS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    protected void signalOpenCount(Level level, BlockPos pos, BlockState blockState, int previous, int current) {
        Block block = blockState.getBlock();
        level.blockEvent(pos, block, 1, current);
    }
}
