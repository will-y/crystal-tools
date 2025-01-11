package dev.willyelton.crystal_tools.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
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
    public CrystalQuarryBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(CrystalQuarryBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // TODO: We are going to need to increase the range a little
        // Also the client block entity knows nothing ATM. Going to need to make a lot of packets.
        // 1. Start mining miningPos (null for stop mining?), maybe state too so we can get some particles later (Subclass BreakingItemParticle)
        // 2. Finished
        // 3. Out of energy?
        BlockPos miningAt = blockEntity.getMiningAt();
        if (miningAt != null) {
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            Vec3 view = camera.getPosition();
            int color = Colors.fromRGB(48, 231, 237, 150);

            poseStack.pushPose();
            // The renderer expects it in camera space, the block entity renderer is initially in block space
            poseStack.translate((double) -blockEntity.getBlockPos().getX() + view.x, (double) -blockEntity.getBlockPos().getY() + view.y, (double) -blockEntity.getBlockPos().getZ() + view.z);

            QuarryLaserRenderer.renderLaser(bufferSource, poseStack, camera, partialTick, blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getMiningAt(), color);

            List<BlockPos> corners = blockEntity.getStabilizerPositions();

            if (!corners.isEmpty()) {
                QuarryLaserRenderer.renderLaser(bufferSource, poseStack, camera, partialTick, blockEntity.getLevel(), corners.get(0), corners.get(1), color);
                QuarryLaserRenderer.renderLaser(bufferSource, poseStack, camera, partialTick, blockEntity.getLevel(), corners.get(1), corners.get(2), color);
                QuarryLaserRenderer.renderLaser(bufferSource, poseStack, camera, partialTick, blockEntity.getLevel(), corners.get(2), corners.get(3), color);
                QuarryLaserRenderer.renderLaser(bufferSource, poseStack, camera, partialTick, blockEntity.getLevel(), corners.get(3), corners.get(0), color);
            }

            poseStack.popPose();
        }
    }

    @Override
    public AABB getRenderBoundingBox(CrystalQuarryBlockEntity blockEntity) {
        // TODO: Make the bounds of the quarry I guess
        return AABB.INFINITE;
    }
}
