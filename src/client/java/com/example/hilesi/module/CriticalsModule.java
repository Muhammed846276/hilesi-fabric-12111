package com.example.hilesi.module;
import net.minecraft.client.Minecraft;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
public class CriticalsModule extends Module {
    public CriticalsModule() { super("Criticals"); }
    @Override
    public void onTick(Minecraft client) {
        if (!isEnabled() || client == null || client.player == null || client.getConnection()==null) return;
        // Basit: saldiri aninda mini hop paketleri (sunucuya bagli)
    }
}
