package dev.enjarai.rollingdowninthedeep.config;

import dev.enjarai.rollingdowninthedeep.compat.yacl.YACLImplementation;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.compat.Compat;

public class SwimConfigScreen {
    public static Screen create(Screen parent) {
        if (!Compat.isYACLLoaded()) {
            return new ConfirmScreen((result) -> {
                if (result) {
                    Util.getPlatform().openUri("https://modrinth.com/mod/yacl/versions");
                }
                Minecraft.getInstance().setScreen(parent);
            }, getText("missing"), getText("missing.message"), CommonComponents.GUI_YES, CommonComponents.GUI_NO);
        } else if (!Compat.isYACLUpToDate()) {
            return new ConfirmScreen((result) -> {
                if (result) {
                    Util.getPlatform().openUri("https://modrinth.com/mod/yacl/versions");
                }
                Minecraft.getInstance().setScreen(parent);
            }, getText("outdated"), getText("outdated.message"), CommonComponents.GUI_YES, CommonComponents.GUI_NO);
        } else {
            return YACLImplementation.generateConfigScreen(parent);
        }
    }

    private static MutableComponent getText(String key) {
        return Component.translatable(Util.makeDescriptionId("config.do_a_barrel_roll.yacl", DoABarrelRoll.id("yacl." + key)));
    }
}
