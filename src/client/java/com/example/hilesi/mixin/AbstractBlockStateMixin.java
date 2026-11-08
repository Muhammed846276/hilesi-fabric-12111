package com.example.hilesi.mixin;

import com.example.hilesi.util.XRayUtil;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {
    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true, require = 0)
    private void hilesi$getRenderType(CallbackInfoReturnable<BlockRenderType> cir) {
        try { if (XRayUtil.shouldHide((BlockState)(Object)this)) cir.setReturnValue(BlockRenderType.INVISIBLE); } catch (Throwable ignored) {}
    }

    @Inject(method = "getOpacity", at = @At("HEAD"), cancellable = true, require = 0)
    private void hilesi$getOpacity(CallbackInfoReturnable<Integer> cir) {
        try { if (XRayUtil.isEnabled()) cir.setReturnValue(0); } catch (Throwable ignored) {}
    }

    @Inject(method = "getAmbientOcclusionLightLevel", at = @At("HEAD"), cancellable = true, require = 0)
    private void hilesi$ao(CallbackInfoReturnable<Float> cir) {
        try { if (XRayUtil.isEnabled()) cir.setReturnValue(1.0f); } catch (Throwable ignored) {}
    }
}
