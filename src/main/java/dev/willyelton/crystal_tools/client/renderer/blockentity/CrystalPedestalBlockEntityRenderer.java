package dev.willyelton.crystal_tools.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalPedestalBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.block.entity.data.PedestalClientData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static dev.willyelton.crystal_tools.common.levelable.block.CrystalPedestalBlock.FACING;

public class CrystalPedestalBlockEntityRenderer implements BlockEntityRenderer<CrystalPedestalBlockEntity> {
    private final ItemRenderer itemRenderer;
    private final RandomSource random = RandomSource.create();

    public CrystalPedestalBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(CrystalPedestalBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();

        if (level != null) {
            ItemStack itemstack = blockEntity.getStack();
            if (itemstack != null && !itemstack.isEmpty()) {
                itemstack = itemstack.copy();
                itemstack.setCount(1);
                PedestalClientData clientData = blockEntity.getClientData();
                poseStack.pushPose();
                poseStack.translate(0.5F, 0.5F, 0.5F);
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.rotateAround(blockEntity.getBlockState().getValue(FACING).getRotation(), 0, 0F, 0F);
                poseStack.mulPose(Axis.YP.rotationDegrees(Mth.rotLerp(partialTick, clientData.previousRot(), clientData.currentRot())));
                poseStack.translate(0, Mth.lerp(partialTick, clientData.previousHeight(), clientData.currentHeight()), 0);
                ItemEntityRenderer.renderMultipleFromCount(itemRenderer, poseStack, bufferSource, packedLight, itemstack, this.random, level);
                poseStack.popPose();
            }
        }
    }
}
