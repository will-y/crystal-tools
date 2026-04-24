package dev.willyelton.crystal_tools.client.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CrystalToolsClientConfig {
    public static final ModConfigSpec CLIENT_CONFIG;


    public static ModConfigSpec.BooleanValue DISABLE_BLOCK_TARGET_RENDERING;



    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        setupClientConfig(builder);
        CLIENT_CONFIG = builder.build();
    }

    private static void setupClientConfig(ModConfigSpec.Builder builder) {
        DISABLE_BLOCK_TARGET_RENDERING = builder.comment("Disables the block highlighting for 3x3 mining and vein mining")
                .define("disable_block_target_rendering", false);
    }
}
