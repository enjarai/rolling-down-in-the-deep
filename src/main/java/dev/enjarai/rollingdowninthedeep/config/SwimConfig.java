package dev.enjarai.rollingdowninthedeep.config;

import net.fabricmc.loader.api.FabricLoader;
import nl.enjarai.cicada.api.util.AbstractModConfig;
import nl.enjarai.doabarrelroll.config.Sensitivity;

public class SwimConfig extends AbstractModConfig {
    public static final SwimConfig INSTANCE = loadConfigFile(
            FabricLoader.getInstance().getConfigDir().resolve("rolling_down_in_the_deep-client.json"), new SwimConfig());

    public static void touch() {
    }

    public boolean enabled = true;

    public double strafeRollStrength = 2.5;
    public double strafeYawStrength = 1;

    public boolean strafeDoStrafe = true;

    public static class Smoothing {
        public boolean smoothingEnabled = true;
        public boolean strafeSmoothingEnabled = true;

        public Sensitivity values = new Sensitivity(.5, .5, .5);
    }
    public Smoothing smoothing = new Smoothing();
}
