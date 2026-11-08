package com.example.shadowpanel.client;

import com.example.shadowpanel.module.ModuleManager;
import com.example.shadowpanel.module.XRayModule;
import com.example.shadowpanel.screen.ClickGuiScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShadowPanelClient implements ClientModInitializer {
    public static final String MOD_ID = "shadowpanel";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyMapping openMenuKey;
    private static KeyMapping xrayKey;

    @Override
    public void onInitializeClient() {
        try {
            LOGGER.info("ShadowPanel yukleniyor (v0.2.0+26.1.2)...");
            ModuleManager.init();

            KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));
            openMenuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                    "key.shadowpanel.menu",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_RIGHT_SHIFT,
                    category
            ));
            xrayKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                    "key.shadowpanel.xray",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_X,
                    category
            ));

            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                try {
                    if (openMenuKey != null) {
                        while (openMenuKey.consumeClick()) {
                            if (client.screen == null) {
                                client.setScreen(new ClickGuiScreen());
                            } else if (client.screen instanceof ClickGuiScreen) {
                                client.setScreen(null);
                            }
                        }
                    }
                    if (xrayKey != null) {
                        while (xrayKey.consumeClick()) {
                            XRayModule xr = ModuleManager.get(XRayModule.class);
                            if (xr != null) xr.toggle();
                        }
                    }
                    ModuleManager.onTick(client);
                } catch (Throwable t) {
                    LOGGER.error("ShadowPanel tick hatasi", t);
                }
            });
            LOGGER.info("ShadowPanel hazir. Sag Shift=menu, X=XRay. Keybinds kayitli.");
        } catch (Throwable t) {
            LOGGER.error("ShadowPanel baslatilamadi (oyun devam edecek)", t);
        }
    }
}
