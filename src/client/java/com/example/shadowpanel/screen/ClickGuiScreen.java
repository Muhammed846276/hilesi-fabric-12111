package com.example.shadowpanel.screen;

import com.example.shadowpanel.module.FlyModule;
import com.example.shadowpanel.module.KillAuraModule;
import com.example.shadowpanel.module.Module;
import com.example.shadowpanel.module.ModuleManager;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Shadow Panel - sekmeli buyuk menu (Sag Shift)
 */
public class ClickGuiScreen extends Screen {
    private static final int TAB_H = 34;
    private static final int TOP_H = 74;
    private static final int PAD = 16;
    private static final int CARD_W = 360;
    private static final int CARD_HDR = 42;
    private static final int SET_H = 30;

    private static final int OVERLAY = 0xB005070D;
    private static final int PANEL = 0xF00B1020;
    private static final int PANEL_IN = 0xCC0F172A;
    private static final int LINE = 0xFF263149;
    private static final int HDR = 0xFF111827;
    private static final int TAB_N = 0xFF111827;
    private static final int TAB_HOV_C = 0xFF1F2937;
    private static final int TAB_A = 0xFF312E81;
    private static final int TAB_A_LINE = 0xFF818CF8;
    private static final int ACCENT = 0xFF7C3AED;
    private static final int ACCENT2 = 0xFFEF4444;
    private static final int CARD = 0xFF0F172A;
    private static final int CARD_HDR_BG = 0xFF111827;
    private static final int SET1 = 0xFF0B1220;
    private static final int SET2 = 0xFF111827;
    private static final int SET_HOV = 0xFF1E293B;
    private static final int TXT = 0xFFF8FAFC;
    private static final int DIM = 0xFF94A3B8;
    private static final int MUTED = 0xFF64748B;
    private static final int ON = 0xFF34D399;
    private static final int OFF = 0xFFF87171;

    private int px, py, pw, ph;
    private final List<Tab> tabs = new ArrayList<>();
    private int activeTab = 0;

    private static class Tab {
        final String name;
        final List<Module> modules = new ArrayList<>();
        final boolean settingsTab;
        Tab(String name, boolean settingsTab) { this.name = name; this.settingsTab = settingsTab; }
    }

    public ClickGuiScreen() { super(Text.literal("Shadow Panel")); }

    @Override
    protected void init() {
        super.init();
        pw = Math.min(this.width - 20, 860);
        ph = Math.min(this.height - 20, 520);
        px = (this.width - pw) / 2;
        py = (this.height - ph) / 2;
        buildTabs();
    }

    private void buildTabs() {
        tabs.clear();
        Tab savas = new Tab("Savas", false);
        Tab hareket = new Tab("Hareket", false);
        Tab gorsel = new Tab("Gorsel", false);
        Tab ayarlar = new Tab("Ayarlar", true);
        for (Module m : ModuleManager.getModules()) {
            String n = m.getName().toLowerCase();
            if (n.contains("aura") || n.contains("kill") || n.contains("attack")) savas.modules.add(m);
            else if (n.contains("fly") || n.contains("fall") || n.contains("speed") || n.contains("jump") || n.contains("nofall")) hareket.modules.add(m);
            else gorsel.modules.add(m);
        }
        tabs.add(savas); tabs.add(hareket); tabs.add(gorsel); tabs.add(ayarlar);
        if (activeTab >= tabs.size()) activeTab = 0;
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        ctx.fill(0, 0, this.width, this.height, OVERLAY);
        ctx.fill(px, py, px + pw, py + ph, PANEL);
        ctx.fill(px + 1, py + TOP_H, px + pw - 1, py + ph - 1, PANEL_IN);
        border(ctx, px, py, px + pw, py + ph, LINE);
        // accent line
        ctx.fill(px + 1, py + TOP_H - 3, px + pw - 1, py + TOP_H - 1, ACCENT);
        int mid = px + pw / 2;
        ctx.fill(mid, py + TOP_H - 3, px + pw - 1, py + TOP_H - 1, ACCENT2);

        drawHeader(ctx);
        drawTabs(ctx, mx, my);
        drawContent(ctx, mx, my);
        super.render(ctx, mx, my, delta);
    }

