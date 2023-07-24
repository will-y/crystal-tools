package dev.willyelton.crystal_tools.levelable.tool;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;

public class HoeLevelableTool extends DiggerLevelableTool {
    public HoeLevelableTool() {
        super(new Item.Properties(), BlockTags.MINEABLE_WITH_HOE, "hoe", -4, 0);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        if (this.isDisabled()) {
            context.getItemInHand().shrink(1);
            return InteractionResult.FAIL;
        }

        if (NBTUtils.getFloatOrAddKey(context.getItemInHand(), "3x3") > 0 && !NBTUtils.getBoolean(context.getItemInHand(), "disable_3x3")) {
            InteractionResult result = null;

            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    InteractionResult hoeResult = ToolUseUtils.useOnHoe(context, this, context.getClickedPos().offset(i, 0, j));
                    if (!hoeResult.equals(InteractionResult.PASS)) {
                        result = hoeResult;
                    }
                }
            }

            return result == null ? InteractionResult.PASS : result;
        }

        return ToolUseUtils.useOnHoe(context, this);


    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return ToolActions.DEFAULT_HOE_ACTIONS.contains(toolAction) || (NBTUtils.getFloatOrAddKey(stack, "shear") >= 1 && ToolActions.DEFAULT_SHEARS_ACTIONS.contains(toolAction));
    }

    @Override
    public net.minecraft.world.@NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, net.minecraft.world.entity.player.@NotNull Player playerIn, @NotNull LivingEntity entity, net.minecraft.world.@NotNull InteractionHand hand) {
        if (this.isDisabled()) {
            stack.shrink(1);
            return InteractionResult.FAIL;
        }

        if (NBTUtils.getFloatOrAddKey(stack, "shear") >= 1 && entity instanceof net.minecraftforge.common.IForgeShearable target) {
            if (entity.level().isClientSide) return net.minecraft.world.InteractionResult.SUCCESS;
            BlockPos pos = BlockPos.containing(entity.position());
            if (target.isShearable(stack, entity.level(), pos)) {
                java.util.List<ItemStack> drops = target.onSheared(playerIn, stack, entity.level(), pos,
                        net.minecraft.world.item.enchantment.EnchantmentHelper.getTagEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.BLOCK_FORTUNE, stack));
                java.util.Random rand = new java.util.Random();
                drops.forEach(d -> {
                    net.minecraft.world.entity.item.ItemEntity ent = entity.spawnAtLocation(d, 1.0F);
                    ent.setDeltaMovement(ent.getDeltaMovement().add((double)((rand.nextFloat() - rand.nextFloat()) * 0.1F), (double)(rand.nextFloat() * 0.05F), (double)((rand.nextFloat() - rand.nextFloat()) * 0.1F)));
                });
                stack.hurtAndBreak(1, playerIn, e -> e.broadcastBreakEvent(hand));
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
