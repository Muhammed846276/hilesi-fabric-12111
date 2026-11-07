package com.example.hilesi.client;

import com.example.hilesi.module.ModuleManager;
import com.example.hilesi.screen.ClickGuiScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Guvenli baslatma: hata firlatsa bile oyun crash vermez, sadece loga yazar.
 */
public class HilesiClient implements ClientModInitializer {
    public static final String MOD_ID = "hilesi";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyMapping openMenuKey;

    @Override
    public void onInitializeClient() {
        try {
            LOGGER.info("Hilesi yukleniyor (v0.1.22+26.1.2)...");
            ModuleManager.init();

            KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));
            openMenuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                    "key.hilesi.open_menu",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_RIGHT_SHIFT,
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
                    ModuleManager.onTick(client);
                } catch (Throwable t) {
                    // Modtick'teki tek bir hatada oyunu crash'leme
                    LOGGER.error("Hilesi tick hatasi", t);
                }
            });
            LOGGER.info("Hilesi hazir. Menuyu acmak icin Sag Shift.");
        } catch (Throwable t) {
            LOGGER.error("Hilesi baslatilamadi (oyun devam edecek)", t);
        }
    }
}
