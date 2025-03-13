package dev.willyelton.crystal_tools.client.model;

// TODO (PORTING): Can probably just do this with an equipment asset, shouldn't need anything else
//public class CrystalElytraLayer<S extends HumanoidRenderState, M extends EntityModel<S>> extends WingsLayer<S, M> {
//    private static final ResourceLocation WINGS_LOCATION = ResourceLocation.fromNamespaceAndPath("crystal_tools", "textures/entity/crystal_elytra.png");
//
//    public CrystalElytraLayer(RenderLayerParent<S, M> renderer, EntityModelSet entityModelSet, EquipmentLayerRenderer equipmentRenderer) {
//        super(renderer, entityModelSet, equipmentRenderer);
//    }
//
//    @Override
//    public @NotNull ResourceLocation getPlayerElytraTexture(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
//        return WINGS_LOCATION;
//    }
//
//    @Override
//    public boolean shouldRender(ItemStack stack, @NotNull LivingEntity entity) {
//        return stack.getItem() == Registration.CRYSTAL_ELYTRA.get();
//    }
//}
