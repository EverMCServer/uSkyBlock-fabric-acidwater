package com.evermc.everisland.fabric.acidwater.mixin;

import com.evermc.everisland.fabric.acidwater.AcidWater;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BoatEntity.class)
public class BoatEntityMixin {
    @Redirect(
            method = "getWaterHeightBelow",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"
            )
    )
    private boolean injectedWaterHeightBelow(FluidState fluidState, TagKey<Fluid> tag) {
        if (tag == FluidTags.WATER) {
            return fluidState.isIn(tag) || fluidState.isOf(AcidWater.ACID) || fluidState.isOf(AcidWater.FLOWING_ACID);
        }
        return fluidState.isIn(tag);
    }

    @Redirect(
            method = "checkBoatInWater",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"
            )
    )
    private boolean injectedBoatInWater(FluidState fluidState, TagKey<Fluid> tag) {
        if (tag == FluidTags.WATER) {
            return fluidState.isIn(tag) || fluidState.isOf(AcidWater.ACID) || fluidState.isOf(AcidWater.FLOWING_ACID);
        }
        return fluidState.isIn(tag);
    }

    @Redirect(
            method = "getUnderWaterLocation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"
            )
    )
    private boolean injectedUnderWaterLocation(FluidState fluidState, TagKey<Fluid> tag) {
        if (tag == FluidTags.WATER) {
            return fluidState.isIn(tag) || fluidState.isOf(AcidWater.ACID) || fluidState.isOf(AcidWater.FLOWING_ACID);
        }
        return fluidState.isIn(tag);
    }

    @Redirect(
            method = "fall",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"
            )
    )
    private boolean injectedFall(FluidState fluidState, TagKey<Fluid> tag) {
        if (tag == FluidTags.WATER) {
            return fluidState.isIn(tag) || fluidState.isOf(AcidWater.ACID) || fluidState.isOf(AcidWater.FLOWING_ACID);
        }
        return fluidState.isIn(tag);
    }
}
