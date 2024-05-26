package dev.willyelton.crystal_tools.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CrystalTridentBlockEntityWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {
    public static final CrystalTridentBlockEntityWithoutLevelRenderer INSTANCE = new CrystalTridentBlockEntityWithoutLevelRenderer();
    // Copy this  because they are private in super
    private final EntityModelSet entityModelSet;

    private TridentModel crystalTridentModel;

    public CrystalTridentBlockEntityWithoutLevelRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.entityModelSet = Minecraft.getInstance().getEntityModels();
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.crystalTridentModel = new TridentModel(this.entityModelSet.bakeLayer(ModelLayers.TRIDENT));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (displayContext == ItemDisplayContext.GUI || displayContext == ItemDisplayContext.GROUND || displayContext == ItemDisplayContext.FIXED) {
            // This is a little gross, have to get rid of the transformations that the first renderer creates, then create our own to do some of the transformations
            poseStack.popPose();
            poseStack.pushPose();
            if (displayContext == ItemDisplayContext.GROUND) {
                // No idea why I need this
                // TODO: Probably a model json issue
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.translate(0, 0.25, 0);
            }
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            BakedModel inventoryModel = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(CrystalTools.MODID, "item/crystal_trident_inventory"));
            for (var model : inventoryModel.getRenderPasses(stack, false)) {
                for (var renderType : model.getRenderTypes(stack, false)) {
                    VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(buffer, renderType,true, stack.hasFoil());
                    itemRenderer.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, vertexConsumer);
                }
            }
        } else {
            poseStack.pushPose();
            poseStack.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer vertexconsumer1 = ItemRenderer.getFoilBufferDirect(buffer, this.crystalTridentModel.renderType(CrystalTridentRenderer.CRYSTAL_TRIDENT_LOCATION), false, stack.hasFoil());
            this.crystalTridentModel.renderToBuffer(poseStack, vertexconsumer1, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }

    }
}
