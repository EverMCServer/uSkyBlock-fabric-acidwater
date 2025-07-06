package com.evermc.everisland.fabric.acidwater.mixin;

import com.evermc.everisland.fabric.acidwater.Acid;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("deprecation")
@Mixin(FluidBlock.class)
public abstract class FluidBlockMixin {
    @Shadow @Final
    protected FlowableFluid fluid;
    @Shadow @Final
    public static ImmutableList<Direction> FLOW_DIRECTIONS;
    @Shadow
    protected abstract void playExtinguishSound(WorldAccess world, BlockPos pos);

    @Inject(method = "receiveNeighborFluids", at = @At("RETURN"), cancellable = true) //inject before "return true"
    private void inReceiveNeighborFluids(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()){ // when return is true
            if (this.fluid.isIn(FluidTags.LAVA)) {
                for (Direction direction : FLOW_DIRECTIONS) {
                    BlockPos blockPos = pos.offset(direction.getOpposite());
                    if (world.getFluidState(blockPos).isOf(Acid.ACID) || world.getFluidState(blockPos).isOf(Acid.FLOWING_ACID)) {
                        Block block = world.getFluidState(pos).isStill() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
                        world.setBlockState(pos, block.getDefaultState());
                        this.playExtinguishSound(world, pos);
                        cir.setReturnValue(false);
                        return;
                    }
                }
            } else if (this.fluid == Acid.ACID || this.fluid == Acid.FLOWING_ACID) {
                for (Direction direction : FLOW_DIRECTIONS) {
                    BlockPos waterPos = pos.offset(direction.getOpposite());
                    if (world.getFluidState(waterPos).isIn(FluidTags.WATER)) {
                        BlockState blockState = Acid.ACID_BLOCK.getStateWithProperties(world.getBlockState(waterPos));
                        world.setBlockState(waterPos, blockState);
                        this.playExtinguishSound(world, waterPos);
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }
            cir.setReturnValue(true);
        }
    }
}
