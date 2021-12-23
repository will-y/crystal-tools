package dev.willyelton.crystal_tools.tool;

import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
// TODO: Store important things in NBT
public class LevelableTool extends Item {
    // Just used for default values, just at netherite for now
    private static Tier tier = Tiers.NETHERITE;

    // Exp
    private int experience = 0;
    private int experienceCap;
    private float experienceModifier = 1.1F;

    // Things that can be upgraded
    private float miningSpeed = tier.getSpeed();
    private float attackSpeed;
    private float attackDamage;
    private int durability = tier.getUses();
    private int maxDurability = tier.getUses();

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
        return (this.blocks.contains(blockState.getBlock()) ? this.miningSpeed : 1.0F) + bonus;
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

    // TODO: Override all of the Item methods that use maxDamage :(
}
