package dev.willyelton.crystal_tools.item.tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.LevelUtilities;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

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
    public boolean hurtEnemy(ItemStack tool, LivingEntity target, LivingEntity attacker) {
        tool.hurtAndBreak(1, attacker, (p_43296_) -> {
            p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });

        if (!LevelUtilities.isBroken(tool)) {
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
        if (slot == EquipmentSlot.MAINHAND && !LevelUtilities.isBroken(stack)) {
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
}
