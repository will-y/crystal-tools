package dev.willyelton.crystal_tools.model;


import dev.willyelton.crystal_tools.item.tool.ModTools;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = "crystal_tools", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModelSetup {
    @SubscribeEvent
    public static void setModelProperties(FMLClientSetupEvent event) {
//        ItemProperties.register(ModTools.CRYSTAL_BOW.get(), new ResourceLocation("pull"), (itemstack, world, entity, something) -> {
//            if (entity == null) {
//                return 0.0F;
//            } else {
//                return entity.getUseItem() != itemstack ? 0.0F : (float) (itemstack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
//            }
//        });
//        ItemProperties.register(ModTools.CRYSTAL_BOW.get(), new ResourceLocation("pulling"),
//                (itemstack, world, entity, something) -> entity != null && entity.isUsingItem() && entity.getUseItem() == itemstack ? 1.0F : 0.0F);

        ItemProperties.register(ModTools.CRYSTAL_BOW.get(), new ResourceLocation("pull"), (p_174635_, p_174636_, p_174637_, p_174638_) -> {
            if (p_174637_ == null) {
                return 0.0F;
            } else {
                return p_174637_.getUseItem() != p_174635_ ? 0.0F : (float)(p_174635_.getUseDuration() - p_174637_.getUseItemRemainingTicks()) / 20.0F;
            }
        });
        ItemProperties.register(ModTools.CRYSTAL_BOW.get(), new ResourceLocation("pulling"), (p_174630_, p_174631_, p_174632_, p_174633_) -> {
            return p_174632_ != null && p_174632_.isUsingItem() && p_174632_.getUseItem() == p_174630_ ? 1.0F : 0.0F;
        });
    }
}
