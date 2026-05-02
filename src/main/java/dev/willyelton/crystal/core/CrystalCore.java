package dev.willyelton.crystal.core;

import dev.willyelton.crystal.core.common.config.CrystalToolsCoreConfig;
import dev.willyelton.crystal.core.common.skill.requirement.SkillDataRequirements;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ApiConstants.CORE_MOD_ID)
public class CrystalCore {

    public static final Logger LOGGER = LogManager.getLogger();

    public CrystalCore(IEventBus modEventBus, ModContainer container) {
        Registration.init(modEventBus);

        container.registerConfig(ModConfig.Type.COMMON, CrystalToolsCoreConfig.COMMON_CONFIG, "crystal_core.toml");

        // TODO: this should be like actions as well
        SkillDataRequirements.bootstrap();
    }
}
