package dev.willyelton.crystal_tools.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CrystalElytraLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends ElytraLayer<T, M> {
    private static final ResourceLocation WINGS_LOCATION = ResourceLocation.fromNamespaceAndPath("crystal_tools", "textures/entity/crystal_elytra.png");

    private final A chestModel;

    public CrystalElytraLayer(RenderLayerParent<T, M> renderer, EntityModelSet entityModelSet, A chestModel) {
        super(renderer, entityModelSet);
        this.chestModel = chestModel;
    }

    @Override
    public @NotNull ResourceLocation getElytraTexture(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return WINGS_LOCATION;
    }

    @Override
    public boolean shouldRender(ItemStack stack, @NotNull LivingEntity entity) {
        return stack.getItem() == Registration.CRYSTAL_ELYTRA.get();
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
        if (shouldRender(itemstack, livingEntity)) {
            renderArmorPiece(poseStack, buffer, livingEntity, EquipmentSlot.CHEST, packedLight, chestModel, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            super.render(poseStack, buffer, packedLight, livingEntity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }

    private void renderArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource, T livingEntity, EquipmentSlot slot, int packedLight, A model, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = livingEntity.getItemBySlot(slot);
        this.getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.body.visible = true;
        model.rightArm.visible = true;
        model.leftArm.visible = true;
        ArmorMaterial armormaterial = ArmorMaterials.NETHERITE.value();

        net.neoforged.neoforge.client.extensions.common.IClientItemExtensions extensions = net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.of(itemstack);
        extensions.setupModelAnimations(livingEntity, itemstack, slot, model, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
        int fallbackColor = extensions.getDefaultDyeColor(itemstack);
        for (int layerIdx = 0; layerIdx < armormaterial.layers().size(); layerIdx++) {
            ArmorMaterial.Layer armormaterial$layer = armormaterial.layers().get(layerIdx);
            int j = extensions.getArmorLayerTintColor(itemstack, livingEntity, armormaterial$layer, layerIdx, fallbackColor);
            if (j != 0) {
                ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "textures/models/armor/crystal_layer_1.png");
                this.renderModel(poseStack, bufferSource, packedLight, model, j, texture);
            }
        }

    }

    private void renderModel(PoseStack p_289664_, MultiBufferSource p_289689_, int p_289681_, net.minecraft.client.model.Model p_289658_, int p_350798_, ResourceLocation p_324344_) {
        VertexConsumer vertexconsumer = p_289689_.getBuffer(RenderType.armorCutoutNoCull(p_324344_));
        p_289658_.renderToBuffer(p_289664_, vertexconsumer, p_289681_, OverlayTexture.NO_OVERLAY, p_350798_);
    }
}
