package dev.willyelton.crystal_tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.util.Objects;

public class CrystalShieldRenderer extends BlockEntityWithoutLevelRenderer {
    public static final CrystalShieldRenderer INSTANCE = new CrystalShieldRenderer();

    private static final Material SHIELD_MATERIAL = new Material(Sheets.SHIELD_SHEET, ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "entity/crystal_shield"));

    private ShieldModel shieldModel;
    private final EntityModelSet entityModelSet;

    public CrystalShieldRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.entityModelSet = Minecraft.getInstance().getEntityModels();
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        shieldModel = new ShieldModel(entityModelSet.bakeLayer(ModelLayers.SHIELD));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BannerPatternLayers bannerpatternlayers = stack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
        DyeColor dyecolor = stack.get(DataComponents.BASE_COLOR);
        boolean hasBanner = !bannerpatternlayers.layers().isEmpty() || dyecolor != null;
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        Material material = SHIELD_MATERIAL;
        VertexConsumer vertexconsumer = material.sprite()
                .wrap(ItemRenderer.getFoilBufferDirect(buffer, this.shieldModel.renderType(material.atlasLocation()), true, stack.hasFoil()));
        this.shieldModel.handle().render(poseStack, vertexconsumer, packedLight, packedOverlay);
        if (hasBanner) {
            BannerRenderer.renderPatterns(poseStack, buffer, packedLight, packedOverlay, this.shieldModel.plate(),
                    material, false, Objects.requireNonNullElse(dyecolor, DyeColor.WHITE),
                    bannerpatternlayers, stack.hasFoil());
        } else {
            this.shieldModel.plate().render(poseStack, vertexconsumer, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }
}
