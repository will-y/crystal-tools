package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.capability.Capabilities;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = CrystalTools.MODID)
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
                Levelable levelable = stack.getCapability(Capabilities.ITEM_SKILL, player.level().registryAccess());
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

                    levelable.addExp(player.level(), player.getOnPos(), player, damageAmount);
                }
            }
        }
    }
}
