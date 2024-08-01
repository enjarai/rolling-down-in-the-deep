package dev.enjarai.rollingdowninthedeep.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import nl.enjarai.doabarrelroll.api.RollEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    @Inject(
        method = "setupTransforms(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/util/math/MatrixStack;FFFF)V",
        at = @At("RETURN")
    )
    /// Apply roll to swimming players
    private void rollingDownInTheDeep$rollPlayerBody(
        AbstractClientPlayerEntity abstractClientPlayerEntity,
        MatrixStack matrixStack,
        float f, float g, float h, float i,
        CallbackInfo ci
    ) {
        if (abstractClientPlayerEntity.isInSwimmingPose()) {
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(((RollEntity) abstractClientPlayerEntity).doABarrelRoll$getRoll()));
        }
    }
}
