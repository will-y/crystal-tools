package dev.willyelton.crystal_tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import dev.willyelton.crystal_tools.utils.Colors;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuarryLaserRenderer {
    public static final float LASER_SPEED_MODIFIER = 1.2F;
    private static final Map<Pair<BlockPos, BlockPos>, LaserRendererProperties> LINE_RENDERERS = new HashMap<>();
    private static final Map<BlockPos, LaserRendererProperties> CUBE_RENDERERS = new HashMap<>();

    public static void startTemporaryLaser(long startTime, long endTime, BlockPos pos1, BlockPos pos2, int color) {
        LINE_RENDERERS.put(new Pair<>(pos1, pos2), new LaserRendererProperties(startTime, endTime, color));
    }

    public static void startTemporaryCube(long startTime, long endTime, BlockPos pos, int color) {
        CUBE_RENDERERS.put(pos, new LaserRendererProperties(startTime, endTime, color));
    }

    public static void clearTemporaryLasers() {
        LINE_RENDERERS.clear();
        CUBE_RENDERERS.clear();
    }

    public static void render(RenderLevelStageEvent event) {
        if (LINE_RENDERERS.isEmpty() && CUBE_RENDERERS.isEmpty()) return;

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        Level level = player.level();
        long gameTime = level.getGameTime();

        List<Pair<BlockPos, BlockPos>> lasersToRemove = new ArrayList<>();
        List<BlockPos> cubesToRemove = new ArrayList<>();

        for (Pair<BlockPos, BlockPos> pair : LINE_RENDERERS.keySet()) {
            LaserRendererProperties properties = LINE_RENDERERS.get(pair);
            int timeLeft = (int) (properties.endTime - gameTime);
            if (timeLeft <= 0) {
                lasersToRemove.add(pair);
            }
            int timeElapsed = (int) (gameTime - properties.startTime);

            if (timeElapsed >= 0) {
                renderLaser(event, level, pair.getFirst(), pair.getSecond(), timeElapsed, timeLeft, (int) (properties.endTime - properties.startTime), properties.color);
            }
        }

        for (BlockPos pos : CUBE_RENDERERS.keySet()) {
            LaserRendererProperties properties = CUBE_RENDERERS.get(pos);
            int timeLeft = (int) (properties.endTime - gameTime);
            if (timeLeft <= 0) {
                cubesToRemove.add(pos);
            }

            int timeElapsed = (int) (gameTime - properties.startTime);
            if (timeElapsed >= 0) {
                BlockOverlayRenderer.renderBlockPos(event.getPoseStack(), Minecraft.getInstance().renderBuffers().bufferSource(), pos, properties.color());
            }
        }

        lasersToRemove.forEach(LINE_RENDERERS::remove);
        cubesToRemove.forEach(CUBE_RENDERERS::remove);
    }

    public static void renderLaser(MultiBufferSource bufferSource, PoseStack poseStack, Camera camera, float partialTick, Level level, BlockPos pos1, BlockPos pos2, int colorIn) {
        renderLaser(bufferSource, poseStack, camera, partialTick, level, pos1, pos2, -1, -1, -1, colorIn);
    }

    public static void renderLaser(MultiBufferSource bufferSource, PoseStack poseStack, Camera camera, float partialTick, Level level, float pos1X, float pos1Y, float pos1Z, BlockPos pos2, int colorIn) {
        renderLaser(bufferSource, poseStack, camera, partialTick, level, pos1X, pos1Y, pos1Z, pos2.getX(), pos2.getY(), pos2.getZ(), -1, -1, -1, colorIn);
    }

    public static void renderLaser(MultiBufferSource bufferSource, PoseStack poseStack, Camera camera, float partialTick, Level level, BlockPos pos1, float pos2X, float pos2Y, float pos2Z, int colorIn) {
        renderLaser(bufferSource, poseStack, camera, partialTick, level, pos1.getX(), pos1.getY(), pos1.getZ(), pos2X, pos2Y, pos2Z, -1, -1, -1, colorIn);
    }

    public static void renderLaser(MultiBufferSource bufferSource, PoseStack poseStack, Camera camera, float partialTick, Level level, BlockPos pos1, BlockPos pos2, int timeElapsed, int timeLeft, int duration, int colorIn) {
        renderLaser(bufferSource, poseStack, camera, partialTick, level, pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ(), timeElapsed, timeLeft, duration, colorIn);
    }

    public static void renderLaser(MultiBufferSource bufferSource, PoseStack poseStack, Camera camera, float partialTick, Level level, float pos1X, float pos1Y, float pos1Z, float pos2X, float pos2Y, float pos2Z, int timeElapsed, int timeLeft, int duration, int colorIn) {
        long gameTime = level.getGameTime();
        Vector3f pos1Vector = new Vector3f(pos1X, pos1Y, pos1Z);
        Vector3f pos2Vector = new Vector3f(pos2X, pos2Y, pos2Z);
        float height = pos1Vector.distance(pos2Vector);
        int color;
        if (duration > 0) {
            color = Colors.addAlpha(colorIn, Math.max(10, Mth.lerpDiscrete(timeLeft / (float) duration, 0, 255)));
        } else {
            color = colorIn;
        }

        float beamRadius = 0.05f;
        float glowRadius = 0.06f;
        float yMax;

        if (timeElapsed >= 0) {
            yMax = Math.min(height, timeElapsed / LASER_SPEED_MODIFIER);
        } else {
            yMax = height;
        }

        Vec3 view = camera.getPosition();
        poseStack.pushPose();
        poseStack.translate(-view.x, -view.y, -view.z);
        poseStack.translate(pos1X, pos1Y, pos1Z);
        poseStack.translate(0.5, 0.5, 0.5);
        Vector3f verticalNormalVector = new Vector3f(0, 1, 0);
        pos2Vector.sub(pos1Vector).normalize();
        Vector3f half = new Vector3f(verticalNormalVector).add(pos2Vector).normalize();
        Vector3f cross =  new Vector3f(verticalNormalVector).cross(half);
        Quaternionf quaternionf = new Quaternionf(cross.x, cross.y, cross.z, verticalNormalVector.dot(half)).normalize();

        poseStack.rotateAround(quaternionf, 0, 0, 0);

        // TODO: What are these
        float f = (float) Math.floorMod(gameTime, 40) + partialTick;
        float f1 = height < 0 ? f : -f;
        float f2 = Mth.frac(f1 * 0.2F - (float) Mth.floor(f1 * 0.1F));
        poseStack.pushPose();
//        poseStack.mulPose(Axis.YP.rotationDegrees(f * 2.25F - 45.0F));

        float x1;
        float z2;
        float x3 = -beamRadius;
        float z4 = -beamRadius;
        float maxV = -1.0F + f2;
        float minV = height * (0.5F / beamRadius) + maxV;

        renderPart(poseStack, bufferSource.getBuffer(CrystalToolsRenderTypes.QUARRY_LASER), color, 0, yMax, 0.0F,
                beamRadius, beamRadius, 0.0F, x3, 0.0F, 0.0F, z4, 0.0F, 1.0F, minV, maxV);
        poseStack.popPose();
        x1 = -glowRadius;
        float f4 = -glowRadius;
        z2 = -glowRadius;
        x3 = -glowRadius;
        maxV = -1.0F + f2;
        minV = height + maxV;
        renderPart(poseStack, bufferSource.getBuffer(CrystalToolsRenderTypes.QUARRY_LASER), FastColor.ARGB32.color(54, color),
                0, yMax, x1, f4, glowRadius, z2, x3, glowRadius, glowRadius, glowRadius, 0.0F, 1.0F, minV, maxV);
        poseStack.popPose();
    }

    private static void renderLaser(RenderLevelStageEvent event, Level level, BlockPos pos1, BlockPos pos2, int timeElapsed, int timeLeft, int duration, int colorIn) {
        renderLaser(Minecraft.getInstance().renderBuffers().bufferSource(), event.getPoseStack(), event.getCamera(), event.getPartialTick().getGameTimeDeltaTicks(), level, pos1, pos2, timeElapsed, timeLeft, duration, colorIn);
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

    private record LaserRendererProperties(long startTime, long endTime, int color) {}
}
