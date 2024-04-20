package dev.enjarai.rollingdowninthedeep.config;

import net.fabricmc.loader.api.FabricLoader;
import nl.enjarai.cicada.api.util.AbstractModConfig;

public class SwimConfig extends AbstractModConfig {
    public static final SwimConfig INSTANCE = loadConfigFile(
            FabricLoader.getInstance().getConfigDir().resolve("rolling_down_in_the_deep-client.json"), new SwimConfig());

    public static void touch() {
    }

    public boolean enabled = true;
}
