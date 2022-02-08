package dev.willyelton.crystal_tools.utils;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.item.LevelableItem;
import dev.willyelton.crystal_tools.item.skill.SkillData;
import dev.willyelton.crystal_tools.item.skill.SkillDataNode;
import dev.willyelton.crystal_tools.item.skill.SkillNodeType;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;
import java.util.List;

public class LevelUtils {
    // I stole this from Level.java because it is bad and I need to make it better
    public static boolean destroyBlock(Level level, BlockPos pPos, boolean pDropBlock, @Nullable Entity pEntity, int pRecursionLeft, ItemStack tool) {
        BlockState blockstate = level.getBlockState(pPos);
        if (blockstate.isAir()) {
            return false;
        } else {
            FluidState fluidstate = level.getFluidState(pPos);
            if (!(blockstate.getBlock() instanceof BaseFireBlock)) {
                level.levelEvent(2001, pPos, Block.getId(blockstate));
            }

            if (pDropBlock) {
                BlockEntity blockentity = blockstate.hasBlockEntity() ? level.getBlockEntity(pPos) : null;
                // Change to tool
                if (pEntity != null) {
                    Block.dropResources(blockstate, level, pEntity.getOnPos().above(), blockentity, pEntity, tool);
                } else {
                    Block.dropResources(blockstate, level, pPos, blockentity, pEntity, tool);
                }
            }

            boolean flag = level.setBlock(pPos, fluidstate.createLegacyBlock(), 3, pRecursionLeft);
            if (flag) {
                level.gameEvent(pEntity, GameEvent.BLOCK_DESTROY, pPos);
            }

            return flag;
        }
    }
}
