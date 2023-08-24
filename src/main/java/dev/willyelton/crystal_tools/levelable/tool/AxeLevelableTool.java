package dev.willyelton.crystal_tools.levelable.tool;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.keybinding.KeyBindings;
import dev.willyelton.crystal_tools.utils.BlockCollectors;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import net.minecraft.core.BlockPos;
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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class AxeLevelableTool extends LevelableTool implements VeinMinerLevelableTool {
    public AxeLevelableTool() {
        super(new Item.Properties(), BlockTags.MINEABLE_WITH_AXE, "axe", 5.0F, -3.0F);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext pContext) {
        if (this.isDisabled()) {
            pContext.getItemInHand().shrink(1);
            return InteractionResult.FAIL;
        }
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

        if (depth > CrystalToolsConfig.AXE_VEIN_MINER_DEFAULT_RANGE.get() + NBTUtils.getFloatOrAddKey(itemStack, "tree_strip") - 1 || durability <= 1) {
            return;
        }

        BlockState blockState = level.getBlockState(blockPos);
        Optional<BlockState> optional = Optional.ofNullable(blockState.getToolModifiedState(context, net.minecraftforge.common.ToolActions.AXE_STRIP, false));

        if (optional.isPresent()) {
            level.setBlock(blockPos, optional.get(), 11);

            if (player != null) {
                itemStack.hurtAndBreak(1, player, (p_150686_) -> p_150686_.broadcastBreakEvent(slot));
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
    public boolean onBlockStartBreak(ItemStack tool, BlockPos pos, Player player) {
        Level level = player.level();
        BlockState blockState = level.getBlockState(pos);
        if (NBTUtils.getFloatOrAddKey(tool, "tree_chop") > 0 && KeyBindings.veinMine.isDown() && canVeinMin(blockState)) {
            Collection<BlockPos> toMine = BlockCollectors.collectVeinMine(pos, level, this.getVeinMinerPredicate(blockState), this.getMaxBlocks(tool));
            this.breakBlockCollection(tool, level, toMine, player, blockState.getDestroySpeed(level, pos));
        }

        return super.onBlockStartBreak(tool, pos, player);
    }

    @Override
    public boolean correctTool(ItemStack tool, BlockState blockState) {
        return super.correctTool(tool, blockState) || (NBTUtils.getFloatOrAddKey(tool, "leaf_mine") > 0 && blockState.is(BlockTags.MINEABLE_WITH_HOE));
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_AXE.get();
    }

    @Override
    public Predicate<BlockState> getVeinMinerPredicate(BlockState minedBlockState) {
        return blockState -> blockState.is(minedBlockState.getBlock());
    }

    @Override
    public int getMaxBlocks(ItemStack stack) {
        // TODO: 0 if broken? Or put broken logic in renderer somewhere
        return (int) (CrystalToolsConfig.AXE_VEIN_MINER_DEFAULT_RANGE.get() + NBTUtils.getFloatOrAddKey(stack, "tree_chop") - 1);
    }

    @Override
    public boolean canVeinMin(BlockState blockState) {
        return blockState.is(BlockTags.MINEABLE_WITH_AXE);
    }
}
