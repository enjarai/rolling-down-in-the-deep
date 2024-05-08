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
//        double smoothedRoll = ROLL_SMOOTHER.smooth();
//
//        RotationInstant.of(
//                smoothness.pitch == 0 ? rotationInstant.pitch() : pitchSmoother.smooth(rotationInstant.pitch(), 1 / smoothness.pitch * context.getRenderDelta()),
//                smoothness.yaw == 0 ? rotationInstant.yaw() : yawSmoother.smooth(rotationInstant.yaw(), 1 / smoothness.yaw * context.getRenderDelta()),
//                smoothness.roll == 0 ? rotationInstant.roll() : rollSmoother.smooth(rotationInstant.roll(), 1 / smoothness.roll * context.getRenderDelta())
//        )

        return RotationInstant.of(
                smoothedPitch,
                smoothedYaw,
                rotationInstant.roll()
        );
    }
}