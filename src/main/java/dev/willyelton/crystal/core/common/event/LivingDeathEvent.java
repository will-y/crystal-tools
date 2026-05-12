package dev.willyelton.crystal.core.common.event;

import dev.willyelton.crystal.core.common.datacomponent.DataComponents;
import dev.willyelton.crystal.core.common.datamap.CrystalCoreDataMaps;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = ApiConstants.CORE_MOD_ID)
public class LivingDeathEvent {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handle(net.neoforged.neoforge.event.entity.living.LivingDeathEvent event) {
        Entity deadEntity = event.getEntity();

        if (event.getSource().getEntity() != null) {
            ItemStack weaponStack = event.getSource().getEntity().getWeaponItem();

            if (weaponStack != null && deadEntity.level() instanceof ServerLevel serverLevel) {
                if (weaponStack.getOrDefault(DataComponents.BEHEADING, 0).floatValue() > serverLevel.getRandom().nextFloat()) {
                    Item skullItem = deadEntity.getType().builtInRegistryHolder().getData(CrystalCoreDataMaps.MOB_HEADS);
                    spawnItem(serverLevel, deadEntity, skullItem);
                }

                if (weaponStack.getOrDefault(DataComponents.CAPTURING, 0).floatValue() > serverLevel.getRandom().nextFloat()) {
                    SpawnEggItem.byId(deadEntity.getType()).ifPresent(holder -> {
                        spawnItem(serverLevel, deadEntity, holder.value());
                    });

                }
            }
        }
    }

    private static void spawnItem(ServerLevel level, Entity entity, Item item) {
        if (item != null) {
            spawnItem(level, entity, new ItemStack(item));
        }
    }

    private static void spawnItem(ServerLevel level, Entity entity, ItemStack stack) {
        level.addFreshEntity(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), stack));
    }
}
