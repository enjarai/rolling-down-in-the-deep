package dev.enjarai.rollingdowninthedeep;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;
import nl.enjarai.cicada.api.util.ProperLogger;
import nl.enjarai.doabarrelroll.DoABarrelRollClient;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.api.event.RollEvents;
import nl.enjarai.doabarrelroll.api.event.RollGroup;
import nl.enjarai.doabarrelroll.config.ModConfig;
import nl.enjarai.doabarrelroll.config.Sensitivity;
import nl.enjarai.doabarrelroll.flight.RotationModifiers;
import nl.enjarai.doabarrelroll.math.MagicNumbers;
import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.slf4j.Logger;

public class RollingDownInTheDeep implements ModInitializer {
	public static final String MOD_ID = "rolling_down_in_the_deep";
	//	public static final Logger LOGGER = ProperLogger.getLogger(MOD_ID);
	public static final Sensitivity SMOOTHING = new Sensitivity(.02, .02, .02);
	public static final RollGroup SWIM_GROUP = RollGroup.of(id("swimming"));
	public static final RollGroup DABR_GROUP = RollGroup.of(new Identifier("do_a_barrel_roll", "fall_flying"));

	@Override
	public void onInitialize() {
		SWIM_GROUP.trueIf(RollingDownInTheDeep::shouldRoll);

		// Smooth the camera rotation
		RollEvents.EARLY_CAMERA_MODIFIERS.register(context -> context
				.useModifier(CameraModifiers::smoothCamera), 1000, () -> SWIM_GROUP.get() && !DABR_GROUP.get());

		RollEvents.EARLY_CAMERA_MODIFIERS.register(context -> context.useModifier(StrafeRollModifiers::applyStrafeRoll),
				1000, () -> SWIM_GROUP.get() && !DABR_GROUP.get());

		RollEvents.EARLY_CAMERA_MODIFIERS.register(context -> context.useModifier(ModConfig.INSTANCE::configureRotation),
				1000, () -> SWIM_GROUP.get() && !DABR_GROUP.get());

		RollEvents.LATE_CAMERA_MODIFIERS.register(context -> context.useModifier(SwimModifiers::reorient),
				40, SWIM_GROUP);

		RollEvents.LATE_CAMERA_MODIFIERS.register(context -> context.useModifier(RotationModifiers.smoothing(DoABarrelRollClient.PITCH_SMOOTHER, DoABarrelRollClient.YAW_SMOOTHER,
						DoABarrelRollClient.ROLL_SMOOTHER, SMOOTHING
				)),
				3000, () -> SWIM_GROUP.get() && !DABR_GROUP.get());
	}

	public static Vector3d handleSwimVelocity(ClientPlayerEntity player, Vector3d moveInput, double speed) {
		// Rotate the input vector to match the player's rotation
		var matrix = new Matrix3d();
		matrix.rotateY(-player.getYaw() * MagicNumbers.TORAD);
		matrix.rotateX(player.getPitch() * MagicNumbers.TORAD);
		matrix.rotateZ(((RollEntity) player).doABarrelRoll$getRoll() * MagicNumbers.TORAD);

		moveInput.mul(matrix);
		if (moveInput.lengthSquared() > 1) {
			moveInput.normalize();
		}
		moveInput.mul(speed);
		// (this scalers feel right idk)
		moveInput.x *= 1.5;
		moveInput.z *= 1.5;
		moveInput.y *= 1.5;
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