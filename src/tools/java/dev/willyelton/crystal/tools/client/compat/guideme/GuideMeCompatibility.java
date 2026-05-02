package dev.willyelton.crystal.tools.client.compat.guideme;

import guideme.Guide;
import net.neoforged.fml.ModList;

import static dev.willyelton.crystal.tools.CrystalTools.rl;

public class GuideMeCompatibility {
    public static void loadGuide() {
        if (!ModList.get().isLoaded("guideme")) {
            return;
        }

        Guide.builder(rl("guide"))
                .build();
    }
}
