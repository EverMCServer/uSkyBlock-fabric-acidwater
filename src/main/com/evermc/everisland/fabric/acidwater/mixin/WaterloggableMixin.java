package com.evermc.everisland.fabric.acidwater.mixin;

import com.evermc.everisland.fabric.acidwater.AcidWater;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Waterloggable.class)
public class WaterloggableMixin {
    @Inject(method = "tryDrainFluid", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void modifyDrainFluidReturn(PlayerEntity player, WorldAccess world, BlockPos pos, BlockState state, CallbackInfoReturnable<ItemStack> cir) {
        if (cir.getReturnValue().getItem() == Items.WATER_BUCKET) {
            // 替换为你自己的物品
            cir.setReturnValue(new ItemStack(AcidWater.ACID_BUCKET));
        }
    }
}
