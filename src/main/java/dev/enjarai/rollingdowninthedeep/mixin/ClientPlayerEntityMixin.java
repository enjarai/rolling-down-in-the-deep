package dev.enjarai.rollingdowninthedeep.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.enjarai.rollingdowninthedeep.RollingDownInTheDeep;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @WrapWithCondition(
        method = "tickMovement",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;knockDownwards()V")
    )
    /// Cancel the downwards velocity added by holding sneak
    private boolean rollingDownInTheDeep$cancelDownwardsSwim(ClientPlayerEntity instance) {
        return !RollingDownInTheDeep.shouldRoll();
    }
}
