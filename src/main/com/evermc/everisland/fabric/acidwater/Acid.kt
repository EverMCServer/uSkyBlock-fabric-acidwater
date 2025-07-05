package com.evermc.everisland.fabric.acidwater

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.ModInitializer
import net.minecraft.block.Block
import net.minecraft.enchantment.Enchantment
import net.minecraft.fluid.FlowableFluid
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier


@Environment(EnvType.SERVER)
class Acid : ModInitializer {
    companion object {
        val namespace = "acid"

        lateinit var ACID: FlowableFluid
        lateinit var FLOWING_ACID: FlowableFluid
        lateinit var ACID_BUCKET: Item
        lateinit var ACID_BLOCK: Block
        lateinit var ANTI_ACID: RegistryKey<Enchantment>
    }

    override fun onInitialize() {
        ACID = Registry.register(Registries.FLUID, Identifier.of(namespace, "acid"), AcidFluid.Still())
        FLOWING_ACID = Registry.register(Registries.FLUID, Identifier.of(namespace, "flowing_acid"), AcidFluid.Flowing())
        ACID_BUCKET = Registry.register(
            Registries.ITEM, Identifier.of(namespace, "acid_bucket"),
            AcidBucketItem()
        )
        ACID_BLOCK = Registry.register(
            Registries.BLOCK,
            Identifier.of(namespace, "acid_block"),
            AcidFluidBlock())

        ANTI_ACID = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(namespace, "anti_acid"))
    }
}