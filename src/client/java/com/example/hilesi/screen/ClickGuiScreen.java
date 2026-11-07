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
 * Sag Shift menusu - "Aurora" tema (floating/suruklenebilir)
 */
public class ClickGuiScreen extends Screen {
    private static final int PANEL_W = 278;
    private static final int HEADER_H = 42;
    private static final int ROW_H = 22;
    private static final int SETTING_ROW_H = 17;
    private static final int FOOTER_H = 20;
    private static final int PAD = 8;
    private static final int AURA_SETTING_COUNT = 6;
    private static final int FLY_SETTING_COUNT = 3;

    // Aurora palette
    private static final int PANEL_BG      = 0xF0101218;
    private static final int PANEL_BG2     = 0xF0141720;
    private static final int OUTLINE       = 0xFF2A3345;
    private static final int OUTLINE_SOFT  = 0xFF1E2633;
    private static final int ACCENT        = 0xFF7C3AED; // purple
    private static final int ACCENT2       = 0xFF22D3EE; // cyan
    private static final int ACCENT_DIM    = 0x4438BDF8;
    private static final int ROW_NORM      = 0xFF171C25;
    private static final int ROW_HOV       = 0xFF1F2733;
    private static final int ROW_ON        = 0x2610B981;
    private static final int SET_BG        = 0xFF121720;
    private static final int SET_HOV       = 0xFF1A2230;
    private static final int TXT           = 0xFFF1F5F9;
    private static final int TXT_DIM       = 0xFF94A3B8;
    private static final int TXT_MUTED     = 0xFF64748B;
    private static final int GREEN         = 0xFF34D399;
    private static final int GREEN_BG      = 0x5534D399;
    private static final int RED           = 0xFFF87171;
    private static final int RED_BG        = 0x44F87171;
    private static final int CHIP_BG       = 0xFF1E293B;

    private int panelX = 20;
    private int panelY = 20;
    private boolean dragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public ClickGuiScreen() { super(Text.literal("Hilesi Menu")); }

