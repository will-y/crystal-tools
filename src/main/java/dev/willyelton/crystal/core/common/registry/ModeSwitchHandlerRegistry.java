package dev.willyelton.crystal.core.common.registry;

import dev.willyelton.crystal.core.common.skill.SkillData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class ModeSwitchHandlerRegistry {
    public static final int HIGHEST = 1;
    public static final int LOWEST = 1000;
    public static final int NORMAL = 500;

    private static final TreeSet<ModeSwitchHandlerWrapper> MODE_SWITCH_HANDLERS = new TreeSet<>(Comparator.comparingInt(ModeSwitchHandlerWrapper::priority));

    public static void addModeSwitchHandler(ModeSwitchHandler handler) {
        addModeSwitchHandler(handler, NORMAL);
    }

    public static void addModeSwitchHandler(ModeSwitchHandler handler, int priority) {
        MODE_SWITCH_HANDLERS.add(new ModeSwitchHandlerWrapper(handler, priority));
    }

    public static List<ModeSwitchHandler> getModeSwitchHandlers() {
        return MODE_SWITCH_HANDLERS.stream().map(ModeSwitchHandlerWrapper::handler).toList();
    }

    @ApiStatus.Internal
    public record ModeSwitchHandlerWrapper(ModeSwitchHandler handler, int priority) {}

    @FunctionalInterface
    public interface ModeSwitchHandler {
        /// Handles a mode switch.
        ///
        /// Called on the server whenever the player presses {@link dev.willyelton.crystal.core.client.event.RegisterKeyBindingsEvent#MODE_SWITCH}.
        ///
        /// Will be called on any item that the player is holding, and any armor they are wearing as long as they are Levelable.
        ///
        /// @param player The player who pressed the mode switch key
        /// @param stack The levelable stack
        /// @param skillData The skill data of the stack
        /// @param hasShiftDown If the player is holding shift
        /// @param hasCtrlDown If the player is holding control
        /// @param hasAltDown If the player is holding alt
        /// @param isHeld If the player is holding the item in either hand
        /// @return True if this handler is handling the interaction (blocks all further mode switch actions)
        boolean handle(Player player, ItemStack stack, SkillData skillData, boolean hasShiftDown, boolean hasCtrlDown,
                       boolean hasAltDown, boolean isHeld);
    }
}
