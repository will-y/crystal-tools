package dev.willyelton.crystal_tools.levelable.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.willyelton.crystal_tools.CreativeTabs;
import dev.willyelton.crystal_tools.levelable.LevelableItem;
import dev.willyelton.crystal_tools.levelable.ModItems;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class LevelableArmor extends ArmorItem implements LevelableItem, Wearable {
    protected final String itemType;
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    public LevelableArmor(String itemType, EquipmentSlot slot) {
        super(ArmorMaterials.NETHERITE, slot, new Properties().fireResistant().tab(CreativeTabs.CRYSTAL_TOOLS_TAB).defaultDurability(tier.getUses()));
        this.itemType = itemType;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == this.slot) {
            UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[slot.getIndex()];
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            if (!ToolUtils.isBroken(stack)) {
                builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", this.getDefense(stack), AttributeModifier.Operation.ADDITION));
                builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", this.getToughness(stack), AttributeModifier.Operation.ADDITION));
                int health = (int) NBTUtils.getFloatOrAddKey(stack, "health_bonus");

                if (health > 0) {
                    builder.put(Attributes.MAX_HEALTH, new AttributeModifier(uuid, "Health modifier", health, AttributeModifier.Operation.ADDITION));
                }

                float speedBonus = NBTUtils.getFloatOrAddKey(stack, "speed_bonus") / 5;

                if (speedBonus > 0) {
                    builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, "Speed modifier", speedBonus, AttributeModifier.Operation.MULTIPLY_BASE));
                }
            }

            return builder.build();
        } else {
            return super.getAttributeModifiers(slot, stack);
        }
    }

    // Attributes
    public int getDefense(ItemStack stack) {
        return this.getDefense() + (int) NBTUtils.getFloatOrAddKey(stack, "armor_bonus");
    }

    public float getToughness(ItemStack stack) {
        return this.getToughness() + NBTUtils.getFloatOrAddKey(stack, "toughness_bonus");
    }

    public float getKnockbackResistance(ItemStack stack) {
        return this.knockbackResistance;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return String.format("crystal_tools:textures/models/armor/%s_layer_%d%s.png", "crystal", (slot == EquipmentSlot.LEGS ? 2 : 1), type == null ? "" : String.format("_%s", type));
    }

    @Override
    public String getItemType() {
        return this.itemType;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        ToolUtils.appendHoverText(itemStack, level, components, flag, this);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        int bonusDurability = (int) NBTUtils.getFloatOrAddKey(stack, "durability_bonus");
        return tier.getUses() + bonusDurability;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        int durability = this.getMaxDamage(stack) - (int) NBTUtils.getFloatOrAddKey(stack, "Damage");

        if (durability - amount <= 0) {
            return 0;
        } else {
            return amount;
        }
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return false;
    }

    // Changing these two to what they should be @minecraft
    @Override
    public int getBarWidth(ItemStack itemStack) {
        return Math.round(13.0F - (float) itemStack.getDamageValue() * 13.0F / (float) itemStack.getMaxDamage());
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        float f = Math.max(0.0F, ((float)itemStack.getMaxDamage() - (float)itemStack.getDamageValue()) / (float) itemStack.getMaxDamage());
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull Entity entity, int inventorySlot, boolean inHand) {
        ToolUtils.inventoryTick(itemStack, level, entity, inventorySlot, inHand);
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack tool, @NotNull ItemStack repairItem) {
        return repairItem.is(ModItems.CRYSTAL.get());
    }

    @Override
    public int getEnchantmentValue() {
        return tier.getEnchantmentValue();
    }

    @Override
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        if (!ToolUtils.isBroken(stack) && NBTUtils.getFloatOrAddKey(stack, "night_vision") > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false));
        }
    }
}
