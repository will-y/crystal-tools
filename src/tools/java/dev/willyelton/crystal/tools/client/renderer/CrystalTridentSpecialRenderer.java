package dev.willyelton.crystal.tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import dev.willyelton.crystal.tools.CrystalTools;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.projectile.TridentModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class CrystalTridentSpecialRenderer implements NoDataSpecialModelRenderer {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(CrystalTools.MODID, "textures/entity/crystal_trident.png");

    private final TridentModel crystalTridentModel;

    public CrystalTridentSpecialRenderer(TridentModel crystalTridentModel) {
        this.crystalTridentModel = crystalTridentModel;
    }

    @Override
    public void getExtents(Consumer<Vector3fc> vectors) {
        PoseStack posestack = new PoseStack();
        posestack.scale(1.0F, -1.0F, -1.0F);
        this.crystalTridentModel.root().getExtentsForGui(posestack, vectors);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        submitNodeCollector.order(0).submitModel(this.crystalTridentModel, Unit.INSTANCE, poseStack, this.crystalTridentModel.renderType(TEXTURE),
                lightCoords, overlayCoords, outlineColor, null);
        poseStack.popPose();
    }

    public record Unbaked() implements NoDataSpecialModelRenderer.Unbaked {
        public static final MapCodec<NoDataSpecialModelRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<NoDataSpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public CrystalTridentSpecialRenderer bake(BakingContext context) {
            return new CrystalTridentSpecialRenderer(new TridentModel(context.entityModelSet().bakeLayer(ModelLayers.TRIDENT)));
        }
    }

}
