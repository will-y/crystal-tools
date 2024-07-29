package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;

import java.util.Collections;

public class CrystalRocket extends LevelableTool {
    public CrystalRocket() {
        super(new Item.Properties(), null, "crystal_rocket", -4, 0, 100);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.isDisabled()) {
            itemstack.shrink(1);
            return InteractionResultHolder.fail(itemstack);
        }

        if (ToolUtils.isBroken(itemstack)) {
            return InteractionResultHolder.pass(itemstack);
        }

        if (pPlayer.isFallFlying()) {
            if (!pLevel.isClientSide) {
                int flightTime = itemstack.getOrDefault(DataComponents.FLIGHT_TIME, 1);
                Fireworks fireworks = new Fireworks(flightTime, Collections.emptyList());
                itemstack.set(net.minecraft.core.component.DataComponents.FIREWORKS, fireworks);
                FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(pLevel, itemstack, pPlayer);
                pLevel.addFreshEntity(fireworkrocketentity);

                pPlayer.awardStat(Stats.ITEM_USED.get(this));
            }

            addExp(itemstack, pLevel, pPlayer.getOnPos(), pPlayer, CrystalToolsConfig.ROCKET_EXPERIENCE_BOOST.get());
            itemstack.hurtAndBreak(1, pPlayer, EquipmentSlot.MAINHAND);

            return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
        } else {
            return InteractionResultHolder.pass(pPlayer.getItemInHand(pHand));
        }
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int inventorySlot, boolean inHand) {
        levelableInventoryTick(itemStack, level, entity, inventorySlot, inHand, CrystalToolsConfig.ROCKET_REPAIR_MODIFIER.get());
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_ROCKET.get();
    }
}
