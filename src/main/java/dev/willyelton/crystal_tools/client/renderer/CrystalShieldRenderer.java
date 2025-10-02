package dev.willyelton.crystal_tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;

public class CrystalShieldRenderer implements SpecialModelRenderer<DataComponentMap> {
    private static final Material SHIELD_MATERIAL = new Material(Sheets.SHIELD_SHEET, ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "entity/crystal_shield"));

    private final ShieldModel shieldModel;
    private final MaterialSet materials;

    public CrystalShieldRenderer(MaterialSet materials, ShieldModel shieldModel) {
        this.materials = materials;
        this.shieldModel = shieldModel;
    }

    @Nullable
    public DataComponentMap extractArgument(ItemStack stack) {
        return stack.immutableComponents();
    }

    @Override
    public void submit(DataComponentMap components, ItemDisplayContext itemDisplayContext, PoseStack poseStack, SubmitNodeCollector nodeCollector, int p_386748_, int p_388858_, boolean p_387642_, int p_451675_) {
        BannerPatternLayers bannerpatternlayers = components != null ? components.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY) : BannerPatternLayers.EMPTY;
        DyeColor dyecolor = components != null ? components.get(DataComponents.BASE_COLOR) : null;
        boolean hasBanner = !bannerpatternlayers.layers().isEmpty() || dyecolor != null;
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        Material material = SHIELD_MATERIAL;
        nodeCollector.submitModelPart(this.shieldModel.handle(), poseStack, this.shieldModel.renderType(material.atlasLocation()),
                p_386748_, p_388858_, this.materials.get(material), false, false, -1, null, p_451675_);

        if (hasBanner) {
            BannerRenderer.submitPatterns(this.materials, poseStack, nodeCollector, p_386748_, p_388858_, this.shieldModel, Unit.INSTANCE,
                    material, false, Objects.requireNonNullElse(dyecolor, DyeColor.WHITE), bannerpatternlayers,
                    p_387642_, null, p_451675_);
        } else {
            nodeCollector.submitModelPart(this.shieldModel.plate(), poseStack, this.shieldModel.renderType(material.atlasLocation()),
                    p_386748_, p_388858_, this.materials.get(material), false, p_387642_, -1,
                    null, p_451675_);
        }
    }

    @Override
    public void getExtents(Set<Vector3f> vectors) {
        PoseStack posestack = new PoseStack();
        posestack.scale(1.0F, -1.0F, -1.0F);
        this.shieldModel.root().getExtentsForGui(posestack, vectors);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final CrystalShieldRenderer.Unbaked INSTANCE = new CrystalShieldRenderer.Unbaked();
        public static final MapCodec<CrystalShieldRenderer.Unbaked> MAP_CODEC = MapCodec.unit(INSTANCE);

        @Override
        public MapCodec<CrystalShieldRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext context) {
            return new CrystalShieldRenderer(context.materials(), new ShieldModel(context.entityModelSet().bakeLayer(ModelLayers.SHIELD)));
        }
    }
}
