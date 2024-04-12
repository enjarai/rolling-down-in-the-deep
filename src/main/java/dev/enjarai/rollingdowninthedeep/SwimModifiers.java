package dev.enjarai.rollingdowninthedeep;

import net.minecraft.client.util.SmoothUtil;
import nl.enjarai.doabarrelroll.DoABarrelRollClient;
import nl.enjarai.doabarrelroll.api.event.RollContext;
import nl.enjarai.doabarrelroll.api.rotation.RotationInstant;
import nl.enjarai.doabarrelroll.config.ModConfig;
import nl.enjarai.doabarrelroll.math.MagicNumbers;

public class SwimModifiers {
    public static final double ROLL_REORIENT_CUTOFF = Math.sqrt(10.0 / 3.0);
    public static final SmoothUtil ROLL_REORIENT_SMOOTHER = new SmoothUtil();

    public static RotationInstant reorient(RotationInstant rotationInstant, RollContext context) {
        var delta = context.getRenderDelta();
        var currentRoll = context.getCurrentRotation().roll() * MagicNumbers.TORAD;
        var strength = 500;

        var cutoff = ROLL_REORIENT_CUTOFF;
        double rollDelta = 0;
        if (-cutoff < currentRoll && currentRoll < cutoff) {
            rollDelta = -Math.pow(currentRoll, 3) / 3.0 + currentRoll; //0.1 * Math.pow(currentRoll, 5);
        }

        return rotationInstant.add(0, 0,
                ROLL_REORIENT_SMOOTHER.smooth(-rollDelta * strength * delta, 0.4 * delta));
    }

    public static RotationInstant smoothRoll(RotationInstant rotationInstant, RollContext context) {
        return RotationInstant.of(
                rotationInstant.pitch(),
                rotationInstant.yaw(),
                DoABarrelRollClient.ROLL_SMOOTHER.smooth(rotationInstant.roll(),
                        ModConfig.INSTANCE.getSmoothing().roll * context.getRenderDelta())
        );
    }
}
