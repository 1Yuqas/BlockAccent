package one.yuqas.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import one.yuqas.overlay.OverlayConfig;
import one.yuqas.overlay.OverlayRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "submitBlockOutline", at = @At("HEAD"), cancellable = true)
    private void onSubmitBlockOutline(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, LevelRenderState levelRenderState, CallbackInfo ci) {
        if (!OverlayConfig.enabled) return;

        Minecraft mc = Minecraft.getInstance();
        HitResult hit = mc.hitResult;

        BlockPos targetPos = null;

        boolean crystalHit = hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof EndCrystal;

        if (crystalHit) {
            EndCrystal crystal = (EndCrystal) ((EntityHitResult) hit).getEntity();
            targetPos = crystal.blockPosition().below();
        } else if (levelRenderState.blockOutlineRenderState != null) {
            targetPos = levelRenderState.blockOutlineRenderState.pos();
        }

        if (targetPos == null) return;

        var state = mc.level.getBlockState(targetPos);
        boolean validBase = state.is(Blocks.OBSIDIAN) || state.is(Blocks.BEDROCK);

        if (OverlayConfig.onlyCrystalBase && !validBase && !crystalHit) return;

        try {
            var field = levelRenderState.getClass().getDeclaredField("blockOutlineRenderState");
            field.setAccessible(true);
            VoxelShape realShape = state.getShape(mc.level, targetPos);
            if (realShape.isEmpty()) realShape = Shapes.block();

            field.set(levelRenderState, new BlockOutlineRenderState(targetPos, false, false, realShape));

            if (OverlayRenderer.render(poseStack, submitNodeCollector, levelRenderState)) {
                ci.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}