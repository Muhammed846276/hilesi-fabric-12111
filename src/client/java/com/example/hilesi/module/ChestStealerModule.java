package com.example.hilesi.module;
import net.minecraft.client.Minecraft;
public class ChestStealerModule extends Module {
    private int delayMs=50; private long last=0;
    public ChestStealerModule(){ super("ChestStealer"); }
    public int getDelayMs(){return delayMs;} public void cycleDelay(int d){delayMs=Math.max(0,Math.min(500,delayMs+d*25));}
    @Override public void onTick(Minecraft client){
        if(!isEnabled()||client==null||client.player==null||client.gameMode==null) return;
        if(client.screen==null) return;
        if(!client.screen.getClass().getSimpleName().toLowerCase().contains("container") && !client.screen.getClass().getSimpleName().toLowerCase().contains("chest")) return;
        long now=System.currentTimeMillis(); if(now-last<delayMs) return;
        try{
            var h=client.player.containerMenu;
            int chestSlots = Math.max(0, h.slots.size()-36);
            for(int i=0;i<chestSlots;i++){
                var s=h.getSlot(i);
                if(s!=null && s.hasItem()){
                    // handleInventoryMouseClick(containerId, slotId, button, clickType, player)
                    for(var mth: client.gameMode.getClass().getMethods()){
                        if(mth.getName().toLowerCase().contains("handleinventorymouse") || mth.getName().toLowerCase().contains("clickslot")){
                            try{ mth.invoke(client.gameMode, h.containerId, i, 0, net.minecraft.world.inventory.ClickType.QUICK_MOVE, client.player); last=now; return; }catch(Throwable e){}
                        }
                    }
                    last=now; return;
                }
            }
        }catch(Throwable ignored){}
    }
}
