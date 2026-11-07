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
 * Sag Shift menusu - floating (suruklenebilir) v3
 * Header'i tut surukle, birak.
 */
public class ClickGuiScreen extends Screen {
    private static final int PANEL_W = 260;
    private static final int HEADER_H = 34;
    private static final int ROW_H = 20;
    private static final int SETTING_ROW_H = 16;
    private static final int PADDING = 8;
    private static final int FOOTER_H = 18;

    private static final int AURA_SETTING_COUNT = 6;
    private static final int FLY_SETTING_COUNT = 3;

    private static final int BG = 0xE6121418;
    private static final int BORDER = 0xFF3A4150;
    private static final int HEADER_BG = 0xFF1E5BB8;
    private static final int HEADER_ACCENT = 0xFF38BDF8;
    private static final int ROW_BG = 0xFF1B2028;
    private static final int ROW_HOVER = 0xFF2A3342;
    private static final int SETTING_BG = 0xFF151A21;
    private static final int SETTING_HOVER = 0xFF232D3C;
    private static final int TEXT = 0xFFFFFFFF;
    private static final int MUTED = 0xFF9AA6B2;
    private static final int GREEN = 0xFF22C55E;
    private static final int RED = 0xFFEF4444;
    private static final int BLUE = 0xFF38BDF8;

    // Floating konumu (her acilista sifirlanir)
    private int panelX = 18;
    private int panelY = 18;
    private boolean dragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public ClickGuiScreen() {
        super(Text.literal("Hilesi Menu"));
    }

