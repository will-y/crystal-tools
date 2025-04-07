package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.EntityTargeter;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.util.List;
import java.util.function.Consumer;

import static dev.willyelton.crystal_tools.common.levelable.armor.LevelableArmor.ARMOR_ID;
import static dev.willyelton.crystal_tools.common.levelable.tool.LevelableTool.ATTACK_DAMAGE_ID;

public class CrystalShield extends ShieldItem implements LevelableItem, EntityTargeter {

    public CrystalShield(Item.Properties properties) {
        super(properties.durability(1000)
                .component(net.minecraft.core.component.DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
                .fireResistant()
                .repairable(Registration.CRYSTAL.get()));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        refreshTarget(stack, level, player);

        return super.use(level, player, hand);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        refreshTarget(stack, level, livingEntity);

        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        clearTarget(stack, entity.level());
        super.onStopUsing(stack, entity, count);
    }

    @Override
    public String getItemType() {
        return "shield";
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        appendLevelableHoverText(stack, tooltipComponents, this, tooltipFlag);
    }

    @Override
    public void addAdditionalTooltips(ItemStack stack, Consumer<Component> components, LevelableItem item) {
        int totemSlots = stack.getOrDefault(DataComponents.TOTEM_SLOTS, 0);
        if (totemSlots > 0) {
            components.accept(Component.literal(String.format("\u00A72%d/%d Totems of Undying", stack.getOrDefault(DataComponents.FILLED_TOTEM_SLOTS, 0), totemSlots)));
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !ItemStack.isSameItem(oldStack, newStack);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        int bonusDurability = stack.getOrDefault(DataComponents.DURABILITY_BONUS, 0);
        return stack.getOrDefault(net.minecraft.core.component.DataComponents.MAX_DAMAGE, 0) + bonusDurability;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
        int durability = this.getMaxDamage(stack) - stack.getDamageValue();

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
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        levelableInventoryTick(stack, level, entity, slot, 1);
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get();
    }
}
