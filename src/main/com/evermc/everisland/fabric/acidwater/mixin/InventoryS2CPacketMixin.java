package com.evermc.everisland.fabric.acidwater.mixin;

import com.evermc.everisland.fabric.acidwater.Acid;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(InventoryS2CPacket.class)
public class InventoryS2CPacketMixin {
    @Mutable @Final @Shadow
    private List<ItemStack> contents;
    @Mutable @Final @Shadow
    private ItemStack cursorStack;

    @Inject(method = "<init>*", at = @At("TAIL"))
    private void onInit(int syncId, int revision, DefaultedList<ItemStack> contents, ItemStack cursorStack, CallbackInfo ci) {

        this.contents = DefaultedList.ofSize(contents.size(), ItemStack.EMPTY);

        for (int i = 0; i < contents.size(); i++) {
            if (contents.get(i).isOf(Acid.ACID_BUCKET)) {
                this.contents.set(i, Acid.getTOXIC_BUCKET());
            } else {
                this.contents.set(i, contents.get(i).copy());
            }
        }

        if (cursorStack.isOf(Acid.ACID_BUCKET)) {
            this.cursorStack = Acid.getTOXIC_BUCKET();
        } else {
            this.cursorStack = cursorStack.copy();
        }
    }
}
