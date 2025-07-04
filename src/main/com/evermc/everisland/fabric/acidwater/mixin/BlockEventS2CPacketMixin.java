package com.evermc.everisland.fabric.acidwater.mixin;

import com.evermc.everisland.fabric.acidwater.Acid;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEventS2CPacket.class)
public class BlockEventS2CPacketMixin {
    @Final @Shadow @Mutable
    private Block block;

    @Inject(method = "<init>*", at = @At("TAIL"))
    private void onInit(BlockPos pos, Block block, int type, int data, CallbackInfo ci) {
        if (block == Acid.ACID_BLOCK) {
            this.block = Acid.getDISPLAY_FLUID();
        } else {
            this.block = block;
        }
    }
}
