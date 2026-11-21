package dev.willyelton.crystal.tools.utils;

import dev.willyelton.crystal.core.Registration;
import dev.willyelton.crystal.core.common.block.FakeUseOnContext;
import dev.willyelton.crystal.core.common.block.entity.CrystalTorch;
import dev.willyelton.crystal.core.common.capability.Capabilities;
import dev.willyelton.crystal.core.common.capability.Levelable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Collection;


public class ToolUseUtils {

    public static InteractionResult useOnTransform(UseOnContext context, Levelable levelable) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        int durability = stack.getMaxDamage() - stack.getDamageValue();
        if (durability <= 1) {
            return InteractionResult.PASS;
        }

        Holder<BlockTransformer> transformerHolder = stack.get(net.minecraft.core.component.DataComponents.BLOCK_TRANSFORMER);

        if (transformerHolder != null) {
            InteractionResult result = transformerHolder.value().transformBlock(context);
            if (result != InteractionResult.PASS) {
                levelable.addExp(level, blockpos, player);
            }

            return result;
        }

        return InteractionResult.PASS;
    }

    public static InteractionResult useOnMultipleBlocks(UseOnContext context, Levelable levelable, Collection<BlockPos> blocks) {
        InteractionResult result = useOnTransform(context, levelable);

        if (result != InteractionResult.PASS) {
            FakeUseOnContext fakeContext = FakeUseOnContext.withBlockPos(context, context.getClickedPos());
            for (BlockPos blockpos : blocks) {
                fakeContext.setBlockPos(blockpos);
                useOnTransform(fakeContext, levelable);
            }
        }

        return result;
    }

    public static InteractionResult useOnTorch(UseOnContext context) {
        ItemStack stack = context.getItemInHand();

        if (stack.getOrDefault(dev.willyelton.crystal.core.common.datacomponent.DataComponents.TORCH, false)) {
            Level level = context.getLevel();
            BlockPos position = context.getClickedPos();
            BlockState state = level.getBlockState(position);
            Direction direction = context.getClickedFace();
            position = position.relative(direction);

            Levelable levelable = stack.getCapability(Capabilities.ITEM_SKILL, level.registryAccess());
            if (levelable == null) {
                return InteractionResult.PASS;
            }

            BlockState torchBlockState;

            if (direction.equals(Direction.UP)) {
                torchBlockState = Registration.CRYSTAL_TORCH.get().defaultBlockState().setValue(CrystalTorch.DROP_ITEM, false);
            } else if (direction.equals(Direction.DOWN)) {
                return InteractionResult.PASS;
            } else {
                torchBlockState = Registration.CRYSTAL_WALL_TORCH.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, direction).setValue(CrystalTorch.DROP_ITEM, false);
            }

            if (level.getBlockState(position).isAir() && state.isFaceSturdy(level, position, direction)) {
                level.setBlock(position, torchBlockState, 0);
                level.playSound(context.getPlayer(), position, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            } else {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide() && context.getPlayer() != null) {
                stack.hurtAndBreak(10, context.getPlayer(), context.getHand().asEquipmentSlot());
            }

            levelable.addExp(level, context.getClickedPos(), context.getPlayer());

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
