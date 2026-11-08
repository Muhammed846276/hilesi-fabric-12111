package com.example.hilesi.module;
import net.minecraft.client.MinecraftClient;
public class InfoHudModule extends Module {
    private boolean coords=true, fps=true;
    public InfoHudModule(){ super("InfoHUD"); }
    public boolean isCoords(){return coords;} public boolean isFps(){return fps;}
    public void toggleCoords(){coords=!coords;} public void toggleFps(){fps=!fps;}
}
