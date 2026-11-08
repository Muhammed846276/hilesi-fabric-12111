package com.example.hilesi.module;
import net.minecraft.client.Minecraft;
public class FastUseModule extends Module {
    public FastUseModule(){ super("FastUse","Kullanim gecikmesini azaltir (deneysel)"); }
    @Override public void onTick(Minecraft client){
        if(!isEnabled()||client==null) return;
        try { // reflection: rightClickDelay / useItemRemaining ticks differ; attempt common fields
            for (var f: client.getClass().getDeclaredFields()){
                String n=f.getName().toLowerCase();
                if(f.getType()==int.class && (n.contains("rightclickdelay")||n.contains("useitemdelay"))) { f.setAccessible(true); if(f.getInt(client)>0) f.setInt(client,0); }
            }
        } catch (Throwable ignored){}
    }
}
