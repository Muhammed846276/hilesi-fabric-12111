package com.example.hilesi.module;

import net.minecraft.client.MinecraftClient;

/**
 * Fly - survival modda ucmayi deneme (sadece kendi oyunun / offline)
 * Client tarafindan abilities.allowFlying acilir.
 */
public class FlyModule extends Module {

    private float flySpeed = 0.05f;

    public FlyModule() {
        super("Fly");
    }

    public float getFlySpeed() { return flySpeed; }
    public void setFlySpeed(float s) { this.flySpeed = Math.max(0.02f, Math.min(0.2f, s)); }

    @Override
    protected void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        apply(client);
    }

    @Override
    protected void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        try {
            if (client != null && client.player != null) {
                client.player.getAbilities().allowFlying = false;
                client.player.getAbilities().flying = false;
                client.player.getAbilities().setFlySpeed(0.05f);
            }
        } catch (Throwable ignored) {}
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client == null || client.player == null) return;
        if (!client.player.isAlive()) return;
        if (client.player.isGliding() || client.player.isSubmergedInWater() || client.player.isInLava()) return;
        apply(client);
    }

    private void apply(MinecraftClient client) {
        try {
            if (client.player == null) return;
            // Yaratici modda zaten var; survival'da client izni
            client.player.getAbilities().allowFlying = true;
            client.player.getAbilities().flying = true;
            client.player.getAbilities().setFlySpeed(flySpeed);
            client.player.setNoFallDamage(true);
        } catch (Throwable ignored) {}
    }
}
