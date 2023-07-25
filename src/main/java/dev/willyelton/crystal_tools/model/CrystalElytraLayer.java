package dev.willyelton.crystal_tools.model;

import dev.willyelton.crystal_tools.Registration;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CrystalElytraLayer<T extends LivingEntity, M extends EntityModel<T>> extends ElytraLayer {
    private static final ResourceLocation WINGS_LOCATION = new ResourceLocation("crystal_tools", "textures/entity/crystal_elytra.png");

    public CrystalElytraLayer(RenderLayerParent<net.minecraft.world.entity.Entity, EntityModel<net.minecraft.world.entity.Entity>> pRenderer, EntityModelSet p_174494_) {
        super(pRenderer, p_174494_);
    }

    @Override
    public @NotNull ResourceLocation getElytraTexture(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return WINGS_LOCATION;
    }

    @Override
    public boolean shouldRender(ItemStack stack, @NotNull LivingEntity entity) {
        return stack.getItem() == Registration.CRYSTAL_ELYTRA.get();
    }
}
