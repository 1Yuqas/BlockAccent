package one.yuqas.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;

public final class OverlayRenderer {

    private OverlayRenderer() {}

    private static double animX, animY, animZ;
    private static double fromX, fromY, fromZ;
    private static double targetX, targetY, targetZ;
    private static float animProgress = 1.0f;
    private static float hoverProgress = 0.0f;
    private static long lastTime = System.currentTimeMillis();
    private static String currentPos = null;

    public static void reset() {
        animProgress = 1.0f;
        hoverProgress = 0.0f;
        currentPos = null;
        lastTime = System.currentTimeMillis();
    }

    public static boolean render(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            LevelRenderState levelRenderState
    ) {
        BlockOutlineRenderState state = levelRenderState.blockOutlineRenderState;
        if (state == null) return false;

        Vec3 cam = levelRenderState.cameraRenderState.pos;
        var pos = state.pos();
        VoxelShape shape = state.shape();

        long now = System.currentTimeMillis();
        float delta = (now - lastTime) / 1000.0f;
        lastTime = now;

        hoverProgress = Math.min(1.0f, hoverProgress + delta * OverlayConfig.animSpeed);

        String posKey = pos.getX() + "," + pos.getY() + "," + pos.getZ();

        if (!OverlayConfig.animEnabled) {
            currentPos = posKey;
            animX = pos.getX() + 0.5;
            animY = pos.getY() + 0.5;
            animZ = pos.getZ() + 0.5;
        } else {
            if (!posKey.equals(currentPos)) {
                fromX = currentPos != null ? animX : pos.getX() + 0.5;
                fromY = currentPos != null ? animY : pos.getY() + 0.5;
                fromZ = currentPos != null ? animZ : pos.getZ() + 0.5;

                currentPos = posKey;

                targetX = pos.getX() + 0.5;
                targetY = pos.getY() + 0.5;
                targetZ = pos.getZ() + 0.5;

                animProgress = 0.0f;
            }

            animProgress = Math.min(1.0f, animProgress + delta * OverlayConfig.animSpeed);

            float eased = OverlayAnimations.apply(
                    OverlayConfig.animationMode,
                    animProgress
            );

            animX = Mth.lerp(eased, fromX, targetX);
            animY = Mth.lerp(eased, fromY, targetY);
            animZ = Mth.lerp(eased, fromZ, targetZ);
        }

        float hover = OverlayAnimations.apply(
                OverlayConfig.animationMode,
                hoverProgress
        );

        int fColor = ARGB.colorFromFloat(
                (OverlayConfig.fillColor.getAlpha() / 255f) * hover,
                OverlayConfig.fillColor.getRed() / 255f,
                OverlayConfig.fillColor.getGreen() / 255f,
                OverlayConfig.fillColor.getBlue() / 255f
        );

        poseStack.pushPose();
        poseStack.translate(
                animX - 0.5 - cam.x,
                animY - 0.5 - cam.y,
                animZ - 0.5 - cam.z
        );

        double offX = animX - 0.5 - cam.x;
        double offY = animY - 0.5 - cam.y;
        double offZ = animZ - 0.5 - cam.z;

        RenderType fillType = OverlayConfig.throughWallsFill
                ? OverlayPipelines.FILL_THROUGH
                : OverlayPipelines.FILL_NORMAL;

        collector.submitCustomGeometry(poseStack, fillType, (rp, consumer) -> {
            renderFilledShape(poseStack, consumer, shape, offX, offY, offZ, fColor);
        });

        if (OverlayConfig.showLine) {
            int lColor = ARGB.colorFromFloat(
                    (OverlayConfig.lineColor.getAlpha() / 255f) * hover,
                    OverlayConfig.lineColor.getRed() / 255f,
                    OverlayConfig.lineColor.getGreen() / 255f,
                    OverlayConfig.lineColor.getBlue() / 255f
            );

            RenderType lineType = OverlayConfig.throughWallsLine
                    ? OverlayPipelines.LINE_THROUGH
                    : OverlayPipelines.LINE_NORMAL;

            collector.submitShapeOutline(
                    poseStack,
                    shape,
                    lineType,
                    lColor,
                    OverlayConfig.lineWidth,
                    false
            );
        }

        poseStack.popPose();
        return true;
    }

    private static void renderFilledShape(PoseStack pose, VertexConsumer consumer,
                                          VoxelShape shape, double ox, double oy, double oz,
                                          int color) {
        float a = ARGB.alpha(color) / 255f;
        float r = ARGB.red(color)   / 255f;
        float g = ARGB.green(color) / 255f;
        float b = ARGB.blue(color)  / 255f;
        Matrix4f matrix = pose.last().pose();

        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            float fx1=(float)(x1+ox), fy1=(float)(y1+oy), fz1=(float)(z1+oz);
            float fx2=(float)(x2+ox), fy2=(float)(y2+oy), fz2=(float)(z2+oz);

            quad(consumer,matrix, fx1,fy1,fz1, fx2,fy1,fz1, fx2,fy1,fz2, fx1,fy1,fz2, r,g,b,a);
            quad(consumer,matrix, fx1,fy1,fz2, fx2,fy1,fz2, fx2,fy1,fz1, fx1,fy1,fz1, r,g,b,a);
            quad(consumer,matrix, fx1,fy2,fz1, fx1,fy2,fz2, fx2,fy2,fz2, fx2,fy2,fz1, r,g,b,a);
            quad(consumer,matrix, fx2,fy2,fz1, fx2,fy2,fz2, fx1,fy2,fz2, fx1,fy2,fz1, r,g,b,a);
            quad(consumer,matrix, fx1,fy1,fz1, fx1,fy2,fz1, fx2,fy2,fz1, fx2,fy1,fz1, r,g,b,a);
            quad(consumer,matrix, fx2,fy1,fz1, fx2,fy2,fz1, fx1,fy2,fz1, fx1,fy1,fz1, r,g,b,a);
            quad(consumer,matrix, fx2,fy1,fz2, fx2,fy2,fz2, fx1,fy2,fz2, fx1,fy1,fz2, r,g,b,a);
            quad(consumer,matrix, fx1,fy1,fz2, fx1,fy2,fz2, fx2,fy2,fz2, fx2,fy1,fz2, r,g,b,a);
            quad(consumer,matrix, fx1,fy1,fz2, fx1,fy2,fz2, fx1,fy2,fz1, fx1,fy1,fz1, r,g,b,a);
            quad(consumer,matrix, fx1,fy1,fz1, fx1,fy2,fz1, fx1,fy2,fz2, fx1,fy1,fz2, r,g,b,a);
            quad(consumer,matrix, fx2,fy1,fz1, fx2,fy2,fz1, fx2,fy2,fz2, fx2,fy1,fz2, r,g,b,a);
            quad(consumer,matrix, fx2,fy1,fz2, fx2,fy2,fz2, fx2,fy2,fz1, fx2,fy1,fz1, r,g,b,a);
        });
    }

    private static void quad(VertexConsumer c, Matrix4f m,
                             float x1,float y1,float z1, float x2,float y2,float z2,
                             float x3,float y3,float z3, float x4,float y4,float z4,
                             float r,float g,float b,float a) {
        c.addVertex(m,x1,y1,z1).setColor(r,g,b,a);
        c.addVertex(m,x2,y2,z2).setColor(r,g,b,a);
        c.addVertex(m,x3,y3,z3).setColor(r,g,b,a);
        c.addVertex(m,x4,y4,z4).setColor(r,g,b,a);
    }
}