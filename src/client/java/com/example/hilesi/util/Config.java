package com.example.hilesi.util;

import com.example.hilesi.module.Module;
import com.example.hilesi.module.ModuleManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public final class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path path() { return FabricLoader.getInstance().getConfigDir().resolve("hilesi.json"); }
    private Config(){}
    public static void save() {
        try {
            JsonObject root = new JsonObject();
            JsonObject mods = new JsonObject();
            for (Module m : ModuleManager.getModules()) {
                JsonObject o = new JsonObject();
                o.addProperty("enabled", m.isEnabled());
                o.addProperty("bind", m.getBind());
                mods.add(m.getName(), o);
            }
            root.add("modules", mods);
            Files.createDirectories(path().getParent());
            Files.writeString(path(), GSON.toJson(root));
        } catch (Throwable ignored) {}
    }
    public static void load() {
        try {
            Path p = path();
            if (!Files.exists(p)) return;
            JsonObject root = GSON.fromJson(Files.readString(p), JsonObject.class);
            if (root == null || !root.has("modules")) return;
            JsonObject mods = root.getAsJsonObject("modules");
            for (Module m : ModuleManager.getModules()) {
                if (!mods.has(m.getName())) continue;
                JsonObject o = mods.getAsJsonObject(m.getName());
                try { if (o.has("bind")) m.setBind(o.get("bind").getAsInt()); } catch (Throwable ignored) {}
                try {
                    boolean en = o.has("enabled") && o.get("enabled").getAsBoolean();
                    if (en && !m.isEnabled()) m.toggle();
                    if (!en && m.isEnabled()) m.toggle();
                } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}
    }
}
