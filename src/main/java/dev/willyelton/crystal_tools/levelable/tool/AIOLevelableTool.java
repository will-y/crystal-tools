package dev.willyelton.crystal_tools.levelable.tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.willyelton.crystal_tools.keybinding.KeyBindings;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.StringUtils;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class AIOLevelableTool extends DiggerLevelableTool {
    public AIOLevelableTool() {
        super(new Properties(), BlockTags.MINEABLE_WITH_PICKAXE, "aiot");
    }

    @Override
    public boolean correctTool(ItemStack tool, BlockState blockState) {
        return true;
    }

    // From Sword
    @Override
    public boolean hurtEnemy(ItemStack tool, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
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

            addExp(tool, target.getLevel(), attacker.getOnPos(), attacker, (int) (SwordLevelableTool.getAttackDamage(tool)));
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
    public @NotNull InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();

        UseMode mode = UseMode.fromString(NBTUtils.getString(stack, "use_mode"));

        switch (mode) {
            case HOE -> {
                return useOnHoe(context);
            }
            case SHOVEL -> {
                return useOnShovel(context);
            }
            case AXE -> {
                return useOnAxe(context);
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

    public InteractionResult useOnAxe(UseOnContext pContext) {
        return ToolUseUtils.useOnAxe(pContext, this);
    }

    public InteractionResult useOnShovel(UseOnContext pContext) {
        return ToolUseUtils.useOnShovel(pContext, this);
    }

    public InteractionResult useOnTorch(UseOnContext context) {
        return ToolUseUtils.useOnTorch(context, this);
    }

    public InteractionResult useOnHoe(UseOnContext context) {
        return ToolUseUtils.useOnHoe(context, this);
    }
}
