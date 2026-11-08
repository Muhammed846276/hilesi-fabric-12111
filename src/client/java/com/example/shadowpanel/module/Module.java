package com.example.shadowpanel.module;

import net.minecraft.client.MinecraftClient;

public abstract class Module {
    private final String name;
    private boolean enabled;

    protected Module(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    protected void onEnable() {}

    protected void onDisable() {}

    public void onTick(MinecraftClient client) {}
}
