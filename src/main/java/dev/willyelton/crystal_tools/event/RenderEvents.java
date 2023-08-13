package dev.willyelton.crystal_tools.event;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.levelable.tool.DiggerLevelableTool;
import dev.willyelton.crystal_tools.renderer.BlockOverlayRenderer;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT)
public class RenderEvents {
    @SubscribeEvent
    public static void handleRenderLevelStageEvent(RenderLevelStageEvent event) {
        if (event.getStage() != net.minecraftforge.client.event.RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);

            if (itemStack.getItem() instanceof DiggerLevelableTool && NBTUtils.getFloatOrAddKey(itemStack, "3x3") > 0 && !NBTUtils.getBoolean(itemStack, "disable_3x3")) {
                BlockOverlayRenderer.render(event);
            }
        }
    }

    @SubscribeEvent
    public static void handleHighlightEvent(RenderHighlightEvent event) {
        Player player = Minecraft.getInstance().player;

        if (player == null) {
            return;
        }

        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

        if (stack.getItem() instanceof DiggerLevelableTool && NBTUtils.getFloatOrAddKey(stack, "3x3") > 0 && !NBTUtils.getBoolean(stack, "disable_3x3")) {
            event.setCanceled(true);
        }
    }
}
