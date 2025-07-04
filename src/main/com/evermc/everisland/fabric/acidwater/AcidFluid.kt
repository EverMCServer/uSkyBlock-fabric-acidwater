package com.evermc.everisland.fabric.acidwater

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.FluidBlock
import net.minecraft.fluid.FlowableFluid
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.FluidState
import net.minecraft.item.Item
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.tag.FluidTags
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.state.StateManager
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.*
import java.util.*

abstract class AcidFluid: FlowableFluid() {
    override fun getFlowing(): Fluid {
        return Acid.FLOWING_ACID
    }

    override fun getStill(): Fluid {
        return Acid.ACID
    }

    override fun getBucketItem(): Item {
        return Acid.ACID_BUCKET
    }

    public override fun randomDisplayTick(world: World, pos: BlockPos, state: FluidState, random: Random) {
        val blockPos = pos.up()
        if (world.getBlockState(blockPos).isAir && !world.getBlockState(blockPos).isOpaqueFullCube(world, blockPos)) {
            if (random.nextInt(100) == 0) {
                val d = pos.x.toDouble() + random.nextDouble()
                val e = pos.y.toDouble() + 1.0
                val f = pos.z.toDouble() + random.nextDouble()
                world.addParticle(ParticleTypes.HAPPY_VILLAGER, d, e, f, 0.0, 0.0, 0.0)
                world.playSound(
                    d,
                    e,
                    f,
                    SoundEvents.BLOCK_WATER_AMBIENT,
                    SoundCategory.BLOCKS,
                    0.2f + random.nextFloat() * 0.2f,
                    0.9f + random.nextFloat() * 0.15f,
                    false
                )
            }

            if (random.nextInt(200) == 0) {
                world.playSound(
                    pos.x.toDouble(),
                    pos.y.toDouble(),
                    pos.z.toDouble(),
                    SoundEvents.BLOCK_WATER_AMBIENT,
                    SoundCategory.BLOCKS,
                    0.2f + random.nextFloat() * 0.2f,
                    0.9f + random.nextFloat() * 0.15f,
                    false
                )
            }
        }
    }

    public override fun getParticle(): ParticleEffect? {
        return ParticleTypes.HAPPY_VILLAGER
    }

    override fun isInfinite(world: World): Boolean {
        return world.gameRules.getBoolean(GameRules.WATER_SOURCE_CONVERSION)
    }

    override fun beforeBreakingBlock(world: WorldAccess, pos: BlockPos?, state: BlockState) {
        val blockEntity = if (state.hasBlockEntity()) world.getBlockEntity(pos) else null
        Block.dropStacks(state, world, pos, blockEntity)
    }

    public override fun getMaxFlowDistance(world: WorldView): Int {
        return 4
    }

    public override fun toBlockState(state: FluidState): BlockState {
        return Acid.ACID_BLOCK.defaultState.with(FluidBlock.LEVEL, getBlockStateLevel(state)) as BlockState
    }

    override fun matchesType(fluid: Fluid): Boolean {
        return fluid === Acid.ACID || fluid === Acid.FLOWING_ACID
    }

    public override fun getLevelDecreasePerBlock(world: WorldView): Int {
        return 1
    }

    override fun getTickRate(world: WorldView): Int {
        return 5
    }

    public override fun canBeReplacedWith(
        state: FluidState,
        world: BlockView,
        pos: BlockPos,
        fluid: Fluid,
        direction: Direction
    ): Boolean {
        return direction == Direction.DOWN && !fluid.isIn(FluidTags.WATER)
    }

    override fun getBlastResistance(): Float {
        return 100.0f
    }

    override fun getBucketFillSound(): Optional<SoundEvent> {
        return Optional.of(SoundEvents.ITEM_BUCKET_FILL)
    }

    class Flowing: AcidFluid() {
        override fun appendProperties(builder: StateManager.Builder<Fluid, FluidState>) {
            super.appendProperties(builder)
            builder.add(LEVEL)
        }

        override fun getLevel(state: FluidState): Int {
            return state.get(LEVEL)
        }

        override fun isStill(state: FluidState): Boolean {
            return false
        }
    }

    class Still: AcidFluid() {
        override fun getLevel(state: FluidState): Int {
            return 8
        }

        override fun isStill(state: FluidState): Boolean {
            return true
        }
    }
}