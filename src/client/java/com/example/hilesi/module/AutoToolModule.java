package com.example.hilesi.module;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
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
            int best = client.player.getInventory().selectedSlot;
            float bestSpeed = 1f;
            ItemStack cur = client.player.getMainHandStack();
            bestSpeed = cur.getMiningSpeedMultiplier(state);
            for (int i = 0; i < 9; i++) {
                ItemStack s = client.player.getInventory().getStack(i);
                float sp = s.getMiningSpeedMultiplier(state);
                if (sp > bestSpeed) { bestSpeed = sp; best = i; }
            }
            if (best != client.player.getInventory().selectedSlot) {
                client.player.getInventory().selectedSlot = best;
            }
        } catch (Throwable ignored) {}
    }
}
