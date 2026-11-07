package com.example.hilesi.module;

import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();

    private ModuleManager() {}

    public static void init() {
        MODULES.clear();
        addSafe(new KillAuraModule());
        addSafe(new SpearAssistModule());
        addSafe(new FlyModule());
        addSafe(new NoFallModule());
        addSafe(new FullBrightModule());
        // Sonraki adimlarda yeni moduller buraya eklenecek.
    }

    private static void addSafe(Module m) {
        try {
            if (m != null) MODULES.add(m);
        } catch (Throwable ignored) {}
    }

    public static List<Module> getModules() {
        return Collections.unmodifiableList(MODULES);
    }

    public static void onTick(MinecraftClient client) {
        for (Module module : MODULES) {
            try {
                module.onTick(client);
            } catch (Throwable ignored) {}
        }
    }
}
