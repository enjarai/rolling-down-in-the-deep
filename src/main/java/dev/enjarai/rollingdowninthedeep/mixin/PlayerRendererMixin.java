package dev.enjarai.rollingdowninthedeep.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import nl.enjarai.doabarrelroll.api.RollEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
    @Inject(
        method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V",
        at = @At("RETURN")
    )
    /// Apply roll to swimming players
    private void rollingDownInTheDeep$rollPlayerBody(
            AbstractClientPlayer abstractClientPlayerEntity,
        PoseStack matrixStack,
        float f, float g, float h, float i,
        CallbackInfo ci
    ) {
        if (abstractClientPlayerEntity.isVisuallySwimming()) {
            matrixStack.mulPose(Axis.YP.rotationDegrees(((RollEntity) abstractClientPlayerEntity).doABarrelRoll$getRoll()));
        }
    }
}
