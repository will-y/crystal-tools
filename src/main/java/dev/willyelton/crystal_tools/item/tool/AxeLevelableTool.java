package dev.willyelton.crystal_tools.item.tool;

import dev.willyelton.crystal_tools.keybinding.KeyBindings;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class AxeLevelableTool extends LevelableTool {
    public static final int MAX_RECURSIVE_DEPTH = 10;


    public AxeLevelableTool() {
        super(new Item.Properties(), BlockTags.MINEABLE_WITH_AXE, "axe");
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext pContext) {
        InteractionResult result = ToolUseUtils.useOnAxe(pContext, this);

        if (result == InteractionResult.SUCCESS || result == InteractionResult.sidedSuccess(pContext.getLevel().isClientSide)) {
            ItemStack itemStack = pContext.getItemInHand();
            Level level = pContext.getLevel();
            Player player = pContext.getPlayer();
            BlockPos blockPos = pContext.getClickedPos();

            if (NBTUtils.getFloatOrAddKey(itemStack, "tree_strip") > 0 && KeyBindings.veinMine.isDown()) {
                stripHelper(pContext, level, itemStack, player, blockPos.above(), pContext.getHand(), 0);
            }
        }

        return result;
    }

    private void stripHelper(UseOnContext context, Level level, ItemStack itemStack, Player player, BlockPos blockPos, InteractionHand slot, int depth) {
        int durability = this.getMaxDamage(itemStack) - (int) NBTUtils.getFloatOrAddKey(itemStack, "Damage");

        if (depth > MAX_RECURSIVE_DEPTH || durability <= 1) {
            return;
        }

        BlockState blockState = level.getBlockState(blockPos);
        Optional<BlockState> optional = Optional.ofNullable(blockState.getToolModifiedState(context, net.minecraftforge.common.ToolActions.AXE_STRIP, false));

        if (optional.isPresent()) {
            level.setBlock(blockPos, optional.get(), 11);

            if (player != null) {
                itemStack.hurtAndBreak(1, player, (p_150686_) -> {
                    p_150686_.broadcastBreakEvent(slot);
                });
            }

            addExp(itemStack, level, blockPos, player);

            List<BlockPos> positionsToLook = List.of(blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(),
                    blockPos.north().east(), blockPos.north().west(), blockPos.south().east(), blockPos.south().west());

            for (BlockPos pos : positionsToLook) {
                stripHelper(context, level, itemStack, player, pos, slot, depth + 1);
            }

            for (BlockPos pos : positionsToLook) {
                stripHelper(context, level, itemStack, player, pos.above(), slot, depth + 1);
            }

            stripHelper(context, level, itemStack, player, blockPos.above(), slot, depth + 1);
        }
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return net.minecraftforge.common.ToolActions.DEFAULT_AXE_ACTIONS.contains(toolAction);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack tool, Level level, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity entity) {
//        if (!level.isClientSide && blockState.getDestroySpeed(level, blockPos) != 0.0F) {
//            tool.hurtAndBreak(1, entity, (player) -> {
//                player.broadcastBreakEvent(EquipmentSlot.MAINHAND);
//            });
//        }

        if (NBTUtils.getFloatOrAddKey(tool, "tree_chop") > 0 && KeyBindings.veinMine.isDown() && blockState.is(BlockTags.MINEABLE_WITH_AXE)) {
            Block minedBlock = blockState.getBlock();
            recursiveBreakHelper(tool, level, blockPos, entity, minedBlock, 0);
        }

//        addExp(tool, level, blockPos);

        super.mineBlock(tool, level, blockState, blockPos, entity);

        return true;
    }

    private void recursiveBreakHelper(ItemStack tool, Level level, BlockPos blockPos, LivingEntity entity, Block block, int depth) {

        if (depth > MAX_RECURSIVE_DEPTH) {
            return;
        }

        BlockState state = level.getBlockState(blockPos);
        if (state.is(block)) {
            level.destroyBlock(blockPos, true, entity);
            tool.hurtAndBreak(1, entity, (player) -> {
                player.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        } else {
            return;
        }

        List<BlockPos> positionsToLook = List.of(blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(),
                blockPos.north().east(), blockPos.north().west(), blockPos.south().east(), blockPos.south().west());

        for (BlockPos pos : positionsToLook) {
            recursiveBreakHelper(tool, level, pos, entity, block, depth + 1);
        }

        for (BlockPos pos : positionsToLook) {
            recursiveBreakHelper(tool, level, pos.above(), entity, block, depth + 1);
        }

        recursiveBreakHelper(tool, level, blockPos.above(), entity, block, depth + 1);
    }

    @Override
    public boolean correctTool(ItemStack tool, BlockState blockState) {
        return super.correctTool(tool, blockState) || (NBTUtils.getFloatOrAddKey(tool, "leaf_mine") > 0 && blockState.is(BlockTags.MINEABLE_WITH_HOE));
    }
}
