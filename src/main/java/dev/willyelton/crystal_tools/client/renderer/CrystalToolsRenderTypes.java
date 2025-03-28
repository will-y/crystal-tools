package dev.willyelton.crystal_tools.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;

import static net.minecraft.client.renderer.RenderStateShard.BLOCK_SHEET_MIPPED;
import static net.minecraft.client.renderer.RenderStateShard.LIGHTMAP;
import static net.minecraft.client.renderer.RenderStateShard.OVERLAY;
import static net.minecraft.client.renderer.RenderStateShard.TRANSLUCENT_TARGET;
import static net.minecraft.client.renderer.RenderType.create;

public class CrystalToolsRenderTypes {
    public static final ResourceLocation QUARRY_LASER_LOCATION = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "textures/entity/quarry_laser.png");


    public static final RenderType BLOCK_OVERLAY = create(
            "CrystalToolsBlockOverlay",
            256,
            false,
            false,
            RenderPipelines.TRANSLUCENT,
            RenderType.CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setTextureState(BLOCK_SHEET_MIPPED)
                    .setOutputState(TRANSLUCENT_TARGET)
                    .createCompositeState(true));

    public static final RenderType QUARRY_LASER = create("QuarryLaser",
            1536,
            false,
            true,
            RenderPipelines.BEACON_BEAM_TRANSLUCENT,
            RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(QUARRY_LASER_LOCATION, TriState.FALSE, false))
                    .createCompositeState(true));

    public static final RenderType QUARRY_CUBE = create("QuarryCube",
            1536,
            true,
            false,
            RenderPipelines.ENTITY_CUTOUT_NO_CULL,
            RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "textures/entity/crystal_quarry_cube.png"), TriState.FALSE, false))
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true));
}
