package com.example.hilesi.module;

import net.minecraft.client.MinecraftClient;

/**
 * Fly - survival modda ucmayi deneme (sadece kendi oyunun / offline)
 * Client tarafindan abilities.allowFlying acilir.
 * Double Jump (Space x2) ile ac/kapa.
 */
public class FlyModule extends Module {

    private float flySpeed = 0.05f; // 0.02 - 0.20

    // Double jump algilama
    private boolean jumpWasPressed = false;
    private long firstJumpMs = 0L;
    private boolean waitingSecond = false;
    private static final long DOUBLE_TAP_MS = 350L;

    public FlyModule() {
        super("Fly");
    }

    public float getFlySpeed() { return flySpeed; }
    public void setFlySpeed(float s) { this.flySpeed = clamp(s, 0.02f, 0.20f); }

    public void cycleSpeed(int dir) { setFlySpeed(flySpeed + 0.01f * dir); }
    public void setSpeedPercent(int pct) {
        float v = 0.02f + (0.20f - 0.02f) * (pct / 100f);
        setFlySpeed(v);
    }

    private static float clamp(float v, float min, float max) { return Math.max(min, Math.min(max, v)); }

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
        if (client == null || client.player == null) return;

        // Double jump: Fly kapaliyken de dinlemeliyiz (acmak icin)
        // Menü acikken tetiklenmesin
        if (client.currentScreen == null) {
            try {
                boolean jump = client.options.jumpKey.isPressed();
                long now = System.currentTimeMillis();
                if (jump && !jumpWasPressed) {
                    if (waitingSecond && (now - firstJumpMs) <= DOUBLE_TAP_MS) {
                        toggle();
                        waitingSecond = false;
                    } else {
                        firstJumpMs = now;
                        waitingSecond = true;
                    }
                }
                if (waitingSecond && (now - firstJumpMs) > DOUBLE_TAP_MS) {
                    waitingSecond = false;
                }
                jumpWasPressed = jump;
            } catch (Throwable ignored) {}
        }

        if (!isEnabled()) return;
        if (!client.player.isAlive()) return;
        if (client.player.isGliding() || client.player.isSubmergedInWater() || client.player.isInLava()) return;
        apply(client);
    }

    private void apply(MinecraftClient client) {
        try {
            if (client.player == null) return;
            client.player.getAbilities().allowFlying = true;
            client.player.getAbilities().flying = true;
            client.player.getAbilities().setFlySpeed(flySpeed);
        } catch (Throwable ignored) {}
    }
}
