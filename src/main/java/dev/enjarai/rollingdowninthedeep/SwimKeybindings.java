package dev.enjarai.rollingdowninthedeep;

import dev.enjarai.rollingdowninthedeep.config.SwimConfig;
import dev.enjarai.rollingdowninthedeep.config.SwimConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class SwimKeybindings {
    public static final KeyBinding TOGGLE_ENABLED = new KeyBinding(
            "key.rolling_down_in_the_deep.toggle_enabled",
            GLFW.GLFW_KEY_O,
            "category.rolling_down_in_the_deep.rolling_down_in_the_deep"
    );
    public static final KeyBinding OPEN_CONFIG = new KeyBinding(
            "key.rolling_down_in_the_deep.open_config",
            InputUtil.UNKNOWN_KEY.getCode(),
            "category.rolling_down_in_the_deep.rolling_down_in_the_deep"
    );

    public static void clientTick(MinecraftClient client) {
        while (TOGGLE_ENABLED.wasPressed()) {
            SwimConfig.INSTANCE.enabled = !SwimConfig.INSTANCE.enabled;
            SwimConfig.INSTANCE.save();

            if (client.player != null) {
                client.player.sendMessage(
                        Text.translatable(
                                "key.rolling_down_in_the_deep." +
                                        (SwimConfig.INSTANCE.enabled ? "toggle_enabled.enable" : "toggle_enabled.disable")
                        ),
                        true
                );
            }
        }
        while (OPEN_CONFIG.wasPressed()) {
            client.setScreen(SwimConfigScreen.create(client.currentScreen));
        }
    }
}
