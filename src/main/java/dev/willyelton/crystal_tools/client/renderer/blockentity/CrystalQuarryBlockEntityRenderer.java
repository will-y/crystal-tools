package dev.willyelton.crystal_tools.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.willyelton.crystal_tools.client.renderer.CrystalToolsRenderTypes;
import dev.willyelton.crystal_tools.client.renderer.QuarryCubeModel;
import dev.willyelton.crystal_tools.client.renderer.QuarryLaserRenderer;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalQuarryBlockEntity;
import dev.willyelton.crystal_tools.utils.Colors;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

public class CrystalQuarryBlockEntityRenderer implements BlockEntityRenderer<CrystalQuarryBlockEntity, CrystalQuarryBlockEntityRenderer.QuarryRenderState> {
    private static final int FRAME_COLOR = Colors.fromRGB(48, 231, 237, 255);
    private static final int MINING_COLOR = Colors.fromRGB(0, 144, 180, 255);

    private final QuarryCubeModel model;

    public CrystalQuarryBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new QuarryCubeModel(context.bakeLayer(QuarryCubeModel.LOCATION));
    }

    @Override
    public QuarryRenderState createRenderState() {
        return new QuarryRenderState();
    }

    @Override
    public void extractRenderState(CrystalQuarryBlockEntity blockEntity, QuarryRenderState renderState, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, overlay);
        BlockPos quarryPos = blockEntity.getBlockPos();

        renderState.quarryPos = blockEntity.getBlockPos();
        renderState.miningAt = blockEntity.getMiningAt();
        renderState.rotation = ((float) blockEntity.getLevel().getGameTime() + partialTick) * 3.0F;
        renderState.cubePos = new Vec3(blockEntity.getCenterX() - quarryPos.getX(), blockEntity.getCenterY() - quarryPos.getY() - 1.5, blockEntity.getCenterZ() - quarryPos.getZ());
        renderState.centerPos = new Vec3(blockEntity.getCenterX(), blockEntity.getCenterY(), blockEntity.getCenterZ());
        renderState.ageInTicks = blockEntity.getLevel().getGameTime() + partialTick;
        List<QuarryLaserRenderer.QuarryLaserRenderState> laserRenderStates = new ArrayList<>();
        laserRenderStates.add(QuarryLaserRenderer.extractQuarryLaserRenderState(renderState.centerPos, blockEntity.getMiningAt().getCenter(), cameraPos, blockEntity.getLevel(), partialTick, MINING_COLOR));

        List<BlockPos> corners = blockEntity.getStabilizerPositions();

        if (corners.size() == 4) {
            laserRenderStates.add(QuarryLaserRenderer.extractQuarryLaserRenderState(corners.get(0), corners.get(1), cameraPos, blockEntity.getLevel(), partialTick, FRAME_COLOR));
            laserRenderStates.add(QuarryLaserRenderer.extractQuarryLaserRenderState(corners.get(1), corners.get(2), cameraPos, blockEntity.getLevel(), partialTick, FRAME_COLOR));
            laserRenderStates.add(QuarryLaserRenderer.extractQuarryLaserRenderState(corners.get(2), corners.get(3), cameraPos, blockEntity.getLevel(), partialTick, FRAME_COLOR));
            laserRenderStates.add(QuarryLaserRenderer.extractQuarryLaserRenderState(corners.get(3), corners.get(0), cameraPos, blockEntity.getLevel(), partialTick, FRAME_COLOR));
        }

        for (BlockPos corner : corners) {
            laserRenderStates.add(QuarryLaserRenderer.extractQuarryLaserRenderState(corner.getCenter(), renderState.centerPos, cameraPos, blockEntity.getLevel(), partialTick, FRAME_COLOR));
        }

        renderState.quarryLaserRenderStates = laserRenderStates;
    }

    @Override
    public void submit(QuarryRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        BlockPos miningAt = renderState.miningAt;

        if (miningAt != null) {
            BlockPos quarryPos = renderState.quarryPos;
            poseStack.pushPose();
            poseStack.translate(renderState.cubePos);
            nodeCollector.submitModel(model, renderState, poseStack, RenderTypes.entityCutoutNoCull(rl("textures/entity/crystal_quarry_cube.png")), 0x00F000F0,
                    OverlayTexture.NO_OVERLAY, 0, null);
            poseStack.popPose();

            Vec3 view = cameraRenderState.pos;

            poseStack.pushPose();
            // The renderer expects it in camera space, the block entity renderer is initially in block space
            poseStack.translate((double) -quarryPos.getX() + view.x, (double) -quarryPos.getY() + view.y, (double) -quarryPos.getZ() + view.z);

            for (QuarryLaserRenderer.QuarryLaserRenderState quarryLaserRenderState : renderState.quarryLaserRenderStates) {
                nodeCollector.submitCustomGeometry(poseStack, CrystalToolsRenderTypes.QUARRY_LASER,
                        (pose, consumer) -> {
                            QuarryLaserRenderer.renderLaser(poseStack, consumer, quarryLaserRenderState);
                        });
            }

            poseStack.popPose();
        }
    }

    @Override
    public AABB getRenderBoundingBox(CrystalQuarryBlockEntity blockEntity) {
        return blockEntity.getAABB() == null ? AABB.INFINITE : blockEntity.getAABB();
    }

    @Override
    public boolean shouldRender(CrystalQuarryBlockEntity blockEntity, Vec3 cameraPos) {
        return !blockEntity.isFinished();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    public static class QuarryRenderState extends BlockEntityRenderState {
        public BlockPos miningAt;
        public BlockPos quarryPos;
        // Relative cube pos to block entity
        public Vec3 cubePos;
        public Vec3 centerPos;
        public float rotation;
        public float ageInTicks;
        public List<QuarryLaserRenderer.QuarryLaserRenderState> quarryLaserRenderStates;
    }
}
