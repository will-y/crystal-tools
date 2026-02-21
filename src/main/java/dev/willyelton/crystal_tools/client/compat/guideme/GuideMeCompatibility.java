package dev.willyelton.crystal_tools.client.compat.guideme;

import dev.willyelton.crystal_tools.CrystalTools;
import guideme.Guide;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModList;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

public class GuideMeCompatibility {
    public static void loadGuide() {
        if (!ModList.get().isLoaded("guideme")) {
            return;
        }

        Guide.builder(rl("guide"))
                .build();
    }
}
