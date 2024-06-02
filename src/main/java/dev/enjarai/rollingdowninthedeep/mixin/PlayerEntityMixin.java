package dev.enjarai.rollingdowninthedeep.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import dev.enjarai.rollingdowninthedeep.RollingDownInTheDeep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @WrapWithCondition(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"
            )
    )
    private boolean rollingDownInTheDeep$disableJankyVerticalSwimVelocity(PlayerEntity instance, Vec3d vec3d) {
        // Disables vertical velocity that is applied separately from horizontal velocity with vanilla swimming
        // We handle vertical velocity ourselves in RollingDownInTheDeep#handleSwimVelocity
        return !(instance instanceof ClientPlayerEntity && RollingDownInTheDeep.shouldRoll());
    }

    @SuppressWarnings("ConstantValue")
    @ModifyExpressionValue(
        method = "adjustMovementForSneaking",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;clipAtLedge()Z")
    )
    /// Disable ledge grabbing
    private boolean rollingDownInTheDeep$disableLedgeGrabbing(boolean original) {
        return (!((Object) this instanceof ClientPlayerEntity clientPlayer) || !RollingDownInTheDeep.shouldRoll()) && original;
    }
}
