package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;

public class SwordLevelableTool extends LevelableTool {

    public SwordLevelableTool() {
        this("sword", 3, -2.4f);
    }

    public SwordLevelableTool(String itemType, float attackDamageModifier, float attackSpeedModifier) {
        super(new Item.Properties(), null, itemType, attackDamageModifier, attackSpeedModifier);
    }

    public boolean canAttackBlock(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, Player pPlayer) {
        return !pPlayer.isCreative();
    }

    public float getDestroySpeed(@NotNull ItemStack pStack, @NotNull BlockState pState) {
        if (pState.is(Blocks.COBWEB) && !ToolUtils.isBroken(pStack)) {
            return 15.0F;
        } else {
            return pState.is(BlockTags.SWORD_EFFICIENT) ? 1.5F : 1.0F;
        }
    }

    public boolean isCorrectToolForDrops(BlockState pBlock) {
        return pBlock.is(Blocks.COBWEB);
    }

    @Override
    public boolean hurtEnemy(ItemStack tool, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (this.isDisabled()) {
            tool.shrink(1);
            return false;
        }

        tool.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);

        if (!ToolUtils.isBroken(tool)) {
            if (tool.getOrDefault(DataComponents.FIRE, false)) {
                target.igniteForSeconds(2);
            }

            if (ToolUtils.isValidEntity(target)) {
                int heal = tool.getOrDefault(DataComponents.LIFESTEAL, 0);

                if (heal > 0) {
                    attacker.heal(heal);
                }

                addExp(tool, target.level(), attacker.getOnPos(), attacker, (int) (getAttackDamage(tool) * getExperienceBoost()));
            }
        }

        return true;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(itemAbility);
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_SWORD.get();
    }

    protected double getExperienceBoost() {
        return CrystalToolsConfig.SWORD_EXPERIENCE_BOOST.get();
    }
}
