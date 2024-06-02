package dev.enjarai.rollingdowninthedeep;

import dev.enjarai.rollingdowninthedeep.config.SwimConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.SmoothUtil;
import nl.enjarai.doabarrelroll.api.event.RollContext;
import nl.enjarai.doabarrelroll.api.rotation.RotationInstant;
import nl.enjarai.doabarrelroll.config.ModConfig;

public class StrafeRollModifiers {
    public static final SmoothUtil STRAFE_ROLL_SMOOTHER = new SmoothUtil();
    public static final SmoothUtil STRAFE_YAW_SMOOTHER = new SmoothUtil();

    public static RotationInstant applyStrafeRoll(RotationInstant rotationInstant, RollContext context) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return rotationInstant;

        GameOptions options = MinecraftClient.getInstance().options;
        double rollDelta = 0;
        double yawDelta = 0;

        if (options.leftKey.isPressed() && !options.rightKey.isPressed()) {
            rollDelta = -SwimConfig.INSTANCE.strafeRollStrength;
            yawDelta = -SwimConfig.INSTANCE.strafeYawStrength;
        } else if (options.rightKey.isPressed() && !options.leftKey.isPressed()) {
            rollDelta = SwimConfig.INSTANCE.strafeRollStrength;
            yawDelta = SwimConfig.INSTANCE.strafeYawStrength;
        }

        if (SwimConfig.INSTANCE.smoothing.strafeSmoothingEnabled) {
            rollDelta = STRAFE_ROLL_SMOOTHER.smooth(rollDelta,
                    1 / SwimConfig.INSTANCE.smoothing.values.roll * context.getRenderDelta());
            yawDelta = STRAFE_YAW_SMOOTHER.smooth(yawDelta,
                    1 / SwimConfig.INSTANCE.smoothing.values.yaw * context.getRenderDelta());
        }

        return rotationInstant.add(0, yawDelta * context.getRenderDelta(), rollDelta * context.getRenderDelta());
    }
}
