package dev.willyelton.crystal_tools.levelable.tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.levelable.LevelableItem;
import dev.willyelton.crystal_tools.utils.LevelUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public abstract class LevelableTool extends Item implements LevelableItem {

    // Blocks that can be mined by default, null for none
    protected final TagKey<Block> blocks;
    protected final String itemType;
    protected final int initialDurability;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
    private final UUID reachUUID;
    private Attribute reach;

    public LevelableTool(Item.Properties properties, TagKey<Block> mineableBlocks, String itemType, float attackDamageModifier, float attackSpeedModifier) {
        this(properties, mineableBlocks, itemType, attackDamageModifier, attackSpeedModifier, tier.getUses());
    }

    public LevelableTool(Item.Properties properties, TagKey<Block> mineableBlocks, String itemType, float attackDamageModifier, float attackSpeedModifier, int durability) {
        super(properties.defaultDurability(durability).fireResistant());
        this.blocks = mineableBlocks;
        this.itemType = itemType;
        this.initialDurability = durability;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", tier.getAttackDamageBonus() + attackDamageModifier, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", attackSpeedModifier, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
        this.reachUUID = UUID.randomUUID();
    }

    // From DiggerItem.java
    @Override
    public float getDestroySpeed(@NotNull ItemStack tool, @NotNull BlockState blockState) {
        float bonus = NBTUtils.getFloatOrAddKey(tool, "speed_bonus");
        if (ToolUtils.isBroken(tool)) {
            // broken
            return 0.1F;
        }
        return correctTool(tool, blockState) ? tier.getSpeed() + bonus * 20 : 1.0F;
    }

    public boolean correctTool(ItemStack tool, BlockState blockState) {
        return this.blocks != null && blockState.is(this.blocks);
    }

    @Override
    public boolean hurtEnemy(ItemStack tool, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (this.isDisabled()) {
            tool.shrink(1);
            return false;
        }

        tool.hurtAndBreak(2, attacker, (player) -> player.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack tool, BlockPos pos, Player player) {
        Level level = player.level();

        if (NBTUtils.getFloatOrAddKey(tool, "auto_smelt") > 0 && !NBTUtils.getBoolean(tool, "disable_auto_smelt")) {
            if (!level.isClientSide) {
                dropSmeltedItem(tool, level, level.getBlockState(pos), pos, player);
            }
            this.mineBlock(tool, level, level.getBlockState(pos), pos, player);
            return true;
        }

        return false;
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack tool, Level level, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity entity) {
        // If this tool is disabled break on use
        if (this.isDisabled()) {
            tool.shrink(1);
            return false;
        }

        if (!level.isClientSide) {
            tool.hurtAndBreak(1, entity, (player) -> player.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }

        addExp(tool, level, blockPos, entity);

        return true;
    }

    public void breakBlock(ItemStack tool, Level level, BlockPos blockPos, LivingEntity entity) {
        // TODO: Don't break if tool is broken
        BlockState blockState = level.getBlockState(blockPos);
        if (isCorrectToolForDrops(tool, blockState)) {
            if (NBTUtils.getFloatOrAddKey(tool, "auto_smelt") > 0 && !NBTUtils.getBoolean(tool, "disable_auto_smelt")) {
                if (!level.isClientSide) {
                    dropSmeltedItem(tool, level, blockState, blockPos, entity);
                }
            } else {
                LevelUtils.destroyBlock(level, blockPos, true, entity, 512, tool);
            }
            if (!level.isClientSide) {
                tool.hurtAndBreak(1, entity, (player) -> player.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            }
            addExp(tool, level, blockPos, entity);
        }
    }

    protected void dropSmeltedItem(ItemStack tool, Level level, BlockState blockState, BlockPos pos, LivingEntity entity) {
        List<ItemStack> drops = Block.getDrops(blockState, (ServerLevel) level, pos, null, entity, tool);
        List<ItemStack> toDrop = new ArrayList<>();

        for (ItemStack stack : drops) {
            int count = stack.getCount();

            Optional<SmeltingRecipe> recipeOptional = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), level);

            if (recipeOptional.isPresent()) {
                SmeltingRecipe recipe = recipeOptional.get();
                popExperience((ServerLevel) level, entity, recipe.getExperience());
                ItemStack result = recipe.getResultItem(level.registryAccess()).copy();
                result.setCount(count * result.getCount());

                if (!result.is(Items.AIR)) {
                    toDrop.add(result);
                }
            }
        }

        LevelUtils.destroyBlock(level, pos, toDrop.isEmpty(), entity, 512, tool);

        for (ItemStack stack : toDrop) {
            Block.popResource(level, pos, stack);
        }
    }

    private void popExperience(ServerLevel level, LivingEntity entity, float experience) {
        int fullXp = (int) Math.floor(experience);
        float partialXp = experience - fullXp;

        if (partialXp > 0 && Math.random() < partialXp) {
            ExperienceOrb.award(level, entity.position(), fullXp + 1);
        } else {
            ExperienceOrb.award(level, entity.position(), fullXp);
        }
    }

    public void breakBlockCollection(ItemStack tool, Level level, Collection<BlockPos> blockPosCollection, LivingEntity entity, float firstBlockSpeed) {
        for (BlockPos pos : blockPosCollection) {
            if (level.getBlockState(pos).getDestroySpeed(level, pos) <= firstBlockSpeed + 20) {
                breakBlock(tool, level, pos, entity);
            }
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
        return repairItem.is(Registration.CRYSTAL.get());
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
        float unbreakingLevel = NBTUtils.getFloatOrAddKey(stack, "unbreaking");
        int damageToTake = 0;

        while (amount > 0) {
            if (unbreakingLevel < Math.random()) {
                damageToTake++;
            }
            amount--;
        }

        if (durability - damageToTake <= 0) {
            return 0;
        } else {
            return damageToTake;
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND && !ToolUtils.isBroken(stack)) {
            if (reach == null) {
                reach = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("forge", "reach_distance"));
                assert reach != null;
            }

            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.putAll(defaultModifiers);
            builder.put(reach, new AttributeModifier(reachUUID, "Reach modifier", 2, AttributeModifier.Operation.ADDITION));
            return builder.build();
        }

        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack stack) {
        // Just to get the right text color always
        return Rarity.RARE;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }
}
