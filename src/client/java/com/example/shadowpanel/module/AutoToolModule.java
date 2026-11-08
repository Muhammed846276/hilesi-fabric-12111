package com.example.shadowpanel.module;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class AutoToolModule extends Module {
    public AutoToolModule() { super("AutoTool"); }
    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client == null || client.player == null || client.world == null) return;
        if (!client.options.attackKey.isPressed()) return;
        HitResult hr = client.crosshairTarget;
        if (!(hr instanceof BlockHitResult bhr) || hr.getType() != HitResult.Type.BLOCK) return;
        try {
            BlockState state = client.world.getBlockState(bhr.getBlockPos());
            var inv = client.player.getInventory();
            int cur = getSelectedSlot(inv);
            int best = cur;
            float bestSpeed = client.player.getMainHandStack().getMiningSpeedMultiplier(state);
            for (int i = 0; i < 9; i++) {
                float sp = inv.getStack(i).getMiningSpeedMultiplier(state);
                if (sp > bestSpeed) { bestSpeed = sp; best = i; }
            }
            if (best != cur) setSelectedSlot(inv, best);
        } catch (Throwable ignored) {}
    }
    private int getSelectedSlot(net.minecraft.entity.player.PlayerInventory inv) {
        try { var m = inv.getClass().getMethod("getSelectedSlot"); return (int) m.invoke(inv); } catch (Throwable e) {}
        try { var f = inv.getClass().getDeclaredField("selectedSlot"); f.setAccessible(true); return f.getInt(inv); } catch (Throwable e) {}
        return 0;
    }
    private void setSelectedSlot(net.minecraft.entity.player.PlayerInventory inv, int slot) {
        try { var m = inv.getClass().getMethod("setSelectedSlot", int.class); m.invoke(inv, slot); return; } catch (Throwable e) {}
        try { var f = inv.getClass().getDeclaredField("selectedSlot"); f.setAccessible(true); f.setInt(inv, slot); } catch (Throwable e) {}
    }
}
