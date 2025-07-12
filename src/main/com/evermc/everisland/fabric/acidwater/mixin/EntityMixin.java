package com.evermc.everisland.fabric.acidwater.mixin;

import com.evermc.everisland.fabric.acidwater.AcidWater;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class EntityMixin {
    @Redirect(
            method = "updateMovementInFluid",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"
            )
    )
    private boolean injectedMovement(FluidState fluidState, TagKey<Fluid> tag) {
        if (tag == FluidTags.WATER) {
            return fluidState.isIn(tag) || fluidState.isOf(AcidWater.ACID) || fluidState.isOf(AcidWater.FLOWING_ACID);
        }
        return fluidState.isIn(tag);
    }

    @Redirect(
            method = "updateSwimming",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"
            )
    )
    private boolean injectedSwimming(FluidState fluidState, TagKey<Fluid> tag) {
        if (tag == FluidTags.WATER) {
            return fluidState.isIn(tag) || fluidState.isOf(AcidWater.ACID) || fluidState.isOf(AcidWater.FLOWING_ACID);
        }
        return fluidState.isIn(tag);
    }
}
