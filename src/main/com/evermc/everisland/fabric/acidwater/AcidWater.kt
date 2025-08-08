package com.evermc.everisland.fabric.acidwater

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.block.Block
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.Enchantments.UNBREAKING
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FlowableFluid
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.UUID


@Environment(EnvType.SERVER)
class AcidWater : ModInitializer {

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

        ServerTickEvents.END_SERVER_TICK.register(::doAcidDamageTick)
    }

    private fun checkAcidRain(server: MinecraftServer) {
        for (world in server.worlds) {
            if (world.isRaining) {  // 判断当前世界是否下雨
                for (player in world.players) { // 判断玩家是否在天空下且在雨中
                    if (world.isSkyVisible(player.blockPos)) {
                        acidPlayer(player)
                    }
                }
            }
        }
    }

    private fun getAntiAcidArmors(player: PlayerEntity): List<Pair<ItemStack, EquipmentSlot>> {
        val ret = mutableListOf<Pair<ItemStack, EquipmentSlot>>()  // Mutable list to hold armor items with Anti-Acid enchantment
        listOf(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET).forEach { slot ->
            val armor = player.getEquippedStack(slot)
            if (!armor.isEmpty) {
                if (armor.hasEnchantments()) {
                    if (armor.enchantments.enchantments.map { it.key.get() }.contains(ANTI_ACID)) {
                        ret.add(Pair(armor, slot))
                    }
                }
            }
        }
        return ret
    }
    private fun doAcidDamageTick(server: MinecraftServer) {
        checkAcidRain(server)

        dmgTick.forEach { (uuid, pair) ->
            val (ticks, inAcid) = pair
            val player = server.playerManager.getPlayer(uuid) ?: run {
                dmgTick.remove(uuid)
                return@forEach
            }

            if (inAcid) {
                if (ticks <= 1) {
                    val antiAcidArmors = getAntiAcidArmors(player)
                    if (antiAcidArmors.isEmpty()) {
                        player.damage(player.world.damageSources.magic(), 1.0f)
                    } else {
                        val n = antiAcidArmors.size
                        // This is intended: more antiacid armor, less chance to be damaged
                        if (player.world.random.nextInt(n) == 0) {
                            // Damage one random antiacid armor 1 durability
                            val (armor, slot) = antiAcidArmors.random()
                            // Get unbreaking level of the armor
                            val unbreaking = armor.enchantments.enchantments.find { it.key.get()==UNBREAKING }
                            var level = 0
                            if (unbreaking!=null) {
                                level = armor.enchantments.getLevel(unbreaking)
                            }
                            print("Unbreaking level: $level\n")
                            if (player.world.random.nextInt(level+1)==0) {
                                // Damage armor
                                armor.damage(1, player, slot)
                            }
                        }
                    }
                    dmgTick[uuid] = Pair(10, false) // Reset ticks after damage
                } else {
                    dmgTick[uuid] = Pair(ticks - 1, false)
                }
            } else {
                dmgTick.remove(uuid)
            }
        }
    }

    companion object {
        val namespace = "acidwater"
        //  uuid -> (#ticks to next damage, in acid now?)
        val dmgTick = HashMap<UUID, Pair<Int, Boolean>>()
        lateinit var ACID: FlowableFluid
        lateinit var FLOWING_ACID: FlowableFluid
        lateinit var ACID_BUCKET: Item
        lateinit var ACID_BLOCK: Block
        lateinit var ANTI_ACID: RegistryKey<Enchantment>

        val antiAcidBlockEffectMap: Cache<PlayerEntity, Long> = CacheBuilder.newBuilder() //player: expire time
            .maximumSize(200)
            .expireAfterWrite(Duration.of(5, ChronoUnit.SECONDS)) //this is just use for auto clean
            .build()

        fun acidPlayer(player: PlayerEntity) {
            if (player.isSpectator || player.isCreative) return

            val checkBlockEffectResult = antiAcidBlockEffectMap.getIfPresent(player)
            if (checkBlockEffectResult != null) {
                if (checkBlockEffectResult >= System.currentTimeMillis()) return
            }

            val value = dmgTick.get(player.uuid)
            value?.let {
                dmgTick.put(player.uuid, Pair(it.first, true))
            }?: run {
                dmgTick.put(player.uuid, Pair(10, true))
            }
        }
    }
}