    private int contentHeight() {
        int rows = 0;
        for (Module m : ModuleManager.getModules()) {
            rows += ROW_H + 2;
            if (m instanceof KillAuraModule) rows += (ROW_H + 2) + AURA_SETTING_COUNT * SETTING_ROW_H + 4;
            else if (m instanceof FlyModule) rows += (ROW_H + 2) + FLY_SETTING_COUNT * SETTING_ROW_H + 4;
            else rows += 4;
        }
        return HEADER_H + rows + FOOTER_H + PADDING * 2;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        if (dragging) {
            panelX = mouseX - dragOffsetX;
            panelY = mouseY - dragOffsetY;
        }
        int h = contentHeight();
        int x = panelX, y0 = panelY;

        ctx.fill(x + 3, y0 + 4, x + PANEL_W + 3, y0 + h + 4, 0x66000000);
        ctx.fill(x, y0, x + PANEL_W, y0 + h, BG);
        ctx.fill(x, y0, x + PANEL_W, y0 + 1, BORDER);
        ctx.fill(x, y0 + h - 1, x + PANEL_W, y0 + h, BORDER);
        ctx.fill(x, y0, x + 1, y0 + h, BORDER);
        ctx.fill(x + PANEL_W - 1, y0, x + PANEL_W, y0 + h, BORDER);

        drawHeader(ctx);
        int y = y0 + HEADER_H + 2;
        for (Module module : ModuleManager.getModules()) {
            boolean hovered = mouseX >= x + 4 && mouseX <= x + PANEL_W - 4
                    && mouseY >= y && mouseY <= y + ROW_H;
            drawModuleRow(ctx, module, y, hovered);
            y += ROW_H + 2;
            if (module instanceof KillAuraModule k) y = renderAura(ctx, k, y, mouseX, mouseY);
            else if (module instanceof FlyModule f) y = renderFly(ctx, f, y, mouseX, mouseY);
            else y += 4;
        }
        drawFooter(ctx, y0 + h - FOOTER_H);
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawHeader(DrawContext ctx) {
        int x = panelX, y0 = panelY;
        ctx.fill(x, y0, x + PANEL_W, y0 + HEADER_H, HEADER_BG);
        ctx.fill(x, y0 + HEADER_H - 2, x + PANEL_W, y0 + HEADER_H, HEADER_ACCENT);
        ctx.drawTextWithShadow(this.textRenderer, "H I L E S I", x + 10, y0 + 6, TEXT);
        ctx.drawTextWithShadow(this.textRenderer, "Sag Shift ile kapat  •  Surukle", x + 10, y0 + 18, 0xFFD6EAF8);
        String v = "v0.1.15";
        ctx.drawTextWithShadow(this.textRenderer, v, x + PANEL_W - this.textRenderer.getWidth(v) - 10, y0 + 10, 0xFFD6EAF8);
        ctx.drawTextWithShadow(this.textRenderer, "1.21.11", x + PANEL_W - this.textRenderer.getWidth("1.21.11") - 10, y0 + 21, 0xFF9CC3ED);
    }

    private void drawModuleRow(DrawContext ctx, Module module, int y, boolean hovered) {
        int x = panelX;
        int x1 = x + 4;
        int x2 = x + PANEL_W - 4;
        ctx.fill(x1, y, x2, y + ROW_H, hovered ? ROW_HOVER : ROW_BG);
        int dotY = y + 8;
        ctx.fill(x1 + 7, dotY, x1 + 11, dotY + 4, module.isEnabled() ? GREEN : RED);
        ctx.drawTextWithShadow(this.textRenderer, module.getName(), x1 + 16, y + 6, TEXT);
        String state = module.isEnabled() ? "ON" : "OFF";
        ctx.drawTextWithShadow(this.textRenderer, state, x2 - this.textRenderer.getWidth(state) - 10, y + 6,
                module.isEnabled() ? GREEN : RED);
    }

    private void drawFooter(DrawContext ctx, int y) {
        int x = panelX;
        ctx.fill(x + 4, y, x + PANEL_W - 4, y + 1, BORDER);
        String hint = "Sol tik: ON/OFF  •  Ayar: sol +  sag -";
        ctx.drawTextWithShadow(this.textRenderer, hint, x + (PANEL_W - this.textRenderer.getWidth(hint)) / 2, y + 6, MUTED);
    }

    private int renderAura(DrawContext ctx, KillAuraModule k, int y, int mouseX, int mouseY) {
        int x = panelX;
        int x1 = x + 10, x2 = x + PANEL_W - 10;
        ctx.fill(x1, y, x2, y + ROW_H, SETTING_BG);
        ctx.drawTextWithShadow(this.textRenderer, "Kill Aura Ayarlari", x1 + 6, y + 6, BLUE);
        y += ROW_H;
        String[] lines = {
                "Menzil:  " + String.format("%.1f", k.getRange()) + "  (1.0-8.0)",
                "CPS:  " + k.getCps(),
                "FOV:  " + k.getFov(),
                "Sadece Oyuncular:  " + (k.isPlayersOnly() ? "ON" : "OFF"),
                "Otomatik Hedef:  " + (k.isAutoSwitch() ? "ON" : "OFF"),
                "Oto Aim:  " + (k.isAutoAim() ? "ON" : "OFF")
        };
        for (String line : lines) {
            boolean hov = mouseX >= x1 && mouseX <= x2 && mouseY >= y && mouseY <= y + SETTING_ROW_H;
            ctx.fill(x1, y, x2, y + SETTING_ROW_H, hov ? SETTING_HOVER : SETTING_BG);
            ctx.drawTextWithShadow(this.textRenderer, line, x1 + 6, y + 4, 0xFFE2E8F0);
            y += SETTING_ROW_H;
        }
        return y + 4;
    }

    private int renderFly(DrawContext ctx, FlyModule f, int y, int mouseX, int mouseY) {
        int x = panelX;
        int x1 = x + 10, x2 = x + PANEL_W - 10;
        ctx.fill(x1, y, x2, y + ROW_H, SETTING_BG);
        ctx.drawTextWithShadow(this.textRenderer, "Fly Ayarlari  •  Space x2 ile ac/kapa", x1 + 6, y + 6, BLUE);
        y += ROW_H;
        String[] lines = {
                "Ucus Hizi:  " + String.format("%.2f", f.getFlySpeed()),
                "Hiz:  " + Math.round((f.getFlySpeed() - 0.02f) / 0.18f * 100) + "%",
                "Double Jump:  " + (f.isDoubleJump() ? "ON" : "OFF")
        };
        for (String line : lines) {
            boolean hov = mouseX >= x1 && mouseX <= x2 && mouseY >= y && mouseY <= y + SETTING_ROW_H;
            ctx.fill(x1, y, x2, y + SETTING_ROW_H, hov ? SETTING_HOVER : SETTING_BG);
            ctx.drawTextWithShadow(this.textRenderer, line, x1 + 6, y + 4, 0xFFE2E8F0);
            y += SETTING_ROW_H;
        }
        return y + 4;
    }

    private int moduleRowTop(int idx) {
        int y = panelY + HEADER_H + 2;
        for (int i = 0; i < idx; i++) {
            Module m = ModuleManager.getModules().get(i);
            y += ROW_H + 2;
            if (m instanceof KillAuraModule) y += (ROW_H + 2) + AURA_SETTING_COUNT * SETTING_ROW_H + 4;
            else if (m instanceof FlyModule) y += (ROW_H + 2) + FLY_SETTING_COUNT * SETTING_ROW_H + 4;
            else y += 4;
        }
        return y;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mx = click.x();
        double my = click.y();
        int button = click.button();

        // Header surukleme (sol tik)
        if (button == 0 && mx >= panelX && mx <= panelX + PANEL_W && my >= panelY && my <= panelY + HEADER_H) {
            dragging = true;
            dragOffsetX = (int) mx - panelX;
            dragOffsetY = (int) my - panelY;
            return true;
        }

        if (button != 0 && button != 1) return super.mouseClicked(click, doubled);
        int dir = (button == 0) ? 1 : -1;

        java.util.List<Module> mods = ModuleManager.getModules();
        for (int mi = 0; mi < mods.size(); mi++) {
            Module module = mods.get(mi);
            int y = moduleRowTop(mi);
            boolean inRow = mx >= panelX + 4 && mx <= panelX + PANEL_W - 4 && my >= y && my <= y + ROW_H;
            if (inRow) { module.toggle(); return true; }
            y += ROW_H + 2;
            if (module instanceof KillAuraModule k) {
                y += ROW_H;
                for (int i = 0; i < AURA_SETTING_COUNT; i++) {
                    boolean inS = mx >= panelX + 10 && mx <= panelX + PANEL_W - 10 && my >= y && my <= y + SETTING_ROW_H;
                    if (inS) {
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
                    boolean inS = mx >= panelX + 10 && mx <= panelX + PANEL_W - 10 && my >= y && my <= y + SETTING_ROW_H;
                    if (inS) {
                        if (i == 2) f.toggleDoubleJump();
                        else f.cycleSpeed(dir);
                        return true;
                    }
                    y += SETTING_ROW_H;
                }
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(Click click) {
        dragging = false;
        return super.mouseReleased(click);
    }

    @Override
    public boolean shouldPause() { return false; }
}
