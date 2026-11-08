package com.example.hilesi.module;
import net.minecraft.client.MinecraftClient;
public class AutoEatModule extends Module {
    private int foodLevel=16; public AutoEatModule(){ super("AutoEat"); }
    public int getFoodLevel(){return foodLevel;} public void cycleFood(int d){foodLevel=Math.max(1,Math.min(20,foodLevel+d));}
    @Override public void onTick(MinecraftClient client){ if(!isEnabled()||client==null||client.player==null||client.currentScreen!=null) return; try{ if(client.player.getHungerManager().getFoodLevel()>foodLevel){ client.options.useKey.setPressed(false); return; } }catch(Throwable ignored){} }
}
