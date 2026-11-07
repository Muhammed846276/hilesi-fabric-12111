package com.example.hilesi.module;
import net.minecraft.client.Minecraft;
/** ESP/Tracers/Nametags - world render hook/mixin gerekir (ileri/deneysel). */
public class EspModule extends Module {
    private boolean tracers=false, nametags=true;
    public EspModule(){ super("ESP"); }
    public boolean isTracers(){return tracers;} public void toggleTracers(){tracers=!tracers;}
    public boolean isNametags(){return nametags;} public void toggleNametags(){nametags=!nametags;}
}
