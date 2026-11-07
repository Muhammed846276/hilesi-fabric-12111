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
        addSafe(new TriggerBotModule());
        addSafe(new CriticalsModule());
        addSafe(new VelocityModule());
        addSafe(new ReachModule());
        addSafe(new SpearAssistModule());
        addSafe(new AutoSprintModule());
        addSafe(new SpeedModule());
        addSafe(new StepModule());
        addSafe(new FlyModule());
        addSafe(new NoFallModule());
        addSafe(new NoSlowModule());
        addSafe(new ScaffoldModule());
        addSafe(new FastPlaceModule());
        addSafe(new FastBreakModule());
        addSafe(new AutoToolModule());
        addSafe(new ChestStealerModule());
        addSafe(new AutoArmorModule());
        addSafe(new FreecamModule());
        addSafe(new TimerModule());
        addSafe(new FullBrightModule());
        addSafe(new XRayModule());
        addSafe(new EspModule());
    }

    private static void addSafe(Module m) {
        try {
            if (m != null) MODULES.add(m);
        } catch (Throwable ignored) {}
    }

    public static List<Module> getModules() {
        return Collections.unmodifiableList(MODULES);
    }

    public static <T extends Module> T get(Class<T> cls) {
        for (Module m : MODULES) if (cls.isInstance(m)) return cls.cast(m);
        return null;
    }

    public static void onTick(MinecraftClient client) {
        for (Module module : MODULES) {
            try {
                module.onTick(client);
            } catch (Throwable ignored) {}
        }
    }
}
