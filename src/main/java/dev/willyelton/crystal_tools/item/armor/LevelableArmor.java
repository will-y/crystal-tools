package dev.willyelton.crystal_tools.item.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.willyelton.crystal_tools.CreativeTabs;
import dev.willyelton.crystal_tools.item.LevelableItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Wearable;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class LevelableArmor extends LevelableItem implements Wearable {
    protected final EquipmentSlot slot;
    private final int defense;
    private final float toughness;
    protected final float knockbackResistance;
    protected final ArmorMaterial material;

    public LevelableArmor(String itemType, EquipmentSlot slot) {
        super(new Properties().fireResistant().tab(CreativeTabs.CRYSTAL_TOOLS_TAB), itemType);
        this.material = ArmorMaterials.NETHERITE;
        this.slot = slot;
        this.defense = material.getDefenseForSlot(slot);
        this.toughness = material.getToughness();
        this.knockbackResistance = material.getKnockbackResistance();
    }

    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return this.slot;
    }

    // Functions from ArmorItem
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        EquipmentSlot equipmentslot = Mob.getEquipmentSlotForItem(itemstack);
        ItemStack itemstack1 = player.getItemBySlot(equipmentslot);
        if (itemstack1.isEmpty()) {
            player.setItemSlot(equipmentslot, itemstack.copy());
            if (!level.isClientSide()) {
                player.awardStat(Stats.ITEM_USED.get(this));
            }

            itemstack.setCount(0);
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    @Override
    @Nullable
    public SoundEvent getEquipSound() {
        return this.material.getEquipSound();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == this.slot) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ARMOR, new AttributeModifier("Armor", this.getDefense(stack), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier("Armor", this.getToughness(stack), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier("Weapon modifier", this.getKnockbackResistance(stack), AttributeModifier.Operation.ADDITION));
            return builder.build();
        } else {
            return super.getAttributeModifiers(slot, stack);
        }
    }

    // Attributes
    public int getDefense(ItemStack stack) {
        return this.defense;
    }

    public float getToughness(ItemStack stack) {
        return this.toughness;
    }

    public float getKnockbackResistance(ItemStack stack) {
        return this.knockbackResistance;
    }
}
