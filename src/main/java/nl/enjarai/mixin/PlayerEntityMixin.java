package nl.enjarai.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import nl.enjarai.RollingDownInTheDeep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @SuppressWarnings("ConstantConditions")
    @ModifyArg(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"
            ),
            index = 1
    )
    private double rollingDownInTheDeep$modifyVerticalSwimVelocity(double original) {
        if ((Object) this instanceof ClientPlayerEntity clientPlayer && RollingDownInTheDeep.shouldRoll()) {
            var forwardMovement = clientPlayer.input.hasForwardMovement();
            var sprinting = clientPlayer.isSprinting();
            var multiplier = sprinting ? 1 : forwardMovement ? 0.5 : 0;

            return original * multiplier;
        }

        return original;
    }
}
