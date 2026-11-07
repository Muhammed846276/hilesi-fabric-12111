package com.example.hilesi.module;
import net.minecraft.client.Minecraft;
public class ChestStealerModule extends Module {
    private int delayMs=50; private long last=0;
    public ChestStealerModule(){ super("ChestStealer"); }
    public int getDelayMs(){return delayMs;} public void cycleDelay(int d){delayMs=Math.max(0,Math.min(500,delayMs+d*25));}
    @Override public void onTick(Minecraft client){
        if(!isEnabled()||client==null||client.player==null||client.gameMode==null) return;
        if(client.screen==null) return;
        String sn = client.screen.getClass().getSimpleName().toLowerCase();
        if(!sn.contains("container") && !sn.contains("chest") && !sn.contains("shulker")) return;
        long now=System.currentTimeMillis(); if(now-last<delayMs) return;
        try{
            var h=client.player.containerMenu;
            int chestSlots = Math.max(0, h.slots.size()-36);
            Object quickMove = null;
            try { for (Object o : net.minecraft.world.inventory.ClickType.values()) if(o.toString().contains("QUICK")) { quickMove=o; break; } } catch(Throwable e){}
            for(int i=0;i<chestSlots;i++){
                var s=h.getSlot(i);
                if(s!=null && s.hasItem() && quickMove!=null){
                    for(var mth: client.gameMode.getClass().getMethods()){
                        String n=mth.getName().toLowerCase();
                        if(n.contains("handleinventory") || n.contains("click")) {
                            try{ mth.invoke(client.gameMode, h.containerId, i, 0, quickMove, client.player); last=now; return; }catch(Throwable e){}
                        }
                    }
                    last=now; return;
                }
            }
        }catch(Throwable ignored){}
    }
}
