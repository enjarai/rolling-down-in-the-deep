package dev.enjarai.rollingdowninthedeep.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import dev.enjarai.rollingdowninthedeep.RollingDownInTheDeep;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @SuppressWarnings("ConstantConditions")
    @ModifyArg(
        method = "travel",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyFluidMovingSpeed(DZLnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"),
        index = 0
    )
    /// Nullify applyFluidMovingSpeed() by setting gravity to 0
    private double rollingDownInTheDeep$modifySwimGravity(double original) {
        return (Object) this instanceof ClientPlayerEntity && RollingDownInTheDeep.shouldRoll() ? 0 : original;
    }

    @SuppressWarnings("ConstantConditions")
    @ModifyArg(
        method = "travel",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;multiply(DDD)Lnet/minecraft/util/math/Vec3d;", ordinal = 0),
        index = 1
    )
    /// Disable the built-in velocity attenuation for the Y-axis
    private double rollingDownInTheDeep$fixVerticalVelocity(double original, @Local(ordinal = 0) float f) {
        return (Object) this instanceof ClientPlayerEntity && RollingDownInTheDeep.shouldRoll() ? f : original;
    }

    @SuppressWarnings("ConstantConditions")
    @WrapWithCondition(
        method = "tickMovement",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;swimUpward(Lnet/minecraft/registry/tag/TagKey;)V")
    )
    /// Cancel the upwards velocity added by holding jump
    private boolean rollingDownInTheDeep$cancelUpwardsSwim(LivingEntity instance, TagKey<Fluid> fluid) {
        return !((Object) this instanceof ClientPlayerEntity && RollingDownInTheDeep.shouldRoll());
    }
}
