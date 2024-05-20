package dev.willyelton.crystal_tools.model;


import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.levelable.tool.BowLevelableItem;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModelSetup {
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
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        EntityModelSet entityModels = event.getEntityModels();
        event.getSkins().forEach(s -> {
            LivingEntityRenderer<? extends Player, ? extends EntityModel<? extends Player>> livingEntityRenderer = event.getSkin(s);
            if(livingEntityRenderer instanceof PlayerRenderer playerRenderer){
                playerRenderer.addLayer(new CrystalElytraLayer(playerRenderer, entityModels));
            }
        });
        LivingEntityRenderer<ArmorStand, ? extends EntityModel<ArmorStand>> livingEntityRenderer = event.getRenderer(EntityType.ARMOR_STAND);
        if(livingEntityRenderer instanceof ArmorStandRenderer armorStandRenderer){
            armorStandRenderer.addLayer(new CrystalElytraLayer(armorStandRenderer, entityModels));
        }
    }
}
