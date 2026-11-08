package com.example.shadowpanel.module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
public class AutoRespawnModule extends Module {
    public AutoRespawnModule(){ super("AutoRespawn"); }
    @Override public void onTick(MinecraftClient client){ if(!isEnabled()||client==null||client.player==null) return; try { if(client.currentScreen instanceof DeathScreen){ client.player.requestRespawn(); client.setScreen(null);} } catch(Throwable ignored){} }
}
