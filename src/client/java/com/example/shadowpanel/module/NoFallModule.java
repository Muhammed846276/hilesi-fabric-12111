package com.example.shadowpanel.module;

import net.minecraft.client.MinecraftClient;

import java.lang.reflect.Constructor;

/**
 * NoFall - dusme hasarini azaltir/engeller (client-side + ground packet denemesi).
 * Tek oyunculuda genelde calisir; sunucuda davranis sunucuya baglidir.
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
            if (client.player.isGliding() || client.player.getAbilities().flying) return;
        } catch (Throwable ignored) {}
        try {
            if (client.player.isSubmergedInWater() || client.player.isInLava()) return;
        } catch (Throwable ignored) {}

        try {
            double fall = client.player.fallDistance;
            double vy = client.player.getVelocity().y;
            // Tehlikeli dususte fallDistance'i sifirla ve ground paket dene
            if (fall > 2.0 || vy < -0.55) {
                client.player.fallDistance = 0.0;
                sendGroundPacket(client);
            } else if (fall > 0.0) {
                client.player.fallDistance = 0.0;
            }
        } catch (Throwable ignored) {}
    }

    private void sendGroundPacket(MinecraftClient client) {
        try {
            if (client.player == null || client.getNetworkHandler() == null) return;
            boolean horizontal = false;
            try {
                horizontal = client.player.horizontalCollision;
            } catch (Throwable ignored) {}

            Object packet = null;
            try {
                Class<?> base = Class.forName("net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket");
                for (Class<?> inner : base.getDeclaredClasses()) {
                    if (!"OnGroundOnly".equals(inner.getSimpleName())) continue;
                    // (boolean onGround, boolean horizontalCollision)
                    try {
                        Constructor<?> c = inner.getDeclaredConstructor(boolean.class, boolean.class);
                        c.setAccessible(true);
                        packet = c.newInstance(true, horizontal);
                        break;
                    } catch (Throwable ignored) {}
                    // (boolean onGround)
                    try {
                        Constructor<?> c = inner.getDeclaredConstructor(boolean.class);
                        c.setAccessible(true);
                        packet = c.newInstance(true);
                        break;
                    } catch (Throwable ignored) {}
                }
            } catch (Throwable ignored) {}

            if (packet != null) {
                try {
                    client.getNetworkHandler().sendPacket((net.minecraft.network.packet.Packet<?>) packet);
                } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}
    }
}