    private void drawHeader(DrawContext ctx) {
        ctx.fill(px + 1, py + 1, px + pw - 1, py + TOP_H - 3, HDR);
        ctx.drawTextWithShadow(this.textRenderer, "SHADOW PANEL", px + 20, py + 16, TXT);
        ctx.drawTextWithShadow(this.textRenderer, "Sag Shift: ac/kapa  •  Sekmeli panel  •  1.21.11 Fabric", px + 20, py + 34, DIM);
        badge(ctx, px + pw - 150, py + 16, "v0.1.20", 0xFF1F2937, 0xFFC4B5FD);
        badge(ctx, px + pw - 72, py + 16, "SHADOW", 0xFF1F2937, 0xFFFCA5A5);
        long onCount = ModuleManager.getModules().stream().filter(Module::isEnabled).count();
        ctx.drawTextWithShadow(this.textRenderer, "Aktif: " + onCount, px + pw - 150, py + 36, MUTED);
    }

    private void badge(DrawContext ctx, int x, int y, String t, int bg, int fg) {
        int w = this.textRenderer.getWidth(t) + 14;
        ctx.fill(x, y, x + w, y + 18, bg);
        border(ctx, x, y, x + w, y + 18, LINE);
        ctx.drawTextWithShadow(this.textRenderer, t, x + 7, y + 5, fg);
    }

    private int tabX(int i) { return px + PAD + i * 126; }

    private void drawTabs(DrawContext ctx, int mx, int my) {
        int ty = py + TOP_H;
        for (int i = 0; i < tabs.size(); i++) {
            int tx = tabX(i), tw = 118;
            boolean active = i == activeTab;
            boolean hov = mx >= tx && mx <= tx + tw && my >= ty + 4 && my <= ty + TAB_H + 2;
            ctx.fill(tx, ty + 4, tx + tw, ty + TAB_H + 2, active ? TAB_A : (hov ? TAB_HOV_C : TAB_N));
            border(ctx, tx, ty + 4, tx + tw, ty + TAB_H + 2, active ? 0xFF6366F1 : LINE);
            if (active) ctx.fill(tx, ty + TAB_H, tx + tw, ty + TAB_H + 2, TAB_A_LINE);
            Tab t = tabs.get(i);
            String label = t.settingsTab ? t.name : (t.name + " (" + t.modules.size() + ")");
            ctx.drawTextWithShadow(this.textRenderer, label, tx + 10, ty + 14, active ? TXT : DIM);
        }
        ctx.fill(px + 1, ty + TAB_H + 4, px + pw - 1, ty + TAB_H + 5, LINE);
    }

    private void drawContent(DrawContext ctx, int mx, int my) {
        if (tabs.isEmpty()) return;
        Tab tab = tabs.get(activeTab);
        int cy = py + TOP_H + TAB_H + 14;
        int cx = px + PAD;
        if (tab.settingsTab) { drawSettingsTab(ctx, cx, cy, mx, my); return; }
        int maxY = py + ph - 12;
        for (Module m : tab.modules) {
            int h = cardHeight(m);
            if (cy + h > maxY) break;
            drawCard(ctx, m, cx, cy, mx, my);
            cy += h + 10;
        }
        // sag durum paneli (bosluk varsa)
        if (tab.modules.size() == 1 && cy + 10 < maxY) drawStatusCard(ctx, cx + CARD_W + 14, py + TOP_H + TAB_H + 14);
    }

    private void drawSettingsTab(DrawContext ctx, int x, int y, int mx, int my) {
        int w = 420, h = 220;
        ctx.fill(x, y, x + w, y + h, CARD);
        border(ctx, x, y, x + w, y + h, LINE);
        ctx.fill(x, y, x + 4, y + h, ACCENT);
        ctx.drawTextWithShadow(this.textRenderer, "Panel Ayarlari / Bilgi", x + 14, y + 14, TXT);
        ctx.drawTextWithShadow(this.textRenderer, "• Otomatik guncelleme: acilis + her surumde Release kontrolu", x + 14, y + 38, DIM);
        ctx.drawTextWithShadow(this.textRenderer, "• Yeni surum bulunursa mods/ icine indirilir", x + 14, y + 56, DIM);
        ctx.drawTextWithShadow(this.textRenderer, "• Repo: Muhammed846276/hilesi-fabric-12111 (public)", x + 14, y + 74, DIM);
        ctx.drawTextWithShadow(this.textRenderer, "• Menu: Sag Shift  •  Ayar: sol + / sag -", x + 14, y + 92, DIM);
        ctx.drawTextWithShadow(this.textRenderer, "• Fly: hiz ayari + Double Jump toggle", x + 14, y + 110, DIM);
        ctx.drawTextWithShadow(this.textRenderer, "Surum: v0.1.22  |  1.21.11 Fabric", x + 14, y + 140, MUTED);
        ctx.drawTextWithShadow(this.textRenderer, "Shadow Panel temasi aktif.", x + 14, y + 158, 0xFFC4B5FD);
    }

