package dev.enjarai.rollingdowninthedeep.mixin;

import dev.enjarai.rollingdowninthedeep.RollingDownInTheDeep;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
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
