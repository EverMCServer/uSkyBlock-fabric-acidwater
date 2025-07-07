package com.evermc.everisland.fabric.acidwater

import eu.pb4.polymer.core.api.item.PolymerItem
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.ItemEnchantmentsComponent
import net.minecraft.component.type.LoreComponent
import net.minecraft.enchantment.Enchantments
import net.minecraft.item.BucketItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class AcidBucketItem: PolymerItem, BucketItem(AcidWater.ACID, Settings().recipeRemainder(Items.BUCKET).maxCount(1)) {
    override fun getPolymerItem(p0: ItemStack?, p1: ServerPlayerEntity?): Item {
        return Items.WATER_BUCKET
    }

    override fun getPolymerItemStack(
        itemStack: ItemStack,
        tooltipType: TooltipType,
        lookup: RegistryWrapper.WrapperLookup,
        player: ServerPlayerEntity?
    ): ItemStack {
        val toxicBucket = ItemStack(Items.WATER_BUCKET)
        toxicBucket.set(DataComponentTypes.ITEM_NAME, Text.literal("有毒的水桶").formatted(Formatting.DARK_AQUA))
        toxicBucket.set(DataComponentTypes.LORE, LoreComponent(
            listOf(
                Text.literal("从污染水源里面舀到的水").formatted(Formatting.AQUA),
                Text.literal("剧毒").formatted(Formatting.AQUA)
            )
        ));
        toxicBucket.set(DataComponentTypes.ENCHANTMENTS,
            ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT)
                .apply { add(lookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING), 1) }
                .build()
        );
        return toxicBucket
    }
}