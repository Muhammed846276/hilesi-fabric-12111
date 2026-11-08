package com.example.hilesi.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UpdateChecker {
    private static final String REPO = "Muhammed846276/hilesi-fabric-12111";
    private static final String API = "https://api.github.com/repos/" + REPO + "/releases/latest";
    private static volatile String pendingChat = null;
    private static volatile boolean checked = false;

    private UpdateChecker() {}

    public static void checkAsync() {
        if (checked) return;
        checked = true;
        Thread t = new Thread(() -> {
            try {
                run();
            } catch (Throwable e) {
                HilesiClient.LOGGER.warn("Guncelleme kontrolu basarisiz: {}", e.toString());
            }
        }, "hilesi-update-check");
        t.setDaemon(true);
        t.start();
    }

    public static void tickShowMessage(MinecraftClient client) {
        String msg = pendingChat;
        if (msg == null || client == null || client.player == null) return;
        pendingChat = null;
        try {
            client.player.sendMessage(Text.literal(msg), false);
        } catch (Throwable e) {
            HilesiClient.LOGGER.info("[Hilesi] {}", msg);
        }
    }

    private static void run() throws Exception {
        String current = FabricLoader.getInstance().getModContainer(HilesiClient.MOD_ID)
                .map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("0.0.0");
        HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        HttpRequest req = HttpRequest.newBuilder(URI.create(API))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "hilesi-updater")
                .GET().build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) {
            HilesiClient.LOGGER.info("Guncelleme kontrolu: HTTP {}", res.statusCode());
            return;
        }
        String json = res.body();
        String tag = find(json, "\"tag_name\"\\s*:\\s*\"([^\"]+)\"");
        if (tag == null) return;
        String latest = tag.startsWith("v") ? tag.substring(1) : tag;
        if (!isNewer(latest, current)) {
            HilesiClient.LOGGER.info("Hilesi guncel ({}).", current);
            return;
        }
        String url = findDownloadUrl(json);
        if (url == null) {
            pendingChat = "§e[Hilesi] Yeni surum var: " + tag + " (indirme linki bulunamadi). GitHub Releases kontrol et.";
            HilesiClient.LOGGER.info("Yeni surum var: {} (url yok)", tag);
            return;
        }
        Path modsDir = FabricLoader.getInstance().getGameDir().resolve("mods");
        Files.createDirectories(modsDir);
        String fileName = "hilesi-" + latest + ".jar";
        Path out = modsDir.resolve(fileName);
        if (Files.exists(out)) {
            pendingChat = "§a[Hilesi] Yeni surum zaten mods klasorunde: " + fileName + " — eski jar'i silip oyunu yeniden baslat.";
            HilesiClient.LOGGER.info("Guncel jar zaten var: {}", out);
            return;
        }
        HilesiClient.LOGGER.info("Yeni surum indiriliyor: {} -> {}", tag, out);
        HttpRequest dl = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(60))
                .header("User-Agent", "hilesi-updater")
                .GET().build();
        HttpResponse<InputStream> dres = http.send(dl, HttpResponse.BodyHandlers.ofInputStream());
        if (dres.statusCode() != 200) {
            pendingChat = "§c[Hilesi] Guncelleme indirilemedi (HTTP " + dres.statusCode() + ").";
            return;
        }
        Path tmp = modsDir.resolve(fileName + ".part");
        try (InputStream in = dres.body()) {
            Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
        }
        Files.move(tmp, out, StandardCopyOption.REPLACE_EXISTING);
        pendingChat = "§a[Hilesi] Guncellendi: " + fileName + " indirildi. Eski hilesi jar'ini silip oyunu yeniden baslat.";
        HilesiClient.LOGGER.info("Guncelleme indirildi: {}", out);
    }

    private static String findDownloadUrl(String json) {
        // non-sources jar asset
        Pattern p = Pattern.compile("\"browser_download_url\"\\s*:\\s*\"([^\"]*hilesi-[^\"]*\\.jar)\"");
        Matcher m = p.matcher(json);
        String fallback = null;
        while (m.find()) {
            String u = m.group(1);
            if (u.contains("-sources")) continue;
            return u;
        }
        // fallback any hilesi jar
        m = p.matcher(json);
        if (m.find()) return m.group(1);
        return null;
    }

    private static String find(String s, String regex) {
        Matcher m = Pattern.compile(regex).matcher(s);
        return m.find() ? m.group(1) : null;
    }

    private static boolean isNewer(String latest, String current) {
        try {
            int[] a = parseVer(latest);
            int[] b = parseVer(current);
            for (int i = 0; i < Math.max(a.length, b.length); i++) {
                int av = i < a.length ? a[i] : 0;
                int bv = i < b.length ? b[i] : 0;
                if (av != bv) return av > bv;
            }
            return false;
        } catch (Exception e) {
            return !latest.equals(current);
        }
    }

    private static int[] parseVer(String v) {
        String[] parts = v.split("[^0-9]+");
        int[] out = new int[parts.length];
        int n = 0;
        for (String p : parts) {
            if (p.isEmpty()) continue;
            out[n++] = Integer.parseInt(p);
        }
        int[] r = new int[n];
        System.arraycopy(out, 0, r, 0, n);
        return r;
    }
}
