package com.example.hilesi.module;
import net.minecraft.client.MinecraftClient;
/** Timer - tick hizlandirma icin mixin gerekir (ileri/deneysel). */
public class TimerModule extends Module {
    private double speed = 1.2;
    public TimerModule(){ super("Timer"); }
    public double getSpeed(){return speed;}
    public void cycleSpeed(int d){ speed=Math.max(0.1,Math.min(5.0, speed+d*0.1)); }
}
