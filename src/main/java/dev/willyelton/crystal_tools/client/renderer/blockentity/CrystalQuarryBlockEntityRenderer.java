package dev.willyelton.crystal_tools.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.willyelton.crystal_tools.client.renderer.QuarryCubeModel;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalQuarryBlockEntity;
import dev.willyelton.crystal_tools.utils.Colors;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.willyelton.crystal_tools.client.renderer.CrystalToolsRenderTypes.QUARRY_CUBE;

public class CrystalQuarryBlockEntityRenderer implements BlockEntityRenderer<CrystalQuarryBlockEntity, CrystalQuarryBlockEntityRenderer.QuarryRenderState> {
    private static final int FRAME_COLOR = Colors.fromRGB(48, 231, 237, 255);
    private static final int MINING_COLOR = Colors.fromRGB(0, 144, 180, 255);

    private final QuarryCubeModel model;

    public CrystalQuarryBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new QuarryCubeModel(context.bakeLayer(QuarryCubeModel.LOCATION));
    }

//    @Override
//    public void render(CrystalQuarryBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {
//        BlockPos miningAt = blockEntity.getMiningAt();
//
//        if (miningAt != null) {
//            BlockPos quarryPos = blockEntity.getBlockPos();
//            cubeRenderer.render((int) blockEntity.getLevel().getGameTime(), partialTick, poseStack, bufferSource, packedLight,
//                    blockEntity.getCenterX() - quarryPos.getX(), blockEntity.getCenterY() - quarryPos.getY(), blockEntity.getCenterZ() - quarryPos.getZ());
//
//
//            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
//            Vec3 view = camera.getPosition();
//            int color = Colors.fromRGB(48, 231, 237, 255);
//            int miningLaserColor = Colors.fromRGB(0, 144, 180, 255);
//
//            poseStack.pushPose();
//            // The renderer expects it in camera space, the block entity renderer is initially in block space
//            poseStack.translate((double) -blockEntity.getBlockPos().getX() + view.x, (double) -blockEntity.getBlockPos().getY() + view.y, (double) -blockEntity.getBlockPos().getZ() + view.z);
//
//            float centerX = blockEntity.getCenterX();
//            float centerY = blockEntity.getCenterY();
//            float centerZ = blockEntity.getCenterZ();
//            QuarryLaserRenderer.renderLaser(bufferSource, poseStack, camera, partialTick, blockEntity.getLevel(), centerX, centerY, centerZ, blockEntity.getMiningAt(), miningLaserColor);
//
//            List<BlockPos> corners = blockEntity.getStabilizerPositions();
//
//            if (!corners.isEmpty()) {
//                QuarryLaserRenderer.renderLaser(bufferSource, poseStack, camera, partialTick, blockEntity.getLevel(), corners.get(0), corners.get(1), color);
//                QuarryLaserRenderer.renderLaser(bufferSource, poseStack, camera, partialTick, blockEntity.getLevel(), corners.get(1), corners.get(2), color);
//                QuarryLaserRenderer.renderLaser(bufferSource, poseStack, camera, partialTick, blockEntity.getLevel(), corners.get(2), corners.get(3), color);
//                QuarryLaserRenderer.renderLaser(bufferSource, poseStack, camera, partialTick, blockEntity.getLevel(), corners.get(3), corners.get(0), color);
//            }
//
//            for (BlockPos corner : corners) {
//                QuarryLaserRenderer.renderLaser(bufferSource, poseStack, camera, partialTick, blockEntity.getLevel(), corner, centerX, centerY, centerZ, color);
//            }
//
//            poseStack.popPose();
//        }
//    }

    @Override
    public QuarryRenderState createRenderState() {
        return new QuarryRenderState();
    }

    @Override
    public void extractRenderState(CrystalQuarryBlockEntity blockEntity, QuarryRenderState renderState, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, overlay);
        BlockPos quarryPos = blockEntity.getBlockPos();

        renderState.rotation = ((float) blockEntity.getLevel().getGameTime() + partialTick) * 3.0F;

        renderState.cubePos = new Vec3(blockEntity.getCenterX() - quarryPos.getX(), blockEntity.getCenterY() - quarryPos.getY(), blockEntity.getCenterZ() - quarryPos.getZ());
    }

    @Override
    public void submit(QuarryRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        BlockPos miningAt = renderState.miningAt;

        if (miningAt != null) {
            BlockPos quarryPos = renderState.quarryPos;
            poseStack.pushPose();
            poseStack.translate(renderState.cubePos);
            nodeCollector.submitModel(model, renderState, poseStack, QUARRY_CUBE, renderState.lightCoords, OverlayTexture.NO_OVERLAY, -1, null);
            poseStack.popPose();

            Vec3 view = cameraRenderState.pos;

            poseStack.pushPose();
            // The renderer expects it in camera space, the block entity renderer is initially in block space
            poseStack.translate((double) -quarryPos.getX() + view.x, (double) -quarryPos.getY() + view.y, (double) -quarryPos.getZ() + view.z);

            float centerX = (float) renderState.centerPos.x();
            float centerY = (float) renderState.centerPos.y();
            float centerZ = (float) renderState.centerPos.z();
//            QuarryLaserRenderer.renderLaser(bufferSource, poseStack, cameraRenderState, partialTick, blockEntity.getLevel(), centerX, centerY, centerZ, blockEntity.getMiningAt(), MINING_COLOR);

            List<BlockPos> corners = renderState.stabilizerPositions;

            if (!corners.isEmpty()) {
//                QuarryLaserRenderer.renderLaser(bufferSource, poseStack, cameraRenderState, partialTick, blockEntity.getLevel(), corners.get(0), corners.get(1), FRAME_COLOR);
//                QuarryLaserRenderer.renderLaser(bufferSource, poseStack, cameraRenderState, partialTick, blockEntity.getLevel(), corners.get(1), corners.get(2), FRAME_COLOR);
//                QuarryLaserRenderer.renderLaser(bufferSource, poseStack, cameraRenderState, partialTick, blockEntity.getLevel(), corners.get(2), corners.get(3), FRAME_COLOR);
//                QuarryLaserRenderer.renderLaser(bufferSource, poseStack, cameraRenderState, partialTick, blockEntity.getLevel(), corners.get(3), corners.get(0), FRAME_COLOR);
            }

            for (BlockPos corner : corners) {
//                QuarryLaserRenderer.renderLaser(bufferSource, poseStack, cameraRenderState, partialTick, blockEntity.getLevel(), corner, centerX, centerY, centerZ, FRAME_COLOR);
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
        public Vec3 cubePos;
        public Vec3 centerPos;
        public float rotation;
        public int ageInTicks;
        public List<BlockPos> stabilizerPositions;
    }
}
