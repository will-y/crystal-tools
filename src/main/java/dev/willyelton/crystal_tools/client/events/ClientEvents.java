package dev.willyelton.crystal_tools.client.events;


import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.client.model.CrystalElytraLayer;
import dev.willyelton.crystal_tools.client.renderer.CrystalTridentBlockEntityWithoutLevelRenderer;
import dev.willyelton.crystal_tools.client.renderer.CrystalTridentRenderer;
import dev.willyelton.crystal_tools.client.renderer.QuarryCubeRenderer;
import dev.willyelton.crystal_tools.client.renderer.blockentity.CrystalQuarryBlockEntityRenderer;
import dev.willyelton.crystal_tools.common.levelable.tool.BowLevelableItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.common.ItemAbilities;

@EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientEvents {
    @SubscribeEvent
    public static void setModelProperties(FMLClientSetupEvent event) {
        // Bow things
        ItemProperties.register(Registration.CRYSTAL_BOW.get(), ResourceLocation.withDefaultNamespace("pull"), (stack, level, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                if (stack.getItem() instanceof BowLevelableItem bowItem) {
                    return entity.getUseItem() != stack ? 0.0F : (float)(stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / bowItem.getChargeTime(stack);
                }
                return 0;
            }
        });
        ItemProperties.register(Registration.CRYSTAL_BOW.get(), ResourceLocation.withDefaultNamespace("pulling"),
                (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);

        // Trident things
        ItemProperties.register(Registration.CRYSTAL_TRIDENT.get(), ResourceLocation.withDefaultNamespace("throwing"),
                (pStack, pLevel, pEntity, pSeed) -> pEntity != null && pEntity.isUsingItem() && pEntity.getUseItem() == pStack ? 1.0F : 0.0F);

        // Fishing Rod
        ItemProperties.register(Registration.CRYSTAL_FISHING_ROD.get(), ResourceLocation.withDefaultNamespace("cast"),
                (stack, level, entity, seed) -> {
                    if (entity == null) {
                        return 0.0F;
                    } else {
                        boolean flag = entity.getMainHandItem() == stack;
                        boolean flag1 = entity.getOffhandItem() == stack && !entity.getMainHandItem().getItem().canPerformAction(stack, ItemAbilities.FISHING_ROD_CAST);

                        return (flag || flag1) && entity instanceof Player && ((Player) entity).fishing != null ? 1.0F : 0.0F;
                    }
        });

        // Shield
        ItemProperties.register(Registration.CRYSTAL_SHIELD.get(), ResourceLocation.withDefaultNamespace("blocking"),
                (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        EntityModelSet entityModels = event.getEntityModels();
        event.getSkins().forEach(s -> {
            LivingEntityRenderer<? extends Player, ? extends EntityModel<? extends Player>> livingEntityRenderer = event.getSkin(s);
            if (livingEntityRenderer instanceof PlayerRenderer playerRenderer){
                playerRenderer.addLayer(new CrystalElytraLayer<>(playerRenderer, entityModels));
            }
        });
        LivingEntityRenderer<ArmorStand, ? extends EntityModel<ArmorStand>> livingEntityRenderer = event.getRenderer(EntityType.ARMOR_STAND);
        if (livingEntityRenderer instanceof ArmorStandRenderer armorStandRenderer){
            armorStandRenderer.addLayer(new CrystalElytraLayer<>(armorStandRenderer, entityModels));
        }
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Registration.CRYSTAL_TRIDENT_ENTITY.get(), CrystalTridentRenderer::new);
        event.registerBlockEntityRenderer(Registration.CRYSTAL_QUARRY_BLOCK_ENTITY.get(), CrystalQuarryBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(QuarryCubeRenderer.LOCATION, QuarryCubeRenderer::createBodyLayer);
    }

    @SubscribeEvent
    public static void loadModels(ModelEvent.RegisterAdditional event) {
        event.register(CrystalTridentBlockEntityWithoutLevelRenderer.CRYSTAL_TRIDENT_MODEL_RESOURCE_LOCATION);
    }
}
