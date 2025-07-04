package com.evermc.everisland.fabric.acidwater

import eu.pb4.polymer.core.api.item.PolymerItem
import net.minecraft.item.BucketItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity

class AcidBucketItem: PolymerItem, BucketItem(Acid.ACID, Settings().recipeRemainder(Items.BUCKET).maxCount(1)) {
    override fun getPolymerItem(p0: ItemStack?, p1: ServerPlayerEntity?): Item {
        return Items.WATER_BUCKET
    }
}