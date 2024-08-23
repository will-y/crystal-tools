package dev.willyelton.crystal_tools.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

// TODO: There should probably be more things here
public class CrystalToolsServerConfig {
    public static final ModConfigSpec SERVER_CONFIG;

    public static ModConfigSpec.IntValue CREATIVE_FLIGHT_POINTS;

    static {
        ModConfigSpec.Builder configBuilder = new ModConfigSpec.Builder();
        setupConfig(configBuilder);
        SERVER_CONFIG = configBuilder.build();
    }

    private static void setupConfig(ModConfigSpec.Builder builder) {
        CREATIVE_FLIGHT_POINTS = builder.comment("The number of points you need to have in the creative flight node for it to enable")
                .defineInRange("creative_flight_points", 100, 1, 1000000);
    }
}
