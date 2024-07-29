package dev.willyelton.crystal_tools.client.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CrystalToolsClientConfig {
    public static final ModConfigSpec CLIENT_SPEC;

    public static ModConfigSpec.ConfigValue<String> UPGRADE_SCREEN_BACKGROUND;
    public static ModConfigSpec.DoubleValue BACKGROUND_OPACITY;
    public static ModConfigSpec.BooleanValue DISABLE_BLOCK_TARGET_RENDERING;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        setupClientConfig(builder);
        CLIENT_SPEC = builder.build();

    }

    private static void setupClientConfig(ModConfigSpec.Builder builder) {
        UPGRADE_SCREEN_BACKGROUND = builder.comment("The block texture to use for the background of the upgrade screen. Can either be a vanilla block name (Ex: cracked_deepslate_tiles), or a modded block name (Ex: crystal_tools:crystal_block)")
                .define("upgrade_screen_background", "cracked_deepslate_tiles");
        BACKGROUND_OPACITY = builder.comment("Controls the opacity of the skill tree background")
                .defineInRange("background_opacity", 0.1F, 0, 1.0F);
        DISABLE_BLOCK_TARGET_RENDERING = builder.comment("Disables the block highlighting for 3x3 mining")
                .define("disable_block_target_rendering", false);
    }
}
