package com.example.hilesi.mixin;

import com.example.hilesi.util.XRayUtil;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @Shadow protected abstract BlockState asState();

    @Inject(method = "getRenderShape", at = @At("HEAD"), cancellable = true)
    private void hilesi$getRenderShape(CallbackInfoReturnable<RenderShape> cir) {
        try {
            if (XRayUtil.shouldHide(this.asState())) cir.setReturnValue(RenderShape.INVISIBLE);
        } catch (Throwable ignored) {}
    }

    @Inject(method = "getLightBlock", at = @At("HEAD"), cancellable = true)
    private void hilesi$getLightBlock(CallbackInfoReturnable<Integer> cir) {
        try {
            if (XRayUtil.isEnabled()) cir.setReturnValue(0);
        } catch (Throwable ignored) {}
    }

    @Inject(method = "propagatesSkylightDown", at = @At("HEAD"), cancellable = true)
    private void hilesi$propagatesSkylightDown(CallbackInfoReturnable<Boolean> cir) {
        try {
            if (XRayUtil.isEnabled()) cir.setReturnValue(true);
        } catch (Throwable ignored) {}
    }
}
