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
    public boolean persistentSwimming = true;
    public boolean swapYawAndRoll = false;

    public double strafeRollStrength = 0.2;
    public double strafeYawStrength = 1;

    public boolean strafeDoStrafe = true;

    public boolean velocityEnable = true;
    public double velocityMin = 0.4;
    public double velocityMax = 1.0;
    public double velocityScale = 8.65;

    public static class Smoothing {
        public boolean smoothingEnabled = true;
        public boolean strafeSmoothingEnabled = true;

        public Sensitivity values = new Sensitivity(.5, .5, .5);
    }

    public Smoothing smoothing = new Smoothing();
}
