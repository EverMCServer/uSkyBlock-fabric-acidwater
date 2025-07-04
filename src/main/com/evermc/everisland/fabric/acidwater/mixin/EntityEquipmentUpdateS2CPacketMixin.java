package com.evermc.everisland.fabric.acidwater.mixin;

import com.evermc.everisland.fabric.acidwater.Acid;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntityEquipmentUpdateS2CPacket.class)
public class EntityEquipmentUpdateS2CPacketMixin {
    @Shadow @Final @Mutable
    private List<Pair<EquipmentSlot, ItemStack>> equipmentList;

    @Inject(method = "<init>*", at = @At("TAIL"))
    private void onInit(int entityId, List<Pair<EquipmentSlot, ItemStack>> equipmentList, CallbackInfo ci) {
        List<Pair<EquipmentSlot, ItemStack>> newList = new java.util.ArrayList<>(List.of());

        for (Pair<EquipmentSlot, ItemStack> equipmentSlotItemStackPair : equipmentList) {
            if (equipmentSlotItemStackPair.getSecond().isOf(Acid.ACID_BUCKET)) {
                newList.add(Pair.of(equipmentSlotItemStackPair.getFirst(), Acid.getTOXIC_BUCKET()));
            } else {
                newList.add(equipmentSlotItemStackPair);
            }
        }


        this.equipmentList = newList;
    }
}
