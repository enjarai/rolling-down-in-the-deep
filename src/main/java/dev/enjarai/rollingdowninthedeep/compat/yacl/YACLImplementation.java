package dev.enjarai.rollingdowninthedeep.compat.yacl;

import dev.enjarai.rollingdowninthedeep.RollingDownInTheDeep;
import dev.enjarai.rollingdowninthedeep.SwimKeybindings;
import dev.enjarai.rollingdowninthedeep.config.SwimConfig;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class YACLImplementation {
    public static Screen generateConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
            .title(getText("title"))
            .category(ConfigCategory.createBuilder()
                .name(getText("general"))
                .option(getBooleanOption("general", "mod_enabled", false, false)
                    .description(OptionDescription.createBuilder()
                        .text(Text.translatable("config.rolling_down_in_the_deep.general.mod_enabled.description",
                            SwimKeybindings.TOGGLE_ENABLED.getBoundKeyLocalizedText()))
                        .build())
                    .binding(true, () -> SwimConfig.INSTANCE.enabled, value -> SwimConfig.INSTANCE.enabled = value)
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(getText("general", "strafing"))
                    .option(getOption(Double.class, "general.strafing", "yaw_strength", false, false)
                        .controller(option -> getDoubleSlider(option, 0.1, 5, 0.1))
                        .binding(1.0, () -> SwimConfig.INSTANCE.strafeYawStrength, value -> SwimConfig.INSTANCE.strafeYawStrength = value)
                        .build())
                    .option(getOption(Double.class, "general.strafing", "roll_strength", false, false)
                        .controller(option -> getDoubleSlider(option, 0.1, 5, 0.1))
                        .binding(2.5, () -> SwimConfig.INSTANCE.strafeRollStrength, value -> SwimConfig.INSTANCE.strafeRollStrength = value)
                        .build())
                    .option(getBooleanOption("general.strafing", "do_strafe", true, false)
                        .binding(true, () -> SwimConfig.INSTANCE.strafeDoStrafe, value -> SwimConfig.INSTANCE.strafeDoStrafe = value)
                        .build())
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(getText("general", "strafe_velocity"))
                    .option(getBooleanOption("general.strafe_velocity", "do_velocity", true, false)
                        .binding(true, () -> SwimConfig.INSTANCE.velocityEnable, value -> SwimConfig.INSTANCE.velocityEnable = value)
                        .build())
                    .option(getOption(Double.class, "general.strafe_velocity", "clamp_min", false, false)
                        .controller(option -> getDoubleSlider(option, 0.0, 4, 0.05))
                        .binding(0.4, () -> SwimConfig.INSTANCE.velocityMin, value -> SwimConfig.INSTANCE.velocityMin = value)
                        .build())
                    .option(getOption(Double.class, "general.strafe_velocity", "clamp_max", false, false)
                        .controller(option -> getDoubleSlider(option, 0.0, 4, 0.05))
                        .binding(1.0, () -> SwimConfig.INSTANCE.velocityMax, value -> SwimConfig.INSTANCE.velocityMax = value)
                        .build())
                    .option(getOption(Double.class, "general.strafe_velocity", "scale", false, false)
                        .controller(option -> getDoubleSlider(option, 0.0, 90, 0.05))
                        .binding(8.65, () -> SwimConfig.INSTANCE.velocityScale, value -> SwimConfig.INSTANCE.velocityScale = value)
                        .build())
                    .build())
                .build()
            )
            .category(ConfigCategory.createBuilder()
                .name(getText("smoothing"))
                .option(getBooleanOption("smoothing", "enabled", false, false)
                    .binding(true, () -> SwimConfig.INSTANCE.smoothing.smoothingEnabled, value -> SwimConfig.INSTANCE.smoothing.smoothingEnabled = value)
                    .build())
                .option(getBooleanOption("smoothing", "strafe_enabled", false, false)
                    .binding(true, () -> SwimConfig.INSTANCE.smoothing.strafeSmoothingEnabled, value -> SwimConfig.INSTANCE.smoothing.strafeSmoothingEnabled = value)
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(getText("smoothing", "strength"))
                    .option(getOption(Double.class, "smoothing.strength", "pitch", false, false)
                        .controller(option -> getDoubleSlider(option, 0.1, 5, 0.1))
                        .binding(.5, () -> SwimConfig.INSTANCE.smoothing.values.pitch, value -> SwimConfig.INSTANCE.smoothing.values.pitch = value)
                        .build())
                    .option(getOption(Double.class, "smoothing.strength", "yaw", false, false)
                        .controller(option -> getDoubleSlider(option, 0.1, 5, 0.1))
                        .binding(.5, () -> SwimConfig.INSTANCE.smoothing.values.yaw, value -> SwimConfig.INSTANCE.smoothing.values.yaw = value)
                        .build())
                    .option(getOption(Double.class, "smoothing.strength", "roll", false, false)
                        .controller(option -> getDoubleSlider(option, 0.1, 5, 0.1))
                        .binding(.5, () -> SwimConfig.INSTANCE.smoothing.values.roll, value -> SwimConfig.INSTANCE.smoothing.values.roll = value)
                        .build())
                    .build())
                .build()
            )
            .save(SwimConfig.INSTANCE::save)
            .build()
            .generateScreen(parent);
    }

    private static <T> Option.Builder<T> getOption(Class<T> clazz, String category, String key, boolean description, boolean image) {
        Option.Builder<T> builder = Option.<T>createBuilder()
            .name(getText(category, key));
        var descBuilder = OptionDescription.createBuilder();
        if (description) {
            descBuilder.text(getText(category, key + ".description"));
        }
        if (image) {
            descBuilder.image(RollingDownInTheDeep.id("textures/gui/config/images/" + category + "/" + key + ".png"), 480, 275);
        }
        builder.description(descBuilder.build());
        return builder;
    }

    private static Option.Builder<Boolean> getBooleanOption(String category, String key, boolean description, boolean image) {
        return getOption(Boolean.class, category, key, description, image)
            .controller(TickBoxControllerBuilder::create);
    }

    private static MutableText getText(String category, String key) {
        return Text.translatable("config.rolling_down_in_the_deep." + category + "." + key);
    }

    private static MutableText getText(String key) {
        return Text.translatable("config.rolling_down_in_the_deep." + key);
    }

    private static DoubleSliderControllerBuilder getDoubleSlider(Option<Double> option, double min, double max, double step) {
        return DoubleSliderControllerBuilder.create(option)
            .range(min, max)
            .step(step);
    }
}
