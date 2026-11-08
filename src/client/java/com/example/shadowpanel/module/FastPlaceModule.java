package com.example.shadowpanel.module;

import net.minecraft.client.Minecraft;

public class FastPlaceModule extends Module {
    public FastPlaceModule() { super("FastPlace"); }
    @Override
    public void onTick(Minecraft client) {
        if (!isEnabled() || client == null) return;
        try {
            // itemUseCooldown alanini reflection ile sifirla (mapping farklarina dayanikli)
            for (var f : Minecraft.class.getDeclaredFields()) {
                if (f.getType() == int.class && f.getName().toLowerCase().contains("itemuse")) {
                    f.setAccessible(true);
                    f.setInt(client, 0);
                    break;
                }
            }
        } catch (Throwable ignored) {}
    }
}
