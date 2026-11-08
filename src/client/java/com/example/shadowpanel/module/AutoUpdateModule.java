package com.example.shadowpanel.module;

import com.example.shadowpanel.client.ShadowPanelClient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.time.Duration;
import java.util.regex.*;

public class AutoUpdateModule extends Module {
    private static final String REPO = "Muhammed846276/hilesi-fabric-12111";
    private static final String API = "https://api.github.com/repos/" + REPO + "/releases?per_page=15";
    private volatile boolean checked = false;
    private volatile String pending = null;

    public AutoUpdateModule() { super("AutoUpdate", "Yeni surumu kontrol eder/indirir (mods klasoru)"); try{setEnabled(true);}catch(Throwable ignored){} }

    @Override protected void onEnable() { if (!checked) checkAsync(); }

    @Override public void onTick(MinecraftClient mc) {
        if (!isEnabled()) return;
        if (!checked && mc != null && mc.player != null) checkAsync();
        String msg = pending;
        if (msg != null && mc != null && mc.player != null) {
            pending = null;
            try { mc.player.sendMessage(Text.literal(msg), false); }
            catch (Throwable t) { ShadowPanelClient.LOGGER.info("[ShadowPanel] {}", msg); }
        }
    }

    private void checkAsync() {
        checked = true;
        Thread t = new Thread(() -> { try { run(); } catch (Throwable e) { ShadowPanelClient.LOGGER.warn("AutoUpdate: {}", e.toString()); } }, "shadowpanel-autoupdate");
        t.setDaemon(true); t.start();
    }

    private void run() throws Exception {
        String mcVer = FabricLoader.getInstance().getModContainer("minecraft").map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("");
        String current = FabricLoader.getInstance().getModContainer(ShadowPanelClient.MOD_ID).map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("0.0.0");
        String curMod = modPart(current);
        HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest req = HttpRequest.newBuilder(URI.create(API)).timeout(Duration.ofSeconds(15))
                .header("Accept","application/vnd.github+json").header("User-Agent","shadowpanel-updater").GET().build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode()!=200) { ShadowPanelClient.LOGGER.info("AutoUpdate HTTP {}", res.statusCode()); return; }
        String json = res.body();
        Pattern tagP = Pattern.compile("\\{[^\\{]*?\"tag_name\"\\s*:\\s*\"([^\"]+)\"[^\\{]*?\"assets\"\\s*:\\[(.*?)\\]", Pattern.DOTALL);
        Matcher tm = tagP.matcher(json);
        String bestTag=null, bestMod=null, bestUrl=null;
        while (tm.find()) {
            String tag = tm.group(1);
            if (!matchesMc(tag, mcVer)) continue;
            String mod = modPart(tag.startsWith("v")?tag.substring(1):tag);
            String url = findAsset(tm.group(2), mcVer);
            if (url==null) continue;
            if (bestMod==null || newer(mod,bestMod)) { bestTag=tag; bestMod=mod; bestUrl=url; }
        }
        if (bestTag==null || bestUrl==null) { ShadowPanelClient.LOGGER.info("AutoUpdate: bu MC surumu icin release yok (mc={})", mcVer); return; }
        if (!newer(bestMod, curMod)) { ShadowPanelClient.LOGGER.info("ShadowPanel guncel ({}).", current); return; }
        Path modsDir = FabricLoader.getInstance().getGameDir().resolve("mods");
        Files.createDirectories(modsDir);
        String fileName = "shadowpanel-" + bestMod + "+" + normalizeMc(mcVer) + ".jar";
        Path out = modsDir.resolve(fileName);
        if (Files.exists(out)) { pending = "[ShadowPanel] Guncelleme hazir: " + fileName + " (eski jar'i silip yeniden baslat)"; return; }
        ShadowPanelClient.LOGGER.info("AutoUpdate indiriliyor: {} -> {}", bestTag, out);
        HttpRequest dl = HttpRequest.newBuilder(URI.create(bestUrl)).timeout(Duration.ofSeconds(60)).header("User-Agent","shadowpanel-updater").GET().build();
        HttpResponse<InputStream> dr = http.send(dl, HttpResponse.BodyHandlers.ofInputStream());
        if (dr.statusCode()!=200) { pending = "[ShadowPanel] Guncelleme indirilemedi (HTTP "+dr.statusCode()+"): "+bestTag; return; }
        Path tmp = modsDir.resolve(fileName+".part");
        try (InputStream in = dr.body()) { Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING); }
        Files.move(tmp, out, StandardCopyOption.REPLACE_EXISTING);
        pending = "[ShadowPanel] Guncellendi -> " + fileName + " indirildi. Eski jar'i silip oyunu yeniden baslat.";
    }

    private static String normalizeMc(String mc){ return mc==null?"":mc.trim(); }
    private static boolean matchesMc(String tag, String mcVer){
        if (tag==null) return false; String n = normalizeMc(mcVer); if (n.isEmpty()) return true;
        return tag.contains("+"+n) || tag.endsWith(n);
    }
    private static String modPart(String v){ if (v==null) return "0.0.0"; String s=v.startsWith("v")?v.substring(1):v; int p=s.indexOf('+'); return p>=0?s.substring(0,p):s; }
    private static String findAsset(String assetsJson, String mcVer){
        Pattern p = Pattern.compile("\"browser_download_url\"\\s*:\\s*\"([^\"]+)\"");
        Matcher m = p.matcher(assetsJson); String nmc = normalizeMc(mcVer);
        while (m.find()){ String u=m.group(1); if(u.contains("-sources")) continue; if(!u.contains("shadowpanel-")) continue; if(nmc.isEmpty()||u.contains("+"+nmc)) return u; }
        return null;
    }
    private static boolean newer(String a,String b){
        try { int[] A=parse(a),B=parse(b); for(int i=0;i<Math.max(A.length,B.length);i++){ int av=i<A.length?A[i]:0,bv=i<B.length?B[i]:0; if(av!=bv) return av>bv; } return false; }
        catch(Exception e){ return !a.equals(b); }
    }
    private static int[] parse(String v){ String[] p=v.split("[^0-9]+"); int[] o=new int[p.length]; int n=0; for(String x:p) if(!x.isEmpty()) o[n++]=Integer.parseInt(x); int[] r=new int[n]; System.arraycopy(o,0,r,0,n); return r; }
}
