package com.example.hilesi.screen;

import com.example.hilesi.module.FlyModule;
import com.example.hilesi.module.KillAuraModule;
import com.example.hilesi.module.Module;
import com.example.hilesi.module.ModuleManager;
import com.example.hilesi.module.SpearAssistModule;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Tam ekran sekmeli menu - Sag Shift ile ac/kapa
 */
public class ClickGuiScreen extends Screen {
    private static final int TAB_H = 34;
    private static final int TOP_H = 52;
    private static final int PADDING = 14;
    private static final int CARD_W = 320;
    private static final int CARD_HEADER_H = 30;
    private static final int SET_ROW_H = 20;

    // Renkler
    private static final int OVERLAY = 0xAA05070C;
    private static final int PANEL = 0xF00B1020;
    private static final int PANEL_2 = 0xFF0F1628;
    private static final int LINE = 0xFF243047;
    private static final int TAB_N = 0xFF121A2D;
    private static final int TAB_HOV = 0xFF1A2540;
    private static final int TAB_A = 0xFF1E3A8A;
    private static final int ACCENT = 0xFF38BDF8;
    private static final int ACCENT2 = 0xFF8B5CF6;
    private static final int CARD = 0xFF131C30;
    private static final int CARD_HOV = 0xFF18233C;
    private static final int SET_BG = 0xFF0E1526;
    private static final int SET_HOV = 0xFF162038;
    private static final int TXT = 0xFFF8FAFC;
    private static final int DIM = 0xFF94A3B8;
    private static final int MUTED = 0xFF64748B;
    private static final int ON = 0xFF34D399;
    private static final int OFF = 0xFFF87171;
    private static final int ON_BG = 0x3334D399;
    private static final int OFF_BG = 0x33F87171;

    private int panelX, panelY, panelW, panelH;
    private final List<Tab> tabs = new ArrayList<>();
    private int activeTab = 0;

    private static class Tab {
        final String name;
        final List<Module> modules = new ArrayList<>();
        Tab(String name) { this.name = name; }
    }

    public ClickGuiScreen() {
        super(Text.literal("Hilesi Menu"));
    }

    @Override
    protected void init() {
        super.init();
        panelW = Math.min(this.width - 24, 760);
        panelH = Math.min(this.height - 24, 480);
        panelX = (this.width - panelW) / 2;
        panelY = (this.height - panelH) / 2;
        buildTabs();
    }

    private void buildTabs() {
        tabs.clear();
        Tab combat = new Tab("Savas");
        Tab movement = new Tab("Hareket");
        Tab visual = new Tab("Gorsel");
        for (Module m : ModuleManager.getModules()) {
            String n = m.getName().toLowerCase();
            if (n.contains("aura") || n.contains("attack") || n.contains("kill") || n.contains("spear")) combat.modules.add(m);
            else if (n.contains("fly") || n.contains("fall") || n.contains("speed") || n.contains("jump") || n.contains("boost") || n.contains("dash")) movement.modules.add(m);
            else visual.modules.add(m);
        }
        tabs.add(combat);
        tabs.add(movement);
        tabs.add(visual);
        if (activeTab >= tabs.size()) activeTab = 0;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, OVERLAY);
        // panel
        ctx.fill(panelX, panelY, panelX + panelW, panelY + panelH, PANEL);
        ctx.fill(panelX + 1, panelY + TOP_H, panelX + panelW - 1, panelY + panelH - 1, PANEL_2);
        // border
        box(ctx, panelX, panelY, panelX + panelW, panelY + panelH, LINE);
        // top accent
        ctx.fill(panelX, panelY, panelX + panelW, panelY + 2, ACCENT2);
        ctx.fill(panelX, panelY + 2, panelX + panelW, panelY + 3, ACCENT);

        drawTop(ctx);
        drawTabs(ctx, mouseX, mouseY);
        drawContent(ctx, mouseX, mouseY);
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawTop(DrawContext ctx) {
        ctx.drawTextWithShadow(this.textRenderer, "HILESI MENU", panelX + 16, panelY + 12, TXT);
        String sub = "Sag Shift: kapat  •  Sekmeli buyuk gorunum";
        ctx.drawTextWithShadow(this.textRenderer, sub, panelX + 16, panelY + 26, DIM);
        String ver = "v0.1.19  |  1.21.11  Fabric";
        ctx.drawTextWithShadow(this.textRenderer, ver, panelX + panelW - this.textRenderer.getWidth(ver) - 14, panelY + 18, MUTED);
    }

    private int tabX(int i) { return panelX + PADDING + i * 130; }

