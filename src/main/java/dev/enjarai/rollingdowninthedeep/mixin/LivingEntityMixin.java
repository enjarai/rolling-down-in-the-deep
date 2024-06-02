package dev.enjarai.rollingdowninthedeep.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import dev.enjarai.rollingdowninthedeep.RollingDownInTheDeep;
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
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;applyFluidMovingSpeed(DZLnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"
            ),
            index = 0
    )
    private double rollingDownInTheDeep$modifySwimGravity(double original) {
        // Remove most of the gravity that is applied to swimming players,
        // allowing them to be suspended in water more easily
        if ((Object) this instanceof ClientPlayerEntity clientPlayer &&
                RollingDownInTheDeep.shouldRoll() &&
                clientPlayer.isSwimming()) {

            return 0;
        }

        return original;
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
