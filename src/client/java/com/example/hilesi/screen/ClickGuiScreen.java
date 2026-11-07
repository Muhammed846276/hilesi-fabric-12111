package com.example.hilesi.screen;

import com.example.hilesi.module.Module;
import com.example.hilesi.module.ModuleManager;
import com.example.hilesi.module.KillAuraModule;
import com.example.hilesi.module.SpearAssistModule;
import com.example.hilesi.module.FlyModule;
import com.example.hilesi.module.StepModule;
import com.example.hilesi.module.SpeedModule;
import com.example.hilesi.module.TriggerBotModule;
import com.example.hilesi.module.ReachModule;
import com.example.hilesi.module.XRayModule;
import com.example.hilesi.module.VelocityModule;
import com.example.hilesi.module.TimerModule;
import com.example.hilesi.module.EspModule;
import com.example.hilesi.module.ChestStealerModule;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private static final int TAB_H = 34, TOP_H = 52, PADDING = 14, CARD_W = 320, CARD_HEADER_H = 30, SET_ROW_H = 20;
    private static final int OVERLAY = 0xAA05070C, PANEL = 0xF00B1020, PANEL_2 = 0xFF0F1628, LINE = 0xFF243047;
    private static final int TAB_N = 0xFF121A2D, TAB_HOV = 0xFF1A2540, TAB_A = 0xFF1E3A8A, ACCENT = 0xFF38BDF8, ACCENT2 = 0xFF8B5CF6;
    private static final int CARD = 0xFF131C30, CARD_HOV = 0xFF18233C, SET_BG = 0xFF0E1526, SET_HOV = 0xFF162038;
    private static final int TXT = 0xFFF8FAFC, DIM = 0xFF94A3B8, MUTED = 0xFF64748B, ON = 0xFF34D399, OFF = 0xFFF87171, ON_BG = 0x3334D399, OFF_BG = 0x33F87171;

    private int panelX, panelY, panelW, panelH;
    private final List<Tab> tabs = new ArrayList<>();
    private int activeTab = 0;

    private static class Tab { final String name; final List<Module> modules = new ArrayList<>(); Tab(String name){this.name=name;} }

    public ClickGuiScreen() { super(Component.literal("Hilesi Menu")); }

    @Override protected void init() {
        super.init();
        panelW = Math.min(this.width - 24, 760); panelH = Math.min(this.height - 24, 520);
        panelX = (this.width - panelW) / 2; panelY = (this.height - panelH) / 2;
        buildTabs();
    }

    private void buildTabs() {
        tabs.clear();
        Tab combat = new Tab("Savas"), movement = new Tab("Hareket"), player = new Tab("Player"), visual = new Tab("Gorsel");
        for (Module m : ModuleManager.getModules()) {
            String n = m.getName().toLowerCase();
            if (n.contains("aura")||n.contains("attack")||n.contains("kill")||n.contains("spear")||n.contains("trigger")||n.contains("crit")||n.contains("reach")||n.contains("velocity")) combat.modules.add(m);
            else if (n.contains("fly")||n.contains("speed")||n.contains("step")||n.contains("jump")||n.contains("sprint")||n.contains("scaffold")||n.contains("noslow")||n.contains("freecam")||n.contains("timer")) movement.modules.add(m);
            else if (n.contains("fall")||n.contains("place")||n.contains("break")||n.contains("tool")||n.contains("chest")||n.contains("armor")||n.contains("auto")) player.modules.add(m);
            else visual.modules.add(m);
        }
        tabs.add(combat); tabs.add(movement); tabs.add(player); tabs.add(visual);
        if (activeTab >= tabs.size()) activeTab = 0;
    }

    @Override public void extractRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, OVERLAY);
        ctx.fill(panelX, panelY, panelX + panelW, panelY + panelH, PANEL);
        ctx.fill(panelX + 1, panelY + TOP_H, panelX + panelW - 1, panelY + panelH - 1, PANEL_2);
        box(ctx, panelX, panelY, panelX + panelW, panelY + panelH, LINE);
        ctx.fill(panelX, panelY, panelX + panelW, panelY + 2, ACCENT2);
        ctx.fill(panelX, panelY + 2, panelX + panelW, panelY + 3, ACCENT);
        ctx.text(this.font, "HILESI MENU", panelX + 16, panelY + 12, TXT);
        ctx.text(this.font, "Sag Shift: kapat | X: XRay tusu", panelX + 16, panelY + 26, DIM);
        String ver = "v0.1.24+26.1.1.2  |  26.1.2  Fabric";
        ctx.text(this.font, ver, panelX + panelW - this.font.width(ver) - 14, panelY + 18, MUTED);
        drawTabs(ctx, mouseX, mouseY); drawContent(ctx, mouseX, mouseY);
        super.extractRenderState(ctx, mouseX, mouseY, delta);
    }

    private int tabX(int i) { return panelX + PADDING + i * 120; }
    private void drawTabs(GuiGraphicsExtractor ctx, int mx, int my) {
        int ty = panelY + TOP_H - TAB_H;
        for (int i = 0; i < tabs.size(); i++) {
            int tx = tabX(i), tw = 112; boolean active = i == activeTab; boolean hov = mx >= tx && mx <= tx + tw && my >= ty && my <= ty + TAB_H - 2;
            ctx.fill(tx, ty, tx + tw, ty + TAB_H - 2, active ? TAB_A : (hov ? TAB_HOV : TAB_N));
            if (active) ctx.fill(tx, ty + TAB_H - 4, tx + tw, ty + TAB_H - 2, ACCENT);
            String label = tabs.get(i).name + " (" + tabs.get(i).modules.size() + ")";
            ctx.text(this.font, label, tx + 8, ty + 11, active ? TXT : DIM);
        }
        ctx.fill(panelX + 1, panelY + TOP_H - 1, panelX + panelW - 1, panelY + TOP_H, LINE);
    }

    private void drawContent(GuiGraphicsExtractor ctx, int mx, int my) {
        if (tabs.isEmpty()) return;
        int cx = panelX + PADDING, cy = panelY + TOP_H + 10, maxY = panelY + panelH - 10;
        for (Module m : tabs.get(activeTab).modules) {
            int h = cardHeight(m); if (cy + h > maxY) break;
            drawCard(ctx, m, cx, cy, mx, my); cy += h + 8;
        }
    }

    private int rows(Module m) {
        if (m instanceof KillAuraModule) return 6;
        if (m instanceof SpearAssistModule) return 7;
        if (m instanceof FlyModule) return 3;
        if (m instanceof StepModule) return 1;
        if (m instanceof SpeedModule) return 1;
        if (m instanceof TriggerBotModule) return 2;
        if (m instanceof ReachModule) return 1;
        if (m instanceof XRayModule) return 1;
        if (m instanceof VelocityModule) return 2;
        if (m instanceof TimerModule) return 1;
        if (m instanceof EspModule) return 2;
        if (m instanceof ChestStealerModule) return 1;
        return 0;
    }
    private int cardHeight(Module m) { int r = rows(m); return CARD_HEADER_H + 6 + (r > 0 ? r * SET_ROW_H + 6 : 10); }

    private void drawCard(GuiGraphicsExtractor ctx, Module m, int x, int y, int mx, int my) {
        int h = cardHeight(m); boolean hov = mx >= x && mx <= x + CARD_W && my >= y && my <= y + CARD_HEADER_H;
        ctx.fill(x, y, x + CARD_W, y + h, CARD); ctx.fill(x, y, x + CARD_W, y + CARD_HEADER_H, hov ? CARD_HOV : CARD);
        box(ctx, x, y, x + CARD_W, y + h, LINE); ctx.fill(x, y, x + 3, y + h, m.isEnabled() ? ON : 0xFF334155);
        ctx.text(this.font, m.getName(), x + 12, y + 10, TXT);
        drawToggle(ctx, x + CARD_W - 64, y + 7, m.isEnabled());
        int sy = y + CARD_HEADER_H + 4;
        if (rows(m)==0) ctx.text(this.font, "Ayar yok. Tıkla: ac/kapa", x + 12, sy + 2, MUTED);
        else drawSettings(ctx, m, x, sy, mx, my);
    }

    private void drawToggle(GuiGraphicsExtractor ctx, int x, int y, boolean on) {
        int w=52,h=16; ctx.fill(x,y,x+w,y+h,on?ON_BG:OFF_BG); box(ctx,x,y,x+w,y+h,on?ON:OFF);
        String t=on?"ACIK":"KAPALI"; ctx.text(this.font,t,x+(w-this.font.width(t))/2,y+4,on?ON:OFF);
    }
    private void row(GuiGraphicsExtractor ctx,int mx,int my,int cardX,int y,String label,String val){ int x1=cardX+8,x2=cardX+CARD_W-8; boolean hov=mx>=x1&&mx<=x2&&my>=y&&my<=y+SET_ROW_H-2; ctx.fill(x1,y,x2,y+SET_ROW_H-2,hov?SET_HOV:SET_BG); ctx.text(this.font,label,x1+8,y+5,DIM); ctx.text(this.font,val,x2-this.font.width(val)-8,y+5,TXT); }
    private void rowBool(GuiGraphicsExtractor ctx,int mx,int my,int cardX,int y,String label,boolean on){ int x1=cardX+8,x2=cardX+CARD_W-8; boolean hov=mx>=x1&&mx<=x2&&my>=y&&my<=y+SET_ROW_H-2; ctx.fill(x1,y,x2,y+SET_ROW_H-2,hov?SET_HOV:SET_BG); ctx.text(this.font,label,x1+8,y+5,DIM); String v=on?"ACIK":"KAPALI"; ctx.text(this.font,v,x2-this.font.width(v)-8,y+5,on?ON:OFF); }

    private void drawSettings(GuiGraphicsExtractor ctx, Module m, int x, int y, int mx, int my) {
        if (m instanceof KillAuraModule k) { row(ctx,mx,my,x,y,"Menzil",String.format("%.1f blok",k.getRange()));y+=SET_ROW_H; row(ctx,mx,my,x,y,"CPS",""+k.getCps());y+=SET_ROW_H; row(ctx,mx,my,x,y,"FOV",k.getFov()+"°");y+=SET_ROW_H; rowBool(ctx,mx,my,x,y,"Sadece Oyuncular",k.isPlayersOnly());y+=SET_ROW_H; rowBool(ctx,mx,my,x,y,"Otomatik Hedef",k.isAutoSwitch());y+=SET_ROW_H; rowBool(ctx,mx,my,x,y,"Oto Aim",k.isAutoAim()); }
        else if (m instanceof SpearAssistModule s) { row(ctx,mx,my,x,y,"Min Menzil",String.format("%.0f blok",s.getMinRange()));y+=SET_ROW_H; row(ctx,mx,my,x,y,"Max Menzil",String.format("%.0f blok",s.getMaxRange()));y+=SET_ROW_H; row(ctx,mx,my,x,y,"Hiz / Itki",String.format("%.2f",s.getBoostSpeed()));y+=SET_ROW_H; row(ctx,mx,my,x,y,"Aci (90-180)", s.getFov()>=180?"360 (her yon)":(s.getFov()+"°"));y+=SET_ROW_H; rowBool(ctx,mx,my,x,y,"Aim Kilidi (mouse)",s.isAimLock());y+=SET_ROW_H; rowBool(ctx,mx,my,x,y,"Duvarlari Yoksay",s.isIgnoreWalls());y+=SET_ROW_H; rowBool(ctx,mx,my,x,y,"Sadece Oyuncular",s.isPlayersOnly()); }
        else if (m instanceof FlyModule f) { row(ctx,mx,my,x,y,"Ucus Hizi",String.format("%.2f",f.getFlySpeed()));y+=SET_ROW_H; row(ctx,mx,my,x,y,"Hiz",Math.round((f.getFlySpeed()-0.02f)/0.18f*100)+"%");y+=SET_ROW_H; rowBool(ctx,mx,my,x,y,"Double Jump (Space x2)",f.isDoubleJump()); }
        else if (m instanceof StepModule s) { row(ctx,mx,my,x,y,"Yukseklik",String.format("%.2f",s.getHeight())); }
        else if (m instanceof SpeedModule s) { row(ctx,mx,my,x,y,"Carpan",String.format("%.2f",s.getMultiplier())); }
        else if (m instanceof TriggerBotModule t) { row(ctx,mx,my,x,y,"CPS",""+t.getCps());y+=SET_ROW_H; rowBool(ctx,mx,my,x,y,"Sadece Oyuncular",t.isPlayersOnly()); }
        else if (m instanceof ReachModule r) { row(ctx,mx,my,x,y,"Ekstra",String.format("%.2f blok",r.getExtra())); }
        else if (m instanceof XRayModule xr) { rowBool(ctx,mx,my,x,y,"Cave Mode (deneysel)",xr.isCaveMode()); }
        else if (m instanceof VelocityModule v) { row(ctx,mx,my,x,y,"Yatay %",""+v.getHorizontal());y+=SET_ROW_H; row(ctx,mx,my,x,y,"Dikey %",""+v.getVertical()); }
        else if (m instanceof TimerModule t) { row(ctx,mx,my,x,y,"Hiz",String.format("%.2f",t.getSpeed())); }
        else if (m instanceof EspModule e) { rowBool(ctx,mx,my,x,y,"Tracers (deneysel)",e.isTracers());y+=SET_ROW_H; rowBool(ctx,mx,my,x,y,"Nametags (deneysel)",e.isNametags()); }
        else if (m instanceof ChestStealerModule c) { row(ctx,mx,my,x,y,"Gecikme ms",""+c.getDelayMs()); }
    }

    private void box(GuiGraphicsExtractor ctx,int x1,int y1,int x2,int y2,int c){ ctx.fill(x1,y1,x2,y1+1,c); ctx.fill(x1,y2-1,x2,y2,c); ctx.fill(x1,y1,x1+1,y2,c); ctx.fill(x2-1,y1,x2,y2,c); }

    @Override public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        double mx=event.x(), my=event.y(); int button=event.button(); int ty=panelY+TOP_H-TAB_H;
        for(int i=0;i<tabs.size();i++){ int tx=tabX(i),tw=112; if(mx>=tx&&mx<=tx+tw&&my>=ty&&my<=ty+TAB_H-2){ activeTab=i; return true; } }
        if(button!=0&&button!=1||tabs.isEmpty()) return super.mouseClicked(event,isDoubleClick);
        int dir=(button==0)?1:-1; int cx=panelX+PADDING, cy=panelY+TOP_H+10, maxY=panelY+panelH-10;
        for(Module m: tabs.get(activeTab).modules){
            int h=cardHeight(m); if(cy+h>maxY) break;
            if(mx>=cx&&mx<=cx+CARD_W&&my>=cy&&my<=cy+CARD_HEADER_H){ m.toggle(); return true; }
            int sy=cy+CARD_HEADER_H+4; if(handleSettings(m,mx,my,cx,sy,dir)) return true;
            cy+=h+8;
        }
        return super.mouseClicked(event,isDoubleClick);
    }

    private boolean handleSettings(Module m,double mx,double my,int cx,int sy,int dir){
        int r=rows(m);
        for(int i=0;i<r;i++) if(hitRow(mx,my,cx,sy+i*SET_ROW_H)){
            if(m instanceof KillAuraModule k){ switch(i){case 0->k.cycleRange(dir);case 1->k.cycleCps(dir);case 2->k.cycleFov(dir);case 3->k.togglePlayersOnly();case 4->k.toggleAutoSwitch();case 5->k.toggleAutoAim();} return true; }
            if(m instanceof SpearAssistModule s){ switch(i){case 0->s.cycleMinRange(dir);case 1->s.cycleMaxRange(dir);case 2->s.cycleBoostSpeed(dir);case 3->s.cycleFov(dir);case 4->s.toggleAimLock();case 5->s.toggleIgnoreWalls();case 6->s.togglePlayersOnly();} return true; }
            if(m instanceof FlyModule f){ if(i==2) f.toggleDoubleJump(); else f.cycleSpeed(dir); return true; }
            if(m instanceof StepModule s){ s.cycleHeight(dir); return true; }
            if(m instanceof SpeedModule s){ s.cycleMultiplier(dir); return true; }
            if(m instanceof TriggerBotModule t){ if(i==0)t.cycleCps(dir); else t.togglePlayersOnly(); return true; }
            if(m instanceof ReachModule rm){ rm.cycleExtra(dir); return true; }
            if(m instanceof XRayModule xr){ xr.toggleCaveMode(); return true; }
            if(m instanceof VelocityModule v){ if(i==0)v.cycleHorizontal(dir); else v.cycleVertical(dir); return true; }
            if(m instanceof TimerModule t){ t.cycleSpeed(dir); return true; }
            if(m instanceof EspModule e){ if(i==0)e.toggleTracers(); else e.toggleNametags(); return true; }
            if(m instanceof ChestStealerModule c){ c.cycleDelay(dir); return true; }
        }
        return false;
    }
    private boolean hitRow(double mx,double my,int cardX,int y){ return mx>=cardX+8&&mx<=cardX+CARD_W-8&&my>=y&&my<=y+SET_ROW_H-2; }
    @Override public boolean isPauseScreen(){ return false; }
}
