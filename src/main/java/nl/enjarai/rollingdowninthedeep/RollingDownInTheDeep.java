package nl.enjarai.rollingdowninthedeep;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;
import nl.enjarai.doabarrelroll.DoABarrelRollClient;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.api.event.RollEvents;
import nl.enjarai.doabarrelroll.api.event.RollGroup;
import nl.enjarai.doabarrelroll.config.Sensitivity;
import nl.enjarai.doabarrelroll.flight.ElytraMath;
import nl.enjarai.doabarrelroll.flight.RotationModifiers;
import nl.enjarai.doabarrelroll.util.ProperLogger;
import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.slf4j.Logger;

public class RollingDownInTheDeep implements ModInitializer {
	public static final String MOD_ID = "rolling_down_in_the_deep";
	public static final Logger LOGGER = ProperLogger.getLogger(MOD_ID);
	public static final Sensitivity SMOOTHING = new Sensitivity(5, 5, 1);

	public static final RollGroup SWIM_GROUP = RollGroup.of(id("swimming"));
	public static final RollGroup DABR_GROUP = RollGroup.of(new Identifier("do_a_barrel_roll", "fall_flying"));

	@Override
	public void onInitialize() {
		SWIM_GROUP.trueIf(RollingDownInTheDeep::shouldRoll);

//		RollEvents.EARLY_CAMERA_MODIFIERS.register(context -> context
//				.useModifier(RotationModifiers.strafeButtons(1800)), // hahha yes, we dont need this do we? i sure hope we dont
//				,
//				10, () -> SWIM_GROUP.get() && !DABR_GROUP.get());

		RollEvents.LATE_CAMERA_MODIFIERS.register(context -> context // TODO slight roll in yaw direction
				.useModifier(SwimModifiers::reorient),
				40, SWIM_GROUP);

		RollEvents.LATE_CAMERA_MODIFIERS.register(context -> context
				.useModifier(RotationModifiers.smoothing(
						DoABarrelRollClient.PITCH_SMOOTHER, DoABarrelRollClient.YAW_SMOOTHER,
						DoABarrelRollClient.ROLL_SMOOTHER, SMOOTHING
				)),
				30, () -> SWIM_GROUP.get() && !DABR_GROUP.get());
	}

	public static Vector3d handleSwimVelocity(ClientPlayerEntity player, Vector3d moveInput, double speed) {
		// Rotate the input vector to match the player's rotation
		var matrix = new Matrix3d();
		matrix.rotateY(-player.getYaw() * ElytraMath.TORAD);
		matrix.rotateX(player.getPitch() * ElytraMath.TORAD);
		matrix.rotateZ(((RollEntity) player).doABarrelRoll$getRoll() * ElytraMath.TORAD);

		moveInput.mul(matrix);
		if (moveInput.lengthSquared() > 1) {
			moveInput.normalize();
		}
		moveInput.mul(speed);
		// Multiply the Y component to compensate for funky vanilla velocity handling in water
		moveInput.y *= 4;
		return moveInput;
	}

	public static boolean shouldRoll() {
		var player = MinecraftClient.getInstance().player;

		return enabled() && player != null && player.isSwimming() && player.isSubmergedInWater();
	}

	public static boolean enabled() {
		return true;
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}