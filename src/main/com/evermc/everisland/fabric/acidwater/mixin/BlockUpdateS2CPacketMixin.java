package com.evermc.everisland.fabric.acidwater.mixin;

import com.evermc.everisland.fabric.acidwater.Acid;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockUpdateS2CPacket.class)
public class BlockUpdateS2CPacketMixin {
    @Shadow @Final @Mutable
    private BlockState state;

    // 修改发包时的 blockState 参数，如果是 Acid，则替换为水
    @Inject(method = "<init>*", at = @At("TAIL"))
    private void onInit(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (state.getBlock() == Acid.ACID_BLOCK) {
            this.state = Acid.getDISPLAY_FLUID().getStateWithProperties(state);
        } else {
            this.state = state;
        }
    }
}
