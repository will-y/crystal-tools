package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import static net.neoforged.neoforge.common.ItemAbilities.DEFAULT_AXE_ACTIONS;
import static net.neoforged.neoforge.common.ItemAbilities.DEFAULT_HOE_ACTIONS;
import static net.neoforged.neoforge.common.ItemAbilities.DEFAULT_PICKAXE_ACTIONS;
import static net.neoforged.neoforge.common.ItemAbilities.DEFAULT_SHEARS_ACTIONS;
import static net.neoforged.neoforge.common.ItemAbilities.DEFAULT_SHOVEL_ACTIONS;
import static net.neoforged.neoforge.common.ItemAbilities.DEFAULT_SWORD_ACTIONS;

public class AIOLevelableTool extends DiggerLevelableTool {
    public static final Set<ItemAbility> AIOT_ACTIONS = new HashSet<>();

    public AIOLevelableTool() {
        super(new Properties(), BlockTags.MINEABLE_WITH_PICKAXE, "aiot", 3, -2.4F);
        AIOT_ACTIONS.addAll(DEFAULT_AXE_ACTIONS);
        AIOT_ACTIONS.addAll(DEFAULT_HOE_ACTIONS);
        AIOT_ACTIONS.addAll(DEFAULT_SHOVEL_ACTIONS);
        AIOT_ACTIONS.addAll(DEFAULT_PICKAXE_ACTIONS);
        AIOT_ACTIONS.addAll(DEFAULT_SWORD_ACTIONS);
    }

    @Override
    public boolean correctTool(ItemStack tool, BlockState blockState) {
        // TODO: Seems wrong
        return blockState.getDestroySpeed(null, null) != -1;
    }

    // TODO: Move this and one in sword to LevelableTool
    @Override
    public boolean hurtEnemy(ItemStack tool, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (this.isDisabled()) {
            tool.shrink(1);
            return false;
        }

        tool.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);

        if (!ToolUtils.isBroken(tool)) {
            if (tool.getOrDefault(DataComponents.FIRE, false)) {
                target.setRemainingFireTicks(5);
            }

            if (ToolUtils.isValidEntity(target)) {
                int heal = tool.getOrDefault(DataComponents.LIFESTEAL, 0);

                if (heal > 0) {
                    attacker.heal(heal);
                }

                addExp(tool, target.level(), attacker.getOnPos(), attacker, (int) (getAttackDamage(tool)));
            }
        }

        return true;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();

        if (this.isDisabled()) {
            stack.shrink(1);
            return InteractionResult.FAIL;
        }

        UseMode mode = UseMode.fromString(stack.getOrDefault(DataComponents.USE_MODE, ""));

        switch (mode) {
            case HOE -> {
                return ToolUseUtils.useOnHoe3x3(context, this);
            }
            case SHOVEL -> {
                return ToolUseUtils.useOnShovel3x3(context, this);
            }
            case AXE -> {
                return ToolUseUtils.useOnAxeVeinStrip(context, this);
            }
            case TORCH -> {
                return useOnTorch(context);
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return AIOT_ACTIONS.contains(itemAbility);
    }

    public InteractionResult useOnTorch(UseOnContext context) {
        return ToolUseUtils.useOnTorch(context, this);
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_AIOT.get();
    }

    @Override
    public boolean canVeinMin(ItemStack stack, BlockState blockState) {
        return blockState.is(Tags.Blocks.ORES) || blockState.is(BlockTags.LOGS) || (blockState.is(BlockTags.LEAVES));
    }
}
