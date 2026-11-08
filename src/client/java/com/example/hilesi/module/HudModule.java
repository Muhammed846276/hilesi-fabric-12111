package com.example.hilesi.module;
import net.minecraft.client.Minecraft;
public class HudModule extends Module {
    public HudModule(){ super("HUD","Acik modulleri ekranda gosterir"); }
    @Override protected void onEnable(){ /* render via mixin/hook reads enabled state */ }
}
