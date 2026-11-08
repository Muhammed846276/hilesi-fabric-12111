package com.example.shadowpanel.module;
import net.minecraft.client.Minecraft;
public class InfoHudModule extends Module {
    private boolean coords=true, fps=true;
    public InfoHudModule(){ super("InfoHUD","Koordinat/FPS bilgisi"); }
    public boolean isCoords(){return coords;} public boolean isFps(){return fps;}
    public void toggleCoords(){coords=!coords;} public void toggleFps(){fps=!fps;}
}
