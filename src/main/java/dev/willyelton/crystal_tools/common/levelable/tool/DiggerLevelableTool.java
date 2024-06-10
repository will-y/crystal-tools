package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.VeinMiners;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.BlockCollectors;
import dev.willyelton.crystal_tools.utils.RayTraceUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.Tags;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Class for the shovel, pickaxe, AIOT
 * Handles the 3x3 mining and vein mining
 */
public abstract class DiggerLevelableTool extends LevelableTool implements VeinMinerLevelableTool {
    public DiggerLevelableTool(Properties properties, TagKey<Block> mineableBlocks, String toolType, float attackDamageModifier, float attackSpeedModifier) {
        super(properties, mineableBlocks, toolType, attackDamageModifier, attackSpeedModifier);
    }

    @Override
    public boolean mineBlock(ItemStack tool, Level level, BlockState blockState, BlockPos pos, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (player instanceof ServerPlayer serverPlayer
                    && tool.getOrDefault(DataComponents.VEIN_MINER, 0) > 0
                    && canVeinMin(tool, blockState)
                    && VeinMiners.isVeinMining(serverPlayer)) {
                Collection<BlockPos> toMine = BlockCollectors.collectVeinMine(pos, level, this.getVeinMinerPredicate(blockState), this.getMaxBlocks(tool));
                this.breakBlockCollection(tool, level, toMine, player, blockState.getDestroySpeed(level, pos), false);
            }
        }

        return super.mineBlock(tool, level, blockState, pos, entity);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack tool, BlockPos pos, Player player, BlockState blockState) {
        if (tool.getOrDefault(DataComponents.HAS_3x3, false) && !tool.getOrDefault(DataComponents.DISABLE_3x3, false)) {
            Level level = player.level();
            BlockHitResult result = RayTraceUtils.rayTrace(player);
            Direction direction = result.getDirection();
            float firstBlockSpeed = blockState.getDestroySpeed(level, pos);
            this.breakBlockCollection(tool, level, BlockCollectors.collect3x3(pos, direction), player, firstBlockSpeed);
            return false;
        }

        return super.onBlockStartBreak(tool,pos, player, blockState);
    }

    @Override
    public Predicate<BlockState> getVeinMinerPredicate(BlockState minedBlockState) {
        return blockState -> blockState.is(minedBlockState.getBlock());
    }

    @Override
    public int getMaxBlocks(ItemStack stack) {
        if (ToolUtils.isBroken(stack)) return 0;
        return CrystalToolsConfig.VEIN_MINER_DEFAULT_RANGE.get() + stack.getOrDefault(DataComponents.VEIN_MINER, 0) - 1;
    }

    @Override
    public boolean canVeinMin(ItemStack stack, BlockState blockState) {
        return blockState.is(Tags.Blocks.ORES);
    }
}