    private void drawTabs(DrawContext ctx, int mx, int my) {
        int ty = panelY + TOP_H - TAB_H;
        for (int i = 0; i < tabs.size(); i++) {
            int tx = tabX(i);
            int tw = 122;
            boolean active = i == activeTab;
            boolean hov = mx >= tx && mx <= tx + tw && my >= ty && my <= ty + TAB_H - 2;
            int bg = active ? TAB_A : (hov ? TAB_HOV : TAB_N);
            ctx.fill(tx, ty, tx + tw, ty + TAB_H - 2, bg);
            if (active) ctx.fill(tx, ty + TAB_H - 4, tx + tw, ty + TAB_H - 2, ACCENT);
            Tab t = tabs.get(i);
            String label = t.name + " (" + t.modules.size() + ")";
            ctx.drawTextWithShadow(this.textRenderer, label, tx + 10, ty + 11, active ? TXT : DIM);
        }
        ctx.fill(panelX + 1, panelY + TOP_H - 1, panelX + panelW - 1, panelY + TOP_H, LINE);
    }

    private void drawContent(DrawContext ctx, int mx, int my) {
        if (tabs.isEmpty()) return;
        Tab tab = tabs.get(activeTab);
        int cx = panelX + PADDING;
        int cy = panelY + TOP_H + 10;
        int maxY = panelY + panelH - 10;
        for (Module m : tab.modules) {
            int h = cardHeight(m);
            if (cy + h > maxY) break;
            drawCard(ctx, m, cx, cy, mx, my);
            cy += h + 8;
        }
    }

    private int cardHeight(Module m) {
        int h = CARD_HEADER_H + 6;
        if (m instanceof KillAuraModule) h += 6 * SET_ROW_H + 6;
        else if (m instanceof SpearAssistModule) h += 4 * SET_ROW_H + 6;
        else if (m instanceof FlyModule) h += 3 * SET_ROW_H + 6;
        else h += 10;
        return h;
    }

    private void drawCard(DrawContext ctx, Module m, int x, int y, int mx, int my) {
        int h = cardHeight(m);
        boolean hov = mx >= x && mx <= x + CARD_W && my >= y && my <= y + CARD_HEADER_H;
        ctx.fill(x, y, x + CARD_W, y + h, CARD);
        ctx.fill(x, y, x + CARD_W, y + CARD_HEADER_H, hov ? CARD_HOV : CARD);
        box(ctx, x, y, x + CARD_W, y + h, LINE);
        ctx.fill(x, y, x + 3, y + h, m.isEnabled() ? ON : 0xFF334155);
        ctx.drawTextWithShadow(this.textRenderer, m.getName(), x + 12, y + 10, TXT);
        drawToggle(ctx, x + CARD_W - 64, y + 7, m.isEnabled());

        int sy = y + CARD_HEADER_H + 4;
        if (m instanceof KillAuraModule k) drawAuraSettings(ctx, k, x, sy, mx, my);
        else if (m instanceof SpearAssistModule s) drawSpearSettings(ctx, s, x, sy, mx, my);
        else if (m instanceof FlyModule f) drawFlySettings(ctx, f, x, sy, mx, my);
        else ctx.drawTextWithShadow(this.textRenderer, "Ayar yok. Tıkla: ac/kapa", x + 12, sy + 2, MUTED);
    }

    private void drawToggle(DrawContext ctx, int x, int y, boolean on) {
        int w = 52, h = 16;
        ctx.fill(x, y, x + w, y + h, on ? ON_BG : OFF_BG);
        box(ctx, x, y, x + w, y + h, on ? ON : OFF);
        String t = on ? "ACIK" : "KAPALI";
        int tw = this.textRenderer.getWidth(t);
        ctx.drawTextWithShadow(this.textRenderer, t, x + (w - tw) / 2, y + 4, on ? ON : OFF);
    }

    private void drawAuraSettings(DrawContext ctx, KillAuraModule k, int x, int y, int mx, int my) {
        row(ctx, mx, my, x, y, "Menzil", String.format("%.1f blok", k.getRange())); y += SET_ROW_H;
        row(ctx, mx, my, x, y, "CPS", String.valueOf(k.getCps())); y += SET_ROW_H;
        row(ctx, mx, my, x, y, "FOV", k.getFov() + "°"); y += SET_ROW_H;
        rowBool(ctx, mx, my, x, y, "Sadece Oyuncular", k.isPlayersOnly()); y += SET_ROW_H;
        rowBool(ctx, mx, my, x, y, "Otomatik Hedef", k.isAutoSwitch()); y += SET_ROW_H;
        rowBool(ctx, mx, my, x, y, "Oto Aim", k.isAutoAim());
    }

    private void drawSpearSettings(DrawContext ctx, SpearAssistModule s, int x, int y, int mx, int my) {
        row(ctx, mx, my, x, y, "Min Menzil", String.format("%.0f blok", s.getMinRange())); y += SET_ROW_H;
        row(ctx, mx, my, x, y, "Max Menzil", String.format("%.0f blok", s.getMaxRange())); y += SET_ROW_H;
        row(ctx, mx, my, x, y, "Hiz / Itki", String.format("%.2f", s.getBoostSpeed())); y += SET_ROW_H;
        rowBool(ctx, mx, my, x, y, "Sadece Oyuncular", s.isPlayersOnly());
    }

