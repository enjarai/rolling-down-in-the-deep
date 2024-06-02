package dev.enjarai.rollingdowninthedeep.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import dev.enjarai.rollingdowninthedeep.RollingDownInTheDeep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Shadow public abstract boolean isSubmergedInWater();


    @WrapWithCondition(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;knockDownwards()V"
            )
    )
    private boolean rollingDownInTheDeep$disableSneakEqualsDown(ClientPlayerEntity instance) {
        // Disable the downwards momentum applied when sneaking in water
        return !RollingDownInTheDeep.enabled() || !instance.isSwimming();
    }
}
