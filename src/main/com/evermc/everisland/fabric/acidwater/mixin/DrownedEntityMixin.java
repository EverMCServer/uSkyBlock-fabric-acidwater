package com.evermc.everisland.fabric.acidwater.mixin;

import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DrownedEntity.class)
public class DrownedEntityMixin {
    @Redirect(
            method = "canSpawn*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"
            )
    )
    private static boolean injectedWaterHeightBelow(FluidState fluidState, TagKey<Fluid> tag) {
        if (tag == FluidTags.WATER) {
            return fluidState.getFluid().getRegistryEntry().isIn(tag);
        }
        return fluidState.isIn(tag);
    }
}
