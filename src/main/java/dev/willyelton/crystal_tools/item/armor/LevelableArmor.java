package dev.willyelton.crystal_tools.item.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.willyelton.crystal_tools.CreativeTabs;
import dev.willyelton.crystal_tools.item.LevelableItem;
import dev.willyelton.crystal_tools.item.ModItems;
import dev.willyelton.crystal_tools.utils.LevelUtilities;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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
            builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", this.getDefense(stack), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", this.getToughness(stack), AttributeModifier.Operation.ADDITION));
            int health = (int) NBTUtils.getFloatOrAddKey(stack, "health_bonus");

            if (health > 0) {
                builder.put(Attributes.MAX_HEALTH, new AttributeModifier(uuid, "Health modifier", health, AttributeModifier.Operation.ADDITION));
            }
//            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier("Weapon modifier", this.getKnockbackResistance(stack), AttributeModifier.Operation.ADDITION));
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
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        LevelUtilities.appendHoverText(itemStack, level, components, flag, this);
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
    public boolean isFoil(ItemStack stack) {
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
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int inventorySlot, boolean inHand) {
//        LevelUtilities.inventoryTick(itemStack, level, entity, inventorySlot, inHand);
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
        if (NBTUtils.getFloatOrAddKey(stack, "night_vision") > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 100, 0, true, false));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        System.out.println(itemstack.getTag());
        return super.use(pLevel, pPlayer, pHand);
    }
}
