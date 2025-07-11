package dev.willyelton.crystal_tools.client.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.config.CrystalToolsClientConfig;
import dev.willyelton.crystal_tools.client.renderer.BlockOverlayRenderer;
import dev.willyelton.crystal_tools.client.renderer.QuarryLaserRenderer;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.tool.LevelableTool;
import dev.willyelton.crystal_tools.common.levelable.tool.VeinMinerLevelableTool;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT)
public class RenderEvents {
    @SubscribeEvent
    public static void handleRenderLevelStageEvent(RenderLevelStageEvent.AfterWeather event) {
        QuarryLaserRenderer.render(event);
    }

    @SubscribeEvent
    public static void handleRenderLevelStageEvent(RenderLevelStageEvent.AfterTranslucentBlocks event) {
        if (CrystalToolsClientConfig.DISABLE_BLOCK_TARGET_RENDERING.get()) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        if (player != null && !player.isCreative() && !player.isSpectator()) {
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

            if (RegisterKeyBindingsEvent.VEIN_MINE.isDown()
                    && stack.getItem() instanceof VeinMinerLevelableTool veinMinerLevelableTool
                    && stack.getOrDefault(DataComponents.VEIN_MINER, 0) > 0) {
                BlockOverlayRenderer.renderVeinMiner(event, veinMinerLevelableTool, stack);
            } else if (stack.getItem() instanceof LevelableTool toolItem
                    && stack.getOrDefault(DataComponents.HAS_3x3, false)
                    && !stack.getOrDefault(DataComponents.DISABLE_3x3, false)) {
                BlockOverlayRenderer.render3x3(event, toolItem, stack);
            }
        }
    }

    @SubscribeEvent
    public static void handleHighlightEvent(RenderHighlightEvent.Block event) {
        if (CrystalToolsClientConfig.DISABLE_BLOCK_TARGET_RENDERING.get()) return;

        Player player = Minecraft.getInstance().player;

        if (player == null || player.isCreative() || player.isSpectator()) return;

        if (event.getTarget() instanceof BlockHitResult blockHitResult) {
            BlockState state = player.level().getBlockState(blockHitResult.getBlockPos());
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

            if (stack.getItem() instanceof LevelableTool toolItem
                    && stack.getOrDefault(DataComponents.HAS_3x3, false)
                    && !stack.getOrDefault(DataComponents.DISABLE_3x3, false)) {
                if (toolItem.isCorrectToolForDrops(stack, state)) {
                    event.setCanceled(true);
                }
            }
        }
    }
}
