package one.yuqas.overlay;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.BlendFactor;
import com.mojang.blaze3d.platform.CompareOp;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public final class OverlayPipelines {

    public static final RenderPipeline FILL_NORMAL_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation("pipeline/block_overlay_fill_normal")
                    .withCull(false)
                    .build()
    );

    public static final RenderPipeline FILL_THROUGH_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation("pipeline/block_overlay_fill_through")
                    .withColorTargetState(
                            new ColorTargetState(
                                    new BlendFunction(
                                            BlendFactor.SRC_ALPHA,
                                            BlendFactor.ONE_MINUS_SRC_ALPHA,
                                            BlendFactor.ZERO,
                                            BlendFactor.ONE
                                    )
                            )
                    )
                    .withDepthStencilState(
                            new DepthStencilState(
                                    CompareOp.ALWAYS_PASS,
                                    false
                            )
                    )
                    .build()
    );

    public static final RenderPipeline LINE_NORMAL_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                    .withLocation("pipeline/block_overlay_line_normal")
                    .build()
    );

    public static final RenderPipeline LINE_THROUGH_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                    .withLocation("pipeline/block_overlay_line_through")
                    .withColorTargetState(
                            new ColorTargetState(
                                    new BlendFunction(
                                            BlendFactor.SRC_ALPHA,
                                            BlendFactor.ONE_MINUS_SRC_ALPHA,
                                            BlendFactor.ZERO,
                                            BlendFactor.ONE
                                    )
                            )
                    )
                    .withDepthStencilState(
                            new DepthStencilState(
                                    CompareOp.ALWAYS_PASS,
                                    false
                            )
                    )
                    .build()
    );

    public static final RenderType FILL_NORMAL = RenderType.create(
            "block_overlay_fill_normal",
            RenderSetup.builder(FILL_NORMAL_PIPELINE)
                    .sortOnUpload()
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .createRenderSetup()
    );

    public static final RenderType FILL_THROUGH = RenderType.create(
            "block_overlay_fill_through",
            RenderSetup.builder(FILL_THROUGH_PIPELINE)
                    .sortOnUpload()
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .createRenderSetup()

    );

    public static final RenderType LINE_NORMAL = RenderType.create(
            "block_overlay_line_normal",
            RenderSetup.builder(LINE_NORMAL_PIPELINE)
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .createRenderSetup()
    );

    public static final RenderType LINE_THROUGH = RenderType.create(
            "block_overlay_line_through",
            RenderSetup.builder(LINE_THROUGH_PIPELINE)
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)

                    .createRenderSetup()
    );

    private OverlayPipelines() {
        throw new UnsupportedOperationException("Utility class");
    }
}