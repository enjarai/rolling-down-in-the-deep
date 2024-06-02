package dev.enjarai.rollingdowninthedeep.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import dev.enjarai.rollingdowninthedeep.RollingDownInTheDeep;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract void setVelocity(Vec3d velocity);

    @Shadow public abstract Vec3d getVelocity();


    @SuppressWarnings("ConstantValue")
    @Inject(
            method = "updateVelocity",
            at = @At("HEAD"),
            cancellable = true
    )
    private void rollingDownInTheDeep$fixJankySwimVelocity(float speed, Vec3d movementInput, CallbackInfo ci) {
        // Redirect input -> velocity handling to our own method during swimming
        if ((Object) this instanceof ClientPlayerEntity clientPlayer &&
                RollingDownInTheDeep.shouldRoll() &&
                clientPlayer.isSwimming()) {

            var vec = RollingDownInTheDeep.handleSwimVelocity(clientPlayer,
                    new Vector3d(movementInput.x, movementInput.y, movementInput.z), speed);
            setVelocity(getVelocity().add(vec.x, vec.y, vec.z));

            ci.cancel();
        }
    }
}
