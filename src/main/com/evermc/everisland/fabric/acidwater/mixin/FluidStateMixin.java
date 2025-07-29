package com.evermc.everisland.fabric.acidwater.mixin;

import com.evermc.everisland.fabric.acidwater.AcidWater;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidState.class)
public abstract class FluidStateMixin {
    @Shadow
    public abstract Fluid getFluid();
    @Shadow
    public abstract boolean isOf(Fluid fluid);

    @Inject(
            method = "isIn*",
            at = @At("RETURN"),
            cancellable = true
    )
    private void injectIsIn(TagKey<Fluid> tag, CallbackInfoReturnable<Boolean> cir) {
        boolean result;
        if (tag == FluidTags.WATER) {
            result = this.getFluid().getRegistryEntry().isIn(tag) || isOf(AcidWater.ACID) || isOf(AcidWater.FLOWING_ACID);
        } else {
            result = this.getFluid().getRegistryEntry().isIn(tag);
        }
        cir.setReturnValue(result);
    }
}