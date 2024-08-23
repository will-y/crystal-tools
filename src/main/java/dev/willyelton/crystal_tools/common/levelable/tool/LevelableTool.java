package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.common.network.data.BlockBreakPayload;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public abstract class LevelableTool extends TieredItem implements LevelableItem {
    protected static final ResourceLocation ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "attack_damage");
    protected static final ResourceLocation ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "attack_speed");
    protected static final ResourceLocation ATTACK_KNOCKBACK_ID = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "attack_knockback");
    protected static final ResourceLocation KNOCKBACK_RESISTANCE_ID = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "knockback_resistance");
    protected static final ResourceLocation REACH_ID = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "reach");
    protected static final ResourceLocation ATTACK_RANGE_ID = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "attack_range");

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
                                new AttributeModifier(BASE_ATTACK_DAMAGE_ID, INITIAL_TIER.getAttackDamageBonus() + attackDamageModifier, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        .add(Attributes.ATTACK_SPEED,
                                new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeedModifier, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        .build()));
        this.blocks = mineableBlocks;
        this.itemType = itemType;
        this.initialDurability = durability;
        this.initialAttackDamage = INITIAL_TIER.getAttackDamageBonus() + attackDamageModifier;
    }

    @Override
    public float getDestroySpeed(ItemStack tool, BlockState blockState) {
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
    public boolean hurtEnemy(ItemStack tool, LivingEntity target, LivingEntity attacker) {
        if (this.isDisabled()) {
            tool.shrink(1);
            return false;
        }

        tool.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);

        if (!ToolUtils.isBroken(tool)) {
            if (tool.getOrDefault(DataComponents.FIRE, false)) {
                target.setRemainingFireTicks(5);
            }

            if (ToolUtils.isValidEntity(target)) {
                int heal = tool.getOrDefault(DataComponents.LIFESTEAL, 0);

                if (heal > 0) {
                    attacker.heal(heal);
                }

                addExp(tool, target.level(), attacker.getOnPos(), attacker, (int) (getAttackDamage(tool) * getAttackExperienceBoost()));
            }
        }

        return true;
    }

    @Override
    public boolean mineBlock(ItemStack tool, Level level, BlockState blockState, BlockPos blockPos, LivingEntity entity) {
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
        BlockState blockState = level.getBlockState(blockPos);
        if (isCorrectToolForDrops(tool, blockState) && !ToolUtils.isBroken(tool) && entity instanceof ServerPlayer serverPlayer) {
            if (!level.isClientSide) {
                level.destroyBlock(blockPos, false, entity);
                Block.dropResources(blockState, level, blockPos, level.getBlockEntity(blockPos), entity, tool);
                tool.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
            }
            addExp(tool, level, blockPos, entity);
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
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    // Changing these two to what they should be @minecraft
    @Override
    public int getBarWidth(ItemStack itemStack) {
        return Math.round(13.0F - (float) itemStack.getDamageValue() * 13.0F / (float) itemStack.getMaxDamage());
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        float f = Math.max(0.0F, ((float) itemStack.getMaxDamage() - (float) itemStack.getDamageValue()) / (float) itemStack.getMaxDamage());
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int inventorySlot, boolean inHand) {
        levelableInventoryTick(itemStack, level, entity, inventorySlot, inHand, 1);
    }

    @Override
    public boolean isValidRepairItem(ItemStack tool, ItemStack repairItem) {
        return repairItem.is(Registration.CRYSTAL.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        appendLevelableHoverText(stack, tooltipComponents, this, flag);
    }

    // TODO: These that I have to override the same in armor / bow ... should probably be a default method I can call
    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
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
        if (!ToolUtils.isBroken(stack)) {
            ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

            float attackDamage = stack.getOrDefault(DataComponents.DAMAGE_BONUS, 0F);
            if (attackDamage > 0) {
                builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            }

            float attackSpeed = stack.getOrDefault(DataComponents.ATTACK_SPEED, 0F);
            if (attackSpeed > 0) {
                builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            }

            float attackKnockback = stack.getOrDefault(DataComponents.KNOCKBACK, 0F);
            if (attackKnockback > 0) {
                builder.add(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(ATTACK_KNOCKBACK_ID, attackKnockback, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            }

            float knockbackResistance = stack.getOrDefault(DataComponents.KNOCKBACK_RESISTANCE, 0F);
            if (knockbackResistance > 0) {
                builder.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(KNOCKBACK_RESISTANCE_ID, knockbackResistance, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            }

            float reach = stack.getOrDefault(DataComponents.REACH, 0F);
            if (reach > 0) {
                builder.add(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(REACH_ID, reach, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
                builder.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(ATTACK_RANGE_ID, reach, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            }

            return builder.build();
        } else {
            return ItemAttributeModifiers.builder().build();
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

    protected double getAttackExperienceBoost() {
        return 1D;
    }
}
