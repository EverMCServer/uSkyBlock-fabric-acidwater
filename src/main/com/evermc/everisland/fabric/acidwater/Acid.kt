package com.evermc.everisland.fabric.acidwater

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.block.Block
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FlowableFluid
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import net.minecraft.world.World
import java.time.Duration
import java.time.temporal.ChronoUnit


@Environment(EnvType.SERVER)
class Acid : ModInitializer {

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

        ServerTickEvents.END_SERVER_TICK.register(::checkAcidRain)
    }

    private fun checkAcidRain(server: MinecraftServer) {
        for (world in server.worlds) {
            if (world.isRaining) {  // 判断当前世界是否下雨
                for (player in world.players) { // 判断玩家是否在天空下且在雨中
                    if (world.isSkyVisible(player.blockPos)) {
                        acidPlayer(player, world)
                    }
                }
            }
        }
    }

    companion object {
        val namespace = "acid"

        lateinit var ACID: FlowableFluid
        lateinit var FLOWING_ACID: FlowableFluid
        lateinit var ACID_BUCKET: Item
        lateinit var ACID_BLOCK: Block
        lateinit var ANTI_ACID: RegistryKey<Enchantment>

        val antiAcidBlockEffectMap: Cache<PlayerEntity, Long> = CacheBuilder.newBuilder() //player: expire time
            .maximumSize(200)
            .expireAfterWrite(Duration.of(5, ChronoUnit.SECONDS)) //this is just use for auto clean
            .build()

        fun acidPlayer(player: PlayerEntity, world: World) {
            val checkBlockEffectResult = antiAcidBlockEffectMap.getIfPresent(player)
            if (checkBlockEffectResult != null) {
                if (checkBlockEffectResult >= System.currentTimeMillis()) return
            }

            var damage = 1.0f

            listOf(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET).forEach { slot ->
                val armor = player.getEquippedStack(slot)
                if (!armor.isEmpty) {
                    if (armor.hasEnchantments()) {
                        armor.enchantments.enchantments.map { it.key.get() }.find { it == Acid.ANTI_ACID }?.run {
                            if (world.random.nextInt(400) == 0) {
                                armor.damage(1, player, slot)
                            }
                            damage -= 0.25f
                        } ?: return@forEach
                    }
                }
            }

            if (damage > 0f) player.damage(world.damageSources.magic(), damage)
        }
    }
}