package dev.willyelton.crystal_tools.client.compat.guideme;

import dev.willyelton.crystal_tools.CrystalTools;
import guideme.Guide;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

public class GuideMeCompatibility {
    public static void loadGuide() {
        if (!ModList.get().isLoaded("guideme")) {
            return;
        }

        Guide.builder(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "guide"))
                .build();
    }
}
