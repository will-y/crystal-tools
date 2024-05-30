package dev.willyelton.crystal_tools.levelable.tool;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.keybinding.KeyBindings;
import dev.willyelton.crystal_tools.utils.BlockCollectors;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.RayTraceUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.Tags;

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
    public boolean onBlockStartBreak(ItemStack tool, BlockPos pos, Player player) {
        Level level = player.level();
        BlockState blockState = level.getBlockState(pos);
        if (NBTUtils.getFloatOrAddKey(tool, "vein_miner") > 0 && canVeinMin(tool, blockState)) {
            if (level.isClientSide && KeyBindings.veinMine.isDown()) {
                Collection<BlockPos> toMine = BlockCollectors.collectVeinMine(pos, level, this.getVeinMinerPredicate(blockState), this.getMaxBlocks(tool));
                this.breakBlockCollection(tool, level, toMine, player, blockState.getDestroySpeed(level, pos), true);
                return true;
            }
        }

        if (NBTUtils.getFloatOrAddKey(tool, "3x3") > 0 && !NBTUtils.getBoolean(tool, "disable_3x3")) {
            BlockHitResult result = RayTraceUtils.rayTrace(player);
            Direction direction = result.getDirection();
            float firstBlockSpeed = blockState.getDestroySpeed(level, pos);
            this.breakBlockCollection(tool, level, BlockCollectors.collect3x3(pos, direction), player, firstBlockSpeed);
            return true;
        }

        return super.onBlockStartBreak(tool, pos, player);
    }

    @Override
    public Predicate<BlockState> getVeinMinerPredicate(BlockState minedBlockState) {
        return blockState -> blockState.is(minedBlockState.getBlock());
    }

    @Override
    public int getMaxBlocks(ItemStack stack) {
        if (ToolUtils.isBroken(stack)) return 0;
        return (int) (CrystalToolsConfig.VEIN_MINER_DEFAULT_RANGE.get() + NBTUtils.getFloatOrAddKey(stack, "vein_miner") - 1);
    }

    @Override
    public boolean canVeinMin(ItemStack stack, BlockState blockState) {
        return blockState.is(Tags.Blocks.ORES);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get() &&
                (super.canApplyAtEnchantingTable(stack, enchantment) || enchantment.category.equals(EnchantmentCategory.DIGGER));
    }
}
