package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.datamap.DataMaps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class LivingDeathEvent {
    @SubscribeEvent
    public static void handle(net.neoforged.neoforge.event.entity.living.LivingDeathEvent event) {
        Entity deadEntity = event.getEntity();
        if (event.getSource().getEntity() != null) {
            ItemStack weaponStack = event.getSource().getEntity().getWeaponItem();

            if (weaponStack != null && deadEntity.level() instanceof ServerLevel serverLevel) {
                if (weaponStack.getOrDefault(DataComponents.BEHEADING, 0).floatValue() > serverLevel.getRandom().nextFloat()) {
                    Item skullItem = deadEntity.getType().builtInRegistryHolder().getData(DataMaps.MOB_SKULLS);
                    spawnItem(serverLevel, deadEntity, skullItem);
                }

                if (weaponStack.getOrDefault(DataComponents.CAPTURING, 0).floatValue() > serverLevel.getRandom().nextFloat()) {
                    SpawnEggItem spawnEggItem = SpawnEggItem.byId(deadEntity.getType());
                    spawnItem(serverLevel, deadEntity, spawnEggItem);
                }
            }
        }
    }

    private static void spawnItem(ServerLevel level, Entity entity, Item item) {
        if (item != null) {
            level.addFreshEntity(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(item)));
        }
    }
}
