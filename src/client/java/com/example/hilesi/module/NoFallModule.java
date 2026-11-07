package com.example.hilesi.module;

import net.minecraft.client.MinecraftClient;

/**
 * NoFall - dusme hasarini sifirlar (sadece kendi oyunun / offline)
 * Her tick'te fallDistance sifirlanir.
 */
public class NoFallModule extends Module {
    public NoFallModule() {
        super("NoFall");
    }

    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client == null || client.player == null) return;
        if (!client.player.isAlive()) return;
        try {
            client.player.fallDistance = 0.0f;
            try {
                client.player.resetFallDistance();
            } catch (Throwable ignored) {}
        } catch (Throwable ignored) {}
        try {
            if (client.player.isGliding()) return;
            // Dusme sirasinda asiri hizi kes (su + lava haric)
            if (!client.player.isSubmergedInWater() && !client.player.isInLava()) {
                if (client.player.getVelocity().getY() < -0.5) {
                    // Sadece dusme hasarini onlemek icin yeterli;
                    // hiz kisma eklenmedi
                }
            }
        } catch (Throwable ignored) {}
    }
}
