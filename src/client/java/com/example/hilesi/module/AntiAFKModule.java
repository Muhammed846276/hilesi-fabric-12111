package com.example.hilesi.module;
import net.minecraft.client.MinecraftClient;
public class AntiAFKModule extends Module {
    private long last=0;
    public AntiAFKModule(){ super("AntiAFK"); }
    @Override public void onTick(MinecraftClient client){ if(!isEnabled()||client==null||client.player==null||client.currentScreen!=null) return; long now=System.currentTimeMillis(); if(now-last<15000) return; last=now; try{ client.player.setYaw(client.player.getYaw()+10f); }catch(Throwable ignored){} }
}
