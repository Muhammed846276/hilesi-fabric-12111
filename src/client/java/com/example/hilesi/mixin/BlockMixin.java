package com.example.hilesi.mixin;

import com.example.hilesi.util.XRayUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "shouldRenderFace(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z", at = @At("HEAD"), cancellable = true, require = 0)
    private static void hilesi$shouldRenderFace(BlockState state, BlockState other, Direction dir, CallbackInfoReturnable<Boolean> cir) {
        try {
            if (XRayUtil.isEnabled()) {
                if (XRayUtil.isTarget(state)) cir.setReturnValue(true);
                else cir.setReturnValue(false);
            }
        } catch (Throwable ignored) {}
    }
}
