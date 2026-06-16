package dev.willyelton.crystal.tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.willyelton.crystal.tools.CrystalTools;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.projectile.TridentModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.entity.state.ThrownTridentRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

public class CrystalTridentRenderer extends ThrownTridentRenderer {
    public static final Identifier CRYSTAL_TRIDENT_LOCATION = Identifier.fromNamespaceAndPath(CrystalTools.MODID, "textures/entity/crystal_trident.png");

    private final TridentModel model;

    public CrystalTridentRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new TridentModel(context.bakeLayer(ModelLayers.TRIDENT));
    }

    @Override
    public void submit(ThrownTridentRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.xRot + 90.0F));
        submitNodeCollector.order(0)
                .submitModel(this.model, Unit.INSTANCE, poseStack, CRYSTAL_TRIDENT_LOCATION, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        if (state.isFoil) {
            submitNodeCollector.order(1)
                    .submitModel(
                            this.model,
                            Unit.INSTANCE,
                            poseStack,
                            RenderTypes.entityGlint(),
                            state.lightCoords,
                            OverlayTexture.NO_OVERLAY,
                            state.outlineColor,
                            null
                    );
        }

        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }
}
