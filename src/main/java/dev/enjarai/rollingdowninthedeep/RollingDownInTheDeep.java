package dev.enjarai.rollingdowninthedeep;

import dev.enjarai.rollingdowninthedeep.config.SwimConfig;
import dev.enjarai.rollingdowninthedeep.config.SwimConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.SmoothDouble;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.api.event.RollEvents;
import nl.enjarai.doabarrelroll.api.event.RollGroup;
import nl.enjarai.doabarrelroll.flight.RotationModifiers;
import nl.enjarai.doabarrelroll.math.MagicNumbers;
import org.joml.Matrix3d;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value = RollingDownInTheDeep.MOD_ID, dist = Dist.CLIENT)
public class RollingDownInTheDeep {
    public static final String MOD_ID = "rolling_down_in_the_deep";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final RollGroup SWIM_GROUP = RollGroup.of(id("swimming"));
    public static final RollGroup DABR_GROUP = RollGroup.of(DoABarrelRoll.id("fall_flying"));

    public static final SmoothDouble YAW_SMOOTHER = new SmoothDouble();
    public static final SmoothDouble PITCH_SMOOTHER = new SmoothDouble();
    public static final SmoothDouble ROLL_SMOOTHER = new SmoothDouble();

    public static final Minecraft client = Minecraft.getInstance();

    public RollingDownInTheDeep(IEventBus bus, ModContainer modContainer) {
        SwimConfig.touch();

        SWIM_GROUP.trueIf(RollingDownInTheDeep::shouldRoll);

        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (container, parent) -> SwimConfigScreen.create(parent));

        bus.addListener(RegisterKeyMappingsEvent.class, SwimKeybindings::register);

        RollEvents.EARLY_CAMERA_MODIFIERS.register(context -> context
                        .useModifier(CameraModifiers::configureRotation),
                1000, () -> SWIM_GROUP.get() && !DABR_GROUP.get());

        RollEvents.EARLY_CAMERA_MODIFIERS.register(context -> context
                        .useModifier(StrafeRollModifiers::applyStrafeRoll),
                2000, () -> SWIM_GROUP.get() && !DABR_GROUP.get());

        RollEvents.LATE_CAMERA_MODIFIERS.register(context -> context
                        .useModifier(RotationModifiers.smoothing(
                                PITCH_SMOOTHER,
                                YAW_SMOOTHER,
                                ROLL_SMOOTHER,
                                SwimConfig.INSTANCE.smoothing.values
                        )),
                3000, () -> SWIM_GROUP.get() && !DABR_GROUP.get() && SwimConfig.INSTANCE.smoothing.smoothingEnabled);

    }


    public static Vector3f movementInputToVelocity(LocalPlayer player, Vector3f moveInput, float speed) {
        Matrix3d matrix = new Matrix3d()
                .rotateY(-player.getYRot() * MagicNumbers.TORAD)
                .rotateX(player.getXRot() * MagicNumbers.TORAD)
                .rotateZ(((RollEntity) player).doABarrelRoll$getRoll() * MagicNumbers.TORAD);

        if (!SwimConfig.INSTANCE.strafeDoStrafe) moveInput.x = 0f;

        if (client.options.keyJump.isDown()) {
            moveInput.add(0f, 1.0f, 0f);
            speed += 0.006f;
        }
        if (client.options.keyShift.isDown()) {
            moveInput.add(0f, -1.0f, 0f);
            speed += 0.006f;
        }

        moveInput.mul(matrix);
        if (moveInput.lengthSquared() > 1f) {
            moveInput.normalize();
        }
        moveInput.mul(speed);
        return moveInput;
    }

    public static boolean shouldRoll() {
        var player = Minecraft.getInstance().player;

        return enabled() && player != null && player.isSwimming() && player.isUnderWater();
    }

    public static boolean enabled() {
        return SwimConfig.INSTANCE.enabled;
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
