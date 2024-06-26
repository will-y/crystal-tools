package dev.willyelton.crystal_tools.event.client;


import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.levelable.tool.BowLevelableItem;
import dev.willyelton.crystal_tools.model.CrystalElytraLayer;
import dev.willyelton.crystal_tools.renderer.CrystalTridentRenderer;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {
    @SubscribeEvent
    public static void setModelProperties(FMLClientSetupEvent event) {
        // Bow things
        ItemProperties.register(Registration.CRYSTAL_BOW.get(), new ResourceLocation("pull"), (stack, level, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                if (stack.getItem() instanceof BowLevelableItem bowItem) {
                    return entity.getUseItem() != stack ? 0.0F : (float)(stack.getUseDuration() - entity.getUseItemRemainingTicks()) / bowItem.getChargeTime(stack);
                }
                return 0;
            }
        });
        ItemProperties.register(Registration.CRYSTAL_BOW.get(), new ResourceLocation("pulling"),
                (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);

        // Trident things
        ItemProperties.register(Registration.CRYSTAL_TRIDENT.get(), new ResourceLocation("throwing"),
                (pStack, pLevel, pEntity, pSeed) -> pEntity != null && pEntity.isUsingItem() && pEntity.getUseItem() == pStack ? 1.0F : 0.0F);

        // Fishing Rod
        ItemProperties.register(Registration.CRYSTAL_FISHING_ROD.get(), new ResourceLocation("cast"),
                (stack, level, entity, seed) -> {
                    if (entity == null) {
                        return 0.0F;
                    } else {
                        boolean flag = entity.getMainHandItem() == stack;
                        boolean flag1 = entity.getOffhandItem() == stack && !entity.getMainHandItem().getItem().canPerformAction(stack, ToolActions.FISHING_ROD_CAST);

                        return (flag || flag1) && entity instanceof Player && ((Player) entity).fishing != null ? 1.0F : 0.0F;
                    }
        });
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
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Registration.CRYSTAL_TRIDENT_ENTITY.get(), CrystalTridentRenderer::new);
    }

    @SubscribeEvent
    public static void loadModels(ModelEvent.RegisterAdditional event) {
        event.register(new ResourceLocation(CrystalTools.MODID, "item/crystal_trident_inventory"));
    }
}
