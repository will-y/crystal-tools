package dev.willyelton.crystal_tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.util.Objects;

public class CrystalShieldRenderer extends ShieldSpecialRenderer {
    private static final Material SHIELD_MATERIAL = new Material(Sheets.SHIELD_SHEET, ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "entity/crystal_shield"));

    private final ShieldModel shieldModel;

    public CrystalShieldRenderer(ShieldModel shieldModel) {
        super(shieldModel);
        this.shieldModel = shieldModel;
    }

    @Override
    public void render(DataComponentMap components, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, boolean withGlint) {
        BannerPatternLayers bannerpatternlayers = components != null ? components.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY) : BannerPatternLayers.EMPTY;
        DyeColor dyecolor = components != null ? components.get(DataComponents.BASE_COLOR) : null;
        boolean hasBanner = !bannerpatternlayers.layers().isEmpty() || dyecolor != null;
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        Material material = SHIELD_MATERIAL;
        VertexConsumer vertexconsumer = material.sprite()
                .wrap(ItemRenderer.getFoilBuffer(buffer, this.shieldModel.renderType(material.atlasLocation()), true, withGlint));
        this.shieldModel.handle().render(poseStack, vertexconsumer, packedLight, packedOverlay);
        if (hasBanner) {
            BannerRenderer.renderPatterns(poseStack, buffer, packedLight, packedOverlay, this.shieldModel.plate(),
                    material, false, Objects.requireNonNullElse(dyecolor, DyeColor.WHITE),
                    bannerpatternlayers, withGlint, false);
        } else {
            this.shieldModel.plate().render(poseStack, vertexconsumer, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }
}
