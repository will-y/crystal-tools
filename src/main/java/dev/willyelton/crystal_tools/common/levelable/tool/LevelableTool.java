package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.common.network.data.BlockBreakPayload;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

public abstract class LevelableTool extends Item implements LevelableItem {
    protected static final ResourceLocation ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "attack_damage");

    // Blocks that can be mined by default, null for none
    protected final String itemType;

    public LevelableTool(Item.Properties properties, String itemType) {
        super(properties.fireResistant()
                .rarity(Rarity.RARE));
        this.itemType = itemType;
    }

    @Override
    public void hurtEnemy(ItemStack tool, LivingEntity target, LivingEntity attacker) {
        if (this.isDisabled()) {
            tool.shrink(1);
            return;
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
        if (isCorrectToolForDrops(tool, blockState) && !ToolUtils.isBroken(tool) && entity instanceof ServerPlayer) {
            if (!level.isClientSide) {
                Block.dropResources(blockState, level, blockPos, level.getBlockEntity(blockPos), entity, tool);
                level.destroyBlock(blockPos, false, entity);
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
    public String getItemType() {
        return this.itemType;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        levelableInventoryTick(itemStack, level, entity, slot, 1);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
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
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get();
    }

    protected float getAttackDamage(ItemStack stack) {
        return CRYSTAL.attackDamageBonus() + stack.getOrDefault(DataComponents.DAMAGE_BONUS, 0F);
    }

    protected double getAttackExperienceBoost() {
        return 1D;
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        // Just ignore data components for now
        return !newStack.is(oldStack.getItem());
    }
}
