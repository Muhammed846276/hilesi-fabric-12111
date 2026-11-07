package com.example.hilesi.client;

import com.example.hilesi.module.ModuleManager;
import com.example.hilesi.module.XRayModule;
import com.example.hilesi.screen.ClickGuiScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HilesiClient implements ClientModInitializer {
    public static final String MOD_ID = "hilesi";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyMapping openMenuKey;
    private static KeyMapping xrayKey;

    @Override
    public void onInitializeClient() {
        try {
            LOGGER.info("Hilesi yukleniyor (v0.1.24+26.2)...");
            ModuleManager.init();

            openMenuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                    "key.hilesi.open_menu",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_RIGHT_SHIFT,
                    KeyMapping.Category.register(net.minecraft.resources.Identifier.fromNamespaceAndPath("hilesi","main"))
            ));
            xrayKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                    "key.hilesi.toggle_xray",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_X,
                    KeyMapping.Category.register(net.minecraft.resources.Identifier.fromNamespaceAndPath("hilesi","main"))
            ));

            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                try {
                    if (openMenuKey != null) {
                        while (openMenuKey.consumeClick()) {
                            if (client.gui.screen() == null) client.gui.setScreen(new ClickGuiScreen());
                            else if (client.gui.screen() instanceof ClickGuiScreen) client.gui.setScreen(null);
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
                    LOGGER.error("Hilesi tick hatasi", t);
                }
            });
            LOGGER.info("Hilesi hazir. Menu: Sag Shift, XRay: X.");
        } catch (Throwable t) {
            LOGGER.error("Hilesi baslatilamadi (oyun devam edecek)", t);
        }
    }
}
