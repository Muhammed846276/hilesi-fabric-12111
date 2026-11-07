package com.example.hilesi.module;
import net.minecraft.client.Minecraft;

public class ChestStealerModule extends Module {
    private int delayMs = 50;
    private long last = 0;
    public ChestStealerModule() { super("ChestStealer"); }
    public int getDelayMs() { return delayMs; }
    public void cycleDelay(int d) { delayMs = Math.max(0, Math.min(500, delayMs + d * 25)); }
    @Override
    public void onTick(Minecraft client) {
        if (!isEnabled() || client == null || client.player == null || client.gameMode == null) return;
        if (client.gui.screen() == null) return;
        String sn = client.gui.screen().getClass().getSimpleName().toLowerCase();
        if (!sn.contains("container") && !sn.contains("chest") && !sn.contains("shulker") && !sn.contains("generic")) return;
        long now = System.currentTimeMillis();
        if (now - last < delayMs) return;
        try {
            var menu = client.player.containerMenu;
            int chestSlots = Math.max(0, menu.slots.size() - 36);
            for (int i = 0; i < chestSlots; i++) {
                var slot = menu.getSlot(i);
                if (slot != null && slot.hasItem()) {
                    // Reflection-based quick-move to survive mapping differences
                    try {
                        Class<?> clickTypeCls = Class.forName("net.minecraft.world.inventory.ClickType");
                        Object quickMove = Enum.valueOf((Class<Enum>) clickTypeCls.asSubclass(Enum.class), "QUICK_MOVE");
                        for (var mth : client.gameMode.getClass().getMethods()) {
                            String n = mth.getName().toLowerCase();
                            if ((n.contains("handleinventory") || n.contains("click")) && mth.getParameterCount() >= 5) {
                                try { mth.invoke(client.gameMode, menu.containerId, i, 0, quickMove, client.player); last = now; return; } catch (Throwable e) {}
                            }
                        }
                    } catch (Throwable e) {}
                    last = now;
                    return;
                }
            }
        } catch (Throwable ignored) {}
    }
}
