package dev.enjarai.rollingdowninthedeep.compat.yacl;

import dev.enjarai.rollingdowninthedeep.RollingDownInTheDeep;
import dev.enjarai.rollingdowninthedeep.SwimKeybindings;
import dev.enjarai.rollingdowninthedeep.config.SwimConfig;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
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
}
