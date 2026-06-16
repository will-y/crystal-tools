package dev.willyelton.crystal.tools.common.network.handler;

import dev.willyelton.crystal.core.utils.InventoryUtils;
import dev.willyelton.crystal.core.utils.ItemStackUtils;
import dev.willyelton.crystal.tools.ModRegistration;
import dev.willyelton.crystal.tools.common.components.DataComponents;
import dev.willyelton.crystal.tools.common.network.data.MagnetModePayload;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MagnetModeSwitchHandler {
    public static final MagnetModeSwitchHandler INSTANCE = new MagnetModeSwitchHandler();

    public void handle(final MagnetModePayload payload, final IPayloadContext context) {
        Player player = context.player();

        ItemStack tool = ItemStackUtils.getHeldLevelableTool(player);

        if (tool.is(ModRegistration.CRYSTAL_MAGNET.get())) {
            toggleMagnet(player, tool, payload.hasShiftDown());
        } else {
            InventoryUtils.findAll(player, stack -> stack.is(ModRegistration.CRYSTAL_MAGNET.get()))
                    .forEach(stack -> toggleMagnet(player, stack, payload.hasShiftDown()));
        }
    }


    private void toggleMagnet(Player player, ItemStack stack, boolean shift) {
        if (shift) {
            if (stack.getOrDefault(DataComponents.PULL_MOBS, false)) {
                boolean disabled = stack.getOrDefault(DataComponents.DISABLE_MOB_PULL, false);
                stack.set(DataComponents.DISABLE_MOB_PULL, !disabled);
                player.sendOverlayMessage(Component.literal("Pulling Mobs " + (disabled ? "Enabled" : "Disabled")));
            }
        } else {
            boolean disabled = stack.getOrDefault(DataComponents.DISABLED, false);
            stack.set(DataComponents.DISABLED, !disabled);
            player.sendOverlayMessage(Component.literal("Magnet " + (disabled ? "Enabled" : "Disabled")));
        }
    }
}
