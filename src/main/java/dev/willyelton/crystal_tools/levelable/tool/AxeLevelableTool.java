package dev.willyelton.crystal_tools.levelable.tool;

import dev.willyelton.crystal_tools.DataComponents;
import dev.willyelton.crystal_tools.VeinMiners;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.keybinding.KeyBindings;
import dev.willyelton.crystal_tools.utils.BlockCollectors;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ToolAction;
import net.neoforged.neoforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class AxeLevelableTool extends DiggerLevelableTool {
    public AxeLevelableTool() {
        super(new Item.Properties(), BlockTags.MINEABLE_WITH_AXE, "axe", 5.0F, -3.0F);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        if (this.isDisabled()) {
            context.getItemInHand().shrink(1);
            return InteractionResult.FAIL;
        }

        return ToolUseUtils.useOnAxeVeinStrip(context, this);
    }

    @Override
    public boolean mineBlock(ItemStack tool, Level level, BlockState blockState, BlockPos pos, LivingEntity entity) {
        if (entity instanceof ServerPlayer player
                && tool.getOrDefault(DataComponents.VEIN_MINER, 0) > 0
                && canVeinMin(tool, blockState)
                && VeinMiners.isVeinMining(player)) {
            if (level.isClientSide && KeyBindings.veinMine.isDown()) {
                Collection<BlockPos> toMine = BlockCollectors.collectVeinMine(pos, level, this.getVeinMinerPredicate(blockState), this.getMaxBlocks(tool));
                this.breakBlockCollection(tool, level, toMine, player, blockState.getDestroySpeed(level, pos), true);
            }
        }

        return super.mineBlock(tool, level, blockState, pos, entity);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_AXE_ACTIONS.contains(toolAction);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack tool, BlockPos pos, Player player) {
        return super.onBlockStartBreak(tool, pos, player);
    }

    @Override
    public boolean correctTool(ItemStack tool, BlockState blockState) {
        return super.correctTool(tool, blockState) || (tool.getOrDefault(DataComponents.LEAF_MINE, false) && blockState.is(BlockTags.LEAVES));
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_AXE.get();
    }

    @Override
    public int getMaxBlocks(ItemStack stack) {
        if (ToolUtils.isBroken(stack)) return 0;
        return CrystalToolsConfig.AXE_VEIN_MINER_DEFAULT_RANGE.get() + stack.getOrDefault(DataComponents.VEIN_MINER, 0) - 1;
    }

    @Override
    public boolean canVeinMin(ItemStack stack, BlockState blockState) {
        return blockState.is(BlockTags.MINEABLE_WITH_AXE) || (stack.getOrDefault(DataComponents.LEAF_MINE, false) && blockState.is(BlockTags.LEAVES));
    }
}
