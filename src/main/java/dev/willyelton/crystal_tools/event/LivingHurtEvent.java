package dev.willyelton.crystal_tools.event;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.item.LevelableItem;
import dev.willyelton.crystal_tools.item.armor.LevelableArmor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=CrystalTools.MODID)
public class LivingHurtEvent {
    @SubscribeEvent
    public static void handleLivingHurtEvent(net.minecraftforge.event.entity.living.LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity instanceof Player player) {
            float damageAmount = event.getAmount();

            for (ItemStack armor : player.getArmorSlots()) {
                if (armor.getItem() instanceof LevelableItem item) {
                    item.addExp(armor, player.getLevel(), player.getOnPos(), player, (int) (damageAmount * CrystalToolsConfig.ARMOR_EXPERIENCE_BOOST.get()));
                }
            }
        }
    }
}
