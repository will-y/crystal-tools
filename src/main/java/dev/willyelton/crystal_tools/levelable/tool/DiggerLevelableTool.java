package dev.willyelton.crystal_tools.levelable.tool;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.keybinding.KeyBindings;
import dev.willyelton.crystal_tools.utils.BlockCollectors;
import dev.willyelton.crystal_tools.utils.LevelUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.RayTraceUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

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
    public boolean mineBlock(@NotNull ItemStack tool, Level level, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity entity) {
        if (NBTUtils.getFloatOrAddKey(tool, "3x3") > 0 && !NBTUtils.getBoolean(tool, "disable_3x3")) {
            BlockHitResult result = RayTraceUtils.rayTrace(entity);
            Direction direction = result.getDirection();
            float firstBlockSpeed = blockState.getDestroySpeed(level, blockPos);
            blockBreakerHelper(tool, level, BlockCollectors.collect3x3(blockPos, direction), entity, firstBlockSpeed);
        }

        if (NBTUtils.getFloatOrAddKey(tool, "vein_miner") > 0 && KeyBindings.veinMine.isDown() && blockState.is(Tags.Blocks.ORES)) {
            this.veinMinerHelper(tool, level, blockState.getBlock(), blockPos, entity,0);
        }

        return super.mineBlock(tool, level, blockState, blockPos, entity);
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

    private void blockBreakerHelper(ItemStack tool, Level level, List<BlockPos> blockPosCollection, LivingEntity entity, float firstBlockSpeed) {
        for (BlockPos pos : blockPosCollection) {
            if (level.getBlockState(pos).getDestroySpeed(level, pos) <= firstBlockSpeed + 20) {
                breakBlock(tool, level, pos, entity);
            }
        }
    }

    private void breakBlock(ItemStack tool, Level level, BlockPos blockPos, LivingEntity entity) {
        BlockState blockState = level.getBlockState(blockPos);
        if (isCorrectToolForDrops(tool, blockState)) {
            if (NBTUtils.getFloatOrAddKey(tool, "auto_smelt") > 0) {
                dropSmeltedItem(tool, level, blockState, blockPos, entity);
            } else {
                LevelUtils.destroyBlock(level, blockPos, true, entity, 512, tool);
            }
            if (!level.isClientSide && blockState.getDestroySpeed(level, blockPos) != 0.0F) {
                tool.hurtAndBreak(1, entity, (player) -> player.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            }
            addExp(tool, level, blockPos, entity);
        }
    }
}
