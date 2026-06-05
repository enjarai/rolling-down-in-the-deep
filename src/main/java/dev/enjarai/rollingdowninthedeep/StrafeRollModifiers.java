package dev.enjarai.rollingdowninthedeep;

import dev.enjarai.rollingdowninthedeep.config.SwimConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.SmoothDouble;
import nl.enjarai.doabarrelroll.api.event.RollContext;
import nl.enjarai.doabarrelroll.api.rotation.RotationInstant;

public class StrafeRollModifiers {
    public static final SmoothDouble STRAFE_ROLL_SMOOTHER = new SmoothDouble();
    public static final SmoothDouble STRAFE_YAW_SMOOTHER = new SmoothDouble();

    public static RotationInstant applyStrafeRoll(RotationInstant rotationInstant, RollContext context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return rotationInstant;

        Options options = Minecraft.getInstance().options;
        double rollDelta = 0;
        double yawDelta = 0;

        double speedMult;
        if (SwimConfig.INSTANCE.velocityEnable) {
            speedMult = 1 + (Math.clamp(
                // 0.5 / Base Velocity = 2.835
                player.getDeltaMovement().length() * 2.835,
                SwimConfig.INSTANCE.velocityMin,
                SwimConfig.INSTANCE.velocityMax
            ) * SwimConfig.INSTANCE.velocityScale);
        } else {
            speedMult = 1.0;
        }
        double velocityStrength = 50 * speedMult;

        if (options.keyLeft.isDown() && !options.keyRight.isDown()) {
            rollDelta = -SwimConfig.INSTANCE.strafeRollStrength;
            yawDelta = -SwimConfig.INSTANCE.strafeYawStrength;
        } else if (options.keyRight.isDown() && !options.keyLeft.isDown()) {
            rollDelta = SwimConfig.INSTANCE.strafeRollStrength;
            yawDelta = SwimConfig.INSTANCE.strafeYawStrength;
        }
        rollDelta *= velocityStrength;
        yawDelta *= velocityStrength;

        if (SwimConfig.INSTANCE.smoothing.strafeSmoothingEnabled) {
            rollDelta = STRAFE_ROLL_SMOOTHER.getNewDeltaValue(rollDelta,
                1 / SwimConfig.INSTANCE.smoothing.values.roll * context.getRenderDelta());
            yawDelta = STRAFE_YAW_SMOOTHER.getNewDeltaValue(yawDelta,
                1 / SwimConfig.INSTANCE.smoothing.values.yaw * context.getRenderDelta());
        }

        return rotationInstant.add(0, yawDelta * context.getRenderDelta(), rollDelta * context.getRenderDelta());
    }
}
