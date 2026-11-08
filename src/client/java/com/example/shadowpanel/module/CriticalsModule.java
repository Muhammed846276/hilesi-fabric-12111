package com.example.shadowpanel.module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
public class CriticalsModule extends Module {
    public CriticalsModule() { super("Criticals"); }
    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client == null || client.player == null || client.getNetworkHandler()==null) return;
        // Basit: saldiri aninda mini hop paketleri (sunucuya bagli)
    }
}
