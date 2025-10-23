package dev.willyelton.crystal_tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import dev.willyelton.crystal_tools.utils.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.willyelton.crystal_tools.CrystalTools.ck;

public class QuarryLaserRenderer {
    public static final float LASER_SPEED_MODIFIER = 1.2F;

    private static final Map<Pair<BlockPos, BlockPos>, LaserRendererProperties> LINE_RENDERERS = new HashMap<>();
    private static final Map<BlockPos, LaserRendererProperties> CUBE_RENDERERS = new HashMap<>();
    private static final ContextKey<List<QuarryLaserRenderState>> CONTEXT_KEY = ck("quarry_laser");

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

    public static void extractRenderState(ExtractLevelRenderStateEvent event) {
        List<QuarryLaserRenderState> laserRenderStates = new ArrayList<>();

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
                QuarryLaserRenderState renderState = extractQuarryLaserRenderState(pair.getFirst(), pair.getSecond(),
                        event.getCamera().getPosition(), event.getLevel(), event.getCamera().getPartialTickTime(), properties.color());

                renderState.timeLeft = timeLeft;
                renderState.timeElapsed = timeElapsed;
                long duration = properties.endTime - properties.startTime;
                if (duration > 0) {
                    renderState.color = Colors.addAlpha(renderState.color, Math.max(10, Mth.lerpDiscrete(renderState.timeLeft / (float) duration, 0, 255)));
                } else {
                    renderState.color = properties.color();
                }
                laserRenderStates.add(renderState);
            }
        }

        lasersToRemove.forEach(LINE_RENDERERS::remove);
        cubesToRemove.forEach(CUBE_RENDERERS::remove);

        event.getRenderState().setRenderData(CONTEXT_KEY, laserRenderStates);
    }

    public static QuarryLaserRenderState extractQuarryLaserRenderState(BlockPos start, BlockPos end, Vec3 cameraPos, Level level, float partialTick, int color) {
        return extractQuarryLaserRenderState(start.getCenter(), end.getCenter(), cameraPos, level, partialTick, color);
    }

    public static QuarryLaserRenderState extractQuarryLaserRenderState(Vec3 start, Vec3 end, Vec3 cameraPos, Level level, float partialTick, int color) {
        QuarryLaserRenderState renderState = new QuarryLaserRenderState();
        Vector3f startPos = new Vector3f((float) start.x(), (float) start.y(), (float) start.z());
        Vector3f endPos = new Vector3f((float) end.x(), (float) end.y(), (float) end.z());

        renderState.partialTick = partialTick;
        renderState.gameTime = level.getGameTime();
        renderState.color = color;

        renderState.x = -cameraPos.x + startPos.x;
        renderState.y = -cameraPos.y + startPos.y;
        renderState.z = -cameraPos.z + startPos.z;

        renderState.height = startPos.distance(endPos);
        if (renderState.timeElapsed >= 0) {
            renderState.yMax = Math.min(renderState.height, renderState.timeElapsed / LASER_SPEED_MODIFIER);
        } else {
            renderState.yMax = renderState.height;
        }

        Vector3f verticalNormalVector = new Vector3f(0, 1, 0);
        // Is it bad to mutate render state (probably)
        endPos.sub(startPos).normalize();
        Vector3f half = new Vector3f(verticalNormalVector).add(endPos).normalize();
        Vector3f cross =  new Vector3f(verticalNormalVector).cross(half);
        renderState.rotation = new Quaternionf(cross.x, cross.y, cross.z, verticalNormalVector.dot(half)).normalize();

        return renderState;
    }

    public static void render(RenderLevelStageEvent event) {
        List<QuarryLaserRenderState> laserRenderStates = event.getLevelRenderState().getRenderData(CONTEXT_KEY);

        if (laserRenderStates == null) return;

        for (QuarryLaserRenderState renderState : laserRenderStates) {
            renderLaser(event.getPoseStack(), renderState);
        }
    }

    public static void renderLaser(PoseStack poseStack, QuarryLaserRenderState renderState) {
        VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(CrystalToolsRenderTypes.QUARRY_LASER);
        renderLaser(poseStack, consumer, renderState);
    }

    public static void renderLaser(PoseStack poseStack, VertexConsumer consumer, QuarryLaserRenderState renderState) {
        if (renderState == null) return;
        float beamRadius = 0.05f;
        float glowRadius = 0.06f;

        poseStack.pushPose();
        poseStack.translate(renderState.x, renderState.y, renderState.z);

        poseStack.rotateAround(renderState.rotation, 0, 0, 0);

        float f = (float) Math.floorMod(renderState.gameTime, 40) + renderState.partialTick;
        float f1 = renderState.height < 0 ? f : -f;
        float f2 = Mth.frac(f1 * 0.2F - (float) Mth.floor(f1 * 0.1F));
        poseStack.pushPose();

        float x1;
        float z2;
        float x3 = -beamRadius;
        float z4 = -beamRadius;
        float maxV = -1.0F + f2;
        float minV = renderState.height * (0.5F / beamRadius) + maxV;

        renderPart(poseStack, consumer, renderState.color, 0, renderState.yMax, 0.0F,
                beamRadius, beamRadius, 0.0F, x3, 0.0F, 0.0F, z4, 0.0F, 1.0F, minV, maxV);
        poseStack.popPose();
        x1 = -glowRadius;
        float f4 = -glowRadius;
        z2 = -glowRadius;
        x3 = -glowRadius;
        maxV = -1.0F + f2;
        minV = renderState.height + maxV;
        renderPart(poseStack, consumer, ARGB.color(54, renderState.color),
                0, renderState.yMax, x1, f4, glowRadius, z2, x3, glowRadius, glowRadius, glowRadius, 0.0F, 1.0F, minV, maxV);
        poseStack.popPose();
    }

    private static void renderPart(PoseStack poseStack, VertexConsumer consumer, int color, int minY, float maxY, float x1,
                                   float z1, float x2, float z2, float x3, float z3, float x4, float z4, float minU,
                                   float maxU, float minV, float maxV) {
        var pose = poseStack.last();
        renderQuad(pose, consumer, color, minY, maxY, x1, z1, x2, z2, minU, maxU, minV, maxV);
        renderQuad(pose, consumer, color, minY, maxY, x4, z4, x3, z3, minU, maxU, minV, maxV);
        renderQuad(pose, consumer, color, minY, maxY, x2, z2, x4, z4, minU, maxU, minV, maxV);
        renderQuad(pose, consumer, color, minY, maxY, x3, z3, x1, z1, minU, maxU, minV, maxV);
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

    public record LaserRendererProperties(long startTime, long endTime, int color) {}

    public static class QuarryLaserRenderState extends BlockEntityRenderState {
        public float partialTick;
        public long gameTime;
        public long timeLeft;
        public long timeElapsed = -1;
        public int color;
        public float height;
        public float yMax;
        public Quaternionf rotation;
        public double x;
        public double y;
        public double z;
    }
}
