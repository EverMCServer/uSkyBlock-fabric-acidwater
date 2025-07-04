package com.evermc.everisland.fabric.acidwater.mixin;

import com.evermc.everisland.fabric.acidwater.Acid;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandlerSlotUpdateS2CPacket.class)
public class ScreenHandlerSlotUpdateS2CPacketMixin {
    @Shadow @Final @Mutable
    private ItemStack stack;

    @Inject(method = "<init>*", at = @At("TAIL"))
    private void onInit(int syncId, int revision, int slot, ItemStack stack, CallbackInfo ci) {
        if (stack.isOf(Acid.ACID_BUCKET)) {
            this.stack = Acid.getTOXIC_BUCKET();
        } else {
            this.stack = stack.copy();
        }
    }
}
