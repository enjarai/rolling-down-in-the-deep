package dev.enjarai.rollingdowninthedeep.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.enjarai.rollingdowninthedeep.RollingDownInTheDeep;
import dev.enjarai.rollingdowninthedeep.config.SwimConfig;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @WrapOperation(
            method = "updateSwimming",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;isSprinting()Z",
                    ordinal = 0
            )
    )
    private boolean rollingDownInTheDeep$disableSprintCheck(Entity instance, Operation<Boolean> original) {
        if (SwimConfig.INSTANCE.persistentSwimming) {
            // Override a client-side check that would usually send a packet to
            // stop the player from being in swim mode without sprinting.
            return instance instanceof ClientPlayerEntity &&
                    RollingDownInTheDeep.enabled() ||
                    original.call(instance);
        } else {
            return original.call(instance);
        }
    }

    @SuppressWarnings("ConstantValue")
    @Inject(
        method = "updateVelocity",
        at = @At("HEAD"),
        cancellable = true
    )
    private void rollingDownInTheDeep$useCustomVelocity(float speed, Vec3d movementInput, CallbackInfo ci) {
        if ((Object) this instanceof ClientPlayerEntity clientPlayer && RollingDownInTheDeep.shouldRoll()) {
            clientPlayer.setVelocity(clientPlayer.getVelocity().add(
                new Vec3d(RollingDownInTheDeep.movementInputToVelocity(
                    clientPlayer, movementInput.toVector3f(), speed)
                ))
            );
            ci.cancel();
        }
    }
}
