package dev.willyelton.crystal.core.client.event;

import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

import static dev.willyelton.crystal.core.utils.constants.ApiConstants.baseRl;

@EventBusSubscriber(modid = ApiConstants.CORE_MOD_ID, value= Dist.CLIENT)
public class RegisterKeyBindingsEvent {
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(baseRl("category"));

    public static KeyMapping UPGRADE_MENU;

    private static KeyMapping createBinding(String name, int key, RegisterKeyMappingsEvent e) {
        KeyMapping keyBinding = new KeyMapping(getKey(name), key, CATEGORY);
        e.register(keyBinding);
        return keyBinding;
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent e) {
        e.registerCategory(CATEGORY);

        UPGRADE_MENU = createBinding("upgrade_menu", GLFW.GLFW_KEY_K, e);
    }

    private static String getKey(String name) {
        return String.join(".", "key", ApiConstants.CORE_MOD_ID, name);
    }
}
