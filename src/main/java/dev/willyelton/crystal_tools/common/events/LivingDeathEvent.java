package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.datamap.DataMaps;
import dev.willyelton.crystal_tools.common.levelable.MobCaptureTool;
import dev.willyelton.crystal_tools.common.levelable.skill.attachment.EntitySkillData;
import dev.willyelton.crystal_tools.utils.constants.EntitySkills;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class LivingDeathEvent {
    @SubscribeEvent
    public static void handle(net.neoforged.neoforge.event.entity.living.LivingDeathEvent event) {
        Entity deadEntity = event.getEntity();

        if (reviveEntity(deadEntity)) {
            event.setCanceled(true);
            return;
        }

        if (event.getSource().getEntity() != null) {
            ItemStack weaponStack = event.getSource().getEntity().getWeaponItem();

            if (weaponStack != null && deadEntity.level() instanceof ServerLevel serverLevel) {
                if (weaponStack.getOrDefault(DataComponents.BEHEADING, 0).floatValue() > serverLevel.getRandom().nextFloat()) {
                    Item skullItem = deadEntity.getType().builtInRegistryHolder().getData(DataMaps.MOB_HEADS);
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
            spawnItem(level, entity, new ItemStack(item));
        }
    }

    private static void spawnItem(ServerLevel level, Entity entity, ItemStack stack) {
        level.addFreshEntity(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), stack));
    }

    private static boolean reviveEntity(Entity deadEntity) {
        if (deadEntity instanceof LivingEntity livingEntity && livingEntity.level() instanceof ServerLevel serverLevel) {
            EntitySkillData entitySkillData = livingEntity.getData(ModRegistration.ENTITY_SKILL);
            if (entitySkillData.hasSkill(EntitySkills.IMMORTALITY)) {
                livingEntity.setHealth(livingEntity.getMaxHealth() / 2);
                ItemStack cageItem = new ItemStack(ModRegistration.CRYSTAL_DOG_CAGE);
                MobCaptureTool mobCaptureTool = (MobCaptureTool) cageItem.getItem();
                mobCaptureTool.captureMob(cageItem, serverLevel, null, livingEntity, true);
                cageItem.set(DataComponents.BREAK_CAGE_ON_USE, true);

                if (livingEntity instanceof TamableAnimal tamableAnimal) {
                    LivingEntity owner = tamableAnimal.getOwner();
                    if (owner instanceof Player player) {
                        player.displayClientMessage(Component.translatable("tooltip.crystal_tools.pet_died", tamableAnimal.getDisplayName(),
                                        (int) deadEntity.getX(), (int) deadEntity.getY(), (int) deadEntity.getZ()).withStyle(ChatFormatting.RED),
                                true);

                        var handler = owner.getCapability(Capabilities.Item.ENTITY);

                        if (handler != null) {
                            try (Transaction tx = Transaction.openRoot()) {
                                if (handler.insert(ItemResource.of(cageItem), 1, tx) > 0) {
                                    tx.commit();
                                    return true;
                                }
                            }
                        }
                    }
                }

                spawnItem(serverLevel, deadEntity, cageItem);

                return true;
            }
        }

        return false;
    }
}
