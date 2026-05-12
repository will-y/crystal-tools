package dev.willyelton.crystal.tools.common.events;

import dev.willyelton.crystal.core.Registration;
import dev.willyelton.crystal.core.common.skill.attachment.EntitySkillData;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import dev.willyelton.crystal.tools.ModRegistration;
import dev.willyelton.crystal.tools.common.components.DataComponents;
import dev.willyelton.crystal.tools.common.levelable.MobCaptureTool;
import dev.willyelton.crystal.tools.utils.constants.EntitySkills;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

@EventBusSubscriber(modid = ApiConstants.CORE_MOD_ID)
public class LivingDeathEvent {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void handle(net.neoforged.neoforge.event.entity.living.LivingDeathEvent event) {
        Entity deadEntity = event.getEntity();

        if (reviveEntity(deadEntity)) {
            event.setCanceled(true);
        }
    }

    private static void spawnItem(ServerLevel level, Entity entity, ItemStack stack) {
        level.addFreshEntity(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), stack));
    }

    private static boolean reviveEntity(Entity deadEntity) {
        if (deadEntity instanceof LivingEntity livingEntity && livingEntity.level() instanceof ServerLevel serverLevel) {
            EntitySkillData entitySkillData = livingEntity.getData(Registration.ENTITY_SKILL);
            if (entitySkillData.hasSkill(EntitySkills.IMMORTALITY)) {
                livingEntity.setHealth(livingEntity.getMaxHealth() / 2);
                ItemStack cageItem = new ItemStack(ModRegistration.CRYSTAL_DOG_CAGE);
                MobCaptureTool mobCaptureTool = (MobCaptureTool) cageItem.getItem();
                mobCaptureTool.captureMob(cageItem, serverLevel, null, livingEntity, true);
                cageItem.set(DataComponents.BREAK_CAGE_ON_USE, true);

                if (livingEntity instanceof TamableAnimal tamableAnimal) {
                    LivingEntity owner = tamableAnimal.getOwner();
                    if (owner instanceof Player player) {
                        player.sendOverlayMessage(Component.translatable("tooltip.crystal_tools.pet_died", tamableAnimal.getDisplayName(),
                                        (int) deadEntity.getX(), (int) deadEntity.getY(), (int) deadEntity.getZ()).withStyle(ChatFormatting.RED));

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
