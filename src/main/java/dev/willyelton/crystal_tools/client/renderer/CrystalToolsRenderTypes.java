package dev.willyelton.crystal_tools.client.renderer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import static dev.willyelton.crystal_tools.CrystalTools.rl;
import static net.minecraft.client.renderer.RenderPipelines.BEACON_BEAM_TRANSLUCENT;
import static net.minecraft.client.renderer.RenderPipelines.ENTITY_CUTOUT_NO_CULL;
import static net.minecraft.client.renderer.RenderPipelines.MATRICES_PROJECTION_SNIPPET;
import static net.minecraft.client.renderer.rendertype.RenderType.create;

public class CrystalToolsRenderTypes {
    public static final Identifier QUARRY_LASER_LOCATION = Identifier.fromNamespaceAndPath(CrystalTools.MODID, "textures/entity/quarry_laser.png");

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

    public static final VertexFormat POSITION_TEX_LIGHTMAP_COLOR = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("UV0", VertexFormatElement.UV0)
            .add("UV2", VertexFormatElement.UV2)
            .add("Color", VertexFormatElement.COLOR)
            .build();

//    public static RenderPipeline QUARRY_CUBE_PIPELINE = RenderPipeline.builder(MATRICES_FOG_SNIPPET)
//            .withLocation(rl("pipeline/quarry_cube"))
////            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
////            .withShaderDefine("PER_FACE_LIGHTING")
//            .withSampler("Sampler0")
//            .withSampler("Sampler2")
//            .withVertexShader(rl("position_color_tex_lightmap"))
//            .withFragmentShader(rl("position_color_tex_lightmap"))
//            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS)
//            .withCull(false)
//            .build();

    // Copied from the DEBUG_QUADS Render Type
    private static final RenderSetup BLOCK_OVERLAY_SETUP = RenderSetup.builder(POSITION_COLOR_PIPELINE)
        .bufferSize(1536)
        .sortOnUpload()
        .createRenderSetup();
    public static final RenderType BLOCK_OVERLAY = create(
            "CrystalToolsBlockOverlay",
            BLOCK_OVERLAY_SETUP);

    private static final RenderSetup QUARRY_LASER_SETUP = RenderSetup.builder(BEACON_BEAM_TRANSLUCENT)
            .bufferSize(1536)
            .sortOnUpload()
            .withTexture("Sampler0", QUARRY_LASER_LOCATION)
            .createRenderSetup();
    public static final RenderType QUARRY_LASER = create("QuarryLaser",
            QUARRY_LASER_SETUP);

    private static final RenderSetup QUARRY_CUBE_SETUP = RenderSetup.builder(ENTITY_CUTOUT_NO_CULL)
            .bufferSize(1536)
            .sortOnUpload()
            .withTexture("Sampler0", rl("textures/entity/crystal_quarry_cube.png"))
            .useOverlay()
            .createRenderSetup();
    public static final RenderType QUARRY_CUBE = create("QuarryCube",
            QUARRY_CUBE_SETUP);
}
