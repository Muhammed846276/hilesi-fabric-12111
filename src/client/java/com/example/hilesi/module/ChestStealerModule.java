package com.example.hilesi.module;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.ClickType;
public class ChestStealerModule extends Module {
    private int delayMs=50; private long last=0;
    public ChestStealerModule(){ super("ChestStealer"); }
    public int getDelayMs(){return delayMs;} public void cycleDelay(int d){delayMs=Math.max(0,Math.min(500,delayMs+d*25));}
    @Override public void onTick(Minecraft client){
        if(!isEnabled()||client==null||client.player==null||client.gameMode==null) return;
        if(!(client.screen instanceof net.minecraft.client.gui.screens.inventory.ContainerScreen)) return;
        long now=System.currentTimeMillis(); if(now-last<delayMs) return;
        try{
            var h=client.player.containerMenu;
            for(int i=0;i<h.slots.size()-36;i++){
                var s=h.getSlot(i);
                if(s!=null && s.hasStack()){ client.gameMode.clickSlot(h.syncId,i,0,ClickType.QUICK_MOVE,client.player); last=now; return; }
            }
        }catch(Throwable ignored){}
    }
}
