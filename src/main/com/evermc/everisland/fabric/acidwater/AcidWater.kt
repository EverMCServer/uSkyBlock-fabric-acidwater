package com.evermc.everisland.fabric.acidwater

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.Enchantments.UNBREAKING
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FlowableFluid
import net.minecraft.fluid.Fluids
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.BlockEvent
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*


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
        ServerTickEvents.END_SERVER_TICK.register(::doAcidSpreadTick)
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

    private fun doAcidSpreadTick(server: MinecraftServer) {
        val cached = toSpread
        toSpread = HashSet<Pair<World, BlockPos>>()
        cached.forEach { (world, waterPos) ->
            if ((world.getFluidState(waterPos).isOf(Fluids.WATER) || world.getFluidState(waterPos).isOf(Fluids.FLOWING_WATER)) && world.getBlockState(waterPos).isOf(Blocks.WATER)) {
                val blockState = ACID_BLOCK.getStateWithProperties(world.getBlockState(waterPos))
                world.setBlockState(waterPos, blockState)
                // Copied from `playExtinguishSound` in `WaterFluid`
                world.syncWorldEvent(1501, waterPos, 0)
            }
        }
        cached.clear()
    }

    private fun doAcidDamageTick(server: MinecraftServer) {
        checkAcidRain(server)

        val toRemove = mutableListOf<UUID>()
        dmgTick.forEach { (uuid, pair) ->
            val (ticks, inAcid) = pair
            val player = server.playerManager.getPlayer(uuid) ?: run {
                toRemove.add(uuid)
                return@forEach
            }

            if (inAcid) {
                if (ticks <= 1) {
                    val antiAcidArmors = getAntiAcidArmors(player)
                    if (antiAcidArmors.isEmpty()) {
                        // If no antiacid armor, check if player has coal in inventory
                        val coalStack = player.inventory.main.firstOrNull { it.isOf(Items.COAL) }
                        if (coalStack == null) {
                            player.damage(player.world.damageSources.magic(), 1.0f)
                        } else {
                            // If player has coal, consume one coal every 60 times (2 coals per minute)
                            val count = coalCount[player.uuid]
                            coalCount[player.uuid] = if (count==null) 1 else count+1
                            if (coalCount[player.uuid]!! >= 60) {
                                coalCount[player.uuid] = 0
                                coalStack.decrement(1)
                                if (coalStack.count <= 0) {
                                    player.inventory.removeOne(coalStack)
                                }
                            }
                        }
                    } else {
                        val n = antiAcidArmors.size
                        // This is intended: more antiacid armor, less chance to be damaged
                        if (player.world.random.nextInt(n*2) == 0) {
                            // Damage one random antiacid armor 1 durability
                            val (armor, slot) = antiAcidArmors.random()
                            // Get unbreaking level of the armor
                            val unbreaking = armor.enchantments.enchantments.find { it.key.get()==UNBREAKING }
                            var level = 0
                            if (unbreaking!=null) {
                                level = armor.enchantments.getLevel(unbreaking)
                            }

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
                toRemove.add(uuid)
            }
        }
        toRemove.forEach { uuid ->
            dmgTick.remove(uuid)
            coalCount.remove(uuid)
        }
        toRemove.clear()
    }

    companion object {
        val namespace = "acidwater"
        //  uuid -> (#ticks to next damage, in acid now?)
        val dmgTick = HashMap<UUID, Pair<Int, Boolean>>()
        var coalCount = HashMap<UUID, Int>()
        var toSpread = HashSet<Pair<World, BlockPos>>()
        lateinit var ACID: FlowableFluid
        lateinit var FLOWING_ACID: FlowableFluid
        lateinit var ACID_BUCKET: Item
        lateinit var ACID_BLOCK: Block
        lateinit var ANTI_ACID: RegistryKey<Enchantment>

        val antiAcidBlockEffectMap: Cache<PlayerEntity, Long> = CacheBuilder.newBuilder() //player: expire time
            .maximumSize(200)
            .expireAfterWrite(Duration.of(5, ChronoUnit.SECONDS)) //this is just use for auto clean
            .build()

        fun addToSpreadEntry(world: World, pos: BlockPos) {
            toSpread.add(Pair(world, pos))
        }
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