package com.evermc.everisland.fabric.acidwater

import com.evermc.everisland.fabric.acidwater.Acid.Companion.ACID
import eu.pb4.polymer.core.api.block.PolymerBlock
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.FluidBlock
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class AcidFluidBlock: FluidBlock(ACID, Settings.copy(Blocks.WATER)), PolymerBlock {
    override fun getPolymerBlockState(p0: BlockState?): BlockState {
        return Blocks.WATER.getStateWithProperties(p0)
    }

    override fun onEntityCollision(state: BlockState?, world: World, pos: BlockPos?, entity: Entity) {
        if (entity is PlayerEntity) {
            var damage = 1.0f

            listOf(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET).forEach { slot ->
                val armor = entity.getEquippedStack(slot)
                if (!armor.isEmpty) {
                    if (armor.hasEnchantments()) {
                        armor.enchantments.enchantments.map { it.key.get() }.find { it == Acid.ANTI_ACID }?.run {
                            if (world.random.nextInt(400) == 0) {
                                armor.damage(1, entity, slot)
                            }
                            damage -= 0.25f
                        } ?: return@forEach
                    }
                }
            }

            entity.damage(world.damageSources.magic(), damage)
        }
    }
}