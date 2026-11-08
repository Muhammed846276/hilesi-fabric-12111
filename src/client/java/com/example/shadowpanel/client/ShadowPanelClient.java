package com.example.shadowpanel.client;

import com.example.shadowpanel.module.ModuleManager;
import com.example.shadowpanel.screen.ClickGuiScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShadowPanelClient implements ClientModInitializer {
    public static final String MOD_ID = "shadowpanel";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        try {
            LOGGER.info("Shadow Panel yukleniyor (v0.1.22)...");
            ModuleManager.init();
            UpdateChecker.checkAsync();

            openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.shadowpanel.open_menu",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_RIGHT_SHIFT,
                    KeyBinding.Category.GAMEPLAY
            ));

            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                try {
                    UpdateChecker.tickShowMessage(client);
                    if (openMenuKey != null) {
                        while (openMenuKey.wasPressed()) {
                            if (client.currentScreen == null) {
                                client.setScreen(new ClickGuiScreen());
                            } else if (client.currentScreen instanceof ClickGuiScreen) {
                                client.setScreen(null);
                            }
                        }
                    }
                    ModuleManager.onTick(client);
                } catch (Throwable t) {
                    LOGGER.error("Shadow Panel tick hatasi", t);
                }
            });
            LOGGER.info("Shadow Panel hazir. Menuyu acmak icin Sag Shift.");
        } catch (Throwable t) {
            LOGGER.error("Shadow Panel baslatilamadi (oyun devam edecek)", t);
        }
    }
}
