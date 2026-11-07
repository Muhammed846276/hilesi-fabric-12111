package com.example.hilesi.module;

import net.minecraft.client.MinecraftClient;

/**
 * FullBright - karanligi aydinlatir (gamma = 16)
 * Sadece kendi oyunun / offline.
 */
public class FullBrightModule extends Module {

    private double previousGamma = 1.0;

    public FullBrightModule() {
        super("Full Bright");
    }

    @Override
    protected void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        try {
            if (client != null && client.options != null) {
                try {
                    previousGamma = client.options.getGamma().getValue();
                } catch (Throwable ignored) {}
                apply(client);
            }
        } catch (Throwable ignored) {}
    }

    @Override
    protected void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        try {
            if (client != null && client.options != null) {
                client.options.getGamma().setValue(previousGamma);
            }
        } catch (Throwable ignored) {}
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client == null) return;
        apply(client);
    }

    private void apply(MinecraftClient client) {
        try {
            if (client.options == null) return;
            client.options.getGamma().setValue(16.0);
        } catch (Throwable ignored) {}
    }
}
