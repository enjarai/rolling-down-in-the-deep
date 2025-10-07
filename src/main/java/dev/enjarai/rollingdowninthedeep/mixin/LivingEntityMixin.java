package dev.enjarai.rollingdowninthedeep.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import dev.enjarai.rollingdowninthedeep.RollingDownInTheDeep;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @SuppressWarnings("ConstantConditions")
    @ModifyArg(
        method = "travel",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getFluidFallingAdjustedMovement(DZLnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"),
        index = 0
    )
    /// Nullify applyFluidMovingSpeed() by setting gravity to 0
    private double rollingDownInTheDeep$modifySwimGravity(double original) {
        return (Object) this instanceof LocalPlayer && RollingDownInTheDeep.shouldRoll() ? 0 : original;
    }

    @SuppressWarnings("ConstantConditions")
    @ModifyArg(
        method = "travel",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;", ordinal = 0),
        index = 1
    )
    /// Disable the built-in velocity attenuation for the Y-axis
    private double rollingDownInTheDeep$fixVerticalVelocity(double original, @Local(ordinal = 0) float f) {
        return (Object) this instanceof LocalPlayer && RollingDownInTheDeep.shouldRoll() ? f : original;
    }

    @SuppressWarnings("ConstantConditions")
    @WrapWithCondition(
        method = "aiStep",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;jumpInFluid(Lnet/neoforged/neoforge/fluids/FluidType;)V")
    )
    /// Cancel the upwards velocity added by holding jump
    private boolean rollingDownInTheDeep$cancelUpwardsSwim(LivingEntity instance, FluidType fluidType) {
        return !((Object) this instanceof LocalPlayer && RollingDownInTheDeep.shouldRoll());
    }
}
