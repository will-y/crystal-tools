package dev.willyelton.crystal_tools.client.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.gui.ModGUIs;
import dev.willyelton.crystal_tools.client.gui.UpgradeScreen;
import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.network.data.ModeSwitchPayload;
import dev.willyelton.crystal_tools.common.network.data.OpenBackpackPayload;
import dev.willyelton.crystal_tools.common.network.data.TriggerRocketPayload;
import dev.willyelton.crystal_tools.common.network.data.VeinMiningPayload;
import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

@EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT)
public class KeyPressEvent {
    @SubscribeEvent
    public static void handleEventInput(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;

        if (mc.player == null || level == null)
            return;

        if (RegisterKeyBindingsEvent.UPGRADE_MENU.consumeClick()) {
            ItemStack mainHandItem = mc.player.getItemInHand(InteractionHand.MAIN_HAND);

            if (!handleUpgradeMenu(mainHandItem, mc.player)) {
                ItemStack offHandItem = mc.player.getItemInHand(InteractionHand.OFF_HAND);
                if (!handleUpgradeMenu(offHandItem, mc.player) && !mainHandItem.isEmpty()) {
                    mc.player.displayClientMessage(Component.literal("No Skill Tree Defined for " + mainHandItem.getItemName().getString()).withStyle(ChatFormatting.RED), true);
                }
            }
        }

        if (RegisterKeyBindingsEvent.MODE_SWITCH.consumeClick()) {
            handleModeSwitch();
        }

        if (RegisterKeyBindingsEvent.OPEN_BACKPACK.consumeClick()) {
            handleOpenBackpack();
        }

        if (RegisterKeyBindingsEvent.TRIGGER_ROCKET.consumeClick()) {
            handleTriggerRocket();
        }

        // Send vein mining state every 5 ticks
        if (level.getGameTime() % 5 == 0) {
            PacketDistributor.sendToServer(new VeinMiningPayload(RegisterKeyBindingsEvent.VEIN_MINE.isDown()));
        }

    }

    public static boolean handleUpgradeMenu(ItemStack stack, Player player) {
        if (stack.isEmpty()) {
            return false;
        }

        Level level = player.level();
        Optional<Registry<SkillData>> skillDataOptional = level.registryAccess().lookup(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY);
        Optional<ResourceKey<Item>> itemKeyOptional = level.registryAccess().lookupOrThrow(Registries.ITEM).getResourceKey(stack.getItem());
        if (skillDataOptional.isPresent() && itemKeyOptional.isPresent()) {
            Registry<SkillData> skillData = skillDataOptional.get();
            ResourceKey<Item> itemKey = itemKeyOptional.get();

            Optional<Holder.Reference<SkillData>> dataOptional = skillData.get(itemKey.location());

            if (dataOptional.isPresent()) {
                SkillData data = dataOptional.get().value();
                ModGUIs.openScreen(new UpgradeScreen(stack, player, data));
                return true;
            }
        }

        return false;
    }

    /**
     * Handles changing the mining mode (silk touch or fortune)
     */
    public static void handleModeSwitch() {
        PacketDistributor.sendToServer(new ModeSwitchPayload(Screen.hasShiftDown(), Screen.hasControlDown(), Screen.hasAltDown()));
    }

    public static void handleOpenBackpack() {
        PacketDistributor.sendToServer(new OpenBackpackPayload(-1));
    }

    public static void handleTriggerRocket() {
        PacketDistributor.sendToServer(new TriggerRocketPayload());
    }
}
