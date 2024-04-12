package dev.enjarai.rollingdowninthedeep;

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
    private static final double MAX_ROLL_ANGLE = 5.0;
    private static final double MAX_YAW_ANGLE = 2.0;
    public static RotationInstant applyStrafeRoll(RotationInstant rotationInstant, RollContext context) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return rotationInstant;

        GameOptions options = MinecraftClient.getInstance().options;
        double rollDelta = 0;
        double yawDelta = 0;

        if (options.leftKey.isPressed() && !options.rightKey.isPressed()) {
            rollDelta = -MAX_ROLL_ANGLE;
            yawDelta = -MAX_YAW_ANGLE;
        } else if (options.rightKey.isPressed() && !options.leftKey.isPressed()) {
            rollDelta = MAX_ROLL_ANGLE;
            yawDelta = MAX_YAW_ANGLE;
        }

        double currentYaw = rotationInstant.yaw();
        double targetYaw = currentYaw + yawDelta;
        double clampedYaw = clampYaw(targetYaw, -MAX_YAW_ANGLE, MAX_YAW_ANGLE);

        double smoothedRoll = STRAFE_ROLL_SMOOTHER.smooth(rollDelta, ModConfig.INSTANCE.getSmoothing().roll * context.getRenderDelta());
        double smoothedYaw = STRAFE_YAW_SMOOTHER.smooth(clampedYaw - currentYaw, ModConfig.INSTANCE.getSmoothing().yaw * context.getRenderDelta());

        return rotationInstant.add(0, smoothedYaw, smoothedRoll);
    }

    private static double clampYaw(double yaw, double min, double max) {
        return Math.max(min, Math.min(max, yaw));
    }
}