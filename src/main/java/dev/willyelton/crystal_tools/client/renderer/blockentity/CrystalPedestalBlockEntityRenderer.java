package dev.willyelton.crystal_tools.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalPedestalBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.block.entity.data.PedestalClientData;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import static dev.willyelton.crystal_tools.common.levelable.block.CrystalPedestalBlock.FACING;

public class CrystalPedestalBlockEntityRenderer implements BlockEntityRenderer<CrystalPedestalBlockEntity, CrystalPedestalBlockEntityRenderer.PedestalRenderState> {
    private final ItemModelResolver itemModelResolver;
    private final RandomSource random = RandomSource.create();

    public CrystalPedestalBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public PedestalRenderState createRenderState() {
        return new PedestalRenderState();
    }

    @Override
    public void extractRenderState(CrystalPedestalBlockEntity blockEntity, PedestalRenderState renderState, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, overlay);

        ItemStack stack = blockEntity.getStack();
        if (stack != null && !stack.isEmpty() && blockEntity.hasLevel()) {
            PedestalClientData clientData = blockEntity.getClientData();

            renderState.displayItem = new ItemClusterRenderState();
            this.itemModelResolver.updateForTopItem(renderState.displayItem.item, stack, ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);
            renderState.displayItem.count = ItemClusterRenderState.getRenderedAmount(stack.getCount());
            renderState.displayItem.seed = ItemClusterRenderState.getSeedForItemStack(stack);
            renderState.spin = Mth.rotLerp(partialTick, clientData.previousRot(), clientData.currentRot());
            renderState.height = Mth.lerp(partialTick, clientData.previousHeight(), clientData.currentHeight());
            renderState.direction = blockEntity.getBlockState().getValue(FACING);
        }
    }

    @Override
    public void submit(PedestalRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (renderState.displayItem != null) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.rotateAround(renderState.direction.getRotation(), 0, 0F, 0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(renderState.spin));
            poseStack.translate(0, renderState.height, 0);
            ItemEntityRenderer.renderMultipleFromCount(poseStack, nodeCollector, renderState.lightCoords, renderState.displayItem, this.random);
            poseStack.popPose();
        }
    }

    public static class PedestalRenderState extends BlockEntityRenderState {
        public ItemClusterRenderState displayItem;
        public float spin;
        public float height;
        public Direction direction;
    }
}
