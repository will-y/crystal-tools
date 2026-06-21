package dev.willyelton.crystal.tools.client.renderer;

import com.google.common.collect.Streams;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.willyelton.crystal.core.utils.BlockCollectors;
import dev.willyelton.crystal.core.utils.Colors;
import dev.willyelton.crystal.core.utils.RayTraceUtils;
import dev.willyelton.crystal.tools.client.events.RegisterKeyBindingsEvent;
import dev.willyelton.crystal.tools.common.components.DataComponents;
import dev.willyelton.crystal.tools.common.levelable.tool.HoeLevelableTool;
import dev.willyelton.crystal.tools.common.levelable.tool.LevelableTool;
import dev.willyelton.crystal.tools.common.levelable.tool.VeinMinerLevelableTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.CustomFeatureRenderer;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import net.neoforged.neoforge.client.submit.RenderPhaseKeys;
import net.neoforged.neoforge.common.ItemAbilities;
import org.joml.Matrix4f;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static dev.willyelton.crystal.tools.CrystalTools.ck;

public class BlockOverlayRenderer {

    private static final ContextKey<List<BlockPos>> BLOCK_OVERLAY_CONTEXT_KEY = ck("block_overlay");
    private static final int DEFAULT_COLOR = Colors.fromRGB(80, 95, 92);

    public static void extractRenderState(ExtractLevelRenderStateEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        Level level = event.getLevel();

        if (player != null) {
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (stack.getItem() instanceof LevelableTool toolItem) {
                BlockHitResult hitResult = RayTraceUtils.rayTrace(player);
                if (level.getBlockState(hitResult.getBlockPos()).isAir()) {
                    return;
                }

                BlockPos pos = hitResult.getBlockPos();
                BlockState hitBlockState = level.getBlockState(pos);

                List<BlockPos> positions3x3 = extract3x3(stack, toolItem, level, player, pos, hitBlockState, hitResult);
                Collection<BlockPos> positionsVein = extractVeinMiner(stack, toolItem, level, pos, hitBlockState);

                List<BlockPos> totalList = Streams.concat(positions3x3.stream(), positionsVein.stream())
                        .toList();

                event.getRenderState().setRenderData(BLOCK_OVERLAY_CONTEXT_KEY, totalList);
            }
        }
    }

    public static void submit(SubmitCustomGeometryEvent event) {
        List<BlockPos> positions = event.getLevelRenderState().getRenderData(BLOCK_OVERLAY_CONTEXT_KEY);
        if (positions != null && !positions.isEmpty()) {
            Vec3 view = event.getLevelRenderState().cameraRenderState.pos;
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();
            poseStack.translate(-view.x, -view.y, -view.z);

            for (BlockPos pos : positions) {
                submitBlockPos(event, poseStack, pos, DEFAULT_COLOR);
            }

            poseStack.popPose();
        }
    }

    public static void submitBlockPos(SubmitCustomGeometryEvent event, PoseStack poseStack, BlockPos pos, int color) {
        poseStack.pushPose();
        poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
        poseStack.translate(-0.0005f, -0.0005f, -0.0005f);
        poseStack.scale(1.001f, 1.001f, 1.001f);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        event.getSubmitNodeCollector().submitSpecial(RenderPhaseKeys.OUTLINE, new CustomFeatureRenderer.Submit(
                poseStack.last().copy(), CrystalToolsRenderTypes.BLOCK_OVERLAY, new OutlineRenderer(color)));
        poseStack.popPose();
    }

    private static List<BlockPos> extract3x3(ItemStack stack, LevelableTool toolItem, Level level, Player player, BlockPos pos, BlockState hitBlockState, BlockHitResult hitResult) {
        List<BlockPos> blockPosCollection;

        if (stack.getOrDefault(DataComponents.HAS_3x3, false)
                && !stack.getOrDefault(DataComponents.DISABLE_3x3, false)) {
            if (toolItem instanceof HoeLevelableTool
                    && !hitBlockState.is(BlockTags.MINEABLE_WITH_HOE)) {
                UseOnContext context = new UseOnContext(player, InteractionHand.MAIN_HAND, hitResult);
                blockPosCollection = BlockCollectors.collect3x3Hoe(pos)
                        .stream()
                        .filter(_ -> {
                            BlockState state = level.getBlockState(pos);
                            BlockState toolModifiedState = state.getToolModifiedState(context, ItemAbilities.HOE_TILL, true);

                            return (toolModifiedState != null || stack.isCorrectToolForDrops(state)) && !state.isAir() && state.isSolidRender();
                        })
                        .toList();
            } else {
                blockPosCollection = BlockCollectors.collect3x3(pos, hitResult.getDirection())
                        .stream()
                        .filter(shouldRender(level, stack))
                        .toList();
            }

            return blockPosCollection;
        }

        return List.of();

    }

    private static Collection<BlockPos> extractVeinMiner(ItemStack stack, LevelableTool toolItem, Level level, BlockPos pos, BlockState hitBlockState) {
        if (toolItem instanceof VeinMinerLevelableTool veinMinerTool
                && RegisterKeyBindingsEvent.VEIN_MINE.isDown()
                && stack.getOrDefault(DataComponents.VEIN_MINER, 0) > 0) {
            return BlockCollectors.collectVeinMine(pos, level, veinMinerTool.getVeinMinerPredicate(hitBlockState), veinMinerTool.getMaxBlocks(stack))
                    .stream()
                    .filter(shouldRender(level, stack))
                    .toList();
        }

        return Collections.emptyList();
    }

    private static Predicate<BlockPos> shouldRender(Level level, ItemStack stack) {
        return blockPos -> {
            BlockState state = level.getBlockState(blockPos);
            return !state.isAir() && stack.isCorrectToolForDrops(state) && state.isSolidRender();
        };
    }

    public static void renderBlockPos(PoseStack.Pose pose, VertexConsumer builder, int color) {
        float alpha = 0.5f;
        float startX = 0, startY = 0, startZ = -1, endX = 1, endY = 1, endZ = 0;
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        Matrix4f matrix = pose.pose();

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
    }

    private record OutlineRenderer(int color) implements SubmitNodeCollector.CustomGeometryRenderer {
        @Override
        public void render(PoseStack.Pose pose, VertexConsumer vertexConsumer) {
            renderBlockPos(pose, vertexConsumer, color);
        }
    }
}
