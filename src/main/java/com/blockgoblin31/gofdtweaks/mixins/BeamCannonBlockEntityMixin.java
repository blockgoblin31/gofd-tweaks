package com.blockgoblin31.gofdtweaks.mixins;

import com.rekindled.embers.blockentity.BeamCannonBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BeamCannonBlockEntity.class)
public class BeamCannonBlockEntityMixin {
    @ModifyConstant(
            constant = @Constant(intValue = 1000),
            method = "serverTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lcom/rekindled/embers/blockentity/BeamCannonBlockEntity;)V",
            remap = false
    )
    private static int setMinimumShotSize(int in) {
        return 250;
    }
}
