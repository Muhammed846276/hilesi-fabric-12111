package com.example.hilesi.mixin;

import com.example.hilesi.util.XRayUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
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

    @Inject(method = "getRenderShape", at = @At("HEAD"), cancellable = true, require = 0)
    private void hilesi$getRenderShape(CallbackInfoReturnable<RenderShape> cir) {
        try { if (XRayUtil.shouldHide(this.asState())) cir.setReturnValue(RenderShape.INVISIBLE); } catch (Throwable ignored) {}
    }

    @Inject(method = "getLightBlock", at = @At("HEAD"), cancellable = true, require = 0)
    private void hilesi$getLightBlock(CallbackInfoReturnable<Integer> cir) {
        try { if (XRayUtil.isEnabled()) cir.setReturnValue(0); } catch (Throwable ignored) {}
    }

    @Inject(method = "getLightBlock(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)I", at = @At("HEAD"), cancellable = true, require = 0)
    private void hilesi$getLightBlock2(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        try { if (XRayUtil.isEnabled()) cir.setReturnValue(0); } catch (Throwable ignored) {}
    }

    @Inject(method = "propagatesSkylightDown", at = @At("HEAD"), cancellable = true, require = 0)
    private void hilesi$propagatesSkylightDown(CallbackInfoReturnable<Boolean> cir) {
        try { if (XRayUtil.isEnabled()) cir.setReturnValue(true); } catch (Throwable ignored) {}
    }

    @Inject(method = "propagatesSkylightDown(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z", at = @At("HEAD"), cancellable = true, require = 0)
    private void hilesi$propagatesSkylightDown2(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        try { if (XRayUtil.isEnabled()) cir.setReturnValue(true); } catch (Throwable ignored) {}
    }
}
