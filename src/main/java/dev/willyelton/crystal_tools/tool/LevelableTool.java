package dev.willyelton.crystal_tools.tool;

import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
    private static final int BASE_EXPERIENCE_CAP = 50;
    private static final float EXPERIENCE_CAP_MULTIPLIER = 1.25F;

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

        int newExperience = (int) NBTUtils.addValueToTag(tool, "experience", 1);
        int experienceCap = (int) NBTUtils.getFloatOrAddKey(tool, "experience_cap", BASE_EXPERIENCE_CAP);

        if (experienceCap == 0) {
            // fist time
            NBTUtils.setValue(tool, "experience_cap", BASE_EXPERIENCE_CAP);
            experienceCap = BASE_EXPERIENCE_CAP;
        }

        if (newExperience >= experienceCap) {
            // level up
            NBTUtils.addValueToTag(tool, "skill_points", 1);
            // copied from LivingEntity item breaking sound
            // play level up sound
            level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 0.8F, 0.8F + level.random.nextFloat() * 0.4F, false);
            // TODO: Add chat message thing

            NBTUtils.setValue(tool, "experience", 0);
            NBTUtils.setValue(tool, "experience_cap", experienceCap * EXPERIENCE_CAP_MULTIPLIER);
        }

        return true;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.is(blocks) && net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(tier, state);
    }

    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        int newExperience = (int) NBTUtils.getFloatOrAddKey(itemStack, "experience");
        int experienceCap = (int) NBTUtils.getFloatOrAddKey(itemStack, "experience_cap", BASE_EXPERIENCE_CAP);

        components.add(new TextComponent(String.format("%d/%d XP To Next Level", newExperience, experienceCap)));
        int skillPoints = (int) NBTUtils.getFloatOrAddKey(itemStack, "skill_points");
        if (skillPoints > 0) {
            components.add(new TextComponent(String.format("%d Unspent Skill Points", skillPoints)));
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
