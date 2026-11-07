package com.example.hilesi.module;

import net.minecraft.client.MinecraftClient;

/**
 * Forge OP (yerel) - tek oyunculu/offline icin OP benzeri yetenekler:
 * ucmasi, olumemezlik, dusme hasari yok.
 * Baskasinin sunucusunda OP yetkisi vermez.
 */
public class ForgeOPModule extends Module {
    public ForgeOPModule() {
        super("Forge OP");
    }

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
                client.player.getAbilities().invulnerable = false;
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
        apply(client);
    }

    private void apply(MinecraftClient client) {
        try {
            if (client.player == null) return;
            client.player.getAbilities().allowFlying = true;
            client.player.getAbilities().flying = true;
            client.player.getAbilities().invulnerable = true;
            client.player.getAbilities().setFlySpeed(0.12f);
            client.player.fallDistance = 0.0f;
        } catch (Throwable ignored) {}
    }
}
