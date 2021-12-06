package dev.willyelton.crystal_tools.event;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.gui.ModGUIs;
import dev.willyelton.crystal_tools.gui.UpgradeScreen;
import dev.willyelton.crystal_tools.keybindings.KeyBindings;
import dev.willyelton.crystal_tools.tool.LevelableTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT)
public class KeyPressEvent {
    @SubscribeEvent
    public static void handleEventInput(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || event.phase == TickEvent.Phase.START)
            return;

        if (KeyBindings.upgradeMenu.consumeClick()) {
            for (ItemStack i : mc.player.getHandSlots()) {
                if (i.getItem() instanceof LevelableTool) {
                    ModGUIs.openScreen(new UpgradeScreen(i));
                    break;
                }
            }
        }
    }
}
