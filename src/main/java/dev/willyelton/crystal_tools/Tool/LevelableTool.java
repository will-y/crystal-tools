package dev.willyelton.crystal_tools.Tool;

import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

// For now just focus on things that mine (not sword)
// TODO: Store important things in NBT
public class LevelableTool extends Item {
    // Exp
    private int experience = 0;
    private int experienceCap;
    private float experienceModifier = 1.1F;

    // Things that can be upgraded
    private float miningSpeed;
    private float attackSpeed;
    private float attackDamage;
    private int durability;

    // Blocks that can be mined
    private Tag<Block> blocks;

    // Just used for default values, just at netherite for now
    private Tier tier = Tiers.NETHERITE;

    public LevelableTool(Properties properties) {
        super(properties);
    }

    // From TierdItem.java
    @Override
    public int getEnchantmentValue() {
        return this.tier.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack tool, @NotNull ItemStack repairItem) {
        return this.tier.getRepairIngredient().test(repairItem);
    }

    // From DiggerItem.java
    @Override
    public float getDestroySpeed(ItemStack tool, BlockState blockState) {
        return this.blocks.contains(blockState.getBlock()) ? this.miningSpeed : 1.0F;
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

        // Maybe store on server or client only
        this.experience++;
        System.out.println(this.experience);

        return true;
    }

    // FORGE START
    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.is(blocks) && net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(tier, state);
    }
}
