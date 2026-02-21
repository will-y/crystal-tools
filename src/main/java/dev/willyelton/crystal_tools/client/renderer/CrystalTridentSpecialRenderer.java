package dev.willyelton.crystal_tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.projectile.TridentModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
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
    public void submit(ItemDisplayContext itemDisplayContext, PoseStack poseStack, SubmitNodeCollector nodeCollector, int p_388874_, int p_388252_, boolean p_387131_, int p_451703_) {
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        nodeCollector.submitModelPart(this.crystalTridentModel.root(), poseStack, this.crystalTridentModel.renderType(TEXTURE),
                p_388874_, p_388252_, null, false, p_387131_, -1, null, p_451703_);
        poseStack.popPose();
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<SpecialModelRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new CrystalTridentSpecialRenderer.Unbaked());

        @Override
        public MapCodec<SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext context) {
            return new CrystalTridentSpecialRenderer(new TridentModel(context.entityModelSet().bakeLayer(ModelLayers.TRIDENT)));
        }
    }

}
