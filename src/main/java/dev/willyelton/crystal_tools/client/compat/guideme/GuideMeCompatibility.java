package dev.willyelton.crystal_tools.client.compat.guideme;

import dev.willyelton.crystal_tools.CrystalTools;
import guideme.Guide;
import net.minecraft.resources.ResourceLocation;

public class GuideMeCompatibility {
    public static void loadGuide() {
        Guide.builder(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "guide"))
                .build();
    }
}
