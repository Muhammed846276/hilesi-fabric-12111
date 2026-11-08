package com.example.shadowpanel.screen;

import com.example.shadowpanel.module.*;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClickGuiScreen extends Screen {
    private static final int BG = 0xE6101420, PANEL = 0xF0141C2E;
    private static final int SIDE = 0xF0101828, SIDE_SEL = 0xFF1E3A5F, SIDE_HOV = 0xFF182844;
    private static final int ACCENT = 0xFF38BDF8, ACCENT2 = 0xFFA78BFA, ACCENT_DIM = 0xFF2563EB;
    private static final int TXT = 0xFFF1F5F9, DIM = 0xFF94A3B8, MUTED = 0xFF64748B;
    private static final int CARD = 0xFF162033, CARD_HOV = 0xFF1C2942, LINE = 0xFF24324A;
    private static final int ON = 0xFF34D399, ON_BG = 0xFF064E3B, OFF = 0xFF64748B, OFF_BG = 0xFF1E293B;
    private static final int SET_BG = 0xFF111827, SET_HOV = 0xFF1B2740;

    private static final int PAD = 12, SIDE_W = 148, HEAD_H = 44, SEARCH_H = 22, ROW_H = 34, SET_H = 20;

    private int px, py, pw, ph;
    private int activeCat = 0;
    private double scroll = 0;
    private String search = "";
    private boolean searchFocus = false;
    private Module binding = null;
    private Module expanded = null;

    private static class Cat { final String name; final String icon; final List<Module> mods = new ArrayList<>(); Cat(String n,String i){name=n;icon=i;} }
    private final List<Cat> cats = new ArrayList<>();

    public ClickGuiScreen() { super(Component.literal("ShadowPanel")); }

    @Override protected void init() {
        super.init();
        pw = Math.min(this.width - 20, 860); ph = Math.min(this.height - 20, 560);
        px = (this.width - pw) / 2; py = (this.height - ph) / 2;
        buildCats();
    }

    private void buildCats() {
        cats.clear();
        Cat all = new Cat("Tum Moduller", "*");
        Cat combat = new Cat("Savas", "⚔");
        Cat move = new Cat("Hareket", "»");
        Cat player = new Cat("Oyuncu", "●");
        Cat visual = new Cat("Gorsel", "◉");
        for (Module m : ModuleManager.getModules()) {
            all.mods.add(m);
            String n = m.getName().toLowerCase(Locale.ROOT);
            if (n.contains("aura")||n.contains("attack")||n.contains("kill")||n.contains("spear")||n.contains("mace")||n.contains("trigger")||n.contains("crit")||n.contains("reach")||n.contains("velocity")) combat.mods.add(m);
            else if (n.contains("fly")||n.contains("speed")||n.contains("step")||n.contains("jump")||n.contains("sprint")||n.contains("scaffold")||n.contains("noslow")||n.contains("freecam")||n.contains("timer")||n.contains("blink")) move.mods.add(m);
            else if (n.contains("fall")||n.contains("place")||n.contains("break")||n.contains("tool")||n.contains("chest")||n.contains("armor")||n.contains("auto")||n.contains("eat")||n.contains("respawn")||n.contains("friend")) player.mods.add(m);
            else visual.mods.add(m);
        }
        cats.add(all); cats.add(combat); cats.add(move); cats.add(player); cats.add(visual);
        if (activeCat >= cats.size()) activeCat = 0;
        clampScroll();
    }

    private List<Module> visible() {
        if (cats.isEmpty()) return List.of();
        List<Module> src = cats.get(activeCat).mods;
        if (search == null || search.isBlank()) return src;
        String q = search.toLowerCase(Locale.ROOT).trim();
        List<Module> out = new ArrayList<>();
        for (Module m : src) if (m.getName().toLowerCase(Locale.ROOT).contains(q) || m.getDescription().toLowerCase(Locale.ROOT).contains(q)) out.add(m);
        return out;
    }

    private int cx() { return px + SIDE_W + PAD; }
    private int cw() { return pw - SIDE_W - PAD * 2; }
    private int top() { return py + HEAD_H + SEARCH_H + 16; }
    private int bottom() { return py + ph - PAD; }

    private int modH(Module m) { int base = ROW_H; if (expanded == m) base += 6 + (specificRows(m) + 1) * SET_H + 6; return base; }
    private int totalH() { int h=0; List<Module> v=visible(); for(int i=0;i<v.size();i++){ h+=modH(v.get(i)); if(i<v.size()-1) h+=6; } return Math.max(0,h); }
    private void clampScroll() { double max = Math.max(0, totalH() - (bottom()-top())); if (scroll<0) scroll=0; if (scroll>max) scroll=max; }

    @Override
    public void extractRenderState(GuiGraphicsExtractor ctx, int mx, int my, float delta) {
        ctx.fill(0,0,this.width,this.height,0x88050A12);
        ctx.fill(px,py,px+pw,py+ph,PANEL);
        border(ctx,px,py,px+pw,py+ph,LINE);
        ctx.fill(px,py,px+SIDE_W,py+ph,SIDE);
        ctx.fill(px+SIDE_W-1,py,px+SIDE_W,py+ph,LINE);
        ctx.fill(px,py,px+pw,py+2,ACCENT_DIM);
        ctx.text(this.font, "ShadowPanel", px+PAD, py+12, TXT, true);
        ctx.text(this.font, "Ben Shadow Panel'im", px+PAD, py+26, ACCENT2, true);
        ctx.text(this.font, "v0.2.3 | Sag Shift: kapat | X: XRay", px+pw-this.font.width("v0.2.3 | Sag Shift: kapat | X: XRay")-14, py+18, MUTED, true);

        drawCats(ctx, mx, my);
        drawSearch(ctx, mx, my);
        drawModules(ctx, mx, my);
        drawScrollbar(ctx);
        if (binding != null) {
            String msg = "[ " + binding.getName() + " ] tusa bas — ESC iptal, DEL sil";
            int tw = this.font.width(msg)+16;
            int bx = px + (pw-tw)/2, by = py + ph - 28;
            ctx.fill(bx,by,bx+tw,by+18,0xEE0B1220); border(ctx,bx,by,bx+tw,by+18,ACCENT);
            ctx.text(this.font, msg, bx+8, by+5, TXT, true);
        }
        super.extractRenderState(ctx, mx, my, delta);
    }

    private void drawCats(GuiGraphicsExtractor ctx, int mx, int my) {
        int y = py + HEAD_H + 8;
        for (int i=0;i<cats.size();i++) {
            Cat c = cats.get(i);
            int x1=px+8, x2=px+SIDE_W-8, y1=y, y2=y+26;
            boolean sel = i==activeCat, hov = mx>=x1&&mx<=x2&&my>=y1&&my<=y2;
            ctx.fill(x1,y1,x2,y2, sel?SIDE_SEL:(hov?SIDE_HOV:0));
            if (sel) ctx.fill(x1,y1,x1+3,y2,ACCENT);
            String label = c.icon + " " + c.name;
            ctx.text(this.font, label, x1+10, y1+9, sel?TXT:DIM, true);
            String cnt = ""+c.mods.size();
            ctx.text(this.font, cnt, x2-this.font.width(cnt)-6, y1+9, MUTED, true);
            y += 30;
        }
    }

    private int searchX(){ return cx(); }
    private int searchY(){ return py + HEAD_H + 8; }
    private void drawSearch(GuiGraphicsExtractor ctx, int mx, int my) {
        int x1=searchX(), x2=cx()+cw(), y1=searchY(), y2=y1+SEARCH_H;
        boolean hov = mx>=x1&&mx<=x2&&my>=y1&&my<=y2;
        ctx.fill(x1,y1,x2,y2, hov||searchFocus?0xFF1A2742:0xFF121B2E);
        border(ctx,x1,y1,x2,y2, searchFocus?ACCENT:LINE);
        String ph = search.isEmpty() && !searchFocus ? "Ara... (modul adi/aciklama)" : search + (searchFocus?"_":"");
        ctx.text(this.font, ph, x1+8, y1+7, (search.isEmpty()&&!searchFocus)?MUTED:TXT, true);
        if (!search.isEmpty()) ctx.text(this.font, "x", x2-14, y1+7, hov?OFF:MUTED, true);
    }

    private void drawModules(GuiGraphicsExtractor ctx, int mx, int my) {
        int x=cx(), w=cw(), y0=top(), yb=bottom();
        // clip not always available across 26.x minors; draw with bounds check
        double cy = y0 - scroll;
        List<Module> v = visible();
        for (Module m : v) {
            int h = modH(m);
            if (cy + h >= y0-2 && cy <= yb) drawModule(ctx, m, x, (int)cy, w, mx, my);
            cy += h + 6;
        }
        if (v.isEmpty()) ctx.text(this.font, "Modul bulunamadi.", x+8, y0+8, MUTED, true);
    }

    private void drawModule(GuiGraphicsExtractor ctx, Module m, int x, int y, int w, int mx, int my) {
        boolean exp = expanded == m;
        int h = modH(m);
        boolean hov = mx>=x&&mx<=x+w&&my>=y&&my<=y+ROW_H;
        ctx.fill(x,y,x+w,y+h, CARD);
        ctx.fill(x,y,x+w,y+ROW_H, hov?CARD_HOV:CARD);
        border(ctx,x,y,x+w,y+h, LINE);
        ctx.fill(x,y,x+3,y+h, m.isEnabled()?ON:0xFF334155);
        ctx.text(this.font, m.getName(), x+12, y+8, TXT, true);
        String desc = m.getDescription();
        if (desc != null && !desc.isEmpty()) {
            String d = desc.length()>44 ? desc.substring(0,44)+"..." : desc;
            ctx.text(this.font, d, x+12, y+19, MUTED, true);
        }
        drawToggle(ctx, x+w-58, y+9, m.isEnabled());
        String gear = exp ? "[-]" : "[+]";
        ctx.text(this.font, gear, x+w-82, y+13, hov||exp?ACCENT:MUTED, true);
        if (exp) {
            int sy = y + ROW_H + 4;
            drawSettings(ctx, m, x, sy, w, mx, my);
            String bindStr = binding==m ? "[tusa bas... ESC/DEL]" : keyName(m.getBind());
            row(ctx,mx,my,x,sy + specificRows(m)*SET_H,w,"Tus Ata (Bind)", bindStr);
        }
    }

    private void drawToggle(GuiGraphicsExtractor ctx, int x, int y, boolean on) {
        int tw=46, th=14; ctx.fill(x,y,x+tw,y+th, on?ON_BG:OFF_BG); border(ctx,x,y,x+tw,y+th, on?ON:OFF);
        String t = on?"ACIK":"KAPALI"; ctx.text(this.font,t,x+(tw-this.font.width(t))/2,y+3,on?ON:OFF, true);
    }
    private void row(GuiGraphicsExtractor ctx,int mx,int my,int cx,int y,int w,String label,String val){
        int x1=cx+8,x2=cx+w-8; boolean hov=mx>=x1&&mx<=x2&&my>=y&&my<=y+SET_H-2;
        ctx.fill(x1,y,x2,y+SET_H-2, hov?SET_HOV:SET_BG);
        ctx.text(this.font,label,x1+8,y+5,DIM, true);
        ctx.text(this.font,val,x2-this.font.width(val)-8,y+5,TXT, true);
    }
    private void rowBool(GuiGraphicsExtractor ctx,int mx,int my,int cx,int y,int w,String label,boolean on){
        int x1=cx+8,x2=cx+w-8; boolean hov=mx>=x1&&mx<=x2&&my>=y&&my<=y+SET_H-2;
        ctx.fill(x1,y,x2,y+SET_H-2, hov?SET_HOV:SET_BG);
        ctx.text(this.font,label,x1+8,y+5,DIM, true);
        String v=on?"ACIK":"KAPALI"; ctx.text(this.font,v,x2-this.font.width(v)-8,y+5,on?ON:OFF, true);
    }

    private int specificRows(Module m) {
        if (m instanceof KillAuraModule) return 6;
        if (m instanceof SpearAssistModule) return 7;
        if (m instanceof AutoMaceModule) return 5;
        if (m instanceof FlyModule) return 5;
        if (m instanceof TriggerBotModule) return 2;
        if (m instanceof VelocityModule) return 2;
        if (m instanceof EspModule) return 2;
        if (m instanceof StepModule||m instanceof SpeedModule||m instanceof ReachModule||m instanceof XRayModule||m instanceof TimerModule||m instanceof ChestStealerModule) return 1;
        return 0;
    }

    private void drawSettings(GuiGraphicsExtractor ctx, Module m, int x, int y, int w, int mx, int my) {
        if (m instanceof KillAuraModule k){ row(ctx,mx,my,x,y,w,"Menzil",String.format("%.1f",k.getRange()));y+=SET_H; row(ctx,mx,my,x,y,w,"CPS",""+k.getCps());y+=SET_H; row(ctx,mx,my,x,y,w,"FOV",k.getFov()+"°");y+=SET_H; rowBool(ctx,mx,my,x,y,w,"Sadece Oyuncular",k.isPlayersOnly());y+=SET_H; rowBool(ctx,mx,my,x,y,w,"Otomatik Hedef",k.isAutoSwitch());y+=SET_H; rowBool(ctx,mx,my,x,y,w,"Oto Aim",k.isAutoAim()); }
        else if (m instanceof SpearAssistModule s){ row(ctx,mx,my,x,y,w,"Min Menzil",String.format("%.0f",s.getMinRange()));y+=SET_H; row(ctx,mx,my,x,y,w,"Max Menzil",String.format("%.0f",s.getMaxRange()));y+=SET_H; row(ctx,mx,my,x,y,w,"Hiz/Itki",String.format("%.2f",s.getBoostSpeed()));y+=SET_H; row(ctx,mx,my,x,y,w,"Aci",s.getFov()>=180?"360":s.getFov()+"°");y+=SET_H; rowBool(ctx,mx,my,x,y,w,"Aim Kilidi",s.isAimLock());y+=SET_H; rowBool(ctx,mx,my,x,y,w,"Duvar Yoksay",s.isIgnoreWalls());y+=SET_H; rowBool(ctx,mx,my,x,y,w,"Sadece Oyuncular",s.isPlayersOnly()); }
        else if (m instanceof AutoMaceModule a){ row(ctx,mx,my,x,y,w,"Mod",a.getMode().name());y+=SET_H; row(ctx,mx,my,x,y,w,"Menzil",String.format("%.1f",a.getRange()));y+=SET_H; row(ctx,mx,my,x,y,w,"CPS",""+a.getCps());y+=SET_H; rowBool(ctx,mx,my,x,y,w,"Sadece Oyuncular",a.isTargetPlayers());y+=SET_H; row(ctx,mx,my,x,y,w,"FullAuto Yukseklik",String.format("%.0f",a.getBoostHeight())); }
        else if (m instanceof FlyModule f){ row(ctx,mx,my,x,y,w,"Ucus Hizi",String.format("%.2f",f.getFlySpeed()));y+=SET_H; row(ctx,mx,my,x,y,w,"Hiz %",Math.round((f.getFlySpeed()-0.02f)/0.18f*100)+"%");y+=SET_H; rowBool(ctx,mx,my,x,y,w,"Double Jump",f.isDoubleJump());y+=SET_H; rowBool(ctx,mx,my,x,y,w,"Elytra Boost (W)",f.isElytraBoost());y+=SET_H; row(ctx,mx,my,x,y,w,"Elytra Boost Hizi",String.format("%.2f",f.getElytraBoostSpeed())); }
        else if (m instanceof TriggerBotModule t){ row(ctx,mx,my,x,y,w,"CPS",""+t.getCps());y+=SET_H; rowBool(ctx,mx,my,x,y,w,"Sadece Oyuncular",t.isPlayersOnly()); }
        else if (m instanceof VelocityModule v){ row(ctx,mx,my,x,y,w,"Yatay %",""+v.getHorizontal());y+=SET_H; row(ctx,mx,my,x,y,w,"Dikey %",""+v.getVertical()); }
        else if (m instanceof EspModule e){ rowBool(ctx,mx,my,x,y,w,"Tracers",e.isTracers());y+=SET_H; rowBool(ctx,mx,my,x,y,w,"Nametags",e.isNametags()); }
        else if (m instanceof StepModule s){ row(ctx,mx,my,x,y,w,"Yukseklik",String.format("%.2f",s.getHeight())); }
        else if (m instanceof SpeedModule s){ row(ctx,mx,my,x,y,w,"Carpan",String.format("%.2f",s.getMultiplier())); }
        else if (m instanceof ReachModule r){ row(ctx,mx,my,x,y,w,"Ekstra",String.format("%.2f",r.getExtra())); }
        else if (m instanceof XRayModule xr){ rowBool(ctx,mx,my,x,y,w,"Cave Mode",xr.isCaveMode()); }
        else if (m instanceof TimerModule t){ row(ctx,mx,my,x,y,w,"Hiz",String.format("%.2f",t.getSpeed())); }
        else if (m instanceof ChestStealerModule c){ row(ctx,mx,my,x,y,w,"Gecikme ms",""+c.getDelayMs()); }
    }

    private boolean handleSettings(Module m, double mx, double my, int cx, int sy, int w, int dir) {
        int sr = specificRows(m);
        if (hit(mx,my,cx,sy+sr*SET_H,w)) { binding = m; return true; }
        for (int i=0;i<sr;i++) if (hit(mx,my,cx,sy+i*SET_H,w)) {
            if (m instanceof KillAuraModule k){ switch(i){case 0->k.cycleRange(dir);case 1->k.cycleCps(dir);case 2->k.cycleFov(dir);case 3->k.togglePlayersOnly();case 4->k.toggleAutoSwitch();case 5->k.toggleAutoAim();} return true; }
            if (m instanceof SpearAssistModule s){ switch(i){case 0->s.cycleMinRange(dir);case 1->s.cycleMaxRange(dir);case 2->s.cycleBoostSpeed(dir);case 3->s.cycleFov(dir);case 4->s.toggleAimLock();case 5->s.toggleIgnoreWalls();case 6->s.togglePlayersOnly();} return true; }
            if (m instanceof AutoMaceModule a){ switch(i){case 0->a.cycleMode(dir);case 1->a.cycleRange(dir);case 2->a.cycleCps(dir);case 3->a.toggleTargetPlayers();case 4->a.cycleBoostHeight(dir);} return true; }
            if (m instanceof FlyModule f){ if(i==0||i==1) f.cycleSpeed(dir); else if(i==2) f.toggleDoubleJump(); else if(i==3) f.toggleElytraBoost(); else f.cycleElytraBoostSpeed(dir); return true; }
            if (m instanceof TriggerBotModule t){ if(i==0)t.cycleCps(dir); else t.togglePlayersOnly(); return true; }
            if (m instanceof VelocityModule v){ if(i==0)v.cycleHorizontal(dir); else v.cycleVertical(dir); return true; }
            if (m instanceof EspModule e){ if(i==0)e.toggleTracers(); else e.toggleNametags(); return true; }
            if (m instanceof StepModule sm){ sm.cycleHeight(dir); return true; }
            if (m instanceof SpeedModule sp){ sp.cycleMultiplier(dir); return true; }
            if (m instanceof ReachModule rm){ rm.cycleExtra(dir); return true; }
            if (m instanceof XRayModule xr){ xr.toggleCaveMode(); return true; }
            if (m instanceof TimerModule tm){ tm.cycleSpeed(dir); return true; }
            if (m instanceof ChestStealerModule cs){ cs.cycleDelay(dir); return true; }
        }
        return false;
    }
    private boolean hit(double mx,double my,int cx,int y,int w){ return mx>=cx+8&&mx<=cx+w-8&&my>=y&&my<=y+SET_H-2; }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        double mx=event.x(), my=event.y(); int btn=event.button();
        int cy = py + HEAD_H + 8;
        for (int i=0;i<cats.size();i++) {
            int x1=px+8,x2=px+SIDE_W-8,y1=cy,y2=cy+26;
            if (mx>=x1&&mx<=x2&&my>=y1&&my<=y2) { activeCat=i; scroll=0; searchFocus=false; clampScroll(); return true; }
            cy += 30;
        }
        int sx1=searchX(), sx2=cx()+cw(), sy=searchY();
        if (mx>=sx1&&mx<=sx2&&my>=sy&&my<=sy+SEARCH_H) {
            if (!search.isEmpty() && mx>=sx2-18) { search=""; clampScroll(); }
            searchFocus=true; return true;
        } else searchFocus=false;
        if (btn!=0 && btn!=1) return super.mouseClicked(event,isDoubleClick);
        int dir = btn==0?1:-1;
        int x=cx(), w=cw(), y0=top(), yb=bottom();
        if (mx>=x && mx<=x+w && my>=y0 && my<=yb) {
            double cy2 = y0 - scroll;
            for (Module m : visible()) {
                int h = modH(m);
                if (cy2 + h >= y0 && cy2 <= yb) {
                    if (mx>=x+w-86 && mx<=x+w-60 && my>=cy2 && my<=cy2+ROW_H) { expanded = (expanded==m?null:m); clampScroll(); return true; }
                    if (mx>=x+w-58 && mx<=x+w-8 && my>=cy2+6 && my<=cy2+26) { m.toggle(); return true; }
                    if (mx>=x && mx<=x+w && my>=cy2 && my<=cy2+ROW_H) { m.toggle(); return true; }
                    if (expanded==m) {
                        int sy2 = (int)cy2 + ROW_H + 4;
                        if (handleSettings(m,mx,my,x,sy2,w,dir)) return true;
                    }
                }
                cy2 += h + 6;
            }
        }
        return super.mouseClicked(event,isDoubleClick);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hAmt, double vAmt) {
        if (mx>=cx() && mx<=cx()+cw() && my>=top() && my<=bottom()) { scroll -= vAmt*18.0; clampScroll(); return true; }
        return super.mouseScrolled(mx,my,hAmt,vAmt);
    }

    private int keyOf(Object input) {
        try { return (int) input.getClass().getMethod("key").invoke(input); } catch (Throwable e) {}
        try { return (int) input.getClass().getMethod("getKeycode").invoke(input); } catch (Throwable e) {}
        try { return (int) input.getClass().getMethod("keyCode").invoke(input); } catch (Throwable e) {}
        return 0;
    }
    private int cpOf(Object input) {
        try { return (int) input.getClass().getMethod("codepoint").invoke(input); } catch (Throwable e) {}
        try { String s = String.valueOf(input.getClass().getMethod("asString").invoke(input)); if(!s.isEmpty()) return s.codePointAt(0); } catch (Throwable e) {}
        return 0;
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        int k = keyOf(event);
        if (binding != null) {
            if (k==256) { binding=null; return true; }
            if (k==261) { binding.setBind(-1); binding=null; return true; }
            if (k!=0) { binding.setBind(k); binding=null; }
            return true;
        }
        if (searchFocus) {
            if (k==259) { if(!search.isEmpty()){ search=search.substring(0,search.length()-1); clampScroll(); } return true; }
            if (k==256||k==257||k==335) { searchFocus=false; return true; }
            return true;
        }
        if (k==256) { this.onClose(); return true; }
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
        if (searchFocus) {
            int cp = cpOf(event);
            if (cp>=32 && cp!=127 && search.length()<40) { search += new String(Character.toChars(cp)); clampScroll(); return true; }
            return true;
        }
        return super.charTyped(event);
    }

    private void drawScrollbar(GuiGraphicsExtractor ctx) {
        int view = bottom()-top(), total = totalH(); if (total <= view) return;
        int tx = cx()+cw()+4, ty=top(), th=view;
        ctx.fill(tx,ty,tx+3,ty+th,0xFF121B2E);
        int thumb = Math.max(18,(int)(th*(view/(double)total)));
        int max = total-view; int tpy = ty + (int)((th-thumb)*(scroll/Math.max(1,max)));
        ctx.fill(tx,tpy,tx+3,tpy+thumb,ACCENT_DIM);
    }
    private void border(GuiGraphicsExtractor ctx,int x1,int y1,int x2,int y2,int c){
        ctx.fill(x1,y1,x2,y1+1,c); ctx.fill(x1,y2-1,x2,y2,c); ctx.fill(x1,y1,x1+1,y2,c); ctx.fill(x2-1,y1,x2,y2,c);
    }
    private String keyName(int k){
        if (k<=0) return "Yok";
        String n = GLFW.glfwGetKeyName(k,0);
        if (n!=null && !n.isEmpty()) return n.toUpperCase(Locale.ROOT);
        return "KEY_"+k;
    }

    @Override public boolean isPauseScreen(){ return false; }
}
