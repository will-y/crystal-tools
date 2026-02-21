package dev.willyelton.crystal_tools.utils;

import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.ArrayList;
import java.util.List;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

public class AttributeUtils {
    private AttributeUtils() {}

    /**
     * Removes the attribute if it is present, adds it if it is not
     * @param stack The stack to add to
     * @param attribute The attribute to toggle
     * @param data A reference to the stack's SkillData
     * @return True if the attribute was added
     */
    public static boolean toggleAttribute(ItemStack stack, Holder<Attribute> attribute, SkillData data) {
        if (data.getEquipmentSlot() == null) return false;

        ItemAttributeModifiers modifiers = stack.getAttributeModifiers();

        List<ItemAttributeModifiers.Entry> newModifiers = new ArrayList<>();

        boolean found = false;

        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (!entry.attribute().value().equals(attribute.value())) {
                newModifiers.add(entry);
            } else {
                found = true;
            }
        }

        if (!found && attribute.getKey() != null) {
            Identifier rl = rl(attribute.getKey().identifier().getPath());
            newModifiers.add(new ItemAttributeModifiers.Entry(attribute, new AttributeModifier(rl, 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(data.getEquipmentSlot())));
        }

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(newModifiers));

        return !found;
    }
}
