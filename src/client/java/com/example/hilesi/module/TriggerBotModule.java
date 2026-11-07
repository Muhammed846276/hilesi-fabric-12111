package com.example.hilesi.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;

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
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client == null || client.player == null || client.interactionManager == null) return;
        if (client.currentScreen != null) return;
        Entity t = client.targetedEntity;
        if (!(t instanceof LivingEntity) || !t.isAlive()) return;
        if (playersOnly && !(t instanceof net.minecraft.entity.player.PlayerEntity)) return;
        long now = System.currentTimeMillis();
        if (now - last < 1000L / Math.max(1, cps)) return;
        try {
            if (client.player.getAttackCooldownProgress(0.5f) < 1.0f) return;
        } catch (Throwable ignored) {}
        try {
            client.interactionManager.attackEntity(client.player, t);
            client.player.swingHand(Hand.MAIN_HAND);
            last = now;
        } catch (Throwable ignored) {}
    }
}
