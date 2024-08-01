package dev.enjarai.rollingdowninthedeep;

import dev.enjarai.rollingdowninthedeep.config.SwimConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.Smoother;
import nl.enjarai.doabarrelroll.api.event.RollContext;
import nl.enjarai.doabarrelroll.api.rotation.RotationInstant;

public class StrafeRollModifiers {
    public static final Smoother STRAFE_ROLL_SMOOTHER = new Smoother();
    public static final Smoother STRAFE_YAW_SMOOTHER = new Smoother();

    public static RotationInstant applyStrafeRoll(RotationInstant rotationInstant, RollContext context) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return rotationInstant;

        GameOptions options = MinecraftClient.getInstance().options;
        double rollDelta = 0;
        double yawDelta = 0;

        double speedMult;
        if (SwimConfig.INSTANCE.velocityEnable) {
            speedMult = 1 + (Math.clamp(
                // 0.5 / Base Velocity = 2.835
                player.getVelocity().length() * 2.835,
                SwimConfig.INSTANCE.velocityMin,
                SwimConfig.INSTANCE.velocityMax
            ) * SwimConfig.INSTANCE.velocityScale);
        } else {
            speedMult = 1.0;
        }
        double velocityStrength = 50 * speedMult;

        if (options.leftKey.isPressed() && !options.rightKey.isPressed()) {
            rollDelta = -SwimConfig.INSTANCE.strafeRollStrength;
            yawDelta = -SwimConfig.INSTANCE.strafeYawStrength;
        } else if (options.rightKey.isPressed() && !options.leftKey.isPressed()) {
            rollDelta = SwimConfig.INSTANCE.strafeRollStrength;
            yawDelta = SwimConfig.INSTANCE.strafeYawStrength;
        }
        rollDelta *= velocityStrength;
        yawDelta *= velocityStrength;

        if (SwimConfig.INSTANCE.smoothing.strafeSmoothingEnabled) {
            rollDelta = STRAFE_ROLL_SMOOTHER.smooth(rollDelta,
                1 / SwimConfig.INSTANCE.smoothing.values.roll * context.getRenderDelta());
            yawDelta = STRAFE_YAW_SMOOTHER.smooth(yawDelta,
                1 / SwimConfig.INSTANCE.smoothing.values.yaw * context.getRenderDelta());
        }

        return rotationInstant.add(0, yawDelta * context.getRenderDelta(), rollDelta * context.getRenderDelta());
    }
}
