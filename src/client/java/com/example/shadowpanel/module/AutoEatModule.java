package com.example.shadowpanel.module;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
public class AutoEatModule extends Module {
    private int foodLevel=16;
    public AutoEatModule(){ super("AutoEat","Aclikta yemek yer (deneysel)"); }
    public int getFoodLevel(){return foodLevel;} public void cycleFood(int d){foodLevel=Math.max(1,Math.min(20,foodLevel+d));}
    @Override public void onTick(Minecraft client){
        if(!isEnabled()||client==null||client.player==null||client.screen!=null) return;
        try {
            if (client.player.getFoodData().getFoodLevel() > foodLevel) { client.options.keyUse.setDown(false); return; }
            // elde yemek varsa kullanmayi dene
            ItemStack main = client.player.getMainHandItem();
            if (main != null && main.getItem().components().has(net.minecraft.core.component.DataComponents.FOOD)) {
                client.options.keyUse.setDown(true);
            } else {
                client.options.keyUse.setDown(false);
            }
        } catch (Throwable ignored) { try { client.options.keyUse.setDown(false); } catch(Throwable e){} }
    }
}
