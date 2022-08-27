package dev.willyelton.crystal_tools.item.tool;

import dev.willyelton.crystal_tools.CreativeTabs;
import dev.willyelton.crystal_tools.item.LevelableItem;
import dev.willyelton.crystal_tools.item.ModItems;
import dev.willyelton.crystal_tools.utils.LevelUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

// For now just focus on things that mine (not sword)
public class LevelableTool extends Item implements LevelableItem {

    // Blocks that can be mined by default, null for none
    protected final TagKey<Block> blocks;
    protected final String itemType;
    protected final int initialDurability;

    public LevelableTool(Item.Properties properties, TagKey<Block> mineableBlocks, String itemType) {
        this(properties, mineableBlocks, itemType, tier.getUses());
    }

    public LevelableTool(Item.Properties properties, TagKey<Block> mineableBlocks, String itemType, int durability) {
        super(properties.defaultDurability(durability).fireResistant().tab(CreativeTabs.CRYSTAL_TOOLS_TAB));
        this.blocks = mineableBlocks;
        this.itemType = itemType;
        this.initialDurability = durability;
    }

    // From DiggerItem.java
    @Override
    public float getDestroySpeed(@NotNull ItemStack tool, @NotNull BlockState blockState) {
        float bonus = NBTUtils.getFloatOrAddKey(tool, "speed_bonus");
        if (ToolUtils.isBroken(tool)) {
            // broken
            return 0.1F;
        }
        return (correctTool(tool, blockState) ? tier.getSpeed() : 1.0F) + bonus;
    }

    public boolean correctTool(ItemStack tool, BlockState blockState) {
        return this.blocks != null && blockState.is(this.blocks);
    }

    @Override
    public boolean hurtEnemy(ItemStack tool, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        tool.hurtAndBreak(2, attacker, (player) -> player.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack tool, Level level, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity entity) {
        if (!level.isClientSide && blockState.getDestroySpeed(level, blockPos) != 0.0F) {
            tool.hurtAndBreak(1, entity, (player) -> player.broadcastBreakEvent(EquipmentSlot.MAINHAND));

            if (NBTUtils.getFloatOrAddKey(tool, "auto_smelt") > 0 &&!NBTUtils.getBoolean(tool, "disable_auto_smelt")) {
                dropSmeltedItem(tool, level, blockState, blockPos, entity);
            }
        }

        addExp(tool, level, blockPos, entity);

        return true;
    }

    protected void dropSmeltedItem(ItemStack tool, Level level, BlockState blockState, BlockPos pos, LivingEntity entity) {
        if (!level.isClientSide) {
            Block.getDrops(blockState, (ServerLevel) level, pos, null, entity, tool).forEach((itemStack -> {
                Container container = new SimpleContainer(itemStack);
                int count = itemStack.getCount();

                Optional<SmeltingRecipe> recipeOptional = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, container, level);

                if (recipeOptional.isPresent()) {
                    SmeltingRecipe recipe = recipeOptional.get();
                    ExperienceOrb.award((ServerLevel) level, entity.position(), (int) Math.ceil(recipe.getExperience()));
                    ItemStack result = recipe.getResultItem();
                    result.setCount(count * result.getCount());

                    if (!result.is(Items.AIR)) {
//                        System.out.println("dropping: " + result);
                        Block.popResource(level, pos, result);
                    }
                    LevelUtils.destroyBlock(level, pos, result.is(Items.AIR), entity, 512, tool);

                }
            }));
        }
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return correctTool(stack, state) && net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(tier, state);
    }

    @Override
    public String getItemType() {
        return this.itemType;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        int bonusDurability = (int) NBTUtils.getFloatOrAddKey(stack, "durability_bonus");
        return this.initialDurability + bonusDurability;
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
    public int getEnchantmentValue(ItemStack stack) {
        return tier.getEnchantmentValue();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        ToolUtils.appendHoverText(itemStack, level, components, flag, this);
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
}
