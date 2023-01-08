package nl.enjarai;

import com.google.common.math.IntMath;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.SmoothUtil;
import nl.enjarai.doabarrelroll.DoABarrelRollClient;
import nl.enjarai.doabarrelroll.config.ModConfig;
import nl.enjarai.doabarrelroll.flight.ElytraMath;
import nl.enjarai.doabarrelroll.flight.util.RotationInstant;

public class SwimModifiers {
    public static final double ROLL_REORIENT_CUTOFF = Math.sqrt(10.0 / 3.0);
    public static final SmoothUtil ROLL_REORIENT_SMOOTHER = new SmoothUtil();

    public static RotationInstant reorient(RotationInstant rotationInstant) {
        var client = MinecraftClient.getInstance();
        var player = client.player;
        if (player == null) return rotationInstant;

        var delta = rotationInstant.getRenderDelta();
        var currentRoll = ElytraMath.getRoll(player.getYaw(), DoABarrelRollClient.left) * ElytraMath.TORAD;
        var strength = 500;

        var cutoff = ROLL_REORIENT_CUTOFF;
        double rollDelta = 0;
        if (-cutoff < currentRoll && currentRoll < cutoff) {
            rollDelta = -Math.pow(currentRoll, 3) / 3.0 + currentRoll; //0.1 * Math.pow(currentRoll, 5);
        }

        return rotationInstant.add(0, 0,
                ROLL_REORIENT_SMOOTHER.smooth(-rollDelta * strength * delta, 0.4 * delta));
    }

    public static RotationInstant smoothRoll(RotationInstant rotationInstant) {
        return new RotationInstant(
                rotationInstant.getPitch(),
                rotationInstant.getYaw(),
                DoABarrelRollClient.ROLL_SMOOTHER.smooth(rotationInstant.getRoll(),
                        ModConfig.INSTANCE.getSmoothing().roll * rotationInstant.getRenderDelta()),
                rotationInstant.getRenderDelta()
        );
    }
}
