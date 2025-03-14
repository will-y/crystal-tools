package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;
import java.util.Random;

public class HoeLevelableTool extends DiggerLevelableTool {
    public HoeLevelableTool(Item.Properties properties) {
        super(properties, BlockTags.MINEABLE_WITH_HOE, "hoe", -4, 0);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (this.isDisabled()) {
            context.getItemInHand().shrink(1);
            return InteractionResult.FAIL;
        }

        return ToolUseUtils.useOnHoe3x3(context, this);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_HOE_ACTIONS.contains(itemAbility) ||
                (stack.getOrDefault(DataComponents.SHEAR, false) && ItemAbilities.DEFAULT_SHEARS_ACTIONS.contains(itemAbility));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (this.isDisabled()) {
            stack.shrink(1);
            return InteractionResult.FAIL;
        }

        if (stack.getOrDefault(DataComponents.SHEAR, false) && entity instanceof IShearable target) {
            if (entity.level().isClientSide) return net.minecraft.world.InteractionResult.SUCCESS;
            BlockPos pos = BlockPos.containing(entity.position());
            if (target.isShearable(player, stack, entity.level(), pos)) {
                List<ItemStack> drops = target.onSheared(player, stack, entity.level(), pos);
                Random rand = new java.util.Random();
                drops.forEach(d -> {
                    ItemEntity ent = entity.spawnAtLocation((ServerLevel) entity.level(), d, 1.0F);
                    if (ent != null) {
                        ent.setDeltaMovement(ent.getDeltaMovement().add(((rand.nextFloat() - rand.nextFloat()) * 0.1F), (rand.nextFloat() * 0.05F), ((rand.nextFloat() - rand.nextFloat()) * 0.1F)));
                    }
                });
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            }
            return net.minecraft.world.InteractionResult.SUCCESS;
        }
        return net.minecraft.world.InteractionResult.PASS;
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_HOE.get();
    }
}
