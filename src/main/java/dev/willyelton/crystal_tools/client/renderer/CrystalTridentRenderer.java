package dev.willyelton.crystal_tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.entity.CrystalTridentEntity;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.state.ThrownTridentRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;

import java.util.List;

public class CrystalTridentRenderer extends EntityRenderer<CrystalTridentEntity, ThrownTridentRenderState> {
    public static final ResourceLocation CRYSTAL_TRIDENT_LOCATION = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "textures/entity/crystal_trident.png");

    private final TridentModel model;

    public CrystalTridentRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new TridentModel(context.bakeLayer(ModelLayers.TRIDENT));
    }

    @Override
    public void submit(ThrownTridentRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.yRot - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(renderState.xRot + 90.0F));
        List<RenderType> list = ItemRenderer.getFoilRenderTypes(this.model.renderType(CRYSTAL_TRIDENT_LOCATION), false, false);

        for (int i = 0; i < list.size(); i++) {
            nodeCollector.order(i)
                    .submitModel(this.model, Unit.INSTANCE, poseStack, list.get(i), renderState.lightCoords,
                            OverlayTexture.NO_OVERLAY, -1, null, renderState.outlineColor, null);
        }

        poseStack.popPose();
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
    }

    @Override
    public ThrownTridentRenderState createRenderState() {
        return new ThrownTridentRenderState();
    }

    @Override
    public void extractRenderState(CrystalTridentEntity entity, ThrownTridentRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.yRot = entity.getYRot();
        reusedState.xRot = entity.getXRot();
    }
}
