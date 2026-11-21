package dev.willyelton.crystal.core.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;

/// Use on context that can override the clicked block pos (for 3x3 and vein block transformations)
public class FakeUseOnContext extends UseOnContext {
    private BlockPos blockPos;

    public FakeUseOnContext(Player player, InteractionHand hand, BlockHitResult hitResult, BlockPos blockPos) {
        super(player, hand, hitResult);
        this.blockPos = blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    @Override
    public BlockPos getClickedPos() {
        return blockPos;
    }

    public static FakeUseOnContext withBlockPos(UseOnContext context, BlockPos pos) {
        return new FakeUseOnContext(context.getPlayer(), context.getHand(), context.getHitResult(), pos);
    }
}
