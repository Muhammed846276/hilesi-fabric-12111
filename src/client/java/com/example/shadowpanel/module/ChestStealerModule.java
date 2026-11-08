package com.example.shadowpanel.module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.slot.SlotActionType;
public class ChestStealerModule extends Module {
    private int delayMs=50; private long last=0;
    public ChestStealerModule(){ super("ChestStealer"); }
    public int getDelayMs(){return delayMs;} public void cycleDelay(int d){delayMs=Math.max(0,Math.min(500,delayMs+d*25));}
    @Override public void onTick(MinecraftClient client){
        if(!isEnabled()||client==null||client.player==null||client.interactionManager==null) return;
        if(!(client.currentScreen instanceof net.minecraft.client.gui.screen.ingame.GenericContainerScreen)) return;
        long now=System.currentTimeMillis(); if(now-last<delayMs) return;
        try{
            var h=client.player.currentScreenHandler;
            for(int i=0;i<h.slots.size()-36;i++){
                var s=h.getSlot(i);
                if(s!=null && s.hasStack()){ client.interactionManager.clickSlot(h.syncId,i,0,SlotActionType.QUICK_MOVE,client.player); last=now; return; }
            }
        }catch(Throwable ignored){}
    }
}
