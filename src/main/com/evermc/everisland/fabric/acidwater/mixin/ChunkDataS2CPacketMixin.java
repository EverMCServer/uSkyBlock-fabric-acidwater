package com.evermc.everisland.fabric.acidwater.mixin;

import com.evermc.everisland.fabric.acidwater.Acid;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.BitSet;

@Mixin(ChunkDataS2CPacket.class)
public class ChunkDataS2CPacketMixin {
    @Mutable @Final @Shadow
    private ChunkData chunkData;

    @Inject(method = "<init>*", at = @At("TAIL"))
    private void onInit(WorldChunk chunk, LightingProvider lightProvider, @Nullable BitSet skyBits, @Nullable BitSet blockBits, CallbackInfo ci) {
        ChunkSection[] sections = chunk.getSectionArray();

        for (ChunkSection section : sections) {
            if (section == null || section.isEmpty()) continue;

            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        BlockState state = section.getBlockState(x, y, z);
                        if (state.isOf(Acid.ACID_BLOCK)) {
                            section.setBlockState(x, y, z,
                                    Acid.getDISPLAY_FLUID().getStateWithProperties(state)
                            );
                        }
                    }
                }
            }
        }

        this.chunkData = new ChunkData(chunk);
    }
}