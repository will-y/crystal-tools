package dev.willyelton.crystal_tools.client.renderer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
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
import static net.minecraft.client.renderer.RenderStateShard.NO_LIGHTMAP;
import static net.minecraft.client.renderer.RenderStateShard.NO_TEXTURE;
import static net.minecraft.client.renderer.RenderStateShard.OVERLAY;
import static net.minecraft.client.renderer.RenderStateShard.TRANSLUCENT_TARGET;
import static net.minecraft.client.renderer.RenderStateShard.VIEW_OFFSET_Z_LAYERING;
import static net.minecraft.client.renderer.RenderType.create;

public class CrystalToolsRenderTypes {
    public static final ResourceLocation QUARRY_LASER_LOCATION = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "textures/entity/quarry_laser.png");

    public static final RenderPipeline.Snippet POSITION_COLOR_SNIPPET = RenderPipeline.builder()
            .withCull(true)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withColorWrite(true, true)
            .buildSnippet();

    public static RenderPipeline POSITION_COLOR_PIPELINE = RenderPipeline.builder(POSITION_COLOR_SNIPPET)
            .withLocation(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "pipeline/block_overlay"))
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withSampler("Sampler0")
            .build();

    public static final RenderType BLOCK_OVERLAY = create(
            "CrystalToolsBlockOverlay",
            256,
            false,
            false,
            POSITION_COLOR_PIPELINE,
            RenderType.CompositeState.builder()
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setLightmapState(NO_LIGHTMAP)
                    .setTextureState(NO_TEXTURE)
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
