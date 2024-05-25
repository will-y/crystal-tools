package dev.willyelton.crystal_tools.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.entity.CrystalTridentEntity;
import dev.willyelton.crystal_tools.model.CrystalTridentModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrystalTridentRenderer extends EntityRenderer<CrystalTridentEntity> {
    public static final ResourceLocation CRYSTAL_TRIDENT_LOCATION = new ResourceLocation(CrystalTools.MODID, "textures/entity/crystal_trident.png");

    private final CrystalTridentModel model;

    public CrystalTridentRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new CrystalTridentModel(context.bakeLayer(ModelLayers.TRIDENT));
    }

    @Override
    public void render(CrystalTridentEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 90.0F));
        VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(buffer, this.model.renderType(this.getTextureLocation(entity)), false, entity.isFoil());
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CrystalTridentEntity pEntity) {
        return CRYSTAL_TRIDENT_LOCATION;
    }
}
