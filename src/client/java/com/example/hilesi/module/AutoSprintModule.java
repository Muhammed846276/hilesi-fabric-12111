package com.example.hilesi.module;

import net.minecraft.client.Minecraft;

public class AutoSprintModule extends Module {
    public AutoSprintModule() { super("AutoSprint"); }
    @Override
    public void onTick(Minecraft client) {
        if (!isEnabled() || client == null || client.player == null) return;
        if (client.screen != null) return;
        try {
            boolean fwd = false;
            try { fwd = client.player.input.forwardImpulse > 0; } catch(Throwable e){ fwd = client.options.keyUp.isDown(); }
            if (fwd && !client.player.isShiftKeyDown()) client.player.setSprinting(true);
        } catch (Throwable ignored) {}
    }
}
