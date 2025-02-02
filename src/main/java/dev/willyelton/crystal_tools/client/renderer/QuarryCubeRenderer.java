package dev.willyelton.crystal_tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;

import static dev.willyelton.crystal_tools.client.renderer.CrystalToolsRenderTypes.QUARRY_CUBE;

/**
 * Class that handles rendering for the cube hovering above the quarry.
 * Pretty much a copy of the end crystal
 */
public class QuarryCubeRenderer {
    public static ModelLayerLocation LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "quarry_cube"), "main");

//    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "textures/entity/crystal_quarry_cube.png"));
    // Math constants
    private static final float SIN_45 = (float)Math.sin(Math.PI / 4);
    private static final float PI_3 = (float) Math.PI / 3;

    private final ModelPart cube;
    private final ModelPart glass;

    public QuarryCubeRenderer(ModelPart modelPart) {
        cube = modelPart.getChild("cube");
        glass = modelPart.getChild("glass");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("glass", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 64, 16);
    }

    public void render(int gameTime, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float x, float y, float z) {
        poseStack.pushPose();
        float rotation = ((float) gameTime + partialTicks) * 3.0F;
        VertexConsumer vertexconsumer = buffer.getBuffer(QUARRY_CUBE);
        poseStack.pushPose();
        poseStack.scale(2.0F, 2.0F, 2.0F);
        poseStack.translate(x / 2.0F + 0.25F, y / 2.0F + 0.25F, z / 2.0F + 0.25F);
        int packedOverlay = OverlayTexture.NO_OVERLAY;

        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
//        poseStack.translate(0.0F, 1.5F + yOffset / 2.0F, 0.0F);
        poseStack.mulPose(new Quaternionf().setAngleAxis(PI_3, SIN_45, 0.0F, SIN_45));
        this.glass.render(poseStack, vertexconsumer, packedLight, packedOverlay);
        poseStack.scale(0.875F, 0.875F, 0.875F);
        poseStack.mulPose(new Quaternionf().setAngleAxis(PI_3, SIN_45, 0.0F, SIN_45));
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        this.glass.render(poseStack, vertexconsumer, packedLight, packedOverlay);
        poseStack.scale(0.875F, 0.875F, 0.875F);
        poseStack.mulPose(new Quaternionf().setAngleAxis(PI_3, SIN_45, 0.0F, SIN_45));
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        this.cube.render(poseStack, vertexconsumer, packedLight, packedOverlay);
        poseStack.popPose();
        poseStack.popPose();
    }
}
