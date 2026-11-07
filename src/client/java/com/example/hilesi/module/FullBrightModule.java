package com.example.hilesi.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

/**
 * FullBright - karanligi aydinlatir (Night Vision + gamma).
 * Sadece kendi oyunun / offline.
 */
public class FullBrightModule extends Module {

    private static final int NIGHT_VISION_DURATION = 1_000_000;
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
                } catch (Throwable ignored) {
                    previousGamma = 1.0;
                }
            }
        } catch (Throwable ignored) {}
        apply(client);
    }

    @Override
    protected void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        try {
            if (client != null && client.player != null) {
                client.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
            }
        } catch (Throwable ignored) {}
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
            if (client.options != null) {
                client.options.getGamma().setValue(16.0);
            }
        } catch (Throwable ignored) {}
        try {
            if (client.player == null || !client.player.isAlive()) return;
            StatusEffectInstance current = client.player.getStatusEffect(StatusEffects.NIGHT_VISION);
            if (current == null || current.getDuration() < NIGHT_VISION_DURATION / 2) {
                // ambient=true, showParticles=false, showIcon=false
                client.player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.NIGHT_VISION,
                        NIGHT_VISION_DURATION,
                        0,
                        true,
                        false,
                        false
                ));
            }
        } catch (Throwable ignored) {}
    }
}
