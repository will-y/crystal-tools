package dev.willyelton.crystal.tools.client.renderer;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.willyelton.crystal.tools.CrystalTools;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import static dev.willyelton.crystal.tools.CrystalTools.rl;
import static net.minecraft.client.renderer.RenderPipelines.BEACON_BEAM_TRANSLUCENT;
import static net.minecraft.client.renderer.RenderPipelines.ENTITY_CUTOUT;
import static net.minecraft.client.renderer.RenderPipelines.GLOBALS_SNIPPET;
import static net.minecraft.client.renderer.rendertype.RenderType.create;

public class CrystalToolsRenderTypes {
    public static final Identifier QUARRY_LASER_LOCATION = Identifier.fromNamespaceAndPath(CrystalTools.MODID, "textures/entity/quarry_laser.png");

    public static final RenderPipeline.Snippet POSITION_COLOR_SNIPPET = RenderPipeline.builder(GLOBALS_SNIPPET)
            .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
            .withCull(false)
            .buildSnippet();

    public static RenderPipeline POSITION_COLOR_PIPELINE = RenderPipeline.builder(POSITION_COLOR_SNIPPET)
            .withLocation(rl("pipeline/block_overlay"))
            .withCull(false)
            .build();

//    public static final VertexFormat POSITION_TEX_LIGHTMAP_COLOR = VertexFormat.builder()
//            .add("Position", VertexFormatElement.POSITION)
//            .add("UV0", VertexFormatElement.UV0)
//            .add("UV2", VertexFormatElement.UV2)
//            .add("Color", VertexFormatElement.COLOR)
//            .build();

    // Copied from the DEBUG_QUADS Render Type
    private static final RenderSetup BLOCK_OVERLAY_SETUP = RenderSetup.builder(POSITION_COLOR_PIPELINE)
        .sortOnUpload()
        .createRenderSetup();

    public static final RenderType BLOCK_OVERLAY = create(
            "CrystalToolsBlockOverlay",
            BLOCK_OVERLAY_SETUP);

    private static final RenderSetup QUARRY_LASER_SETUP = RenderSetup.builder(BEACON_BEAM_TRANSLUCENT)
            .sortOnUpload()
            .withTexture("Sampler0", QUARRY_LASER_LOCATION)
            .createRenderSetup();
    public static final RenderType QUARRY_LASER = create("QuarryLaser",
            QUARRY_LASER_SETUP);

    private static final RenderSetup QUARRY_CUBE_SETUP = RenderSetup.builder(ENTITY_CUTOUT)
            .sortOnUpload()
            .withTexture("Sampler0", rl("textures/entity/crystal_quarry_cube.png"))
            .useOverlay()
            .createRenderSetup();
    public static final RenderType QUARRY_CUBE = create("QuarryCube",
            QUARRY_CUBE_SETUP);
}
