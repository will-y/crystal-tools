package dev.willyelton.crystal.core.common.network.handler;

import dev.willyelton.crystal.core.common.network.payload.ModeSwitchPayload;
import dev.willyelton.crystal.core.common.registry.ModeSwitchHandlerRegistry;
import dev.willyelton.crystal.core.common.skill.SkillData;
import dev.willyelton.crystal.core.utils.InventoryUtils;
import dev.willyelton.crystal.core.utils.ItemStackUtils;
import dev.willyelton.crystal.core.utils.RegistryUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public class ModeSwitchPacketHandler {
    public static final ModeSwitchPacketHandler INSTANCE = new ModeSwitchPacketHandler();

    // TODO: Try offhand if main hand doesn't get handled
    public void handle(final ModeSwitchPayload payload, final IPayloadContext context) {
        Player player = context.player();
        ItemStack stack = ItemStackUtils.getHeldLevelableTool(player);

        if (tryHandle(player, stack, payload, true)) {
            return;
        }

        // If not handled, try armor
        List<ItemStack> armorItems = InventoryUtils.getArmorItems(player);

        for (ItemStack armorStack : armorItems) {
            if (tryHandle(player, armorStack, payload, false)) {
                return;
            }
        }
    }

    private static boolean tryHandle(Player player, ItemStack stack, ModeSwitchPayload payload, boolean isHeld) {
        SkillData skillData = RegistryUtils.getSkillData(stack, player.level().registryAccess());

        if (skillData != null) {
            for (ModeSwitchHandlerRegistry.ModeSwitchHandler handler : ModeSwitchHandlerRegistry.getModeSwitchHandlers()) {
                if (handler.handle(player, stack, skillData, payload.hasShiftDown(), payload.hasCtrlDown(), payload.hasAltDown(), isHeld)) {
                    return true;
                }
            }
        }

        return false;
    }
}
