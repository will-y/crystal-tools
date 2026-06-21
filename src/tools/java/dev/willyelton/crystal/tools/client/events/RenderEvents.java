package dev.willyelton.crystal.tools.client.events;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.willyelton.crystal.tools.CrystalTools;
import dev.willyelton.crystal.tools.client.config.CrystalToolsClientConfig;
import dev.willyelton.crystal.tools.client.renderer.BlockOverlayRenderer;
import dev.willyelton.crystal.tools.client.renderer.QuarryLaserRenderer;
import dev.willyelton.crystal.tools.common.components.DataComponents;
import dev.willyelton.crystal.tools.common.levelable.tool.LevelableTool;
import dev.willyelton.crystal.tools.common.levelable.tool.VeinMinerLevelableTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.CustomBlockOutlineRenderer;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;

@EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT)
public class RenderEvents {

    @SubscribeEvent
    public static void handleExtractLevelRenderStateEvent(ExtractLevelRenderStateEvent event) {
        QuarryLaserRenderer.extractRenderState(event);
        if (!CrystalToolsClientConfig.DISABLE_BLOCK_TARGET_RENDERING.get()) {
            BlockOverlayRenderer.extractRenderState(event);
        }
    }

    @SubscribeEvent
    public static void handleSubmitGeometryEvent(SubmitCustomGeometryEvent event) {
        BlockOverlayRenderer.submit(event);
        QuarryLaserRenderer.submit(event);
    }

    @SubscribeEvent
    public static void handleHighlightEvent(ExtractBlockOutlineRenderStateEvent event) {
        if (CrystalToolsClientConfig.DISABLE_BLOCK_TARGET_RENDERING.get()) return;

        Player player = Minecraft.getInstance().player;

        if (player == null || player.isCreative() || player.isSpectator()) return;

        BlockState state = event.getBlockState();
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

        if (stack.getItem() instanceof LevelableTool toolItem
                && stack.getOrDefault(DataComponents.HAS_3x3, false)
                && !stack.getOrDefault(DataComponents.DISABLE_3x3, false)) {
            if (toolItem.isCorrectToolForDrops(stack, state)) {
                event.addCustomRenderer(new BlockVanillaBlockOutline());
            }
        }

    }

    static class BlockVanillaBlockOutline implements CustomBlockOutlineRenderer {
        @Override
        public boolean render(BlockOutlineRenderState renderState, SubmitNodeCollector submitNodeCollector, PoseStack poseStack, LevelRenderState levelRenderState) {
            return true;
        }
    }
}
