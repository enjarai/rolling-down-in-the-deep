package nl.enjarai.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import nl.enjarai.RollingDownInTheDeep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Shadow public abstract boolean isSubmergedInWater();

    @ModifyArg(
            method = "sendSprintingPacket",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/network/packet/c2s/play/ClientCommandC2SPacket.<init>(Lnet/minecraft/entity/Entity;Lnet/minecraft/network/packet/c2s/play/ClientCommandC2SPacket$Mode;)V"
            ),
            index = 1
    )
    private ClientCommandC2SPacket.Mode rollingDownInTheDeep$modifySprintPacket(ClientCommandC2SPacket.Mode mode) {
        return RollingDownInTheDeep.enabled() && isSubmergedInWater()
                ? ClientCommandC2SPacket.Mode.START_SPRINTING : mode;
    }
}
