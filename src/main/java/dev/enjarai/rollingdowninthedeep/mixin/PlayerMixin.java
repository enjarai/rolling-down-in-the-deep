package dev.enjarai.rollingdowninthedeep.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.enjarai.rollingdowninthedeep.RollingDownInTheDeep;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @WrapWithCondition(
        method = "travel",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V")
    )
    /// Nullify problematic velocity calculations
    private boolean rollingDownInTheDeep$nullifySwimVelocity(Player instance, Vec3 vec3) {
        return !(instance instanceof LocalPlayer && RollingDownInTheDeep.shouldRoll());
    }

    @SuppressWarnings("ConstantValue")
    @ModifyExpressionValue(
        method = "maybeBackOffFromEdge",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isStayingOnGroundSurface()Z")
    )
    /// Disable ledge grabbing
    private boolean rollingDownInTheDeep$disableLedgeGrabbing(boolean original) {
        return (!((Object) this instanceof LocalPlayer clientPlayer) || !RollingDownInTheDeep.shouldRoll()) && original;
    }
}
