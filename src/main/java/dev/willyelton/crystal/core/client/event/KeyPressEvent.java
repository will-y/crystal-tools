package dev.willyelton.crystal.core.client.event;

import dev.willyelton.crystal.core.client.gui.UpgradeScreen;
import dev.willyelton.crystal.core.common.capability.Capabilities;
import dev.willyelton.crystal.core.common.capability.Levelable;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import static dev.willyelton.crystal.core.utils.ScreenUtils.openScreen;

@EventBusSubscriber(modid = ApiConstants.CORE_MOD_ID, value = Dist.CLIENT)
public class KeyPressEvent {
    @SubscribeEvent
    public static void handleEventInput(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        ClientLevel level = mc.level;

        if (mc.player == null || level == null) {
            return;
        }

        if (RegisterKeyBindingsEvent.UPGRADE_MENU.consumeClick()) {
            ItemStack mainHandItem = mc.player.getItemInHand(InteractionHand.MAIN_HAND);

            if (!handleUpgradeMenu(mainHandItem, mc.player)) {
                ItemStack offHandItem = mc.player.getItemInHand(InteractionHand.OFF_HAND);
                if (!handleUpgradeMenu(offHandItem, mc.player) && !mainHandItem.isEmpty()) {
                    mc.player.sendOverlayMessage (Component.literal("No Skill Tree Defined for " + mainHandItem.getItemName().getString()).withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    public static boolean handleUpgradeMenu(ItemStack stack, Player player) {
        if (stack.isEmpty()) {
            return false;
        }

        Level level = player.level();

        Levelable levelable = stack.getCapability(Capabilities.ITEM_SKILL, level.registryAccess());

        if (levelable != null) {
            openScreen(new UpgradeScreen(stack, player, levelable));
            return true;
        }

        return false;
    }
}
