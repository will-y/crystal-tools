package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.DataComponents;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.common.network.data.BlockBreakPayload;
import dev.willyelton.crystal_tools.utils.LevelUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class LevelableTool extends TieredItem implements LevelableItem {
    public static final UUID ATTACK_DAMAGE_UUID = UUID.randomUUID();
    protected static final UUID ATTACK_SPEED_UUID = UUID.randomUUID();
    protected static final UUID ATTACK_KNOCKBACK_UUID = UUID.randomUUID();
    protected static final UUID KNOCKBACK_RESISTANCE_UUID = UUID.randomUUID();
    protected static final UUID REACH_UUID = UUID.randomUUID();
    protected static final UUID ATTACK_RANGE_UUID = UUID.randomUUID();

    // Blocks that can be mined by default, null for none
    protected final TagKey<Block> blocks;
    protected final String itemType;
    protected final int initialDurability;
    private final float initialAttackDamage;

    public LevelableTool(Item.Properties properties, TagKey<Block> mineableBlocks, String itemType, float attackDamageModifier, float attackSpeedModifier) {
        this(properties, mineableBlocks, itemType, attackDamageModifier, attackSpeedModifier, INITIAL_TIER.getUses());
    }

    public LevelableTool(Item.Properties properties, TagKey<Block> mineableBlocks, String itemType, float attackDamageModifier, float attackSpeedModifier, int durability) {
        super(Tiers.NETHERITE, properties.fireResistant()
                .rarity(Rarity.RARE)
                .attributes(ItemAttributeModifiers.builder()
                        .add(Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", INITIAL_TIER.getAttackDamageBonus() + attackDamageModifier, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        .add(Attributes.ATTACK_SPEED,
                                new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", attackSpeedModifier, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        .build()));
        this.blocks = mineableBlocks;
        this.itemType = itemType;
        this.initialDurability = durability;
        this.initialAttackDamage = INITIAL_TIER.getAttackDamageBonus() + attackDamageModifier;
    }

    // From DiggerItem.java
    @Override
    public float getDestroySpeed(@NotNull ItemStack tool, @NotNull BlockState blockState) {
        float bonus = tool.getOrDefault(DataComponents.MINING_SPEED, 0F);
        if (ToolUtils.isBroken(tool)) {
            // broken
            return 0.1F;
        }
        return correctTool(tool, blockState) ? getTier().getSpeed() + bonus * 20 : 1.0F;
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

        tool.hurtAndBreak(2, attacker, EquipmentSlot.MAINHAND);
        return true;
    }

    /**
     * Called from {@link dev.willyelton.crystal_tools.common.events.BlockEvents#breakEvent(BlockEvent.BreakEvent)}
     * <p>
     * Should try to use {@link this#mineBlock(ItemStack, Level, BlockState, BlockPos, LivingEntity)} instead if possible.
     * <p>
     * This should only be needed for modifying drops, ex: Auto smelt and Auto Pickup
     * @param tool Stack used for breaking the block (player mainhand item)
     * @param pos Position of the block
     * @param player Player breaking the block
     * @return Return true to cancel the original break event
     */
    public boolean onBlockStartBreak(ItemStack tool, BlockPos pos, Player player) {
        Level level = player.level();

        boolean autoPickup = tool.getOrDefault(DataComponents.AUTO_PICKUP, false);
        BlockState originalState = level.getBlockState(pos);
        if (shouldAutoSmelt(tool)) {
            if (!level.isClientSide) {
                dropSmeltedItem(tool, level, originalState, pos, player, autoPickup);
            }
            this.mineBlock(tool, level, originalState, pos, player);
            return true;
        }

        // Could just call LevelUtils.destroyBlock, but I don't completely trust it so might as well use default behavior when I can
        if (autoPickup) {
            if (!level.isClientSide) {
                LevelUtils.destroyBlock(level, pos, true, player, 512, tool, true, false);
                this.mineBlock(tool, level, originalState, pos, player);
            }
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
            tool.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
        }

        addExp(tool, level, blockPos, entity);

        return true;
    }

    public void breakBlock(ItemStack tool, Level level, BlockPos blockPos, LivingEntity entity) {
        // TODO: Don't break if tool is broken
        BlockState blockState = level.getBlockState(blockPos);
        if (isCorrectToolForDrops(tool, blockState)) {
            boolean autoPickup = tool.getOrDefault(DataComponents.AUTO_PICKUP, false);
            if (!level.isClientSide) {
                if (shouldAutoSmelt(tool)) {
                    dropSmeltedItem(tool, level, blockState, blockPos, entity, autoPickup);
                } else {
                    LevelUtils.destroyBlock(level, blockPos, true, entity, 512, tool, autoPickup, false);
                }

                tool.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
            }
            addExp(tool, level, blockPos, entity);
        }
    }

    /**
     * Only Called on Server
     */
    protected void dropSmeltedItem(ItemStack tool, Level level, BlockState blockState, BlockPos pos, LivingEntity entity, boolean autoPickup) {
        List<ItemStack> drops = Block.getDrops(blockState, (ServerLevel) level, pos, null, entity, tool);
        List<ItemStack> toDrop = new ArrayList<>();

        for (ItemStack stack : drops) {
            int count = stack.getCount();

            Optional<RecipeHolder<SmeltingRecipe>> recipeOptional = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), level);

            if (recipeOptional.isPresent()) {
                SmeltingRecipe recipe = recipeOptional.get().value();
                popExperience((ServerLevel) level, entity, recipe.getExperience());
                ItemStack result = recipe.getResultItem(level.registryAccess()).copy();
                result.setCount(count * result.getCount());

                if (!result.is(Items.AIR)) {
                    toDrop.add(result);
                } else {
                    toDrop.add(stack);
                }
            } else {
                toDrop.add(stack);
            }
        }

        LevelUtils.destroyBlock(level, pos, toDrop.isEmpty(), entity, 512, tool, false, false);

        if (autoPickup && entity instanceof ServerPlayer player) {
            LevelUtils.addToInventoryOrDrop(toDrop, player, level, pos);
        } else {
            for (ItemStack stack : toDrop) {
                Block.popResource(level, pos, stack);
            }
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
        breakBlockCollection(tool, level, blockPosCollection, entity, firstBlockSpeed, false);
    }

    public void breakBlockCollection(ItemStack tool, Level level, Collection<BlockPos> blockPosCollection, LivingEntity entity, float firstBlockSpeed, boolean sendServerPackets) {
        for (BlockPos pos : blockPosCollection) {
            if (level.getBlockState(pos).getDestroySpeed(level, pos) <= firstBlockSpeed + 20) {
                breakBlock(tool, level, pos, entity);
                if (sendServerPackets) {
                    PacketDistributor.sendToServer(new BlockBreakPayload(pos));
                }
            }
        }
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return correctTool(stack, state) && INITIAL_TIER.createToolProperties(blocks).isCorrectForDrops(state);
    }

    @Override
    public String getItemType() {
        return this.itemType;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        int bonusDurability = stack.getOrDefault(DataComponents.DURABILITY_BONUS, 0);
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        ToolUtils.appendHoverText(stack, tooltipComponents, flag, this);
    }

    // TODO: These that I have to override the same in armor / bow ... should probably be a default method I can call
    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Runnable onBroken) {
        // TODO: Check this, new vanilla method getDamageValue
        int durability = this.getMaxDamage(stack) - stack.getDamageValue();
        float unbreakingLevel = stack.getOrDefault(DataComponents.UNBREAKING, 0F);
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
    public ItemAttributeModifiers getLevelableAttributeModifiers(ItemStack stack) {
        // TODO: Make sure default attributes are still applied, I assume they should be
        if (!ToolUtils.isBroken(stack)) {
            ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

            float attackDamage = stack.getOrDefault(DataComponents.DAMAGE_BONUS, 0F);
            if (attackDamage > 0) {
                builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            }

            float attackSpeed = stack.getOrDefault(DataComponents.ATTACK_SPEED, 0F);
            if (attackSpeed > 0) {
                builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            }

            float attackKnockback = stack.getOrDefault(DataComponents.KNOCKBACK, 0F);
            if (attackKnockback > 0) {
                builder.add(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(ATTACK_KNOCKBACK_UUID, "Weapon modifier", attackKnockback, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            }

            float knockbackResistance = stack.getOrDefault(DataComponents.KNOCKBACK_RESISTANCE, 0F);
            if (knockbackResistance > 0) {
                builder.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(KNOCKBACK_RESISTANCE_UUID, "Weapon modifier", knockbackResistance, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            }

            float reach = stack.getOrDefault(DataComponents.REACH, 0F);
            if (reach > 0) {
                builder.add(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(REACH_UUID, "Tool modifier", reach, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
                builder.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(ATTACK_RANGE_UUID, "Weapon modifier", reach, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            }

            return builder.build();
        } else {
            return super.getAttributeModifiers(stack);
        }
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get();
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get();
    }

    protected float getAttackDamage(ItemStack stack) {
        return initialAttackDamage + stack.getOrDefault(DataComponents.DAMAGE_BONUS, 0F);
    }

    private boolean shouldAutoSmelt(ItemStack stack) {
        return stack.getOrDefault(DataComponents.AUTO_SMELT, false) && !stack.getOrDefault(DataComponents.DISABLE_AUTO_SMELT, false);
    }
}