    private int contentHeight() {
        int rows = 0;
        for (Module m : ModuleManager.getModules()) {
            rows += ROW_H + 3;
            if (m instanceof KillAuraModule) rows += 18 + AURA_SETTING_COUNT * SETTING_ROW_H + 5;
            else if (m instanceof FlyModule) rows += 18 + FLY_SETTING_COUNT * SETTING_ROW_H + 5;
            else rows += 2;
        }
        return HEADER_H + rows + FOOTER_H + PAD + 2;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        if (dragging) {
            panelX = mouseX - dragOffsetX;
            panelY = mouseY - dragOffsetY;
        }
        int h = contentHeight();
        int x = panelX, y0 = panelY;

        // soft shadow layers
        ctx.fill(x + 4, y0 + 5, x + PANEL_W + 4, y0 + h + 5, 0x33000000);
        ctx.fill(x + 2, y0 + 3, x + PANEL_W + 2, y0 + h + 3, 0x44000000);

        // panel body
        roundedRect(ctx, x, y0, x + PANEL_W, y0 + h, PANEL_BG);
        // inner tint
        ctx.fill(x + 1, y0 + HEADER_H, x + PANEL_W - 1, y0 + h - 1, PANEL_BG2);
        // outline
        drawOutline(ctx, x, y0, x + PANEL_W, y0 + h, OUTLINE);

        drawHeader(ctx, x, y0);

        int y = y0 + HEADER_H + 4;
        for (Module module : ModuleManager.getModules()) {
            boolean hovered = mouseX >= x + PAD && mouseX <= x + PANEL_W - PAD
                    && mouseY >= y && mouseY <= y + ROW_H;
            drawModuleRow(ctx, module, x, y, hovered);
            y += ROW_H + 3;
            if (module instanceof KillAuraModule k) y = renderAura(ctx, k, x, y, mouseX, mouseY);
            else if (module instanceof FlyModule f) y = renderFly(ctx, f, x, y, mouseX, mouseY);
            else y += 2;
        }
        drawFooter(ctx, x, y0 + h - FOOTER_H);
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawHeader(DrawContext ctx, int x, int y0) {
        // gradient-ish header: purple -> deep blue strips
        ctx.fill(x + 1, y0 + 1, x + PANEL_W - 1, y0 + HEADER_H, 0xFF1A1033);
        ctx.fill(x + 1, y0 + 1, x + PANEL_W - 1, y0 + 14, 0xFF2E1065);
        ctx.fill(x + 1, y0 + 14, x + PANEL_W - 1, y0 + 28, 0xFF1E1B4B);
        // accent lines
        ctx.fill(x + 1, y0 + HEADER_H - 2, x + PANEL_W - 1, y0 + HEADER_H - 1, ACCENT);
        ctx.fill(x + 1, y0 + HEADER_H - 1, x + PANEL_W - 1, y0 + HEADER_H, ACCENT2);

        ctx.drawTextWithShadow(this.textRenderer, "✦  HILESI", x + 12, y0 + 8, TXT);
        ctx.drawTextWithShadow(this.textRenderer, "Sag Shift  •  Surukle", x + 12, y0 + 22, TXT_DIM);

        drawChip(ctx, x + PANEL_W - 58, y0 + 7, "v0.1.17", ACCENT_DIM, ACCENT2);
        drawChip(ctx, x + PANEL_W - 58, y0 + 22, "1.21.11", 0x3322C55E, GREEN);
    }

    private void drawChip(DrawContext ctx, int x, int y, String text, int bg, int fg) {
        int w = this.textRenderer.getWidth(text) + 10;
        ctx.fill(x, y, x + w, y + 12, bg);
        ctx.fill(x, y, x + w, y + 1, 0x22FFFFFF);
        ctx.drawTextWithShadow(this.textRenderer, text, x + 5, y + 2, fg);
    }

    private void drawModuleRow(DrawContext ctx, Module module, int x, int y, boolean hovered) {
        int x1 = x + PAD, x2 = x + PANEL_W - PAD;
        int bg = hovered ? ROW_HOV : (module.isEnabled() ? 0xFF18222B : ROW_NORM);
        ctx.fill(x1, y, x2, y + ROW_H, bg);
        // left accent bar when enabled
        if (module.isEnabled()) ctx.fill(x1, y, x1 + 2, y + ROW_H, GREEN);
        else ctx.fill(x1, y, x1 + 2, y + ROW_H, 0xFF334155);

        ctx.drawTextWithShadow(this.textRenderer, module.getName(), x1 + 10, y + 7, TXT);
        drawPill(ctx, x2 - 40, y + 4, module.isEnabled());
    }

    private void drawPill(DrawContext ctx, int x, int y, boolean on) {
        int w = 34, h = 14;
        int bg = on ? GREEN_BG : RED_BG;
        int fg = on ? GREEN : RED;
        ctx.fill(x, y, x + w, y + h, bg);
        ctx.fill(x, y, x + w, y + 1, 0x33FFFFFF);
        ctx.fill(x, y + h - 1, x + w, y + h, 0x33000000);
        String t = on ? "ON" : "OFF";
        int tw = this.textRenderer.getWidth(t);
        ctx.drawTextWithShadow(this.textRenderer, t, x + (w - tw) / 2, y + 3, fg);
        // knob
        if (on) ctx.fill(x + w - 6, y + 3, x + w - 2, y + h - 3, GREEN);
        else ctx.fill(x + 2, y + 3, x + 6, y + h - 3, RED);
    }

    private void drawFooter(DrawContext ctx, int x, int y) {
        ctx.fill(x + PAD, y, x + PANEL_W - PAD, y + 1, OUTLINE_SOFT);
        String hint = "sol tik: ac/kapa  •  ayar: sol +  sag -";
        ctx.drawTextWithShadow(this.textRenderer, hint, x + (PANEL_W - this.textRenderer.getWidth(hint)) / 2, y + 6, TXT_MUTED);
    }

    private int renderAura(DrawContext ctx, KillAuraModule k, int x, int y, int mouseX, int mouseY) {
        int x1 = x + 14, x2 = x + PANEL_W - 14;
        ctx.fill(x1, y, x2, y + 16, 0xFF141A24);
        ctx.fill(x1, y, x1 + 2, y + 16, ACCENT2);
        ctx.drawTextWithShadow(this.textRenderer, "Kill Aura", x1 + 8, y + 4, ACCENT2);
        y += 18;
        String[] lines = {
                "Menzil  " + String.format("%.1f", k.getRange()) + " blok",
                "CPS  " + k.getCps(),
                "FOV  " + k.getFov() + "°",
                "Sadece Oyuncular  " + onOff(k.isPlayersOnly()),
                "Otomatik Hedef  " + onOff(k.isAutoSwitch()),
                "Oto Aim  " + onOff(k.isAutoAim())
        };
        for (String line : lines) {
            boolean hov = mouseX >= x1 && mouseX <= x2 && mouseY >= y && mouseY <= y + SETTING_ROW_H;
            ctx.fill(x1, y, x2, y + SETTING_ROW_H, hov ? SET_HOV : SET_BG);
            drawSettingText(ctx, line, x1 + 8, y + 4);
            y += SETTING_ROW_H;
        }
        return y + 5;
    }

    private int renderFly(DrawContext ctx, FlyModule f, int x, int y, int mouseX, int mouseY) {
        int x1 = x + 14, x2 = x + PANEL_W - 14;
        ctx.fill(x1, y, x2, y + 16, 0xFF141A24);
        ctx.fill(x1, y, x1 + 2, y + 16, ACCENT2);
        ctx.drawTextWithShadow(this.textRenderer, "Fly  •  Space x2", x1 + 8, y + 4, ACCENT2);
        y += 18;
        int pct = Math.round((f.getFlySpeed() - 0.02f) / 0.18f * 100);
        String[] lines = {
                "Hiz  " + String.format("%.2f", f.getFlySpeed()),
                "Hiz  " + pct + "%",
                "Double Jump  " + onOff(f.isDoubleJump())
        };
        for (String line : lines) {
            boolean hov = mouseX >= x1 && mouseX <= x2 && mouseY >= y && mouseY <= y + SETTING_ROW_H;
            ctx.fill(x1, y, x2, y + SETTING_ROW_H, hov ? SET_HOV : SET_BG);
            drawSettingText(ctx, line, x1 + 8, y + 4);
            y += SETTING_ROW_H;
        }
        return y + 5;
    }

    private void drawSettingText(DrawContext ctx, String line, int x, int y) {
        if (line.endsWith("ON") || line.endsWith("OFF")) {
            String val = line.endsWith("ON") ? "ON" : "OFF";
            String label = line.substring(0, line.length() - val.length());
            ctx.drawTextWithShadow(this.textRenderer, label, x, y, TXT_DIM);
            int c = val.equals("ON") ? GREEN : RED;
            ctx.drawTextWithShadow(this.textRenderer, val, x + this.textRenderer.getWidth(label), y, c);
        } else {
            ctx.drawTextWithShadow(this.textRenderer, line, x, y, TXT_DIM);
        }
    }

    private String onOff(boolean b) { return b ? "ON" : "OFF"; }

    private void roundedRect(DrawContext ctx, int x1, int y1, int x2, int y2, int color) {
        // fake rounded corners (2px step) using fills
        ctx.fill(x1 + 1, y1, x2 - 1, y2, color);
        ctx.fill(x1, y1 + 1, x1 + 1, y2 - 1, color);
        ctx.fill(x2 - 1, y1 + 1, x2, y2 - 1, color);
    }

    private void drawOutline(DrawContext ctx, int x1, int y1, int x2, int y2, int c) {
        ctx.fill(x1 + 1, y1, x2 - 1, y1 + 1, c);
        ctx.fill(x1 + 1, y2 - 1, x2 - 1, y2, c);
        ctx.fill(x1, y1 + 1, x1 + 1, y2 - 1, c);
        ctx.fill(x2 - 1, y1 + 1, x2, y2 - 1, c);
    }

    private int moduleRowTop(int idx) {
        int y = panelY + HEADER_H + 4;
        for (int i = 0; i < idx; i++) {
            Module m = ModuleManager.getModules().get(i);
            y += ROW_H + 3;
            if (m instanceof KillAuraModule) y += 18 + AURA_SETTING_COUNT * SETTING_ROW_H + 5;
            else if (m instanceof FlyModule) y += 18 + FLY_SETTING_COUNT * SETTING_ROW_H + 5;
            else y += 2;
        }
        return y;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mx = click.x(), my = click.y();
        int button = click.button();
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
            if (mx >= panelX + PAD && mx <= panelX + PANEL_W - PAD && my >= y && my <= y + ROW_H) {
                module.toggle(); return true;
            }
            y += ROW_H + 3;
            if (module instanceof KillAuraModule k) {
                y += 18;
                for (int i = 0; i < AURA_SETTING_COUNT; i++) {
                    if (mx >= panelX + 14 && mx <= panelX + PANEL_W - 14 && my >= y && my <= y + SETTING_ROW_H) {
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
                y += 18;
                for (int i = 0; i < FLY_SETTING_COUNT; i++) {
                    if (mx >= panelX + 14 && mx <= panelX + PANEL_W - 14 && my >= y && my <= y + SETTING_ROW_H) {
                        if (i == 2) f.toggleDoubleJump(); else f.cycleSpeed(dir);
                        return true;
                    }
                    y += SETTING_ROW_H;
                }
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override public boolean mouseReleased(Click click) { dragging = false; return super.mouseReleased(click); }
    @Override public boolean shouldPause() { return false; }
}
