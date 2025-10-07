package dev.enjarai.rollingdowninthedeep.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.enjarai.rollingdowninthedeep.RollingDownInTheDeep;
import dev.enjarai.rollingdowninthedeep.config.SwimConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
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
                    target = "Lnet/minecraft/world/entity/Entity;isSprinting()Z",
                    ordinal = 0
            )
    )
    private boolean rollingDownInTheDeep$disableSprintCheck(Entity instance, Operation<Boolean> original) {
        if (SwimConfig.INSTANCE.persistentSwimming) {
            // Override a client-side check that would usually send a packet to
            // stop the player from being in swim mode without sprinting.
            return instance instanceof LocalPlayer &&
                    RollingDownInTheDeep.enabled() ||
                    original.call(instance);
        } else {
            return original.call(instance);
        }
    }

    @SuppressWarnings("ConstantValue")
    @Inject(
        method = "moveRelative",
        at = @At("HEAD"),
        cancellable = true
    )
    private void rollingDownInTheDeep$useCustomVelocity(float speed, Vec3 movementInput, CallbackInfo ci) {
        if ((Object) this instanceof LocalPlayer clientPlayer && RollingDownInTheDeep.shouldRoll()) {
            clientPlayer.setDeltaMovement(clientPlayer.getDeltaMovement().add(
                new Vec3(RollingDownInTheDeep.movementInputToVelocity(
                    clientPlayer, movementInput.toVector3f(), speed)
                ))
            );
            ci.cancel();
        }
    }
}
