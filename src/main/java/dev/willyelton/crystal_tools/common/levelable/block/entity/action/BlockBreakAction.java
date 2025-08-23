package dev.willyelton.crystal_tools.common.levelable.block.entity.action;

import com.google.common.primitives.Floats;
import com.mojang.serialization.Codec;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.block.entity.ActionBlockEntity;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static dev.willyelton.crystal_tools.common.levelable.block.CrystalPedestalBlock.FACING;

// TODO: Consider making the quarry use this?
public class BlockBreakAction extends Action {
    private final float[] breakProgress;
    List<ItemStack> noFit = new ArrayList<>();

    public BlockBreakAction(ActionBlockEntity blockEntity, @Nullable ActionParameters params) {
        super(blockEntity, params);
        int bonusRange = Mth.floor(getItem().getAttributeModifiers().modifiers().stream()
                .filter(e -> e.attribute().is(Attributes.BLOCK_INTERACTION_RANGE))
                .findFirst().map(e -> e.modifier()
                        .amount())
                .orElse(0D));
        int range = (params == null ? this.getDefaultParameters().range() : params.range()) + bonusRange;
        breakProgress = new float[range + 1];
    }

    @Override
    public void tickAction(@NotNull Level level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        if (!noFit.isEmpty()) {
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
            if (handler != null) {
                noFit = InventoryUtils.tryInsertStacks(handler, noFit);

                if (!noFit.isEmpty()) {
                    return;
                }
            } else {
                dropItems(level, pos.relative(facing), noFit);
            }
        }

        for (int i = 0; i < breakProgress.length; i++) {
            BlockPos miningPos = pos.relative(facing, i + 1);

            ItemStack stack = getItem();
            int durabilityToUse = Mth.ceil(params.durabilityModifier());
            if (ToolUtils.willBreak(stack, durabilityToUse) || stack.isEmpty()) return;

            BlockState miningState = level.getBlockState(miningPos);

            if (miningState.requiresCorrectToolForDrops() && !stack.isCorrectToolForDrops(miningState)) continue;

            if (miningState.isAir()) continue;
            float miningSpeed = stack.getDestroySpeed(miningState);
            float destroySpeed = state.getDestroySpeed(level, pos);
            if (destroySpeed == -1) continue;
            breakProgress[i] += miningSpeed / destroySpeed / 30;
            level.destroyBlockProgress(-1 - i, miningPos, (int) (breakProgress[i] * 10.0F));

            if (breakProgress[i] >= 1.0F) {
                // Break block
                List<ItemStack> drops = new ArrayList<>(Block.getDrops(miningState, (ServerLevel) level, miningPos, level.getBlockEntity(miningPos), null, stack));
                IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
                if (handler != null) {
                    noFit = InventoryUtils.tryInsertStacks(handler, drops);
                } else {
                    dropItems(level, miningPos, drops);
                }

                level.destroyBlock(miningPos, false);
                // TODO: Abstract this part out into `Action`
                stack.hurtAndBreak(durabilityToUse, (ServerLevel) level, null, item -> {});
                Levelable levelable = stack.getCapability(dev.willyelton.crystal_tools.common.capability.Capabilities.ITEM_SKILL, level.registryAccess());
                if (levelable != null && CrystalToolsConfig.LEVEL_ITEMS_IN_PEDESTAL.get()) {
                    levelable.addExp(level, pos, null);
                }
                breakProgress[i] = 0F;
            }

            if (!noFit.isEmpty()) {
                break;
            }
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.BLOCK_BREAK;
    }

    @Override
    public ActionParameters getDefaultParameters() {
        return new ActionParameters(1);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void save(ValueOutput valueOutput) {
        valueOutput.store("breakProgress", Codec.FLOAT.listOf(), Floats.asList(breakProgress));
        valueOutput.store("NoFit", ItemStack.OPTIONAL_CODEC.listOf(), this.noFit);
    }

    @Override
    public void load(ValueInput valueInput) {
        List<Float> breakingProgressList = valueInput.read("breakProgress", Codec.FLOAT.listOf()).orElse(new ArrayList<>());
        for (int i = 0; i < breakProgress.length; i++) {
            if (breakingProgressList.size() > i) {
                breakProgress[i] = breakingProgressList.get(i);
            }
        }
        this.noFit = valueInput.read("NoFit", ItemStack.OPTIONAL_CODEC.listOf()).orElse(new ArrayList<>());
    }

    @Override
    public void onRemove() {
        if (this.blockEntity.getLevel() == null) return;

        dropItems(this.blockEntity.getLevel(), this.blockEntity.getBlockPos(), this.noFit);

        for (int i = 0; i < this.breakProgress.length; i++) {
            this.blockEntity.getLevel().destroyBlockProgress(-1 - i, this.blockEntity.getBlockPos().relative(this.blockEntity.getBlockState().getValue(FACING), i + 1), -1);
        }
    }

    private void dropItems(Level level, BlockPos pos, List<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack));
        }

        stacks.clear();
    }
}
