package com.blockgoblin31.gofdtweaks.mixins;

import com.simibubi.create.content.kinetics.gauge.StressGaugeBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(StressGaugeBlockEntity.class)
public class StressGaugeBlockEntityMixin {
    @Inject(method = "addToGoggleTooltip(Ljava/util/List;Z)Z", at=@At("TAIL"), remap = false)
    public void modify(List<Component> tooltip, boolean isPlayerSneaking, CallbackInfoReturnable<Boolean> cir) {
        CreateLang.translate("gui.stressometor.using_flywheel", new Object[0]).style(ChatFormatting.WHITE).forGoggles(tooltip);
    }
}
