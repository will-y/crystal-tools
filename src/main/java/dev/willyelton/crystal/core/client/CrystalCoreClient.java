package dev.willyelton.crystal.core.client;

import dev.willyelton.crystal.core.client.gui.config.CrystalToolsCoreClientConfig;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ApiConstants.CORE_MOD_ID, dist = Dist.CLIENT)
public class CrystalCoreClient {
    public CrystalCoreClient(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, CrystalToolsCoreClientConfig.CLIENT_CONFIG);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
