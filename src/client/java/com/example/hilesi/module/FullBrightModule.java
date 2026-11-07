package com.example.hilesi.module;

import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

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
        Minecraft client = Minecraft.getInstance();
        try {
            if (client != null && client.options != null) {
                try {
                    previousGamma = client.options.gamma().get();
                } catch (Throwable ignored) {
                    previousGamma = 1.0;
                }
            }
        } catch (Throwable ignored) {}
        apply(client);
    }

    @Override
    protected void onDisable() {
        Minecraft client = Minecraft.getInstance();
        try {
            if (client != null && client.player != null) {
                client.player.removeEffect(MobEffects.NIGHT_VISION);
            }
        } catch (Throwable ignored) {}
        try {
            if (client != null && client.options != null) {
                client.options.gamma().set(previousGamma);
            }
        } catch (Throwable ignored) {}
    }

    @Override
    public void onTick(Minecraft client) {
        if (!isEnabled() || client == null) return;
        apply(client);
    }

    private void apply(Minecraft client) {
        try {
            if (client.options != null) {
                client.options.gamma().set(16.0);
            }
        } catch (Throwable ignored) {}
        try {
            if (client.player == null || !client.player.isAlive()) return;
            MobEffectInstance current = client.player.getEffect(MobEffects.NIGHT_VISION);
            if (current == null || current.getDuration() < NIGHT_VISION_DURATION / 2) {
                // ambient=true, visible=false, showIcon=false
                client.player.addEffect(new MobEffectInstance(
                        MobEffects.NIGHT_VISION,
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
