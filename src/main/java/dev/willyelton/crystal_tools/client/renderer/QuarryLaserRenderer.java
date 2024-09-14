package dev.willyelton.crystal_tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.willyelton.crystal_tools.utils.Colors;
import dev.willyelton.crystal_tools.utils.ReversiblePair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuarryLaserRenderer {
    public static Map<ReversiblePair<BlockPos>, Integer> LINE_RENDERERS = new HashMap<>();

    public static void startTemporaryLaser(int tickDuration, BlockPos pos1, BlockPos pos2) {
        LINE_RENDERERS.put(new ReversiblePair<>(pos1, pos2), tickDuration);
    }

    public static void render(RenderLevelStageEvent event) {
        List<ReversiblePair<BlockPos>> toRemove = new ArrayList<>();

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        Level level = player.level();

        for (ReversiblePair<BlockPos> pair : LINE_RENDERERS.keySet()) {
//            if (LINE_RENDERERS.get(pair) <= 0) {
//                toRemove.add(pair);
//            }
//
//            LINE_RENDERERS.put(pair, LINE_RENDERERS.get(pair) - 1);

            renderLaser2(event, player, level, pair.first(), pair.second());
        }

        toRemove.forEach(pair -> LINE_RENDERERS.remove(pair));
    }

    private static void renderLaser(RenderLevelStageEvent event, Player player, Level level, BlockPos pos1, BlockPos pos2) {
        long gameTime = level.getGameTime();

        Vec3 view = event.getCamera().getPosition();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        PoseStack matrix = event.getPoseStack();
        VertexConsumer vertexConsumer = buffer.getBuffer(CrystalToolsRenderTypes.QUARRY_LASER);

        matrix.pushPose();

        // Translate from camera space to space of the start pos
        matrix.translate(-view.x, -view.y, -view.z);
        matrix.translate(pos1.getX(), pos1.getY(), pos1.getZ());

        PoseStack.Pose last = matrix.last();
        Matrix3f matrixNormal = last.normal();
        Vector3f normalVector = new Vector3f(0.0F, 0.0F, 0.0F);
        normalVector.mul(matrixNormal);
        Matrix4f positionMatrix = last.pose();

        Vector4f vec1 = new Vector4f(0.5F, 0, 0, 1.0F);
        vec1.mul(positionMatrix);
        Vector4f vec2 = new Vector4f(-0.5F, 10, 10, 1.0F);
        vec2.mul(positionMatrix);
        Vector4f vec3 = new Vector4f(-0.5F, 10, 10, 1.0F);
        vec3.mul(positionMatrix);
        Vector4f vec4 = new Vector4f(0.5F, 0, 0, 1.0F);
        vec4.mul(positionMatrix);

        vertexConsumer.addVertex(vec1.x, vec1.y, vec1.z).setColor(0, 255, 255, 1).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(normalVector.x, normalVector.y, normalVector.z);
        vertexConsumer.addVertex(vec2.x, vec2.y, vec2.z).setColor(0, 255, 255, 1).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(normalVector.x, normalVector.y, normalVector.z);
        vertexConsumer.addVertex(vec3.x, vec3.y, vec3.z).setColor(0, 255, 255, 1).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(normalVector.x, normalVector.y, normalVector.z);
        vertexConsumer.addVertex(vec4.x, vec4.y, vec4.z).setColor(0, 255, 255, 1).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(normalVector.x, normalVector.y, normalVector.z);

//        vertexConsumer.addVertex(0.5F, 0.4F, 0.2F).setColor(0, 255, 255, 1).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(normalVector.x, normalVector.y, normalVector.z);
//        vertexConsumer.addVertex(0, 0.2F, pos2.getZ() - pos1.getZ()).setColor(0, 255, 255, 1).setUv(1, 0.5F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(normalVector.x, normalVector.y, normalVector.z);
//        vertexConsumer.addVertex(0, -0.2F, pos2.getZ() - pos1.getZ()).setColor(0, 255, 255, 1).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(normalVector.x, normalVector.y, normalVector.z);
//        vertexConsumer.addVertex(0.5F, 0, 0.2F).setColor(0, 255, 255, 1).setUv(0, 0.5F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(normalVector.x, normalVector.y, normalVector.z);

        matrix.popPose();
        buffer.endBatch();
    }

    private static void renderLaser2(RenderLevelStageEvent event, Player player, Level level, BlockPos pos1, BlockPos pos2) {
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        float partialTick = event.getPartialTick().getGameTimeDeltaTicks();
        long gameTime = level.getGameTime();
        int yOffset = 0;
        float height = (float) Math.sqrt(pos1.distSqr(pos2));
        int color = Colors.fromRGB(255, 0, 0);
        float beamRadius = 0.1f;
        float glowRadius = 0.1f;
        float yMax = yOffset + height;
        Vec3 view = event.getCamera().getPosition();
        poseStack.pushPose();
        poseStack.translate(-view.x, -view.y, -view.z);
        poseStack.translate(pos1.getX(), pos1.getY(), pos1.getZ());
        poseStack.translate(0.5, 0.5, 0.5);
        Vector3f verticalNormalVector = new Vector3f(0, 1, 0);
        Vector3f pos1Vector = new Vector3f(pos1.getX(), pos1.getY(), pos1.getZ());
        Vector3f pos2Vector = new Vector3f(pos2.getX(), pos2.getY(), pos2.getZ()).sub(pos1Vector).normalize();
        Vector3f cross = verticalNormalVector.cross(pos2Vector);
        float w = (float) Math.sqrt(Math.pow(verticalNormalVector.length(), 2) * Math.pow(pos2Vector.length(), 2)) + verticalNormalVector.dot(pos2Vector);

        Quaternionf quaternionf = new Quaternionf(cross.x, cross.y, cross.z, w).normalize();

        poseStack.rotateAround(quaternionf, 0, 0, 0);

        // TODO: What are these
        float f = (float) Math.floorMod(gameTime, 40) + partialTick;
        float f1 = height < 0 ? f : -f;
        float f2 = Mth.frac(f1 * 0.2F - (float) Mth.floor(f1 * 0.1F));
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(f * 2.25F - 45.0F));

        float f3;
        float f5;
        float f6 = -beamRadius;
        float f9 = -beamRadius;
        float f12 = -1.0F + f2;
        float f13 = height * (0.5F / beamRadius) + f12;

        // Start render part
        renderPart(
                poseStack,
                bufferSource.getBuffer(CrystalToolsRenderTypes.QUARRY_LASER),
                color,
                yOffset,
                yMax,
                0.0F,
                beamRadius,
                beamRadius,
                0.0F,
                f6,
                0.0F,
                0.0F,
                f9,
                0.0F,
                1.0F,
                f13,
                f12
        );
        // End render part
        poseStack.popPose();
        f3 = -glowRadius;
        float f4 = -glowRadius;
        f5 = -glowRadius;
        f6 = -glowRadius;
        f12 = -1.0F + f2;
        f13 = height + f12;
        renderPart(
                poseStack,
                bufferSource.getBuffer(CrystalToolsRenderTypes.QUARRY_LASER),
                FastColor.ARGB32.color(32, color),
                yOffset,
                yMax,
                f3,
                f4,
                glowRadius,
                f5,
                f6,
                glowRadius,
                glowRadius,
                glowRadius,
                0.0F,
                1.0F,
                f13,
                f12
        );
        poseStack.popPose();
    }

    private static void renderPart(PoseStack poseStack, VertexConsumer consumer, int color, int minY, float maxY, float x1,
                                   float z1, float x2, float z2, float x3, float z3, float x4, float z4, float minU,
                                   float maxU, float minV, float maxV) {
        PoseStack.Pose posestack$pose = poseStack.last();
        renderQuad(posestack$pose, consumer, color, minY, maxY, x1, z1, x2, z2, minU, maxU, minV, maxV);
        renderQuad(posestack$pose, consumer, color, minY, maxY, x4, z4, x3, z3, minU, maxU, minV, maxV);
        renderQuad(posestack$pose, consumer, color, minY, maxY, x2, z2, x4, z4, minU, maxU, minV, maxV);
        renderQuad(posestack$pose, consumer, color, minY, maxY, x3, z3, x1, z1, minU, maxU, minV, maxV);
    }

    private static void renderQuad(PoseStack.Pose pose, VertexConsumer consumer, int color, int minY, float maxY, float minX,
                                   float minZ, float maxX, float maxZ, float minU, float maxU, float minV, float maxV) {
        addVertex(pose, consumer, color, maxY, minX, minZ, maxU, minV);
        addVertex(pose, consumer, color, minY, minX, minZ, maxU, maxV);
        addVertex(pose, consumer, color, minY, maxX, maxZ, minU, maxV);
        addVertex(pose, consumer, color, maxY, maxX, maxZ, minU, minV);
    }

    private static void addVertex(PoseStack.Pose pose, VertexConsumer consumer, int color, float y, float x, float z, float u, float v) {
        consumer.addVertex(pose, x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
