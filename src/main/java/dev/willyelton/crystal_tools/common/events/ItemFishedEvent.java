package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.capability.Capabilities;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.tool.CrystalFishingRod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.Collection;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class ItemFishedEvent {
    @SubscribeEvent
    public static void handleItemFishedEvent(net.neoforged.neoforge.event.entity.player.ItemFishedEvent event) {
        Player player = event.getEntity();
        FishingHook hook = event.getHookEntity();
        ItemStack rodStack;
        if (player.getMainHandItem().is(ModRegistration.CRYSTAL_FISHING_ROD.get()) && hook.getTags().contains(CrystalFishingRod.CRYSTAL_TOOLS_FISHING_MAIN_TAG)) {
            rodStack = player.getMainHandItem();
        } else if (player.getOffhandItem().is(ModRegistration.CRYSTAL_FISHING_ROD.get()) && hook.getTags().contains(CrystalFishingRod.CRYSTAL_TOOLS_FISHING_OFF_TAG)) {
            rodStack = player.getOffhandItem();
        } else {
            return;
        }

        if (!rodStack.isEmpty()) {
            if (rodStack.getOrDefault(DataComponents.DOUBLE_DROPS, 0F) > player.level().getRandom().nextFloat()) {
                Collection<ItemStack> copiedDrops = event.getDrops().stream().map(ItemStack::copy).toList();
                CrystalFishingRod.dropExtraItems(copiedDrops, player, hook);
            }

            Levelable levelable = rodStack.getCapability(Capabilities.ITEM_SKILL, player.level().registryAccess());

            if (levelable != null) {
                levelable.addExp(player.level(), player.getOnPos(), player, CrystalToolsConfig.FISHING_ROD_EXP.get());
            }
        }
    }
}
