package dev.willyelton.crystal_tools.tool;

import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

// For now just focus on things that mine (not sword)
public class LevelableTool extends Item {
    private static final int AUTO_REPAIR_COUNTER = 50;

    // Just used for default values, just at netherite for now
    private static Tier tier = Tiers.NETHERITE;

    // Blocks that can be mined
    private Tag<Block> blocks;

    public LevelableTool(Properties properties, Tag<Block> mineableBlocks) {
        super(properties.defaultDurability(tier.getUses()));
        this.blocks = mineableBlocks;
    }

    // From TierdItem.java
    @Override
    public int getEnchantmentValue() {
        return tier.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack tool, @NotNull ItemStack repairItem) {
        return tier.getRepairIngredient().test(repairItem);
    }

    // From DiggerItem.java
    @Override
    public float getDestroySpeed(ItemStack tool, BlockState blockState) {
        float bonus = NBTUtils.getFloatOrAddKey(tool, "speed_bonus");
        return (this.blocks.contains(blockState.getBlock()) ? tier.getSpeed() : 1.0F) + bonus;
    }

    // Idk if these parameters are right, just guessing
    @Override
    public boolean hurtEnemy(ItemStack tool, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        tool.hurtAndBreak(2, attacker, (player) -> {
            player.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack tool, Level level, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity entity) {
        if (!level.isClientSide && blockState.getDestroySpeed(level, blockPos) != 0.0F) {
            tool.hurtAndBreak(1, entity, (player) -> {
                player.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }

        NBTUtils.addValueToTag(tool, "experience", 1);

        return true;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.is(blocks) && net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(tier, state);
    }

    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        if (itemStack.hasTag() && itemStack.getTag().contains("experience")) {
            components.add(new TextComponent("Exp: " + itemStack.getTag().getInt("experience")));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        System.out.println(player.getMainHandItem().getTag());
        return super.use(level, player, interactionHand);
    }

    // Just don't ever add the enchantment effect
    @Override
    public boolean isFoil(ItemStack itemStack) {
        return false;
    }

    // I think the int and boolean parameters are right
    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int inventorySlot, boolean inHand) {
        if (!inHand) {
            if (NBTUtils.getBoolean(itemStack, "auto_repair", false)) {
                if (NBTUtils.addValueToTag(itemStack, "auto_repair_counter", 1) > AUTO_REPAIR_COUNTER) {
                    NBTUtils.setValue(itemStack, "auto_repair_counter", 0);
                    int repairAmount = Math.min((int) NBTUtils.getFloatOrAddKey(itemStack, "auto_repair_amount"), itemStack.getDamageValue());
                    itemStack.setDamageValue(itemStack.getDamageValue() - repairAmount);
                }
            }
        }
    }

    // TODO: Override all of the Item methods that use maxDamage :(

    // Changing these two to what they should be @minecraft
    public int getBarWidth(ItemStack itemStack) {
        return Math.round(13.0F - (float) itemStack.getDamageValue() * 13.0F / (float) itemStack.getMaxDamage());
    }

    public int getBarColor(ItemStack itemStack) {
        float f = Math.max(0.0F, ((float)itemStack.getMaxDamage() - (float)itemStack.getDamageValue()) / (float) itemStack.getMaxDamage());
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public int getMaxDamage(ItemStack itemStack) {
        int bonusDurability = (int) NBTUtils.getFloatOrAddKey(itemStack, "durability_bonus");

        return tier.getUses() + bonusDurability;
    }
}
