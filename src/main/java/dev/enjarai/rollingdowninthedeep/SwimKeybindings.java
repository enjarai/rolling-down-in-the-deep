package dev.enjarai.rollingdowninthedeep;

import com.mojang.blaze3d.platform.InputConstants;
import dev.enjarai.rollingdowninthedeep.config.SwimConfig;
import dev.enjarai.rollingdowninthedeep.config.SwimConfigScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class SwimKeybindings {
    public static final KeyMapping TOGGLE_ENABLED = new KeyMapping(
        "key.rolling_down_in_the_deep.toggle_enabled",
            InputConstants.KEY_0,
        "category.rolling_down_in_the_deep.rolling_down_in_the_deep"
    );
    public static final KeyMapping OPEN_CONFIG = new KeyMapping(
        "key.rolling_down_in_the_deep.open_config",
        InputConstants.UNKNOWN.getValue(),
        "category.rolling_down_in_the_deep.rolling_down_in_the_deep"
    );

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Pre event) {
        Minecraft client = Minecraft.getInstance();
        while (TOGGLE_ENABLED.consumeClick()) {
            SwimConfig.INSTANCE.enabled = !SwimConfig.INSTANCE.enabled;
            SwimConfig.INSTANCE.save();

            if (client.player != null) {
                client.player.displayClientMessage(
                    Component.translatable(
                        "key.rolling_down_in_the_deep." +
                            (SwimConfig.INSTANCE.enabled ? "toggle_enabled.enable" : "toggle_enabled.disable")
                    ),
                    true
                );
            }
        }
        while (OPEN_CONFIG.consumeClick()) {
            client.setScreen(SwimConfigScreen.create(client.screen));
        }
    }

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_ENABLED);
        event.register(OPEN_CONFIG);
    }
}
