package dev.willyelton.crystal_tools.item.tool;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class ShovelLevelableTool extends DiggerLevelableTool {
    // Just from ShovelItem
    protected static final Map<Block, BlockState> FLATTENABLES = Maps.newHashMap((new ImmutableMap.Builder()).put(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.DIRT, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.PODZOL, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.COARSE_DIRT, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.MYCELIUM, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.ROOTED_DIRT, Blocks.DIRT_PATH.defaultBlockState()).build());

    public ShovelLevelableTool() {
        super(new Item.Properties(), BlockTags.MINEABLE_WITH_SHOVEL, "shovel");
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (this.isDisabled()) {
            pContext.getItemInHand().shrink(1);
            return InteractionResult.FAIL;
        }

        ItemStack shovel = pContext.getItemInHand();
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);

        int durability = this.getMaxDamage(shovel) - (int) NBTUtils.getFloatOrAddKey(shovel, "Damage");

        if (durability <= 1) {
            return InteractionResult.PASS;
        }

        if (pContext.getClickedFace() == Direction.DOWN) {
            return InteractionResult.PASS;
        } else {
            Player player = pContext.getPlayer();
            BlockState blockstate1 = blockstate.getToolModifiedState(level, blockpos, player, pContext.getItemInHand(), net.minecraftforge.common.ToolActions.SHOVEL_FLATTEN);
            BlockState blockstate2 = null;
            if (blockstate1 != null && level.isEmptyBlock(blockpos.above())) {
                level.playSound(player, blockpos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                blockstate2 = blockstate1;
            } else if (blockstate.getBlock() instanceof CampfireBlock && blockstate.getValue(CampfireBlock.LIT)) {
                if (!level.isClientSide()) {
                    level.levelEvent((Player)null, 1009, blockpos, 0);
                }

                CampfireBlock.dowse(pContext.getPlayer(), level, blockpos, blockstate);
                blockstate2 = blockstate.setValue(CampfireBlock.LIT, Boolean.valueOf(false));
            }

            if (blockstate2 != null) {
                if (!level.isClientSide) {
                    level.setBlock(blockpos, blockstate2, 11);
                    if (player != null) {
                        pContext.getItemInHand().hurtAndBreak(1, player, (p_43122_) -> {
                            p_43122_.broadcastBreakEvent(pContext.getHand());
                        });
                    }
                }

                addExp(shovel, level, blockpos, pContext.getPlayer());

                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return net.minecraftforge.common.ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(toolAction);
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_SHOVEL.get();
    }
}
