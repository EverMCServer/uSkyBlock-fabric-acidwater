package com.evermc.everisland.fabric.acidwater

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.ModInitializer
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.FluidBlock
import net.minecraft.fluid.FlowableFluid
import net.minecraft.item.BucketItem
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier


@Environment(EnvType.SERVER)
class Acid : ModInitializer {
    companion object {
        lateinit var ACID: FlowableFluid
        lateinit var FLOWING_ACID: FlowableFluid
        lateinit var ACID_BUCKET: Item
        lateinit var ACID_BLOCK: Block
    }

    override fun onInitialize() {
        ACID = Registry.register(Registries.FLUID, Identifier.of("acid", "acid"), AcidFluid.Still())
        FLOWING_ACID = Registry.register(Registries.FLUID, Identifier.of("acid", "flowing_acid"), AcidFluid.Flowing())
        ACID_BUCKET = Registry.register(
            Registries.ITEM, Identifier.of("acid", "acid_bucket"),
            AcidBucketItem()
        )
        ACID_BLOCK = Registry.register(
            Registries.BLOCK,
            Identifier.of("acid", "acid_block"),
            AcidFluidBlock())
    }
}
