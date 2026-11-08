package com.example.hilesi.mixin;

import com.example.hilesi.util.XRayUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "shouldDrawSide(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z", at = @At("HEAD"), cancellable = true, require = 0)
    private static void hilesi$shouldDrawSide(BlockState state, BlockState other, Direction side, CallbackInfoReturnable<Boolean> cir) {
        try {
            if (XRayUtil.isEnabled()) {
                if (XRayUtil.isTarget(state)) cir.setReturnValue(true); else cir.setReturnValue(false);
            }
        } catch (Throwable ignored) {}
    }
}
