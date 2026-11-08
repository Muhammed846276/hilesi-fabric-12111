package com.example.shadowpanel.module;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class AutoToolModule extends Module {
    public AutoToolModule() { super("AutoTool"); }
    @Override
    public void onTick(Minecraft client) {
        if (!isEnabled() || client == null || client.player == null || client.level == null) return;
        try { if (!client.options.keyAttack.isDown()) return; } catch(Throwable e){ return; }
        HitResult hr = client.hitResult;
        if (!(hr instanceof BlockHitResult bhr) || hr.getType() != HitResult.Type.BLOCK) return;
        try {
            BlockState state = client.level.getBlockState(bhr.getBlockPos());
            var inv = client.player.getInventory();
            int cur = getSelected(inv);
            int best = cur;
            float bestSpeed = client.player.getMainHandItem().getDestroySpeed(state);
            for (int i = 0; i < 9; i++) {
                float sp = inv.getItem(i).getDestroySpeed(state);
                if (sp > bestSpeed) { bestSpeed = sp; best = i; }
            }
            if (best != cur) setSelected(inv, best);
        } catch (Throwable ignored) {}
    }
    private int getSelected(net.minecraft.world.entity.player.Inventory inv) {
        try { var f = inv.getClass().getDeclaredField("selected"); f.setAccessible(true); return f.getInt(inv); } catch (Throwable e) {}
        try { var f = inv.getClass().getDeclaredField("selectedSlot"); f.setAccessible(true); return f.getInt(inv); } catch (Throwable e) {}
        return 0;
    }
    private void setSelected(net.minecraft.world.entity.player.Inventory inv, int slot) {
        try { var f = inv.getClass().getDeclaredField("selected"); f.setAccessible(true); f.setInt(inv, slot); return; } catch (Throwable e) {}
        try { var f = inv.getClass().getDeclaredField("selectedSlot"); f.setAccessible(true); f.setInt(inv, slot); } catch (Throwable e) {}
    }
}
