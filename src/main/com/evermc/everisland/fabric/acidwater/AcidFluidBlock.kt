package com.evermc.everisland.fabric.acidwater

import com.evermc.everisland.fabric.acidwater.Acid.Companion.ACID
import eu.pb4.polymer.core.api.block.PolymerBlock
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.FluidBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class AcidFluidBlock: FluidBlock(ACID, Settings.copy(Blocks.WATER)), PolymerBlock {
    override fun getPolymerBlockState(p0: BlockState?): BlockState {
        return Blocks.WATER.getStateWithProperties(p0)
    }

    override fun onEntityCollision(state: BlockState?, world: World, pos: BlockPos?, entity: Entity) {
        if (entity is PlayerEntity) {
            Acid.acidPlayer(entity, world)
        }
    }
}