package dev.willyelton.crystal_tools.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CrystalToolsClientConfig {
    public static final ForgeConfigSpec CLIENT_SPEC;

    public static ForgeConfigSpec.IntValue SHIFT_POINT_SPEND;
    public static ForgeConfigSpec.IntValue CONTROL_POINT_SPEND;

    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        setupConfig(configBuilder);
        CLIENT_SPEC = configBuilder.build();
    }

    private static void setupConfig(ForgeConfigSpec.Builder builder) {
        SHIFT_POINT_SPEND = builder.comment("Number of points to spend while you are holding shift")
                .defineInRange("shift_point_spend", 10, 1, 10000);
        CONTROL_POINT_SPEND = builder.comment("Number of points to spend while you are holding control")
                .defineInRange("control_point_spend", 100, 1, 10000);
    }
}
