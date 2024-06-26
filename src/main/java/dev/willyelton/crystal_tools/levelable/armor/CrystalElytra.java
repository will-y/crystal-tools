package dev.willyelton.crystal_tools.levelable.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.levelable.LevelableItem;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class CrystalElytra extends ElytraItem implements LevelableItem {
    private static final UUID ELYTRA_UUID = UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D");

    public CrystalElytra(Properties pProperties) {
        super(pProperties.fireResistant());
    }

    @Override
    public String getItemType() {
        return "crystal_elytra";
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        int bonusDurability = (int) NBTUtils.getFloatOrAddKey(stack, "durability_bonus");
        return INITIAL_TIER.getUses() + bonusDurability;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        ToolUtils.appendHoverText(itemStack, level, components, flag, this);
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack tool, @NotNull ItemStack repairItem) {
        return repairItem.is(Registration.CRYSTAL.get());
    }

    @Override
    public  EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.CHEST;
    }

    @Override
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        if (this.isDisabled()) {
            stack.shrink(1);
        }
    }

    @Override
    public boolean elytraFlightTick(@NotNull ItemStack stack, net.minecraft.world.entity.LivingEntity entity, int flightTicks) {
        if (!entity.level().isClientSide) {
            int nextFlightTick = flightTicks + 1;
            if (nextFlightTick % 10 == 0) {
                if (nextFlightTick % 20 == 0) {
                    float unbreakingLevel = NBTUtils.getFloatOrAddKey(stack, "durability_bonus") / 200 + 1;
                    if (1 / unbreakingLevel > Math.random()) {
                        stack.hurtAndBreak(1, entity, e -> e.broadcastBreakEvent(net.minecraft.world.entity.EquipmentSlot.CHEST));
                    }

                    this.addExp(stack, entity.level(), entity.getOnPos(), entity);
                }
                entity.gameEvent(GameEvent.ELYTRA_GLIDE);
            }
        }
        return true;
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


    // Armor things
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.CHEST) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            if (!ToolUtils.isBroken(stack)) {
                builder.put(Attributes.ARMOR, new AttributeModifier(ELYTRA_UUID, "Armor modifier", this.getDefense(stack), AttributeModifier.Operation.ADDITION));
                builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ELYTRA_UUID, "Armor toughness", this.getToughness(stack), AttributeModifier.Operation.ADDITION));
                int health = (int) NBTUtils.getFloatOrAddKey(stack, "health_bonus");

                if (health > 0) {
                    builder.put(Attributes.MAX_HEALTH, new AttributeModifier(ELYTRA_UUID, "Health modifier", health, AttributeModifier.Operation.ADDITION));
                }

                float speedBonus = NBTUtils.getFloatOrAddKey(stack, "speed_bonus") / 5;

                if (speedBonus > 0) {
                    builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(ELYTRA_UUID, "Speed modifier", speedBonus, AttributeModifier.Operation.MULTIPLY_BASE));
                }
            }

            return builder.build();
        } else {
            return super.getAttributeModifiers(slot, stack);
        }
    }

    public int getDefense(ItemStack stack) {
        return ArmorMaterials.NETHERITE.getDefenseForType(ArmorItem.Type.CHESTPLATE) + (int) NBTUtils.getFloatOrAddKey(stack, "armor_bonus");
    }

    public float getToughness(ItemStack stack) {
        return ArmorMaterials.NETHERITE.getToughness() + NBTUtils.getFloatOrAddKey(stack, "toughness_bonus");
    }

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
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_ELYTRA.get();
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get();
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get();
    }
}
