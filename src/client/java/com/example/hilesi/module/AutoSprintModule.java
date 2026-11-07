package com.example.hilesi.module;

import net.minecraft.client.Minecraft;

public class AutoSprintModule extends Module {
    public AutoSprintModule() { super("AutoSprint"); }
    @Override
    public void onTick(Minecraft client) {
        if (!isEnabled() || client == null || client.player == null) return;
        if (client.screen != null) return;
        try {
            if (client.player.zza > 0 && !client.player.isSneaking() && !client.player.horizontalCollision) {
                client.player.setSprinting(true);
            }
        } catch (Throwable ignored) {}
    }
}
