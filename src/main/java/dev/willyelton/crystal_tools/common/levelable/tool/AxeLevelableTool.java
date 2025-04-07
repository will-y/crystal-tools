package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.VeinMiners;
import dev.willyelton.crystal_tools.client.events.RegisterKeyBindingsEvent;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.BlockCollectors;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.Collection;

public class AxeLevelableTool extends DiggerLevelableTool {
    public AxeLevelableTool(Item.Properties properties) {
        super(properties.axe(CRYSTAL, 5.0F, -3.0F), "axe");
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
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
            if (level.isClientSide && RegisterKeyBindingsEvent.VEIN_MINE.isDown()) {
                Collection<BlockPos> toMine = BlockCollectors.collectVeinMine(pos, level, this.getVeinMinerPredicate(blockState), this.getMaxBlocks(tool));
                this.breakBlockCollection(tool, level, toMine, player, blockState.getDestroySpeed(level, pos), true);
            }
        }

        return super.mineBlock(tool, level, blockState, pos, entity);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(itemAbility);
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
