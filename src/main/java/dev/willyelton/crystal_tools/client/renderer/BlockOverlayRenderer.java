package dev.willyelton.crystal_tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.willyelton.crystal_tools.common.levelable.tool.HoeLevelableTool;
import dev.willyelton.crystal_tools.common.levelable.tool.LevelableTool;
import dev.willyelton.crystal_tools.common.levelable.tool.VeinMinerLevelableTool;
import dev.willyelton.crystal_tools.utils.BlockCollectors;
import dev.willyelton.crystal_tools.utils.RayTraceUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.Collection;
import java.util.List;

public class BlockOverlayRenderer {

    public static void render3x3(RenderLevelStageEvent event, LevelableTool toolItem, ItemStack stack) {
        final Minecraft mc = Minecraft.getInstance();
        final Player player = mc.player;
        final Level level = mc.level;
        if (player != null && level != null) {
            MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
            Vec3 view = mc.gameRenderer.getMainCamera().getPosition();

            BlockHitResult hitResult = RayTraceUtils.rayTrace(player);
            if (level.getBlockState(hitResult.getBlockPos()).isAir()) {
                return;
            }

            BlockPos pos = hitResult.getBlockPos();
            BlockState hitBlockState = level.getBlockState(pos);
            PoseStack pose = event.getPoseStack();
            pose.pushPose();
            pose.translate(-view.x, -view.y, -view.z);

            VertexConsumer builder = buffer.getBuffer(CrystalToolsRenderType.BLOCK_OVERLAY);

            List<BlockPos> blockPosCollection;
            boolean hoe = false;

            if (toolItem instanceof HoeLevelableTool && !hitBlockState.is(BlockTags.MINEABLE_WITH_HOE)) {
                blockPosCollection = BlockCollectors.collect3x3Hoe(pos);
                hoe = true;
            } else {
                blockPosCollection = BlockCollectors.collect3x3(pos, hitResult.getDirection());
            }

            for (BlockPos renderPos : blockPosCollection) {
                BlockState state = level.getBlockState(renderPos);
                // TODO: Can hoe somehow
                if (state.isAir() || (!toolItem.correctTool(stack, state) && !hoe)) continue;
                BlockOverlayRenderer.renderBlockPos(pose, builder, renderPos, 80.8F / 255, 94.5F / 255, 92.2F / 255);
            }
            pose.popPose();
        }
    }

    public static void renderVeinMiner(RenderLevelStageEvent event, VeinMinerLevelableTool toolItem, ItemStack stack) {
        final Minecraft mc = Minecraft.getInstance();
        final Player player = mc.player;
        final Level level = mc.level;
        if (player != null && level != null) {
            MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
            Vec3 view = mc.gameRenderer.getMainCamera().getPosition();

            BlockHitResult hitResult = RayTraceUtils.rayTrace(player);
            if (level.getBlockState(hitResult.getBlockPos()).isAir()) {
                return;
            }

            BlockPos pos = hitResult.getBlockPos();
            BlockState hitBlockState = level.getBlockState(pos);
            if (!toolItem.canVeinMin(stack, hitBlockState)) return;

            PoseStack pose = event.getPoseStack();
            pose.pushPose();
            pose.translate(-view.x, -view.y, -view.z);

            VertexConsumer builder = buffer.getBuffer(CrystalToolsRenderType.BLOCK_OVERLAY);

            Collection<BlockPos> blockPosCollection = BlockCollectors.collectVeinMine(pos, level, toolItem.getVeinMinerPredicate(hitBlockState), toolItem.getMaxBlocks(stack));

            for (BlockPos renderPos : blockPosCollection) {
                BlockState state = level.getBlockState(renderPos);
                if (state.isAir()) continue;
                BlockOverlayRenderer.renderBlockPos(pose, builder, renderPos, 80.8F / 255, 94.5F / 255, 92.2F / 255);
            }
            pose.popPose();
        }
    }

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
        builder.vertex(matrix, startX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, startY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, startX, startY, endZ).color(red, green, blue, alpha).endVertex();

        //up
        builder.vertex(matrix, startX, endY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, startX, endY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, endY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, endY, startZ).color(red, green, blue, alpha).endVertex();

        //east
        builder.vertex(matrix, startX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, startX, endY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, endY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, startY, startZ).color(red, green, blue, alpha).endVertex();

        //west
        builder.vertex(matrix, startX, startY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, startY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, endY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, startX, endY, endZ).color(red, green, blue, alpha).endVertex();

        //south
        builder.vertex(matrix, endX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, endY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, endY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, endX, startY, endZ).color(red, green, blue, alpha).endVertex();

        //north
        builder.vertex(matrix, startX, startY, startZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, startX, startY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, startX, endY, endZ).color(red, green, blue, alpha).endVertex();
        builder.vertex(matrix, startX, endY, startZ).color(red, green, blue, alpha).endVertex();

        pose.popPose();
    }
}
