package com.example.shadowpanel.module;

import net.minecraft.client.Minecraft;

public class ReachModule extends Module {
    private double extra = 1.5;
    public ReachModule() { super("Reach"); }
    public double getExtra() { return extra; }
    public void setExtra(double v) { extra = Math.max(0.0, Math.min(3.0, v)); }
    public void cycleExtra(int dir) { setExtra(extra + 0.25 * dir); }
    @Override
    public void onTick(Minecraft client) {
        // Not: Gercek reach sunucu tarafindan sinirlanir; bu modül client-side sinirli etki saglar.
        // (Mixin ile block/entity interaction menzili genisletilebilir - ileri asama.)
        if (!isEnabled() || client == null || client.player == null) return;
    }
}
