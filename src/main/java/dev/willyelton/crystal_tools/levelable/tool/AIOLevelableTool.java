package dev.willyelton.crystal_tools.levelable.tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.keybinding.KeyBindings;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.StringUtils;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class AIOLevelableTool extends DiggerLevelableTool {
    public AIOLevelableTool() {
        super(new Properties(), BlockTags.MINEABLE_WITH_PICKAXE, "aiot", 3, -2.4F);
    }

    @Override
    public boolean correctTool(ItemStack tool, BlockState blockState) {
        return blockState.getDestroySpeed(null, null) != -1;
    }

    // From Sword
    @Override
    public boolean hurtEnemy(ItemStack tool, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (this.isDisabled()) {
            tool.shrink(1);
            return false;
        }

        tool.hurtAndBreak(1, attacker, (p_43296_) -> {
            p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });

        if (!ToolUtils.isBroken(tool)) {
            if (NBTUtils.getFloatOrAddKey(tool, "fire") > 0) {
                target.setSecondsOnFire(5);
            }

            int heal = (int) NBTUtils.getFloatOrAddKey(tool, "lifesteal");

            if (heal > 0) {
                attacker.heal(heal);
            }

            addExp(tool, target.level(), attacker.getOnPos(), attacker, (int) (SwordLevelableTool.getAttackDamage(tool)));
        }

        return true;
    }

    // From Sword
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND && !ToolUtils.isBroken(stack)) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", SwordLevelableTool.getAttackDamage(stack), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", SwordLevelableTool.getAttackSpeed(stack), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier("Weapon modifier", SwordLevelableTool.getAttackKnockback(stack), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier("Weapon modifier", SwordLevelableTool.getKnockbackResistance(stack), AttributeModifier.Operation.ADDITION));
            return builder.build();
        } else {
            return super.getAttributeModifiers(slot, stack);
        }
    }

    @Override
    public boolean onBlockStartBreak(ItemStack tool, BlockPos pos, Player player) {
        return super.onBlockStartBreak(tool, pos, player);
    }
    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();

        if (this.isDisabled()) {
            stack.shrink(1);
            return InteractionResult.FAIL;
        }

        UseMode mode = UseMode.fromString(NBTUtils.getString(stack, "use_mode"));

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
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return true;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        super.appendHoverText(itemStack, level, components, flag);
        String toolTip = "\u00A79" + "Mode: " + StringUtils.capitalize(NBTUtils.getString(itemStack, "use_mode").toLowerCase(Locale.ROOT));
        if (KeyBindings.modeSwitch != null) {
            toolTip = toolTip + " (alt + " + KeyBindings.modeSwitch.getKey().getDisplayName().getString() + " to change)";
        }
        components.add(Component.literal(toolTip));
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
