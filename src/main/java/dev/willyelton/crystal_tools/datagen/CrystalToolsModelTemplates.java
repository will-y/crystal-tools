package dev.willyelton.crystal_tools.datagen;

import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

public class CrystalToolsModelTemplates {
    public static final ModelTemplate CRYSTAL_BOW = ExtendedModelTemplateBuilder.builder()
            .parent(ResourceLocation.withDefaultNamespace("item/bow"))
            .requiredTextureSlot(TextureSlot.LAYER0)
            .build();

    public static ModelTemplate CRYSTAL_SHIELD = ExtendedModelTemplateBuilder.builder()
            .parent(ResourceLocation.withDefaultNamespace("item/shield"))
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .build();

    public static ModelTemplate CRYSTAL_SHIELD_BLOCKING = ExtendedModelTemplateBuilder.builder()
            .suffix("_blocking")
            .parent(ResourceLocation.withDefaultNamespace("item/shield_blocking"))
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .build();

    public static ModelTemplate CRYSTAL_TRIDENT_IN_HAND = ExtendedModelTemplateBuilder.builder()
            .suffix("_in_hand")
            .parent(ResourceLocation.withDefaultNamespace("item/trident_in_hand"))
            .guiLight(UnbakedModel.GuiLight.FRONT)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .build();

    public static ModelTemplate CRYSTAL_TRIDENT_THROWING = ExtendedModelTemplateBuilder.builder()
            .suffix("_throwing")
            .parent(ResourceLocation.withDefaultNamespace("item/trident_throwing"))
            .guiLight(UnbakedModel.GuiLight.FRONT)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .build();
}
