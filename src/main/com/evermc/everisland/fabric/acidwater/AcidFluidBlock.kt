package com.evermc.everisland.fabric.acidwater

import com.evermc.everisland.fabric.acidwater.Acid.Companion.ACID
import eu.pb4.polymer.core.api.block.PolymerBlock
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.FluidBlock

class AcidFluidBlock: FluidBlock(ACID, Settings.copy(Blocks.WATER)), PolymerBlock {
    override fun getPolymerBlockState(p0: BlockState?): BlockState {
        return Blocks.WATER.getStateWithProperties(p0)
    }
}