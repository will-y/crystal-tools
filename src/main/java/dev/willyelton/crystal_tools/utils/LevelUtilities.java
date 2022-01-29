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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;
import java.util.List;

public class LevelUtilities {
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
                Block.dropResources(blockstate, level, pPos, blockentity, pEntity, tool);
            }

            boolean flag = level.setBlock(pPos, fluidstate.createLegacyBlock(), 3, pRecursionLeft);
            if (flag) {
                level.gameEvent(pEntity, GameEvent.BLOCK_DESTROY, pPos);
            }

            return flag;
        }
    }

    // I hate this but needed because armor needs to actually be an ArmorItem
    public static void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag, LevelableItem item) {
        int newExperience = (int) NBTUtils.getFloatOrAddKey(itemStack, "experience");
        int experienceCap = (int) NBTUtils.getFloatOrAddKey(itemStack, "experience_cap", CrystalToolsConfig.BASE_EXPERIENCE_CAP.get());

        int durability = item.getMaxDamage(itemStack) - (int) NBTUtils.getFloatOrAddKey(itemStack, "Damage");

        if (durability <= 1) {
            components.add(new TextComponent("\u00A7c\u00A7l" + "Broken"));
        }

        components.add(new TextComponent(String.format("%d/%d XP To Next Level", newExperience, experienceCap)));
        int skillPoints = (int) NBTUtils.getFloatOrAddKey(itemStack, "skill_points");
        if (skillPoints > 0) {
            components.add(new TextComponent(String.format("%d Unspent Skill Points", skillPoints)));
        }

        if (!Screen.hasShiftDown()) {
            components.add(new TextComponent("<Hold Shift For Skills>"));
        } else {
            components.add(new TextComponent("Skills:"));
            int[] points = NBTUtils.getIntArray(itemStack, "points");
            SkillData toolData = SkillData.fromResourceLocation(new ResourceLocation("crystal_tools", String.format("skill_trees/%s.json", item.getItemType())), points);
            for (SkillDataNode dataNode : toolData.getAllNodes()) {
                if (dataNode.isComplete()) {
                    components.add(new TextComponent("    " + dataNode.getName()));
                } else if (dataNode.getType().equals(SkillNodeType.INFINITE) && dataNode.getPoints() > 0) {
                    components.add(new TextComponent("    " + dataNode.getName() + " (" + dataNode.getPoints() + " points)"));
                }
            }
        }
    }

    public static void inventoryTick(ItemStack itemStack, Level level, Entity entity, int inventorySlot, boolean inHand) {
        if (!inHand) {
            if (NBTUtils.getBoolean(itemStack, "auto_repair", false)) {
                if (NBTUtils.addValueToTag(itemStack, "auto_repair_counter", 1) > LevelableItem.AUTO_REPAIR_COUNTER) {
                    NBTUtils.setValue(itemStack, "auto_repair_counter", 0);
                    int repairAmount = Math.min((int) NBTUtils.getFloatOrAddKey(itemStack, "auto_repair_amount"), itemStack.getDamageValue());
                    itemStack.setDamageValue(itemStack.getDamageValue() - repairAmount);
                }
            }
        }
    }
}
