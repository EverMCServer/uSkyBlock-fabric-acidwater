package com.evermc.everisland.fabric.acidwater.mixin;

import com.evermc.everisland.fabric.acidwater.Acid;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkDeltaUpdateS2CPacket.class)
public class ChunkDeltaUpdateS2CPacketMixin {
    @Final @Shadow @Mutable
    private short[] positions;
    @Final @Shadow @Mutable
    private BlockState[] blockStates;

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void onInit(ChunkSectionPos sectionPos, ShortSet positions, ChunkSection section, CallbackInfo ci) {
        int i = positions.size();
        this.positions = new short[i];
        this.blockStates = new BlockState[i];
        int j = 0;

        for(ShortIterator var6 = positions.iterator(); var6.hasNext(); ++j) {
            short s = var6.next();
            this.positions[j] = s;
            BlockState state = section.getBlockState(ChunkSectionPos.unpackLocalX(s), ChunkSectionPos.unpackLocalY(s), ChunkSectionPos.unpackLocalZ(s));
            if (state.getBlock() == Acid.ACID_BLOCK) {
                this.blockStates[j] = Acid.getDISPLAY_FLUID().getStateWithProperties(state);
            } else {
                this.blockStates[j] = state;
            }
        }
    }
}
