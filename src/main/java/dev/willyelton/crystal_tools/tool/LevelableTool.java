package dev.willyelton.crystal_tools.tool;

import dev.willyelton.crystal_tools.item.ModItems;
import dev.willyelton.crystal_tools.tool.skill.SkillData;
import dev.willyelton.crystal_tools.tool.skill.SkillDataNode;
import dev.willyelton.crystal_tools.tool.skill.SkillNodeType;
import dev.willyelton.crystal_tools.utils.LevelUtilities;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
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
public class LevelableTool extends LevelableItem {

    // Blocks that can be mined by default
    protected final Tag<Block> blocks;

    public LevelableTool(Item.Properties properties, Tag<Block> mineableBlocks, String itemType) {
        super(properties, itemType);
        this.blocks = mineableBlocks;
    }

    // From DiggerItem.java
    @Override
    public float getDestroySpeed(ItemStack tool, BlockState blockState) {
        float bonus = NBTUtils.getFloatOrAddKey(tool, "speed_bonus");
        int durability = this.getMaxDamage(tool) - (int) NBTUtils.getFloatOrAddKey(tool, "Damage");
        if (durability <= 1) {
            // broken
            return 0.1F;
        }
        return (correctTool(tool, blockState) ? tier.getSpeed() : 1.0F) + bonus;
    }

    public boolean correctTool(ItemStack tool, BlockState blockState) {
        return this.blocks.contains(blockState.getBlock());
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

            if (NBTUtils.getFloatOrAddKey(tool, "auto_smelt") > 0) {
                dropSmeltedItem(tool, level, blockState, blockPos, entity);
            }
        }

        addExp(tool, level, blockPos);

        return true;
    }

    protected void dropSmeltedItem(ItemStack tool, Level level, BlockState blockState, BlockPos pos, LivingEntity entity) {
        if (!level.isClientSide) {
            Block.getDrops(blockState, (ServerLevel) level, pos, null, entity, tool).forEach((itemStack -> {
//                System.out.println("Item in: " + itemStack);
                Container container = new SimpleContainer(itemStack);
                int count = itemStack.getCount();

                Optional<SmeltingRecipe> recipeOptional = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, container, level);

                if (recipeOptional.isPresent()) {
                    SmeltingRecipe recipe = recipeOptional.get();
                    ItemStack result = recipe.getResultItem();
                    result.setCount(count);

                    LevelUtilities.destroyBlock(level, pos, result.is(Items.AIR), entity, 512, tool);
//                    level.destroyBlock(pos, false, entity);
                    if (!result.is(Items.AIR)) {
//                        System.out.println("dropping: " + result);
                        Block.popResource(level, pos, result);
                    }

                }
            }));
        }
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return correctTool(stack, state) && net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(tier, state);
    }
}
