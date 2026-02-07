package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.client.gui.EntityUpgradeScreen;
import dev.willyelton.crystal_tools.client.gui.ModGUIs;
import dev.willyelton.crystal_tools.common.capability.LevelableEntity;
import dev.willyelton.crystal_tools.common.levelable.MobCaptureTool;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class PlayerInteractEntityEvent {

    @SubscribeEvent
    public static void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        if (addCollar(event)) {
            event.setCanceled(true);
            return;
        }

        if (captureEntity(event)) {
            event.setCanceled(true);
            return;
        }

        Player player = event.getEntity();

        if (player.isShiftKeyDown()) {
            if (event.getTarget() instanceof LivingEntity livingEntity) {
                LevelableEntity levelable = LevelableEntity.of(livingEntity, event.getLevel().registryAccess());
                if (levelable != null && levelable.checkConditions(livingEntity, player)) {
                    if (event.getLevel().isClientSide()) {
                        ModGUIs.openScreen(new EntityUpgradeScreen(livingEntity, player, levelable));
                    }

                    event.setCanceled(true);
                }
            }
        }
    }

    private static boolean addCollar(PlayerInteractEvent.EntityInteract event) {
        Entity entity = event.getTarget();
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();

        if (stack.is(ModRegistration.CRYSTAL_COLLAR) && entity instanceof Wolf wolf && wolf.isOwnedBy(player)) {
            entity.setData(ModRegistration.WOLF_COLLAR, true);
            stack.shrink(1);

            event.setCancellationResult(InteractionResult.CONSUME);
            return true;
        }

        return false;
    }

    private static boolean captureEntity(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof MobCaptureTool mobCaptureTool && event.getLevel() instanceof ServerLevel serverLevel
                && event.getEntity() instanceof ServerPlayer serverPlayer && event.getTarget() instanceof LivingEntity livingEntity) {
            return mobCaptureTool.captureMob(stack, serverLevel, serverPlayer, livingEntity);
        }

        return false;
    }
}
