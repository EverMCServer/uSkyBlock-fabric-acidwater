package com.evermc.everisland.fabric.acidwater.mixin;

import com.evermc.everisland.fabric.acidwater.Acid;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ConduitBlockEntity.class)
public class ConduitBlockEntityMixin {
    /**
     * @author cong
     * @reason fun
     */
    @Inject(method = "givePlayersEffects", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void atGivePlayersEffects(World world,
                                             BlockPos pos,
                                             List<BlockPos> activatingBlocks,
                                             CallbackInfo ci,
                                             int i,
                                             int j,
                                             int k,
                                             int l,
                                             int m
    ) {
        Box box = new Box(k, l, m, k + 1, l + 1, m + 1).expand(j * 2).stretch(0.0, world.getHeight(), 0.0);
        List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, box);
        if (!list.isEmpty()) {
            for (PlayerEntity playerEntity : list) {
                if (pos.isWithinDistance(playerEntity.getBlockPos(), j * 2)) {
                    Acid.Companion.getAntiAcidBlockEffectMap().put(playerEntity, System.currentTimeMillis() + 2000);
                }
            }
        }
    }
}
