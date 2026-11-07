package com.example.hilesi.module;

import net.minecraft.client.MinecraftClient;

/**
 * Kill Aura - ADIM A (ayar menusu)
 * Su an sadece ayar degerleri tutuyor / menu uzerinden degistirilebiliyor.
 * Otomatik hedef odaklama ve saldiri davranisi bilincli olarak eklenmedi;
 * sonraki adimlarda ayri ayri eklenecek.
 */
public class KillAuraModule extends Module {

    // Ayarlar
    private double range = 3.0;        // 3.0 - 8.0, adim 0.5
    private int cps = 5;               // 1 - 20
    private int fov = 180;             // 30 - 360, adim 15
    private boolean playersOnly = false;
    private boolean autoSwitch = true;

    public KillAuraModule() {
        super("Kill Aura");
    }

    public double getRange() { return range; }
    public int getCps() { return cps; }
    public int getFov() { return fov; }
    public boolean isPlayersOnly() { return playersOnly; }
    public boolean isAutoSwitch() { return autoSwitch; }

    public void setRange(double range) {
        this.range = clamp(range, 3.0, 8.0);
    }

    public void setCps(int cps) {
        this.cps = clamp(cps, 1, 20);
    }

    public void setFov(int fov) {
        this.fov = clamp(fov, 30, 360);
    }

    public void setPlayersOnly(boolean v) { this.playersOnly = v; }
    public void setAutoSwitch(boolean v) { this.autoSwitch = v; }

    // UI'dan tikla degistir
    public void cycleRange(int dir) { setRange(range + 0.5 * dir); }
    public void cycleCps(int dir) { setCps(cps + dir); }
    public void cycleFov(int dir) { setFov(fov + 15 * dir); }
    public void togglePlayersOnly() { setPlayersOnly(!playersOnly); }
    public void toggleAutoSwitch() { setAutoSwitch(!autoSwitch); }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client == null) return;
        if (client.player == null || client.world == null) return;
        // TODO sonraki adim: hedef secimi + saldiri mantigi
        // Su an sadece ayar degerleri kullaniliyor (islevsel aura yok).
    }
}
