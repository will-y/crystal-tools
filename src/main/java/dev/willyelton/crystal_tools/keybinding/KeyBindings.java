package dev.willyelton.crystal_tools.keybinding;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid=CrystalTools.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value= Dist.CLIENT)
public class KeyBindings {
    public static KeyMapping upgradeMenu;
    public static KeyMapping veinMine;
    public static KeyMapping modeSwitch;
    public static KeyMapping openBackpack;

    private static KeyMapping createBinding(String name, int key, RegisterKeyMappingsEvent e) {
        KeyMapping keyBinding = new KeyMapping(getKey(name), key, getKey("category"));
        e.register(keyBinding);
        return keyBinding;
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent e) {
        upgradeMenu = createBinding("upgrade_menu", GLFW.GLFW_KEY_K, e);
        veinMine = createBinding("vein_mine", GLFW.GLFW_KEY_GRAVE_ACCENT, e);
        modeSwitch = createBinding("mode_switch", GLFW.GLFW_KEY_M, e);
        openBackpack = createBinding("open_backpack", GLFW.GLFW_KEY_B, e);
    }

    private static String getKey(String name) {
        return String.join(".", "key", CrystalTools.MODID, name);
    }
}
