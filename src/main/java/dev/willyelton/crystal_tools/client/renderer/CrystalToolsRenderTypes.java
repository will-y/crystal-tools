package dev.willyelton.crystal_tools.client.renderer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import static dev.willyelton.crystal_tools.CrystalTools.rl;
import static net.minecraft.client.renderer.RenderPipelines.ENTITY_SNIPPET;
import static net.minecraft.client.renderer.RenderPipelines.MATRICES_PROJECTION_SNIPPET;
import static net.minecraft.client.renderer.RenderStateShard.*;
import static net.minecraft.client.renderer.RenderType.create;

public class CrystalToolsRenderTypes {
    public static final ResourceLocation QUARRY_LASER_LOCATION = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "textures/entity/quarry_laser.png");

    public static final RenderPipeline.Snippet POSITION_COLOR_SNIPPET = RenderPipeline.builder(MATRICES_PROJECTION_SNIPPET)
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withBlend(BlendFunction.TRANSLUCENT)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withCull(false)
            .buildSnippet();

    public static RenderPipeline POSITION_COLOR_PIPELINE = RenderPipeline.builder(POSITION_COLOR_SNIPPET)
            .withLocation(rl("pipeline/block_overlay"))
            .build();

    public static RenderPipeline QUARRY_CUBE_PIPELINE = RenderPipeline.builder(ENTITY_SNIPPET)
            .withLocation(rl("pipeline/quarry_cube"))
            .withVertexShader("core/position_color_tex_lightmap")
            .withFragmentShader("core/position_color_tex_lightmap")
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withSampler("Sampler1")
            .withBlend(BlendFunction.TRANSLUCENT)
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS)
            .build();

    // Copied from the DEBUG_QUADS Render Type
    public static final RenderType BLOCK_OVERLAY = create(
            "CrystalToolsBlockOverlay",
            1536,
            false,
            true,
            POSITION_COLOR_PIPELINE,
            RenderType.CompositeState.builder()
                    .createCompositeState(false));

    public static final RenderType QUARRY_LASER = create("QuarryLaser",
            1536,
            false,
            true,
            RenderPipelines.BEACON_BEAM_TRANSLUCENT,
            RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(QUARRY_LASER_LOCATION, false))
                    .createCompositeState(true));

    public static final RenderType QUARRY_CUBE = create("QuarryCube",
            1536,
            true,
            false,
            QUARRY_CUBE_PIPELINE,
            RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "textures/entity/crystal_quarry_cube.png"), false))
                    .setLightmapState(NO_LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true));
}
