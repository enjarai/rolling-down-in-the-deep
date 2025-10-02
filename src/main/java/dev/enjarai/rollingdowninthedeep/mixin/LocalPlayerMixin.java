package dev.enjarai.rollingdowninthedeep.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.enjarai.rollingdowninthedeep.RollingDownInTheDeep;
import dev.enjarai.rollingdowninthedeep.config.SwimConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Shadow
    public abstract boolean isUnderWater();

    @ModifyArg(
            method = "sendIsSprintingIfNeeded",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/network/protocol/game/ServerboundPlayerCommandPacket.<init>(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/protocol/game/ServerboundPlayerCommandPacket$Action;)V"
            ),
            index = 1
    )
    private ServerboundPlayerCommandPacket.Action rollingDownInTheDeep$modifySprintPacket(ServerboundPlayerCommandPacket.Action action) {
        // Modify the packet that would normally tell the server we've stopped sprinting.
        // This lets us trick the server into letting us stay in swimming mode without having to move constantly.
        return RollingDownInTheDeep.shouldRoll() && this.isUnderWater() && SwimConfig.INSTANCE.persistentSwimming
                ? ServerboundPlayerCommandPacket.Action.START_SPRINTING : action;
    }

    @WrapWithCondition(
            method = "aiStep",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;sinkInFluid(Lnet/neoforged/neoforge/fluids/FluidType;)V")
    )
    /// Cancel the downwards velocity added by holding sneak
    private boolean rollingDownInTheDeep$cancelDownwardsSwim(LocalPlayer instance, FluidType fluidType) {
        return !RollingDownInTheDeep.shouldRoll();
    }
}
