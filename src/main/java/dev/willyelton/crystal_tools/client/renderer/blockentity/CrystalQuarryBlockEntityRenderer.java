package dev.willyelton.crystal_tools.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.willyelton.crystal_tools.client.renderer.QuarryCubeRenderer;
import dev.willyelton.crystal_tools.client.renderer.QuarryLaserRenderer;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalQuarryBlockEntity;
import dev.willyelton.crystal_tools.utils.Colors;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CrystalQuarryBlockEntityRenderer implements BlockEntityRenderer<CrystalQuarryBlockEntity> {
    private final QuarryCubeRenderer cubeRenderer;

    public CrystalQuarryBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        cubeRenderer = new QuarryCubeRenderer(context.bakeLayer(QuarryCubeRenderer.LOCATION));
    }

    @Override
    public void render(CrystalQuarryBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockPos quarryPos = blockEntity.getBlockPos();
        cubeRenderer.render((int) blockEntity.getLevel().getGameTime(), partialTick, poseStack, bufferSource, packedLight,
                blockEntity.getCenterX() - quarryPos.getX(), blockEntity.getCenterY() - quarryPos.getY(), blockEntity.getCenterZ() - quarryPos.getZ());
        BlockPos miningAt = blockEntity.getMiningAt();

        if (miningAt != null) {
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            Vec3 view = camera.getPosition();
            int color = Colors.fromRGB(48, 231, 237, 255);
            int miningLaserColor = Colors.fromRGB(0, 144, 180, 255);

            poseStack.pushPose();
            // The renderer expects it in camera space, the block entity renderer is initially in block space
            poseStack.translate((double) -blockEntity.getBlockPos().getX() + view.x, (double) -blockEntity.getBlockPos().getY() + view.y, (double) -blockEntity.getBlockPos().getZ() + view.z);

            float centerX = blockEntity.getCenterX();
            float centerY = blockEntity.getCenterY();
            float centerZ = blockEntity.getCenterZ();
            QuarryLaserRenderer.renderLaser(bufferSource, poseStack, camera, partialTick, blockEntity.getLevel(), centerX, centerY, centerZ, blockEntity.getMiningAt(), miningLaserColor);

            List<BlockPos> corners = blockEntity.getStabilizerPositions();

            if (!corners.isEmpty()) {
                QuarryLaserRenderer.renderLaser(bufferSource, poseStack, camera, partialTick, blockEntity.getLevel(), corners.get(0), corners.get(1), color);
                QuarryLaserRenderer.renderLaser(bufferSource, poseStack, camera, partialTick, blockEntity.getLevel(), corners.get(1), corners.get(2), color);
                QuarryLaserRenderer.renderLaser(bufferSource, poseStack, camera, partialTick, blockEntity.getLevel(), corners.get(2), corners.get(3), color);
                QuarryLaserRenderer.renderLaser(bufferSource, poseStack, camera, partialTick, blockEntity.getLevel(), corners.get(3), corners.get(0), color);
            }

            for (BlockPos corner : corners) {
                QuarryLaserRenderer.renderLaser(bufferSource, poseStack, camera, partialTick, blockEntity.getLevel(), corner, centerX, centerY, centerZ, color);
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
    public boolean shouldRenderOffScreen(CrystalQuarryBlockEntity blockEntity) {
        return true;
    }
}
