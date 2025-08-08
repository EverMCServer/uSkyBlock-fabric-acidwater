package com.evermc.everisland.fabric.acidwater

import com.evermc.everisland.fabric.acidwater.AcidWater.Companion.ACID
import eu.pb4.polymer.core.api.block.PolymerBlock
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.FluidBlock
import net.minecraft.block.MapColor
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class AcidFluidBlock: FluidBlock(ACID,
    Settings.create()
        .mapColor(MapColor.WATER_BLUE)
        .replaceable()
        .noCollision()
        .strength(100.0f)
        .pistonBehavior(PistonBehavior.DESTROY)
        .dropsNothing()
        .liquid()
        .sounds(BlockSoundGroup.INTENTIONALLY_EMPTY)
    ), PolymerBlock {
    override fun getPolymerBlockState(p0: BlockState?): BlockState {
        return Blocks.WATER.getStateWithProperties(p0)
    }

    override fun onEntityCollision(state: BlockState?, world: World, pos: BlockPos?, entity: Entity) {
        if (entity is PlayerEntity) {
            if (entity.vehicle == null || entity.vehicle !is BoatEntity) {
                AcidWater.acidPlayer(entity)
            }
        }
    }
}