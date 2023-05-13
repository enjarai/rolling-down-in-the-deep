package nl.enjarai;

import net.fabricmc.api.ModInitializer;

import net.minecraft.client.MinecraftClient;
import nl.enjarai.doabarrelroll.DoABarrelRollClient;
import nl.enjarai.doabarrelroll.api.event.RollEvents;
import nl.enjarai.doabarrelroll.config.Sensitivity;
import nl.enjarai.doabarrelroll.flight.RotationModifiers;
import nl.enjarai.doabarrelroll.util.ProperLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RollingDownInTheDeep implements ModInitializer {
	public static final String MOD_ID = "rolling_down_in_the_deep";
	public static final Logger LOGGER = ProperLogger.getLogger(MOD_ID);
	public static final Sensitivity SMOOTHING = new Sensitivity(5, 5, 1);

	@Override
	public void onInitialize() {
		RollEvents.SHOULD_ROLL_CHECK.register(RollingDownInTheDeep::shouldRoll);

		RollEvents.EARLY_CAMERA_MODIFIERS.register((rotationDelta, currentRotation) -> rotationDelta
				.useModifier(RotationModifiers.strafeButtons(1800)),
				10, () -> shouldRoll() && !DoABarrelRollClient.isFallFlying());

		RollEvents.LATE_CAMERA_MODIFIERS.register((rotationDelta, currentRotation) -> rotationDelta // TODO slight roll in yaw direction
				.useModifier(SwimModifiers::reorient),
				40, RollingDownInTheDeep::shouldRoll);

		RollEvents.LATE_CAMERA_MODIFIERS.register((rotationDelta, currentRotation) -> rotationDelta
				.useModifier(RotationModifiers.smoothing(
						DoABarrelRollClient.PITCH_SMOOTHER, DoABarrelRollClient.YAW_SMOOTHER,
						DoABarrelRollClient.ROLL_SMOOTHER, SMOOTHING
				)),
				30, () -> shouldRoll() && !DoABarrelRollClient.isFallFlying());
	}

	public static boolean shouldRoll() {
		var player = MinecraftClient.getInstance().player;

		return enabled() && player != null && player.isSwimming() && player.isSubmergedInWater();
	}

	public static boolean enabled() {
		return true;
	}
}