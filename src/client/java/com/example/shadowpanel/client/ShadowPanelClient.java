package com.example.shadowpanel.client;

import com.example.shadowpanel.module.ModuleManager;
import com.example.shadowpanel.module.XRayModule;
import com.example.shadowpanel.screen.ClickGuiScreen;
import com.example.shadowpanel.module.ShadowHudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShadowPanelClient implements ClientModInitializer {
    public static final String MOD_ID = "shadowpanel";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyBinding openMenuKey;
    private static KeyBinding xrayKey;

    @Override
    public void onInitializeClient() {
        try {
            LOGGER.info("ShadowPanel yukleniyor (v0.2.3+1.21.11)...");
            ModuleManager.init();

            openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.shadowpanel.menu",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_RIGHT_SHIFT,
                    KeyBinding.Category.GAMEPLAY
            ));
            xrayKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.shadowpanel.xray",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_X,
                    KeyBinding.Category.GAMEPLAY
            ));

            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                try {
                    if (openMenuKey != null) {
                        while (openMenuKey.wasPressed()) {
                            if (client.currentScreen == null) client.setScreen(new ClickGuiScreen());
                            else if (client.currentScreen instanceof ClickGuiScreen) client.setScreen(null);
                        }
                    }
                    if (xrayKey != null) {
                        while (xrayKey.wasPressed()) {
                            XRayModule xr = ModuleManager.get(XRayModule.class);
                            if (xr != null) xr.toggle();
                        }
                    }
                    ModuleManager.onTick(client);
                } catch (Throwable t) {
                    LOGGER.error("ShadowPanel tick hatasi", t);
                }
            });
            try {
                HudRenderCallback.EVENT.register((context, tickCounter) -> {
                    try { ShadowHudRenderer.render(net.minecraft.client.MinecraftClient.getInstance(), context); } catch (Throwable ignored) {}
                });
            } catch (Throwable ignored) {}

            LOGGER.info("ShadowPanel hazir. Menu: Sag Shift, XRay: X.");
        } catch (Throwable t) {
            LOGGER.error("ShadowPanel baslatilamadi (oyun devam edecek)", t);
        }
    }
}
