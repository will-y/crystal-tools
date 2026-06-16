package dev.willyelton.crystal.core.common.event;

import dev.willyelton.crystal.core.common.capability.Capabilities;
import dev.willyelton.crystal.core.common.capability.Levelable;
import dev.willyelton.crystal.core.common.capability.LevelableStack;
import dev.willyelton.crystal.core.common.datacomponent.DataComponents;
import dev.willyelton.crystal.core.utils.InventoryUtils;
import dev.willyelton.crystal.core.utils.ToolUtils;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = ApiConstants.CORE_MOD_ID)
public class LivingHurtEvent {
    @SubscribeEvent
    public static void handleLivingHurtEvent(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        float damageAmount = event.getOriginalDamage();

        if (target instanceof Player player) {
            for (ItemStack armor : InventoryUtils.getArmorItems(player)) {
                Levelable levelable = armor.getCapability(Capabilities.ITEM_SKILL, player.level().registryAccess());
                if (levelable != null) {
                    levelable.addExp(player.level(), player.getOnPos(), player, damageAmount);
                }
            }
        }

        if (event.getSource().getEntity() instanceof Player player) {
            ItemStack stack = player.getWeaponItem();
            if (!stack.isEmpty() && !ToolUtils.isBroken(stack)) {
                LevelableStack levelable = stack.getCapability(Capabilities.ITEM_SKILL, player.level().registryAccess());
                if (levelable == null) return;

                if (stack.getOrDefault(DataComponents.FIRE, false)) {
                    // Equal to fire aspect 2
                    target.igniteForSeconds(8);
                }

                if (ToolUtils.isValidEntity(target)) {
                    int heal = stack.getOrDefault(DataComponents.LIFESTEAL, 0);

                    if (heal > 0) {
                        player.heal(heal);
                    }

                    if (levelable.allowDamageXp()) {
                        levelable.addExp(player.level(), player.getOnPos(), player, damageAmount);
                    }
                }
            }
        }
    }
}
