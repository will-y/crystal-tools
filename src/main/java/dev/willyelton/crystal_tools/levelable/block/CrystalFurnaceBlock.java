package dev.willyelton.crystal_tools.levelable.block;

import dev.willyelton.crystal_tools.levelable.block.container.CrystalFurnaceContainer;
import dev.willyelton.crystal_tools.levelable.block.entity.CrystalFurnaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class CrystalFurnaceBlock extends BaseEntityBlock {
    public CrystalFurnaceBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return null;
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            this.openContainer(pLevel, pPos, pPlayer);
            return InteractionResult.CONSUME;
        }
    }

    protected void openContainer(Level level, @NotNull BlockPos pos, @NotNull Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CrystalFurnaceBlockEntity) {
            MenuProvider containerProvider = new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return Component.literal("Crystal Furnace");
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
                    return new CrystalFurnaceContainer(windowId, playerInventory);
                }
            };
            NetworkHooks.openScreen((ServerPlayer) player, containerProvider, be.getBlockPos());
        } else {
            throw new IllegalStateException("Our named container provider is missing!");
        }
    }

    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new CrystalFurnaceBlockEntity(pPos, pState);
    }


}
