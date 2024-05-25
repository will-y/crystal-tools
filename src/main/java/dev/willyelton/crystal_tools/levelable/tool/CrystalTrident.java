package dev.willyelton.crystal_tools.levelable.tool;


import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.renderer.CrystalTridentBlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CrystalTrident extends SwordLevelableTool {
    public CrystalTrident() {
        super("trident", 3, -2.4F);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack pStack, @NotNull BlockState pState) {
        return 1.0F;
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState block) {
        return false;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return false;
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_TRIDENT.get();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return CrystalToolsConfig.ENCHANT_TOOLS.get() && enchantment.category.equals(EnchantmentCategory.TRIDENT);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return CrystalTridentBlockEntityWithoutLevelRenderer.INSTANCE;
            }
        });
    }

    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.SPEAR;
    }

    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getDamageValue() >= itemstack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(itemstack);
        } else if (EnchantmentHelper.getRiptide(itemstack) > 0 && !player.isInWaterOrRain()) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemstack);
        }
    }
}
