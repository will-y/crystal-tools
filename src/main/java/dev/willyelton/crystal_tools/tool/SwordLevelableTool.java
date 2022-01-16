package dev.willyelton.crystal_tools.tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.willyelton.crystal_tools.item.CreativeTabs;
import dev.willyelton.crystal_tools.utils.NBTUtils;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class SwordLevelableTool extends LevelableTool {
    private static final float BASE_ATTACK_DAMAGE = tier.getAttackDamageBonus() + 3;
    private static final float BASE_ATTACK_SPEED = -2.4f;
    private static final float BASE_ATTACK_KNOCKBACK = 0;
    private static final float BASE_KNOCKBACK_RESISTANCE = 0;

    public SwordLevelableTool() {
        // Need an empty block tag
        super(new Item.Properties().fireResistant().tab(CreativeTabs.CRYSTAL_TOOLS_TAB), BlockTags.CARPETS, "sword");
    }


    public float getAttackDamage(ItemStack stack) {
        return BASE_ATTACK_DAMAGE + NBTUtils.getFloatOrAddKey(stack, "damage_bonus");
    }

    public float getAttackSpeed(ItemStack stack) {
        return BASE_ATTACK_SPEED + NBTUtils.getFloatOrAddKey(stack, "attack_speed_bonus");
    }

    public float getAttackKnockback(ItemStack stack) {
        return BASE_ATTACK_KNOCKBACK + NBTUtils.getFloatOrAddKey(stack, "knockback");
    }

    public float getKnockbackResistance(ItemStack stack) {
        return BASE_KNOCKBACK_RESISTANCE + NBTUtils.getFloatOrAddKey(stack, "knockback_resistance");
    }

    public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        return !pPlayer.isCreative();
    }

    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        if (pState.is(Blocks.COBWEB)) {
            return 15.0F;
        } else {
            Material material = pState.getMaterial();
            return material != Material.PLANT && material != Material.REPLACEABLE_PLANT && !pState.is(BlockTags.LEAVES) && material != Material.VEGETABLE ? 1.0F : 1.5F;
        }
    }

    public boolean isCorrectToolForDrops(BlockState pBlock) {
        return pBlock.is(Blocks.COBWEB);
    }

    @Override
    public boolean hurtEnemy(ItemStack tool, LivingEntity target, LivingEntity attacker) {
        tool.hurtAndBreak(1, attacker, (p_43296_) -> {
            p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });

        if (NBTUtils.getFloatOrAddKey(tool, "fire") > 0) {
            target.setSecondsOnFire(5);
        }


        addExp(tool, target.getLevel(), attacker.getOnPos(), (int) getAttackDamage(tool));
        return true;
    }

//    @Override
//    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot) {
//        return pEquipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(pEquipmentSlot);
//    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.getAttackDamage(stack), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", this.getAttackSpeed(stack), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier("Weapon modifier", this.getAttackKnockback(stack), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier("Weapon modifier", this.getKnockbackResistance(stack), AttributeModifier.Operation.ADDITION));
            return builder.build();
        } else {
            return super.getAttributeModifiers(slot, stack);
        }
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return net.minecraftforge.common.ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction);
    }
}
