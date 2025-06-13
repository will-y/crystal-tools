package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.capability.Capabilities;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.InventoryUtils;
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
        LivingEntity entity = event.getEntity();

        if (entity instanceof Player player) {
            float damageAmount = event.getOriginalDamage();

            for (ItemStack armor : InventoryUtils.getArmorItems(player)) {
                Levelable levelable = armor.getCapability(Capabilities.ITEM_SKILL, player.level());
                if (levelable != null) {
                    levelable.addExp(player.level(), player.getOnPos(), player, (int) (damageAmount * CrystalToolsConfig.ARMOR_EXPERIENCE_BOOST.get()));
                }
            }
        }
    }
}
