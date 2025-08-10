package com.evermc.everisland.fabric.acidwater.mixin;

import com.evermc.everisland.fabric.acidwater.AcidWater;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluids;
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

    @Inject(method = "receiveNeighborFluids",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/fluid/FlowableFluid;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"
            ),
            cancellable = true
    ) //inject before "return true"
    private void inReceiveNeighborFluids(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (this.fluid.isIn(FluidTags.LAVA)) {
            for (Direction direction : FLOW_DIRECTIONS) {
                BlockPos blockPos = pos.offset(direction.getOpposite());
                boolean bl = world.getBlockState(pos.down()).isOf(Blocks.SOUL_SOIL);

                if (world.getFluidState(blockPos).isOf(AcidWater.ACID) || world.getFluidState(blockPos).isOf(AcidWater.FLOWING_ACID)) {
                    Block block = world.getFluidState(pos).isStill() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
                    world.setBlockState(pos, block.getDefaultState());
                    this.playExtinguishSound(world, pos);
                    cir.setReturnValue(false);
                    cir.cancel();
                    return;
                }else if (world.getFluidState(blockPos).getFluid().getRegistryEntry().isIn(FluidTags.WATER)) {
                    Block block = world.getFluidState(pos).isStill() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
                    world.setBlockState(pos, block.getDefaultState());
                    this.playExtinguishSound(world, pos);
                    cir.setReturnValue(false);
                    cir.cancel();
                    return;
                }else if (bl && world.getBlockState(blockPos).isOf(Blocks.BLUE_ICE)) {
                    world.setBlockState(pos, Blocks.BASALT.getDefaultState());
                    this.playExtinguishSound(world, pos);
                    cir.setReturnValue(false);
                    cir.cancel();
                    return;
                }
            }
        } else if (this.fluid == AcidWater.ACID || this.fluid == AcidWater.FLOWING_ACID) {
            for (Direction direction : FLOW_DIRECTIONS) {
                BlockPos waterPos = pos.offset(direction.getOpposite());
                if ((world.getFluidState(waterPos).isOf(Fluids.WATER) || world.getFluidState(waterPos).isOf(Fluids.FLOWING_WATER)) && world.getBlockState(waterPos).isOf(Blocks.WATER)) {
                    AcidWater.Companion.addToSpreadEntry(world, waterPos);
                    cir.setReturnValue(true);
                    cir.cancel();
                    return;
                }
            }
        }

        cir.setReturnValue(true);
        cir.cancel();
    }
}
