package com.example.hilesi.client;

import com.example.hilesi.module.ModuleManager;
import com.example.hilesi.module.XRayModule;
import com.example.hilesi.screen.ClickGuiScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HilesiClient implements ClientModInitializer {
    public static final String MOD_ID = "hilesi";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyBinding openMenuKey;
    private static KeyBinding xrayKey;

    @Override
    public void onInitializeClient() {
        try {
            LOGGER.info("Hilesi yukleniyor (v0.1.32+1.21.11)...");
            ModuleManager.init();

            openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.hilesi.open_menu",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_RIGHT_SHIFT,
                    KeyBinding.Category.GAMEPLAY
            ));
            xrayKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.hilesi.toggle_xray",
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
                    LOGGER.error("Hilesi tick hatasi", t);
                }
            });
            LOGGER.info("Hilesi hazir. Menu: Sag Shift, XRay: X.");
        } catch (Throwable t) {
            LOGGER.error("Hilesi baslatilamadi (oyun devam edecek)", t);
        }
    }
}
