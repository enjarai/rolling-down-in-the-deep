package dev.enjarai.rollingdowninthedeep;

import net.minecraft.client.util.SmoothUtil;
import nl.enjarai.doabarrelroll.api.event.RollContext;
import nl.enjarai.doabarrelroll.api.rotation.RotationInstant;
import nl.enjarai.doabarrelroll.config.ModConfig;

public class CameraModifiers {
    public static final SmoothUtil YAW_SMOOTHER = new SmoothUtil();
    public static final SmoothUtil PITCH_SMOOTHER = new SmoothUtil();
    public static final SmoothUtil ROLL_SMOOTHER = new SmoothUtil();

    public static RotationInstant smoothCamera(RotationInstant rotationInstant, RollContext context) {
        double smoothedYaw = YAW_SMOOTHER.smooth(rotationInstant.yaw(), ModConfig.INSTANCE.getSmoothing().yaw * context.getRenderDelta());
        double smoothedPitch = PITCH_SMOOTHER.smooth(rotationInstant.pitch(), ModConfig.INSTANCE.getSmoothing().pitch * context.getRenderDelta());
        return RotationInstant.of(
            smoothedPitch,
            smoothedYaw,
            rotationInstant.roll()
        );
    }

    public static RotationInstant configureRotation(RotationInstant rotationInstant, RollContext context) {
        var pitch = rotationInstant.pitch();
        var yaw = rotationInstant.yaw();
        var roll = rotationInstant.roll();

        var temp = yaw;
        yaw = roll;
        roll = temp;
        if (ModConfig.INSTANCE.getInvertPitch()) {
            pitch = -pitch;
        }

        return RotationInstant.of(pitch, yaw, roll);
    }
}