    private void drawStatusCard(DrawContext ctx, int x, int y) {
        int w = 360, h = 180;
        ctx.fill(x, y, x + w, y + h, CARD);
        border(ctx, x, y, x + w, y + h, LINE);
        ctx.fill(x, y, x + 4, y + h, 0xFF38BDF8);
        ctx.drawTextWithShadow(this.textRenderer, "Durum", x + 14, y + 14, TXT);
        int ry = y + 38;
        for (Module m : ModuleManager.getModules()) {
            int c = m.isEnabled() ? ON : OFF;
            ctx.drawTextWithShadow(this.textRenderer, (m.isEnabled() ? "● " : "○ ") + m.getName() + ": " + (m.isEnabled() ? "ACIK" : "KAPALI"),
                    x + 14, ry, c);
            ry += 18;
        }
    }

    private int cardHeight(Module m) {
        int h = CARD_HDR + 8;
        if (m instanceof KillAuraModule) h += 6 * SET_H + 8;
        else if (m instanceof FlyModule) h += 3 * SET_H + 8;
        else h += 22;
        return h;
    }

    private void drawCard(DrawContext ctx, Module m, int x, int y, int mx, int my) {
        int h = cardHeight(m);
        boolean hov = mx >= x && mx <= x + CARD_W && my >= y && my <= y + CARD_HDR;
        ctx.fill(x, y, x + CARD_W, y + h, CARD);
        ctx.fill(x, y, x + CARD_W, y + CARD_HDR, hov ? 0xFF162033 : CARD_HDR_BG);
        border(ctx, x, y, x + CARD_W, y + h, LINE);
        ctx.fill(x, y, x + 4, y + h, m.isEnabled() ? ON : 0xFF334155);
        ctx.drawTextWithShadow(this.textRenderer, m.getName(), x + 14, y + 15, TXT);
        drawToggle(ctx, x + CARD_W - 76, y + 10, m.isEnabled());
        int sy = y + CARD_HDR + 6;
        if (m instanceof KillAuraModule k) drawAura(ctx, k, x, sy, mx, my);
        else if (m instanceof FlyModule f) drawFly(ctx, f, x, sy, mx, my);
        else ctx.drawTextWithShadow(this.textRenderer, "Ayar yok. Basliga tikla: ac/kapa", x + 14, sy + 4, MUTED);
    }

    private void drawToggle(DrawContext ctx, int x, int y, boolean on) {
        ctx.fill(x, y, x + 62, y + 22, on ? 0x33052E2B : 0x333F1515);
        border(ctx, x, y, x + 62, y + 22, on ? ON : OFF);
        String t = on ? "ACIK" : "KAPALI";
        ctx.drawTextWithShadow(this.textRenderer, t, x + (62 - this.textRenderer.getWidth(t)) / 2, y + 7, on ? ON : OFF);
    }

    private void drawAura(DrawContext ctx, KillAuraModule k, int x, int y, int mx, int my) {
        row(ctx, mx, my, x, y, 0, "Menzil", String.format("%.1f blok", k.getRange())); y += SET_H;
        row(ctx, mx, my, x, y, 1, "CPS", String.valueOf(k.getCps())); y += SET_H;
        row(ctx, mx, my, x, y, 2, "FOV", k.getFov() + "°"); y += SET_H;
        rowB(ctx, mx, my, x, y, 3, "Sadece Oyuncular", k.isPlayersOnly()); y += SET_H;
        rowB(ctx, mx, my, x, y, 4, "Otomatik Hedef", k.isAutoSwitch()); y += SET_H;
        rowB(ctx, mx, my, x, y, 5, "Oto Aim", k.isAutoAim());
    }

