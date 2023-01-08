package nl.enjarai.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import nl.enjarai.RollingDownInTheDeep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @SuppressWarnings("ConstantConditions")
    @ModifyArg(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;applyFluidMovingSpeed(DZLnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"
            ),
            index = 0
    )
    private double rollingDownInTheDeep$modifySwimGravity(double original) {
        if ((Object) this instanceof ClientPlayerEntity clientPlayer &&
                RollingDownInTheDeep.shouldRoll() &&
                clientPlayer.isSwimming()) {

            return 0;
        }

        return original;
    }
}
