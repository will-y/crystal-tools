package dev.willyelton.crystal_tools.levelable.tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SwordLevelableTool extends LevelableTool {
    private static final float BASE_ATTACK_DAMAGE = INITIAL_TIER.getAttackDamageBonus() + 3;
    private static final float BASE_ATTACK_SPEED = -2.4f;
    private static final float BASE_ATTACK_KNOCKBACK = 0;
    private static final float BASE_KNOCKBACK_RESISTANCE = 0;

    public SwordLevelableTool() {
        this("sword", 3, -2.4f);
    }

    public SwordLevelableTool(String itemType, float attackDamageModifier, float attackSpeedModifier) {
        super(new Item.Properties(), null, itemType, attackDamageModifier, attackSpeedModifier);
    }

    public static float getAttackDamage(ItemStack stack) {
        return BASE_ATTACK_DAMAGE + NBTUtils.getFloatOrAddKey(stack, "damage_bonus");
    }

    public static float getAttackSpeed(ItemStack stack) {
        return BASE_ATTACK_SPEED + NBTUtils.getFloatOrAddKey(stack, "attack_speed_bonus");
    }

    public static float getAttackKnockback(ItemStack stack) {
        return BASE_ATTACK_KNOCKBACK + NBTUtils.getFloatOrAddKey(stack, "knockback");
    }

    public static float getKnockbackResistance(ItemStack stack) {
        return BASE_KNOCKBACK_RESISTANCE + NBTUtils.getFloatOrAddKey(stack, "knockback_resistance");
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

        tool.hurtAndBreak(1, attacker, (p_43296_) -> p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND));

        if (!ToolUtils.isBroken(tool)) {
            if (NBTUtils.getFloatOrAddKey(tool, "fire") > 0) {
                target.setSecondsOnFire(5);
            }

            if (ToolUtils.isValidEntity(target)) {
                int heal = (int) NBTUtils.getFloatOrAddKey(tool, "lifesteal");

                if (heal > 0) {
                    attacker.heal(heal);
                }

                addExp(tool, target.level(), attacker.getOnPos(), attacker, (int) (getAttackDamage(tool) * CrystalToolsConfig.SWORD_EXPERIENCE_BOOST.get()));
            }
        }

        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND && !ToolUtils.isBroken(stack)) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", getAttackDamage(stack), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", getAttackSpeed(stack), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier("Weapon modifier", getAttackKnockback(stack), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier("Weapon modifier", getKnockbackResistance(stack), AttributeModifier.Operation.ADDITION));
            // To get reach
            builder.putAll(super.getAttributeModifiers(slot, stack));
            return builder.build();
        } else {
            return super.getAttributeModifiers(slot, stack);
        }
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return net.minecraftforge.common.ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction);
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_SWORD.get();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get() &&
                (super.canApplyAtEnchantingTable(stack, enchantment) || enchantment.category.equals(EnchantmentCategory.WEAPON));
    }
}
