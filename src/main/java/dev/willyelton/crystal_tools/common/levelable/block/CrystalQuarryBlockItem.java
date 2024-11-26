package dev.willyelton.crystal_tools.common.levelable.block;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalQuarryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CrystalQuarryBlockItem extends LevelableBlockItem {
    public CrystalQuarryBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof QuarryStabilizer) {
            List<BlockPos> stabilizers = QuarryStabilizer.findStabilizerSquare(context.getClickedPos(), context.getLevel());

            if (stabilizers.size() == 4) {
                context.getItemInHand().set(DataComponents.QUARRY_BOUNDS, stabilizers);
                if (context.getPlayer() != null && !context.getLevel().isClientSide) {
                    context.getPlayer().sendSystemMessage(Component.literal("Stabilizer Positions Saved to Quarry"));
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof CrystalQuarryBlockEntity crystalQuarryBlockEntity) {
            List<BlockPos> positions = stack.getOrDefault(DataComponents.QUARRY_BOUNDS, new ArrayList<>());

            if (positions.size() != 4) {
                // TODO: Some default thing
                crystalQuarryBlockEntity.setPositions(BlockPos.ZERO, BlockPos.ZERO.offset(1, 1, 1));
            } else {
                crystalQuarryBlockEntity.setPositions(positions.get(0), positions.get(2).atY(-64));
            }
        }

        return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
    }
}
