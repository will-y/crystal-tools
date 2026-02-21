package dev.willyelton.crystal_tools.client.renderer;

import com.mojang.math.Axis;
import dev.willyelton.crystal_tools.client.renderer.blockentity.CrystalQuarryBlockEntityRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.joml.Quaternionf;

import static dev.willyelton.crystal_tools.CrystalTools.rl;
import static dev.willyelton.crystal_tools.client.renderer.CrystalToolsRenderTypes.QUARRY_CUBE;

public class QuarryCubeModel extends Model<CrystalQuarryBlockEntityRenderer.QuarryRenderState> {
    public static ModelLayerLocation LOCATION = new ModelLayerLocation(rl("quarry_cube"), "main");

    // Math constants
    private static final float SIN_45 = (float)Math.sin(Math.PI / 4);
    private static final float PI_3 = (float) Math.PI / 3;
    private static final float PI_180 = (float) Math.PI / 180;

    private final ModelPart cube;
    private final ModelPart outerGlass;
    private final ModelPart innerGlass;

    public QuarryCubeModel(ModelPart root) {
        super(root, rl -> QUARRY_CUBE);

        outerGlass = root.getChild("outer_glass");
        innerGlass = outerGlass.getChild("inner_glass");
        cube = innerGlass.getChild("cube");
    }

    @Override
    public void setupAnim(CrystalQuarryBlockEntityRenderer.QuarryRenderState renderState) {
        super.setupAnim(renderState);
        this.outerGlass.rotateBy(Axis.YP.rotationDegrees(renderState.rotation).rotateAxis(PI_3, SIN_45, 0.0F, SIN_45));
        this.innerGlass.rotateBy(new Quaternionf().setAngleAxis(PI_3, SIN_45, 0.0F, SIN_45).rotateY(renderState.rotation * PI_180));
        this.cube.rotateBy(new Quaternionf().setAngleAxis(PI_3, SIN_45, 0.0F, SIN_45).rotateY(renderState.rotation * PI_180));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        float f = 0.875F;
        CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("outer_glass", cubelistbuilder, PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild("inner_glass", cubelistbuilder, PartPose.ZERO.withScale(0.875F));
        partdefinition2.addOrReplaceChild(
                "cube", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO.withScale(0.765625F)
        );
        return LayerDefinition.create(meshdefinition, 64, 16);
    }
}
