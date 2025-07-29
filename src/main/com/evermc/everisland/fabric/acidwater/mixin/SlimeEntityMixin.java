package com.evermc.everisland.fabric.acidwater.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SlimeEntity.class)
public abstract class SlimeEntityMixin {
    @ModifyVariable(
            method = "canSpawn",
            at = @At(value = "STORE"),
            ordinal = 0 // 第一个 boolean bl 局部变量
    )
    private static boolean modifySlimeChunkCheck(boolean original, EntityType<SlimeEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, @Local ChunkPos chunkPos) {
        if (chunkPos.x % 5 == 0 && chunkPos.z % 5 == 0) { //出生点区块
            return true;
        } else if ((chunkPos.x + 1) % 5 == 0 && chunkPos.z % 5 == 0) {
            return true;
        } else if (chunkPos.x % 5 == 0 && (chunkPos.z + 1) % 5 == 0) {
            return true;
        } else if ((chunkPos.x + 1) % 5 == 0 && (chunkPos.z + 1) % 5 == 0) {
            return true;
        } else {
            return false;
        }
    }
}
