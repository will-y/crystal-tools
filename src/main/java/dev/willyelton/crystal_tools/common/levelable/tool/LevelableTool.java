package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.common.capability.Capabilities;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.common.network.data.BlockBreakPayload;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

public abstract class LevelableTool extends Item implements LevelableItem {
    public LevelableTool(Item.Properties properties) {
        super(properties.fireResistant()
                .rarity(Rarity.RARE)
                .component(net.minecraft.core.component.DataComponents.ENCHANTABLE, null));
    }

    @Override
    public void hurtEnemy(ItemStack tool, LivingEntity target, LivingEntity attacker) {
        tool.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }

    public void breakBlock(ItemStack stack, Level level, BlockPos blockPos, LivingEntity entity) {
        BlockState blockState = level.getBlockState(blockPos);
        if (isCorrectToolForDrops(stack, blockState) && !ToolUtils.isBroken(stack) && entity instanceof ServerPlayer) {
            if (!level.isClientSide) {
                Block.dropResources(blockState, level, blockPos, level.getBlockEntity(blockPos), entity, stack);
                level.destroyBlock(blockPos, false, entity);
                stack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
            }

            Levelable levelable = stack.getCapability(Capabilities.ITEM_SKILL, level.registryAccess());
            if (levelable == null) {
                return;
            }

            levelable.addExp(level, blockPos, entity);
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
                    ClientPacketDistributor.sendToServer(new BlockBreakPayload(pos));
                }
            }
        }
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
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        // Just ignore data components for now
        return !newStack.is(oldStack.getItem());
    }
}
