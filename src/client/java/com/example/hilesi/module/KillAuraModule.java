package com.example.hilesi.module;

import net.minecraft.client.MinecraftClient;

public class KillAuraModule extends Module {
    public KillAuraModule() {
        super("Kill Aura");
    }

    @Override
    protected void onEnable() {
        // Modul acildi - ayarlar/bilgi eklenebilir.
    }

    @Override
    protected void onDisable() {
        // Modul kapandi.
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null) return;

        // TODO: Buraya hedef bulma, mesafe/FOV kontrolu, saldiri gecikmesi
        // ve paket/etkilesim mantigi eklenebilir.
        // Bu iskelette otomatik saldiri davranisi bilincli olarak eklenmedi.
    }
}
