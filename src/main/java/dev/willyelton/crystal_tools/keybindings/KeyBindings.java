package dev.willyelton.crystal_tools.keybindings;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static KeyMapping upgradeMenu;

    public static void init() {
        upgradeMenu = createBinding("upgrade_menu", GLFW.GLFW_KEY_K);
    }

    private static KeyMapping createBinding(String name, int key) {
        KeyMapping keyBinding = new KeyMapping(getKey(name), key, getKey("category"));
        ClientRegistry.registerKeyBinding(keyBinding);
        return keyBinding;
    }

    private static String getKey(String name) {
        return String.join(".", "key", CrystalTools.MODID, name);
    }
}
