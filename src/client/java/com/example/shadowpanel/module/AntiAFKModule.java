package com.example.shadowpanel.module;
import net.minecraft.client.Minecraft;
public class AntiAFKModule extends Module {
    private long last=0;
    public AntiAFKModule(){ super("AntiAFK","AFK atilmayi azaltir"); }
    @Override public void onTick(Minecraft client){
        if(!isEnabled()||client==null||client.player==null||client.gui.screen()!=null) return;
        long now=System.currentTimeMillis(); if(now-last<15000) return; last=now;
        try { float y=client.player.getYRot(); client.player.setYRot(y + (now%2==0?10f:-10f)); } catch (Throwable ignored){}
        try { client.options.keyJump.setDown(true); } catch(Throwable ignored){}
        try { client.options.keyJump.setDown(false); } catch(Throwable ignored){}
    }
}
