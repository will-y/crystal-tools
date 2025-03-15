package dev.willyelton.crystal_tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.MapCodec;
import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

public class CrystalTridentSpecialRenderer implements NoDataSpecialModelRenderer {
//    public static final ModelResourceLocation CRYSTAL_TRIDENT_MODEL_RESOURCE_LOCATION = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "item/crystal_trident_inventory"));
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "textures/entity/crystal_trident.png");

    private final TridentModel crystalTridentModel;

    public CrystalTridentSpecialRenderer(TridentModel crystalTridentModel) {
        this.crystalTridentModel = crystalTridentModel;
    }

    // TODO (PORTING): Why does this work with the normal trident renderer but not with this one?
    @Override
    public void render(ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, boolean glint) {
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        VertexConsumer vertexconsumer = ItemRenderer.getFoilBuffer(buffer, this.crystalTridentModel.renderType(TEXTURE), false, glint);
        this.crystalTridentModel.renderToBuffer(poseStack, vertexconsumer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<SpecialModelRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new CrystalTridentSpecialRenderer.Unbaked());

        @Override
        public MapCodec<SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet p_386553_) {
            return new CrystalTridentSpecialRenderer(new TridentModel(p_386553_.bakeLayer(ModelLayers.TRIDENT)));
        }
    }

}