    private void drawFlySettings(DrawContext ctx, FlyModule f, int x, int y, int mx, int my) {
        row(ctx, mx, my, x, y, "Ucus Hizi", String.format("%.2f", f.getFlySpeed())); y += SET_ROW_H;
        row(ctx, mx, my, x, y, "Hiz", Math.round((f.getFlySpeed() - 0.02f) / 0.18f * 100) + "%"); y += SET_ROW_H;
        rowBool(ctx, mx, my, x, y, "Double Jump (Space x2)", f.isDoubleJump());
    }

    private void row(DrawContext ctx, int mx, int my, int cardX, int y, String label, String val) {
        int x1 = cardX + 8, x2 = cardX + CARD_W - 8;
        boolean hov = mx >= x1 && mx <= x2 && my >= y && my <= y + SET_ROW_H - 2;
        ctx.fill(x1, y, x2, y + SET_ROW_H - 2, hov ? SET_HOV : SET_BG);
        ctx.drawTextWithShadow(this.textRenderer, label, x1 + 8, y + 5, DIM);
        int vw = this.textRenderer.getWidth(val);
        ctx.drawTextWithShadow(this.textRenderer, val, x2 - vw - 8, y + 5, TXT);
    }

    private void rowBool(DrawContext ctx, int mx, int my, int cardX, int y, String label, boolean on) {
        int x1 = cardX + 8, x2 = cardX + CARD_W - 8;
        boolean hov = mx >= x1 && mx <= x2 && my >= y && my <= y + SET_ROW_H - 2;
        ctx.fill(x1, y, x2, y + SET_ROW_H - 2, hov ? SET_HOV : SET_BG);
        ctx.drawTextWithShadow(this.textRenderer, label, x1 + 8, y + 5, DIM);
        String val = on ? "ACIK" : "KAPALI";
        int vw = this.textRenderer.getWidth(val);
        ctx.drawTextWithShadow(this.textRenderer, val, x2 - vw - 8, y + 5, on ? ON : OFF);
    }

    private void box(DrawContext ctx, int x1, int y1, int x2, int y2, int c) {
        ctx.fill(x1, y1, x2, y1 + 1, c);
        ctx.fill(x1, y2 - 1, x2, y2, c);
        ctx.fill(x1, y1, x1 + 1, y2, c);
        ctx.fill(x2 - 1, y1, x2, y2, c);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mx = click.x(), my = click.y();
        int button = click.button();
        // tabs
        int ty = panelY + TOP_H - TAB_H;
        for (int i = 0; i < tabs.size(); i++) {
            int tx = tabX(i), tw = 122;
            if (mx >= tx && mx <= tx + tw && my >= ty && my <= ty + TAB_H - 2) {
                activeTab = i;
                return true;
            }
        }
        if (button != 0 && button != 1) return super.mouseClicked(click, doubled);
        int dir = (button == 0) ? 1 : -1;
        if (tabs.isEmpty()) return super.mouseClicked(click, doubled);
        Tab tab = tabs.get(activeTab);
        int cx = panelX + PADDING;
        int cy = panelY + TOP_H + 10;
        int maxY = panelY + panelH - 10;
        for (Module m : tab.modules) {
            int h = cardHeight(m);
            if (cy + h > maxY) break;
            if (mx >= cx && mx <= cx + CARD_W && my >= cy && my <= cy + CARD_HEADER_H) {
                m.toggle(); return true;
            }
            int sy = cy + CARD_HEADER_H + 4;
            if (m instanceof KillAuraModule k) {
                for (int i = 0; i < 6; i++) {
                    if (hitRow(mx, my, cx, sy + i * SET_ROW_H)) {
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
                }
            } else if (m instanceof SpearAssistModule s) {
                for (int i = 0; i < 4; i++) {
                    if (hitRow(mx, my, cx, sy + i * SET_ROW_H)) {
                        switch (i) {
                            case 0 -> s.cycleMinRange(dir);
                            case 1 -> s.cycleMaxRange(dir);
                            case 2 -> s.cycleBoostSpeed(dir);
                            case 3 -> s.togglePlayersOnly();
                        }
                        return true;
                    }
                }
            } else if (m instanceof FlyModule f) {
                for (int i = 0; i < 3; i++) {
                    if (hitRow(mx, my, cx, sy + i * SET_ROW_H)) {
                        if (i == 2) f.toggleDoubleJump(); else f.cycleSpeed(dir);
                        return true;
                    }
                }
            }
            cy += h + 8;
        }
        return super.mouseClicked(click, doubled);
    }

    private boolean hitRow(double mx, double my, int cardX, int y) {
        return mx >= cardX + 8 && mx <= cardX + CARD_W - 8 && my >= y && my <= y + SET_ROW_H - 2;
    }

    @Override
    public boolean shouldPause() { return false; }
}
