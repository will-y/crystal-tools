package dev.willyelton.crystal.tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.willyelton.crystal.tools.ModRegistration;
import dev.willyelton.crystal.tools.common.components.DataComponents;
import dev.willyelton.crystal.tools.common.entity.BlockPosDirection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.CustomFeatureRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.CommonColors;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import net.neoforged.neoforge.client.submit.RenderPhaseKeys;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.willyelton.crystal.tools.CrystalTools.ck;

public class GolemCommandRodRenderer {
    private static final ContextKey<Map<String, List<BlockPosDirection>>> COMMAND_ROD_CONTEXT_KEY = ck("command_rod");
    private static final String SOURCE = "SOURCE";
    private static final String DESTINATION = "DESTINATION";

    public static void extractRenderState(ExtractLevelRenderStateEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player != null) {
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (stack.is(ModRegistration.CRYSTAL_GOLEM_COMMAND_ROD)) {
                extractFromStack(event, stack);
            } else {
                ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
                if (offHand.is(ModRegistration.CRYSTAL_GOLEM_COMMAND_ROD)) {
                    extractFromStack(event, offHand);
                }
            }
        }
    }

    private static void extractFromStack(ExtractLevelRenderStateEvent event, ItemStack stack) {
        List<BlockPosDirection> SOURCE_BLOCKS = stack.getOrDefault(DataComponents.GOLEM_COMMAND_ROD_SOURCE_POSITIONS, List.of());
        List<BlockPosDirection> DESTINATION_BLOCKS = stack.getOrDefault(DataComponents.GOLEM_COMMAND_ROD_DESTINATION_POSITIONS, List.of());

        Map<String, List<BlockPosDirection>> resultMap = new HashMap<>();
        resultMap.put(SOURCE, SOURCE_BLOCKS);
        resultMap.put(DESTINATION, DESTINATION_BLOCKS);

        event.getRenderState().setRenderData(COMMAND_ROD_CONTEXT_KEY, resultMap);
    }

    public static void submit(SubmitCustomGeometryEvent event) {
        PoseStack poseStack = event.getPoseStack();
        Map<String, List<BlockPosDirection>> positions = event.getLevelRenderState().getRenderData(COMMAND_ROD_CONTEXT_KEY);
        if (positions != null) {
            Vec3 view = event.getLevelRenderState().cameraRenderState.pos;
            poseStack.pushPose();
            poseStack.translate(-view.x, -view.y, -view.z);

            List<BlockPosDirection> sourcePositions = positions.get(SOURCE);
            if (sourcePositions != null) {
                for (BlockPosDirection blockPosDirection : sourcePositions) {
                    submitBlockPosFace(event, poseStack, blockPosDirection.pos(), blockPosDirection.direction(), CommonColors.BLUE);
                }
            }

            List<BlockPosDirection> destinationPositions = positions.get(DESTINATION);
            if (destinationPositions != null) {
                for (BlockPosDirection blockPosDirection : destinationPositions) {
                    submitBlockPosFace(event, poseStack, blockPosDirection.pos(), blockPosDirection.direction(), 0xFF8C00);
                }
            }

            poseStack.popPose();
        }
    }

    private static void submitBlockPosFace(SubmitCustomGeometryEvent event, PoseStack poseStack, BlockPos pos, Direction direction, int color) {
        poseStack.pushPose();
        poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
        poseStack.translate(-0.0005f, -0.0005f, -0.0005f);
        poseStack.scale(1.001f, 1.001f, 1.001f);

        poseStack.rotateAround(direction.getRotation(), 0.5F, 0.5F, 0.5F);
        event.getSubmitNodeCollector().submitSpecial(RenderPhaseKeys.ALWAYS_ON_TOP, new CustomFeatureRenderer.Submit(
                poseStack.last().copy(), CrystalToolsRenderTypes.BLOCK_OVERLAY, new SideRenderer(color)));
        poseStack.popPose();
    }

    private record SideRenderer(int color) implements SubmitNodeCollector.CustomGeometryRenderer {
        @Override
        public void render(PoseStack.Pose pose, VertexConsumer vertexConsumer) {
            Matrix4f matrix = pose.pose();
            float alpha = 0.5F;
            float red = ((color >> 16) & 0xFF) / 255.0F;
            float green = ((color >> 8) & 0xFF) / 255.0F;
            float blue = (color & 0xFF) / 255.0F;
            vertexConsumer.addVertex(matrix, 0, 1, 0).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix, 0, 1, 1).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix, 1, 1, 1).setColor(red, green, blue, alpha);
            vertexConsumer.addVertex(matrix, 1, 1, 0).setColor(red, green, blue, alpha);
        }
    }
}
