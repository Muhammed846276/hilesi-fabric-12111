package com.example.hilesi.screen;

import com.example.hilesi.module.FlyModule;
import com.example.hilesi.module.KillAuraModule;
import com.example.hilesi.module.Module;
import com.example.hilesi.module.ModuleManager;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Sag Shift menusu.
 * - Modul satirina sol tik: ON/OFF
 * - Ayar satirina sol tik: +1 adim, sag tik: -1 adim
 */
public class ClickGuiScreen extends Screen {
    private static final int PANEL_X = 15;
    private static final int PANEL_Y = 15;
    private static final int PANEL_W = 230;
    private static final int HEADER_H = 18;
    private static final int ROW_H = 16;
    private static final int SETTING_ROW_H = 14;
    private static final int PADDING = 6;

    private static final int SETTING_COUNT = 6;
    private static final int FLY_SETTING_COUNT = 2;

    public ClickGuiScreen() {
        super(Text.literal("Hilesi Menu"));
    }

    private int panelHeight() {
        int rows = 0;
        for (Module m : ModuleManager.getModules()) {
            rows += ROW_H;
            if (m instanceof KillAuraModule) rows += ROW_H + SETTING_COUNT * SETTING_ROW_H;
            else if (m instanceof FlyModule) rows += ROW_H + FLY_SETTING_COUNT * SETTING_ROW_H;
        }
        return HEADER_H + PADDING + rows + PADDING;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int h = panelHeight();
        context.fill(PANEL_X, PANEL_Y, PANEL_X + PANEL_W, PANEL_Y + h, 0xCC101010);
        context.fill(PANEL_X, PANEL_Y, PANEL_X + PANEL_W, PANEL_Y + HEADER_H, 0xFF2A6FDB);
        context.drawTextWithShadow(this.textRenderer, "Hilesi - Sag Shift ile kapat",
                PANEL_X + 6, PANEL_Y + 5, 0xFFFFFF);

        int y = PANEL_Y + HEADER_H + PADDING;
        for (Module module : ModuleManager.getModules()) {
            boolean hovered = mouseX >= PANEL_X && mouseX <= PANEL_X + PANEL_W
                    && mouseY >= y - 2 && mouseY <= y + ROW_H - 2;
            int modColor = module.isEnabled() ? 0xFF55FF55 : 0xFFCCCCCC;
            if (hovered) context.fill(PANEL_X + 2, y - 2, PANEL_X + PANEL_W - 2, y + ROW_H - 3, 0x33FFFFFF);
            context.drawTextWithShadow(this.textRenderer,
                    (module.isEnabled() ? "[ON] " : "[OFF] ") + module.getName(),
                    PANEL_X + PADDING, y, modColor);
            y += ROW_H;

            if (module instanceof KillAuraModule k) {
                y = renderKillAuraSettings(context, k, y, mouseX, mouseY);
            } else if (module instanceof FlyModule f) {
                y = renderFlySettings(context, f, y, mouseX, mouseY);
            }
        }
        super.render(context, mouseX, mouseY, delta);
    }

    private int renderKillAuraSettings(DrawContext ctx, KillAuraModule k, int y, int mouseX, int mouseY) {
        ctx.drawTextWithShadow(this.textRenderer, "--- Ayarlar (sol:+ sag:-) ---",
                PANEL_X + PADDING + 8, y, 0xFFAAAAAA);
        y += ROW_H;
        String[] lines = new String[]{
                "Menzil: " + String.format("%.1f", k.getRange()) + " (3-8)",
                "CPS: " + k.getCps() + " (1-20)",
                "FOV: " + k.getFov() + " (30-360)",
                "Sadece Oyuncular: " + (k.isPlayersOnly() ? "ON" : "OFF"),
                "Otomatik Hedef: " + (k.isAutoSwitch() ? "ON" : "OFF"),
                "Oto Aim: " + (k.isAutoAim() ? "ON" : "OFF")
        };
        for (String line : lines) {
            boolean hovered = mouseX >= PANEL_X && mouseX <= PANEL_X + PANEL_W
                    && mouseY >= y - 1 && mouseY <= y + SETTING_ROW_H - 1;
            if (hovered) ctx.fill(PANEL_X + 4, y - 1, PANEL_X + PANEL_W - 4, y + SETTING_ROW_H - 2, 0x22FFFFFF);
            ctx.drawTextWithShadow(this.textRenderer, line, PANEL_X + PADDING + 8, y, 0xFFDDDDDD);
            y += SETTING_ROW_H;
        }
        return y;
    }

    private int renderFlySettings(DrawContext ctx, FlyModule f, int y, int mouseX, int mouseY) {
        ctx.drawTextWithShadow(this.textRenderer, "--- Fly Ayarlari (sol:+ sag:-) ---",
                PANEL_X + PADDING + 8, y, 0xFFAAAAAA);
        y += ROW_H;
        String[] lines = new String[]{
                "Ucus Hizi: " + String.format("%.2f", f.getFlySpeed()) + " (0.02-0.20)",
                "Hiz %: " + Math.round((f.getFlySpeed() - 0.02f) / 0.18f * 100) + "%"
        };
        for (String line : lines) {
            boolean hovered = mouseX >= PANEL_X && mouseX <= PANEL_X + PANEL_W
                    && mouseY >= y - 1 && mouseY <= y + SETTING_ROW_H - 1;
            if (hovered) ctx.fill(PANEL_X + 4, y - 1, PANEL_X + PANEL_W - 4, y + SETTING_ROW_H - 2, 0x22FFFFFF);
            ctx.drawTextWithShadow(this.textRenderer, line, PANEL_X + PADDING + 8, y, 0xFFDDDDDD);
            y += SETTING_ROW_H;
        }
        return y;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (button != 0 && button != 1) return super.mouseClicked(click, doubled);
        int dir = (button == 0) ? 1 : -1;

        int y = PANEL_Y + HEADER_H + PADDING;
        for (Module module : ModuleManager.getModules()) {
            boolean inModuleRow = mouseX >= PANEL_X && mouseX <= PANEL_X + PANEL_W
                    && mouseY >= y - 2 && mouseY <= y + ROW_H - 2;
            if (inModuleRow) {
                module.toggle();
                return true;
            }
            y += ROW_H;

            if (module instanceof KillAuraModule k) {
                y += ROW_H;
                for (int i = 0; i < SETTING_COUNT; i++) {
                    boolean inSetting = mouseX >= PANEL_X && mouseX <= PANEL_X + PANEL_W
                            && mouseY >= y - 1 && mouseY <= y + SETTING_ROW_H - 1;
                    if (inSetting) {
                        switch (i) {
                            case 0 -> k.cycleRange(dir);
                            case 1 -> k.cycleCps(dir);
                            case 2 -> k.cycleFov(dir);
                            case 3 -> k.togglePlayersOnly();
                            case 4 -> k.toggleAutoSwitch();
                            case 5 -> k.toggleAutoAim();
                        }
                        return true;
                    }
                    y += SETTING_ROW_H;
                }
            } else if (module instanceof FlyModule f) {
                y += ROW_H;
                for (int i = 0; i < FLY_SETTING_COUNT; i++) {
                    boolean inSetting = mouseX >= PANEL_X && mouseX <= PANEL_X + PANEL_W
                            && mouseY >= y - 1 && mouseY <= y + SETTING_ROW_H - 1;
                    if (inSetting) {
                        f.cycleSpeed(dir);
                        return true;
                    }
                    y += SETTING_ROW_H;
                }
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
