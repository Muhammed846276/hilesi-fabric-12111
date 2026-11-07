package com.example.hilesi.module;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();

    private ModuleManager() {}

    public static void init() {
        MODULES.clear();
        MODULES.add(new KillAuraModule());
        MODULES.add(new SpearAssistModule());
        MODULES.add(new FlyModule());
        MODULES.add(new NoFallModule());
        MODULES.add(new FullBrightModule());
        // Sonraki adimlarda yeni moduller buraya eklenecek.
    }

    public static List<Module> getModules() {
        return Collections.unmodifiableList(MODULES);
    }

    public static void onTick(Minecraft client) {
        for (Module module : MODULES) {
            try {
                module.onTick(client);
            } catch (Throwable ignored) {}
        }
    }
}
