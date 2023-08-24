package dev.willyelton.crystal_tools.levelable.tool;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.keybinding.KeyBindings;
import dev.willyelton.crystal_tools.utils.BlockCollectors;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.RayTraceUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.Tags;

import java.util.List;

/**
 * Class for the shovel, pickaxe, AIOT
 * Handles the 3x3 mining and vein mining
 */
public abstract class DiggerLevelableTool extends LevelableTool {

    public DiggerLevelableTool(Properties properties, TagKey<Block> mineableBlocks, String toolType, float attackDamageModifier, float attackSpeedModifier) {
        super(properties, mineableBlocks, toolType, attackDamageModifier, attackSpeedModifier);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack tool, BlockPos pos, Player player) {
        Level level = player.level();
        BlockState blockState = level.getBlockState(pos);
        if (NBTUtils.getFloatOrAddKey(tool, "3x3") > 0 && !NBTUtils.getBoolean(tool, "disable_3x3")) {
            BlockHitResult result = RayTraceUtils.rayTrace(player);
            Direction direction = result.getDirection();
            float firstBlockSpeed = blockState.getDestroySpeed(level, pos);
            breakBlockCollection(tool, level, BlockCollectors.collect3x3(pos, direction), player, firstBlockSpeed);
            return true;
        }

        if (NBTUtils.getFloatOrAddKey(tool, "vein_miner") > 0 && KeyBindings.veinMine.isDown() && blockState.is(Tags.Blocks.ORES)) {
            this.veinMinerHelper(tool, level, blockState.getBlock(), pos, player,0);
            return true;
        }

        return super.onBlockStartBreak(tool, pos, player);
    }

    private void veinMinerHelper(ItemStack tool, Level level, Block block, BlockPos blockPos, LivingEntity entity, int depth) {
        if (depth >= CrystalToolsConfig.VEIN_MINER_DEFAULT_RANGE.get() + NBTUtils.getFloatOrAddKey(tool, "vein_miner") - 1 || !level.getBlockState(blockPos).is(block)) {
            return;
        }

        breakBlock(tool, level, blockPos, entity);

        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                for (int z = -1; z < 2; z++) {
                    if (x != 0 || y != 0 || z != 0) {
                        veinMinerHelper(tool, level, block, blockPos.offset(x, y, z), entity, depth + 1);
                    }
                }
            }
        }
    }
}
