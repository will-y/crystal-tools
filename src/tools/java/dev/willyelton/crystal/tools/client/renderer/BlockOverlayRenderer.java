package dev.willyelton.crystal.tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.willyelton.crystal.tools.common.levelable.tool.LevelableTool;
import dev.willyelton.crystal.tools.common.levelable.tool.VeinMinerLevelableTool;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.List;

// TODO
public class BlockOverlayRenderer extends RenderTypeFeatureRenderer<BlockOverlayRenderer.BlockOverlaySubmit> {

    public static final FeatureRendererType<BlockOverlaySubmit> TYPE = FeatureRendererType.create("crystal_tools:block_overlay_submit");

    @Override
    protected void buildGroup(FeatureFrameContext context, List<BlockOverlaySubmit> blockOverlaySubmits) {
        for (BlockOverlaySubmit submit : blockOverlaySubmits) {
            VertexConsumer builder = this.getVertexBuilder(submit.renderType());

            for (BlockPos renderPos : submit.blocksToRender()) {
                BlockOverlayRenderer.renderBlockPos(submit.poseStack, builder, renderPos, 80.8F / 255, 94.5F / 255, 92.2F / 255);
            }
        }
    }

    public record BlockOverlaySubmit(PoseStack poseStack, RenderType renderType, List<BlockPos> blocksToRender) implements TranslucentSubmit {
        @Override
        public FeatureRendererType<? extends TranslucentSubmit> featureType() {
            return TYPE;
        }

        @Override
        public float distanceToCameraSq() {
            // Compute the camera distance.
            return TranslucentSubmit.computeDistanceToCameraSq(this.poseStack.last().pose());
        }
    }

    public static void render3x3(RenderLevelStageEvent event, LevelableTool toolItem, ItemStack stack) {
//        final Minecraft mc = Minecraft.getInstance();
//
//        final Player player = mc.player;
//        final Level level = mc.level;
//        if (player != null && level != null) {
//            MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
//            Vec3 view = mc.gameRenderer.getMainCamera().position();
//
//            BlockHitResult hitResult = RayTraceUtils.rayTrace(player);
//            if (level.getBlockState(hitResult.getBlockPos()).isAir()) {
//                return;
//            }
//
//            BlockPos pos = hitResult.getBlockPos();
//            BlockState hitBlockState = level.getBlockState(pos);
//            PoseStack pose = event.getPoseStack();
//            pose.pushPose();
//            pose.translate(-view.x, -view.y, -view.z);
//
//            VertexConsumer builder = buffer.getBuffer(CrystalToolsRenderTypes.BLOCK_OVERLAY);
//
//            List<BlockPos> blockPosCollection;
//            boolean hoe = false;
//
//            if (toolItem instanceof HoeLevelableTool && !hitBlockState.is(BlockTags.MINEABLE_WITH_HOE)) {
//                blockPosCollection = BlockCollectors.collect3x3Hoe(pos);
//                hoe = true;
//            } else {
//                blockPosCollection = BlockCollectors.collect3x3(pos, hitResult.getDirection());
//            }
//
//            for (BlockPos renderPos : blockPosCollection) {
//                BlockState state = level.getBlockState(renderPos);
//                // TODO: Can hoe somehow
//                if (state.isAir() || (!toolItem.isCorrectToolForDrops(stack, state) && !hoe)) continue;
//                BlockOverlayRenderer.renderBlockPos(pose, builder, renderPos, 80.8F / 255, 94.5F / 255, 92.2F / 255);
//            }
//            pose.popPose();
//        }
    }

    public static void renderVeinMiner(RenderLevelStageEvent event, VeinMinerLevelableTool toolItem, ItemStack stack) {
//        final Minecraft mc = Minecraft.getInstance();
//        final Player player = mc.player;
//        final Level level = mc.level;
//        if (player != null && level != null) {
//            MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
//            Vec3 view = mc.gameRenderer.getMainCamera().position();
//
//            BlockHitResult hitResult = RayTraceUtils.rayTrace(player);
//            if (level.getBlockState(hitResult.getBlockPos()).isAir()) {
//                return;
//            }
//
//            BlockPos pos = hitResult.getBlockPos();
//            BlockState hitBlockState = level.getBlockState(pos);
//            if (!toolItem.canVeinMin(stack, hitBlockState)) return;
//
//            PoseStack pose = event.getPoseStack();
//            pose.pushPose();
//            pose.translate(-view.x, -view.y, -view.z);
//
//            VertexConsumer builder = buffer.getBuffer(CrystalToolsRenderTypes.BLOCK_OVERLAY);
//
//            Collection<BlockPos> blockPosCollection = BlockCollectors.collectVeinMine(pos, level, toolItem.getVeinMinerPredicate(hitBlockState), toolItem.getMaxBlocks(stack));
//
//            for (BlockPos renderPos : blockPosCollection) {
//                BlockState state = level.getBlockState(renderPos);
//                if (state.isAir()) continue;
//                BlockOverlayRenderer.renderBlockPos(pose, builder, renderPos, 80.8F / 255, 94.5F / 255, 92.2F / 255);
//            }
//            pose.popPose();
//        }
    }

//    public static void renderBlockPos(PoseStack pose, MultiBufferSource bufferSource, BlockPos pos, int color) {
//        Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().position();
//        pose.pushPose();
//        pose.translate(-view.x, -view.y, -view.z);
//        renderBlockPos(pose, bufferSource.getBuffer(CrystalToolsRenderTypes.BLOCK_OVERLAY), pos, ((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F, (color & 0xFF) / 255.0F);
//        pose.popPose();
//    }

    public static void renderBlockPos(PoseStack pose, VertexConsumer builder, BlockPos pos, float red, float green, float blue) {
        float alpha = 0.5f;
        float startX = 0, startY = 0, startZ = -1, endX = 1, endY = 1, endZ = 0;

        pose.pushPose();
        pose.translate(pos.getX(), pos.getY(), pos.getZ());
        pose.translate(-0.0005f, -0.0005f, -0.0005f);
        pose.scale(1.001f, 1.001f, 1.001f);
        pose.mulPose(Axis.YP.rotationDegrees(-90.0F));
        Matrix4f matrix = pose.last().pose();

        //down
        builder.addVertex(matrix, startX, startY, startZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, endX, startY, startZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, endX, startY, endZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, startX, startY, endZ).setColor(red, green, blue, alpha);

        //up
        builder.addVertex(matrix, startX, endY, startZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, startX, endY, endZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, endX, endY, endZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, endX, endY, startZ).setColor(red, green, blue, alpha);

        //east
        builder.addVertex(matrix, startX, startY, startZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, startX, endY, startZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, endX, endY, startZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, endX, startY, startZ).setColor(red, green, blue, alpha);

        //west
        builder.addVertex(matrix, startX, startY, endZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, endX, startY, endZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, endX, endY, endZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, startX, endY, endZ).setColor(red, green, blue, alpha);

        //south
        builder.addVertex(matrix, endX, startY, startZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, endX, endY, startZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, endX, endY, endZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, endX, startY, endZ).setColor(red, green, blue, alpha);

        //north
        builder.addVertex(matrix, startX, startY, startZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, startX, startY, endZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, startX, endY, endZ).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, startX, endY, startZ).setColor(red, green, blue, alpha);

        pose.popPose();
    }
}
