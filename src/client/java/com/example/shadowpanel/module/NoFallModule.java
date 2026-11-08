package com.example.shadowpanel.module;

import net.minecraft.client.MinecraftClient;

/**
 * NoFall - dusme hasarini engeller (v2)
 * Her tick'te fallDistance sifirlanir + noFallDamage bayragi reflection ile acilir.
 * Tek oyunculuda tam calisir; sunucuda server tarafi kontrolu atlatmak icin
 * paket spoof gerekir (su an client-only).
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
        } catch (Throwable ignored) {}
        try {
            if (client.player.isGliding()) return;
            if (!client.player.isSubmergedInWater() && !client.player.isInLava()) {
                // noFallDamage alanini bul ve ac (varsa)
                try {
                    Class<?> entityClass = net.minecraft.entity.Entity.class;
                    java.lang.reflect.Field f = null;
                    try {
                        f = entityClass.getDeclaredField("noFallDamage");
                    } catch (NoSuchFieldException ex) {
                        // Yarn'da farklı isim olusuna karsi alternatif ara
                        for (java.lang.reflect.Field field : entityClass.getDeclaredFields()) {
                            if (field.getName().toLowerCase().contains("nofall")) {
                                f = field;
                                break;
                            }
                        }
                    }
                    if (f != null) {
                        f.setAccessible(true);
                        f.setBoolean(client.player, true);
                    }
                } catch (Throwable ignored) {}

                // Fall distance cok buyurse hizi da kes (yere yakinken)
                try {
                    if (client.player.fallDistance > 4.0f) {
                        client.player.fallDistance = 0.0f;
                    }
                } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}
    }
}
