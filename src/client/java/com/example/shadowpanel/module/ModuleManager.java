package com.example.shadowpanel.module;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();
    private static final Map<Integer, Boolean> BIND_STATE = new HashMap<>();

    private ModuleManager() {}

    public static void init() {
        MODULES.clear();
        addSafe(new KillAuraModule());
        addSafe(new TriggerBotModule());
        addSafe(new CriticalsModule());
        addSafe(new VelocityModule());
        addSafe(new ReachModule());
        addSafe(new SpearAssistModule());
        addSafe(new AutoMaceModule());
        addSafe(new AutoSprintModule());
        addSafe(new SpeedModule());
        addSafe(new StepModule());
        addSafe(new FlyModule());
        addSafe(new NoFallModule());
        addSafe(new NoSlowModule());
        addSafe(new ScaffoldModule());
        addSafe(new SafeWalkModule());
        addSafe(new FastPlaceModule());
        addSafe(new FastBreakModule());
        addSafe(new FastUseModule());
        addSafe(new AutoToolModule());
        addSafe(new ChestStealerModule());
        addSafe(new AutoArmorModule());
        addSafe(new AutoEatModule());
        addSafe(new AutoRespawnModule());
        addSafe(new AntiAFKModule());
        addSafe(new MiddleClickFriendModule());
        addSafe(new FreecamModule());
        addSafe(new TimerModule());
        addSafe(new BlinkModule());
        addSafe(new FullBrightModule());
        addSafe(new XRayModule());
        addSafe(new EspModule());
        addSafe(new ChestEspModule());
        addSafe(new SearchModule());
        addSafe(new HudModule());
        addSafe(new InfoHudModule());
        try { com.example.shadowpanel.util.Config.load(); } catch (Throwable ignored) {}
    }

    private static void addSafe(Module m) {
        try { if (m != null) MODULES.add(m); } catch (Throwable ignored) {}
    }

    public static List<Module> getModules() { return Collections.unmodifiableList(MODULES); }

    public static <T extends Module> T get(Class<T> cls) {
        for (Module m : MODULES) if (cls.isInstance(m)) return cls.cast(m);
        return null;
    }

    public static void onTick(Minecraft client) {
        try { handleBinds(client); } catch (Throwable ignored) {}
        for (Module module : MODULES) {
            try { module.onTick(client); } catch (Throwable ignored) {}
        }
    }

    private static void handleBinds(Minecraft client) {
        if (client == null || client.getWindow() == null) return;
        long win = client.getWindow().handle();
        if (client.screen != null) return; // don't toggle while in screens, except our GUI handles binds
        for (Module m : MODULES) {
            int b = m.getBind();
            if (b <= 0) continue;
            boolean down = false;
            try { down = GLFW.glfwGetKey(win, b) == GLFW.GLFW_PRESS; } catch (Throwable ignored) {}
            boolean was = BIND_STATE.getOrDefault(b, false);
            BIND_STATE.put(b, down);
            if (down && !was) m.toggle();
        }
    }
}
