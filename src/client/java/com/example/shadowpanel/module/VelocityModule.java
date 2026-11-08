package com.example.shadowpanel.module;
import net.minecraft.client.Minecraft;
/** Velocity/AntiKnockback - tam etki icin mixin/packet hook gerekir (ileri). */
public class VelocityModule extends Module {
    private int horizontal = 0;
    private int vertical = 0;
    public VelocityModule() { super("Velocity"); }
    public int getHorizontal() { return horizontal; }
    public int getVertical() { return vertical; }
    public void cycleHorizontal(int d){ horizontal=Math.max(0,Math.min(100,horizontal+d*10)); }
    public void cycleVertical(int d){ vertical=Math.max(0,Math.min(100,vertical+d*10)); }
}
