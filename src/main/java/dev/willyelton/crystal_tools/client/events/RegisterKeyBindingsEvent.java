package dev.willyelton.crystal_tools.client.events;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid=CrystalTools.MODID, bus=EventBusSubscriber.Bus.MOD, value= Dist.CLIENT)
public class RegisterKeyBindingsEvent {
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
