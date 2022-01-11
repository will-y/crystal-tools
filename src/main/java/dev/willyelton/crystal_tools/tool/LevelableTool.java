package dev.willyelton.crystal_tools.tool;

import dev.willyelton.crystal_tools.item.ModItems;
import dev.willyelton.crystal_tools.tool.skill.SkillData;
import dev.willyelton.crystal_tools.tool.skill.SkillDataNode;
import dev.willyelton.crystal_tools.tool.skill.SkillNodeType;
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
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

// For now just focus on things that mine (not sword)
public class LevelableTool extends Item {
    private static final int AUTO_REPAIR_COUNTER = 50;
    private static final int BASE_EXPERIENCE_CAP = 50;
    private static final float EXPERIENCE_CAP_MULTIPLIER = 1.25F;

    // Just used for default values, just at netherite for now
    protected static final Tier tier = Tiers.NETHERITE;

    // Blocks that can be mined by default
    protected final Tag<Block> blocks;

    private final String toolType;

    public LevelableTool(Properties properties, Tag<Block> mineableBlocks, String toolType) {
        super(properties.defaultDurability(tier.getUses()));
        this.blocks = mineableBlocks;
        this.toolType = toolType;
    }

    // From TierdItem.java
    @Override
    public int getEnchantmentValue() {
        return tier.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack tool, @NotNull ItemStack repairItem) {
        return repairItem.is(ModItems.CRYSTAL.get());
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
        Block.getDrops(blockState, (ServerLevel) level, pos, null, entity, tool).forEach((itemStack -> {
            Container container = new SimpleContainer(itemStack);

            Optional<SmeltingRecipe> recipeOptional = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, container, level);

            if (recipeOptional.isPresent()) {
                SmeltingRecipe recipe = recipeOptional.get();
                ItemStack result = recipe.getResultItem();

                level.destroyBlock(pos, false, entity);
                Block.popResource(level, pos, result);
            }
        }));


        List<SmeltingRecipe> recipes = level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING);

        for (SmeltingRecipe recipe : recipes) {
            // Maybe?
            recipe.getIngredients().get(0).test(blockState.getBlock().asItem().getDefaultInstance());
        }
    }

    public void addExp(ItemStack tool, Level level, BlockPos blockPos) {
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
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return correctTool(stack, state) && net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(tier, state);
    }

    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        int newExperience = (int) NBTUtils.getFloatOrAddKey(itemStack, "experience");
        int experienceCap = (int) NBTUtils.getFloatOrAddKey(itemStack, "experience_cap", BASE_EXPERIENCE_CAP);

        int durability = this.getMaxDamage(itemStack) - (int) NBTUtils.getFloatOrAddKey(itemStack, "Damage");

        if (durability <= 1) {
            components.add(new TextComponent("\u00A7c\u00A7l" + "Broken"));
        }

        components.add(new TextComponent(String.format("%d/%d XP To Next Level", newExperience, experienceCap)));
        int skillPoints = (int) NBTUtils.getFloatOrAddKey(itemStack, "skill_points");
        if (skillPoints > 0) {
            components.add(new TextComponent(String.format("%d Unspent Skill Points", skillPoints)));
        }

        if (!Screen.hasShiftDown()) {
            components.add(new TextComponent("<Hold Shift For Skills>"));
        } else {
            components.add(new TextComponent("Skills:"));
            int[] points = NBTUtils.getIntArray(itemStack, "points");
            SkillData toolData = SkillData.fromResourceLocation(new ResourceLocation("crystal_tools", String.format("skill_trees/%s.json", toolType)), points);
            for (SkillDataNode dataNode : toolData.getAllNodes()) {
                if (dataNode.isComplete()) {
                    components.add(new TextComponent("    " + dataNode.getName()));
                } else if (dataNode.getType().equals(SkillNodeType.INFINITE) && dataNode.getPoints() > 0) {
                    components.add(new TextComponent("    " + dataNode.getName() + " (" + dataNode.getPoints() + " points)"));
                }
            }
        }
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

    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        int durability = this.getMaxDamage(stack) - (int) NBTUtils.getFloatOrAddKey(stack, "Damage");

        if (durability - amount <= 0) {
            return 0;
        } else {
            return amount;
        }
    }

    public String getToolType() {
        return toolType;
    }
}
