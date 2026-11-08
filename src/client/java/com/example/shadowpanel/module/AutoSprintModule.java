package com.example.shadowpanel.module;

import net.minecraft.client.MinecraftClient;

public class AutoSprintModule extends Module {
    public AutoSprintModule() { super("AutoSprint"); }
    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client == null || client.player == null) return;
        if (client.currentScreen != null) return;
        try {
            if (client.player.forwardSpeed > 0 && !client.player.isSneaking() && !client.player.horizontalCollision) {
                client.player.setSprinting(true);
            }
        } catch (Throwable ignored) {}
    }
}
