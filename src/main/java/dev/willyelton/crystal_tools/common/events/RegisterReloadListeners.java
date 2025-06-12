package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.levelable.SkillTreeCache;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

import java.util.concurrent.CompletableFuture;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

// TODO: Remove or use before deployment
@EventBusSubscriber(modid = CrystalTools.MODID)
public class RegisterReloadListeners {
    @SubscribeEvent
    public static void registerReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(rl("invalidate_skilltree_cache"), (barrier, manager, backgroundExecutor, gameExecutor) -> {
            SkillTreeCache.invalidate();
            return CompletableFuture.supplyAsync(() -> null, backgroundExecutor)
                    .thenCompose(barrier::wait)
                    .thenAcceptAsync(o -> {
                        SkillTreeCache.invalidate();
                    }, gameExecutor);
        });
    }
}
