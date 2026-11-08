package com.example.hilesi.module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
public class AutoRespawnModule extends Module {
    public AutoRespawnModule(){ super("AutoRespawn","Otomatik yeniden dogma"); }
    @Override public void onTick(Minecraft client){
        if(!isEnabled()||client==null||client.player==null) return;
        try { if (client.screen instanceof DeathScreen) client.player.respawn(); } catch (Throwable ignored) {}
    }
}
