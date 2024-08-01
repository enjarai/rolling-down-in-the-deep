package dev.enjarai.rollingdowninthedeep.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.enjarai.rollingdowninthedeep.RollingDownInTheDeep;
import dev.enjarai.rollingdowninthedeep.config.SwimConfig;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Shadow
    public abstract boolean isSubmergedInWater();

    @ModifyArg(
            method = "sendSprintingPacket",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/network/packet/c2s/play/ClientCommandC2SPacket.<init>(Lnet/minecraft/entity/Entity;Lnet/minecraft/network/packet/c2s/play/ClientCommandC2SPacket$Mode;)V"
            ),
            index = 1
    )
    private ClientCommandC2SPacket.Mode rollingDownInTheDeep$modifySprintPacket(ClientCommandC2SPacket.Mode mode) {
        // Modify the packet that would normally tell the server we've stopped sprinting.
        // This lets us trick the server into letting us stay in swimming mode without having to move constantly.
        return RollingDownInTheDeep.shouldRoll() && isSubmergedInWater() && SwimConfig.INSTANCE.persistentSwimming
                ? ClientCommandC2SPacket.Mode.START_SPRINTING : mode;
    }

    @WrapWithCondition(
            method = "tickMovement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;knockDownwards()V")
    )
    /// Cancel the downwards velocity added by holding sneak
    private boolean rollingDownInTheDeep$cancelDownwardsSwim(ClientPlayerEntity instance) {
        return !RollingDownInTheDeep.shouldRoll();
    }
}