    private void drawFly(DrawContext ctx, FlyModule f, int x, int y, int mx, int my) {
        row(ctx, mx, my, x, y, 0, "Ucus Hizi", String.format("%.2f", f.getFlySpeed())); y += SET_H;
        row(ctx, mx, my, x, y, 1, "Hiz", Math.round((f.getFlySpeed() - 0.02f) / 0.18f * 100) + "%"); y += SET_H;
        rowB(ctx, mx, my, x, y, 2, "Double Jump (Space x2)", f.isDoubleJump());
    }

    private void row(DrawContext ctx, int mx, int my, int cx, int y, int idx, String label, String val) {
        int x1 = cx + 10, x2 = cx + CARD_W - 10;
        boolean hov = mx >= x1 && mx <= x2 && my >= y && my <= y + SET_H - 2;
        ctx.fill(x1, y, x2, y + SET_H - 2, hov ? SET_HOV : (idx % 2 == 0 ? SET1 : SET2));
        ctx.drawTextWithShadow(this.textRenderer, label, x1 + 8, y + 9, DIM);
        ctx.drawTextWithShadow(this.textRenderer, val, x2 - this.textRenderer.getWidth(val) - 8, y + 9, TXT);
    }

    private void rowB(DrawContext ctx, int mx, int my, int cx, int y, int idx, String label, boolean on) {
        int x1 = cx + 10, x2 = cx + CARD_W - 10;
        boolean hov = mx >= x1 && mx <= x2 && my >= y && my <= y + SET_H - 2;
        ctx.fill(x1, y, x2, y + SET_H - 2, hov ? SET_HOV : (idx % 2 == 0 ? SET1 : SET2));
        ctx.drawTextWithShadow(this.textRenderer, label, x1 + 8, y + 9, DIM);
        String val = on ? "ACIK" : "KAPALI";
        ctx.drawTextWithShadow(this.textRenderer, val, x2 - this.textRenderer.getWidth(val) - 8, y + 9, on ? ON : OFF);
    }

    private void border(DrawContext ctx, int x1, int y1, int x2, int y2, int c) {
        ctx.fill(x1, y1, x2, y1 + 1, c); ctx.fill(x1, y2 - 1, x2, y2, c);
        ctx.fill(x1, y1, x1 + 1, y2, c); ctx.fill(x2 - 1, y1, x2, y2, c);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mx = click.x(), my = click.y();
        int button = click.button();
        // tabs
        int ty = py + TOP_H;
        for (int i = 0; i < tabs.size(); i++) {
            int tx = tabX(i), tw = 118;
            if (mx >= tx && mx <= tx + tw && my >= ty + 4 && my <= ty + TAB_H + 2) { activeTab = i; return true; }
        }
        if (button != 0 && button != 1) return super.mouseClicked(click, doubled);
        int dir = (button == 0) ? 1 : -1;
        if (tabs.isEmpty()) return super.mouseClicked(click, doubled);
        Tab tab = tabs.get(activeTab);
        if (tab.settingsTab) return super.mouseClicked(click, doubled);
        int cx = px + PAD, cy = py + TOP_H + TAB_H + 14, maxY = py + ph - 12;
        for (Module m : tab.modules) {
            int h = cardHeight(m);
            if (cy + h > maxY) break;
            if (mx >= cx && mx <= cx + CARD_W && my >= cy && my <= cy + CARD_HDR) { m.toggle(); return true; }
            int sy = cy + CARD_HDR + 6;
            if (m instanceof KillAuraModule k) {
                for (int i = 0; i < 6; i++) {
                    if (hit(mx, my, cx, sy + i * SET_H)) {
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
            } else if (m instanceof FlyModule f) {
                for (int i = 0; i < 3; i++) {
                    if (hit(mx, my, cx, sy + i * SET_H)) {
                        if (i == 2) f.toggleDoubleJump(); else f.cycleSpeed(dir);
                        return true;
                    }
                }
            }
            cy += h + 10;
        }
        return super.mouseClicked(click, doubled);
    }

    private boolean hit(double mx, double my, int cx, int y) {
        return mx >= cx + 10 && mx <= cx + CARD_W - 10 && my >= y && my <= y + SET_H - 2;
    }

    @Override
    public boolean shouldPause() { return false; }
}
