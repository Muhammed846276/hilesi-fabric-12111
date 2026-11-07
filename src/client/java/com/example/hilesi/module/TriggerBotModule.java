package com.example.hilesi.module;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;

public class TriggerBotModule extends Module {
    private int cps = 8;
    private boolean playersOnly = false;
    private long last = 0;
    public TriggerBotModule() { super("TriggerBot"); }
    public int getCps() { return cps; }
    public void setCps(int v) { cps = Math.max(1, Math.min(20, v)); }
    public void cycleCps(int dir) { setCps(cps + dir); }
    public boolean isPlayersOnly() { return playersOnly; }
    public void togglePlayersOnly() { playersOnly = !playersOnly; }
    @Override
    public void onTick(Minecraft client) {
        if (!isEnabled() || client == null || client.player == null || client.gameMode == null) return;
        if (client.screen != null) return;
        Entity t = client.crosshairPickEntity;
        if (!(t instanceof LivingEntity) || !t.isAlive()) return;
        if (playersOnly && !(t instanceof net.minecraft.world.entity.player.Player)) return;
        long now = System.currentTimeMillis();
        if (now - last < 1000L / Math.max(1, cps)) return;
        try {
            if (client.player.getAttackStrengthScale(0.5f) < 1.0f) return;
        } catch (Throwable ignored) {}
        try {
            client.gameMode.attack(client.player, t);
            client.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            last = now;
        } catch (Throwable ignored) {}
    }
}
