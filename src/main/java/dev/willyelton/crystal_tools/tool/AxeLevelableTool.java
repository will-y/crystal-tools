package dev.willyelton.crystal_tools.tool;

import dev.willyelton.crystal_tools.item.CreativeTabs;
import dev.willyelton.crystal_tools.keybinding.KeyBindings;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
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

import java.util.Optional;

public class AxeLevelableTool extends LevelableTool {
    public static final int MAX_RECURSIVE_DEPTH = 10;


    public AxeLevelableTool() {
        super(new Item.Properties().fireResistant().tab(CreativeTabs.CRYSTAL_TOOLS_TAB), BlockTags.MINEABLE_WITH_AXE, "axe");
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        Player player = pContext.getPlayer();
        BlockState blockstate = level.getBlockState(blockpos);
        ItemStack axe = pContext.getItemInHand();

        int durability = this.getMaxDamage(axe) - (int) NBTUtils.getFloatOrAddKey(axe, "Damage");

        if (durability <= 1) {
            return InteractionResult.PASS;
        }
        Optional<BlockState> optional = Optional.ofNullable(blockstate.getToolModifiedState(level, blockpos, player, pContext.getItemInHand(), net.minecraftforge.common.ToolActions.AXE_STRIP));
        Optional<BlockState> optional1 = Optional.ofNullable(blockstate.getToolModifiedState(level, blockpos, player, pContext.getItemInHand(), net.minecraftforge.common.ToolActions.AXE_SCRAPE));
        Optional<BlockState> optional2 = Optional.ofNullable(blockstate.getToolModifiedState(level, blockpos, player, pContext.getItemInHand(), net.minecraftforge.common.ToolActions.AXE_WAX_OFF));
        ItemStack itemstack = pContext.getItemInHand();
        Optional<BlockState> optional3 = Optional.empty();
        if (optional.isPresent()) {
            level.playSound(player, blockpos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            optional3 = optional;
        } else if (optional1.isPresent()) {
            level.playSound(player, blockpos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3005, blockpos, 0);
            optional3 = optional1;
        } else if (optional2.isPresent()) {
            level.playSound(player, blockpos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3004, blockpos, 0);
            optional3 = optional2;
        }

        if (optional3.isPresent()) {
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
            }

            level.setBlock(blockpos, optional3.get(), 11);
            if (player != null) {
                itemstack.hurtAndBreak(1, player, (p_150686_) -> {
                    p_150686_.broadcastBreakEvent(pContext.getHand());
                });
            }

            addExp(axe, level, blockpos);

            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return net.minecraftforge.common.ToolActions.DEFAULT_AXE_ACTIONS.contains(toolAction);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack tool, Level level, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity entity) {
        if (!level.isClientSide && blockState.getDestroySpeed(level, blockPos) != 0.0F) {
            tool.hurtAndBreak(1, entity, (player) -> {
                player.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }

        if (NBTUtils.getFloatOrAddKey(tool, "tree_chop") > 0 && KeyBindings.veinMine.isDown() && blockState.is(BlockTags.MINEABLE_WITH_AXE)) {
            Block minedBlock = blockState.getBlock();
            recursiveBreakHelper(tool, level, blockPos, entity, minedBlock, 0);
        }

        addExp(tool, level, blockPos);

        return true;
    }

    private void recursiveBreakHelper(ItemStack tool, Level level, BlockPos blockPos, LivingEntity entity, Block block, int depth) {

        if (depth > 10) {
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

        recursiveBreakHelper(tool, level, blockPos.above(), entity, block, depth + 1);
        recursiveBreakHelper(tool, level, blockPos.north(), entity, block, depth + 1);
        recursiveBreakHelper(tool, level, blockPos.south(), entity, block, depth + 1);
        recursiveBreakHelper(tool, level, blockPos.east(), entity, block, depth + 1);
        recursiveBreakHelper(tool, level, blockPos.west(), entity, block, depth + 1);
    }
}
