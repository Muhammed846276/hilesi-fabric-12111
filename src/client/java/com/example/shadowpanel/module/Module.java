package com.example.shadowpanel.module;

import net.minecraft.client.MinecraftClient;

public abstract class Module {
    private final String name;
    private final String description;
    private boolean enabled;
    private int bind = -1;

    protected Module(String name) { this(name, ""); }
    protected Module(String name, String description) {
        this.name = name;
        this.description = description == null ? "" : description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isEnabled() { return enabled; }
    public int getBind() { return bind; }
    public void setBind(int bind) { this.bind = bind; }
    public void toggle() { setEnabled(!enabled); }
    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) onEnable(); else onDisable();
        try { com.example.shadowpanel.util.Config.save(); } catch (Throwable ignored) {}
    }
    protected void onEnable() {}
    protected void onDisable() {}
    public void onTick(MinecraftClient client) {}
}
