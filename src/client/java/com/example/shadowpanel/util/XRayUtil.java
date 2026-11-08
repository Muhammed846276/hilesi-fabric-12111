package com.example.shadowpanel.util;

import com.example.shadowpanel.module.ModuleManager;
import com.example.shadowpanel.module.XRayModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;

import java.util.Set;

public final class XRayUtil {
    private static final Set<String> TARGETS = Set.of(
            "diamond_ore","deepslate_diamond_ore","iron_ore","deepslate_iron_ore","gold_ore","deepslate_gold_ore",
            "nether_gold_ore","coal_ore","deepslate_coal_ore","copper_ore","deepslate_copper_ore","emerald_ore",
            "deepslate_emerald_ore","lapis_ore","deepslate_lapis_ore","redstone_ore","deepslate_redstone_ore",
            "ancient_debris","nether_quartz_ore","spawner","chest","ender_chest","trapped_chest"
    );
    private XRayUtil(){}
    public static boolean isEnabled() {
        try { XRayModule x = ModuleManager.get(XRayModule.class); return x != null && x.isEnabled(); } catch (Throwable t) { return false; }
    }
    public static boolean isTarget(BlockState state) {
        if (state == null) return false;
        try {
            Block b = state.getBlock();
            String path = Registries.BLOCK.getId(b).getPath();
            if (TARGETS.contains(path)) return true;
            if (path.contains("ore") || path.contains("debris")) return true;
        } catch (Throwable ignored) {}
        return false;
    }
    public static boolean shouldHide(BlockState state) { return isEnabled() && !isTarget(state); }
}
