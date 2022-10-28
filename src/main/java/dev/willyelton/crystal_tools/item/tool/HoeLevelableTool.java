package dev.willyelton.crystal_tools.item.tool;

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

public class HoeLevelableTool extends LevelableTool {
    public HoeLevelableTool() {
        super(new Item.Properties(), BlockTags.MINEABLE_WITH_HOE, "hoe");
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        return ToolUseUtils.useOnHoe(context, this);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return ToolActions.DEFAULT_HOE_ACTIONS.contains(toolAction) || (NBTUtils.getFloatOrAddKey(stack, "shear") >= 1 && ToolActions.DEFAULT_SHEARS_ACTIONS.contains(toolAction));
    }

    @Override
    public net.minecraft.world.@NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, net.minecraft.world.entity.player.@NotNull Player playerIn, @NotNull LivingEntity entity, net.minecraft.world.@NotNull InteractionHand hand) {
        if (NBTUtils.getFloatOrAddKey(stack, "shear") >= 1 && entity instanceof net.minecraftforge.common.IForgeShearable target) {
            if (entity.level.isClientSide) return net.minecraft.world.InteractionResult.SUCCESS;
            BlockPos pos = new BlockPos(entity.getX(), entity.getY(), entity.getZ());
            if (target.isShearable(stack, entity.level, pos)) {
                java.util.List<ItemStack> drops = target.onSheared(playerIn, stack, entity.level, pos,
                        net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.BLOCK_FORTUNE, stack));
                java.util.Random rand = new java.util.Random();
                drops.forEach(d -> {
                    net.minecraft.world.entity.item.ItemEntity ent = entity.spawnAtLocation(d, 1.0F);
                    ent.setDeltaMovement(ent.getDeltaMovement().add(((rand.nextFloat() - rand.nextFloat()) * 0.1F), (rand.nextFloat() * 0.05F), ((rand.nextFloat() - rand.nextFloat()) * 0.1F)));
                });
                stack.hurtAndBreak(1, playerIn, e -> e.broadcastBreakEvent(hand));
                this.addExp(stack, entity.level, playerIn.getOnPos(), playerIn);
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
