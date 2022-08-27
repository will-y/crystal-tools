package dev.willyelton.crystal_tools.item.tool;

import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CrystalRocket extends LevelableTool {
    public CrystalRocket() {
        super(new Item.Properties(), null, "crystal_rocket", 100);
    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (ToolUtils.isBroken(itemstack)) {
            return InteractionResultHolder.pass(itemstack);
        }
        if (pPlayer.isFallFlying()) {

            if (!pLevel.isClientSide) {
                FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(pLevel, itemstack, pPlayer);
                pLevel.addFreshEntity(fireworkrocketentity);

                pPlayer.awardStat(Stats.ITEM_USED.get(this));
            }

            addExp(itemstack, pLevel, pPlayer.getOnPos(), pPlayer, 5);
            itemstack.hurtAndBreak(1, pPlayer, (player) -> player.broadcastBreakEvent(EquipmentSlot.MAINHAND));

            return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
        } else {
            return InteractionResultHolder.pass(pPlayer.getItemInHand(pHand));
        }
    }
}
