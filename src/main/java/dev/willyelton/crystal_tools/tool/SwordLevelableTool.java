package dev.willyelton.crystal_tools.tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.willyelton.crystal_tools.item.CreativeTabs;
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
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public SwordLevelableTool() {
        // Need an empty block tag
        super(new Item.Properties().fireResistant().tab(CreativeTabs.CRYSTAL_TOOLS_TAB), BlockTags.CARPETS, "sword");
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.getDamage(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)this.getAttackSpeed(), AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }


    public float getDamage() {
        return tier.getAttackDamageBonus();
    }

    public float getAttackSpeed() {
        return 1;
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

    public boolean hurtEnemy(ItemStack tool, LivingEntity target, LivingEntity attacker) {
        tool.hurtAndBreak(1, attacker, (p_43296_) -> {
            p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });

        addExp(tool, target.getLevel(), attacker.getOnPos());
        return true;
    }

//    @Override
//    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot) {
//        return pEquipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(pEquipmentSlot);
//    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {

        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return net.minecraftforge.common.ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction);
    }
}
