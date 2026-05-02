package dev.willyelton.crystal.tools.client;

import dev.willyelton.crystal.tools.CrystalTools;
import dev.willyelton.crystal.tools.client.compat.guideme.GuideMeCompatibility;
import dev.willyelton.crystal.tools.client.config.CrystalToolsClientConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = CrystalTools.MODID, dist = Dist.CLIENT)
public class CrystalToolsClient {
    public CrystalToolsClient(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, CrystalToolsClientConfig.CLIENT_CONFIG);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        GuideMeCompatibility.loadGuide();
    }
}
