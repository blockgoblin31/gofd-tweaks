package com.blockgoblin31.gofdtweaks.mixins;

import com.blockgoblin31.gofdtweaks.flywheel.FlywheelBlockEntity;
import com.blockgoblin31.gofdtweaks.flywheel.NetworkFlywheels;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(KineticNetwork.class)
public abstract class KineticNetworkMixin {

    @Shadow public Long id;

    @Shadow public Map<KineticBlockEntity, Float> members;

    @Shadow public Map<KineticBlockEntity, Float> sources;

    @Inject(method = "sync()V",at=@At("TAIL"), remap=false)
    private void modify(CallbackInfo ci) {
        KineticNetwork network = (KineticNetwork)(Object) this;
        NetworkFlywheels.NetworkData flyNetwork = NetworkFlywheels.get(network);
        flyNetwork.update();

        float stress = network.calculateStress();
        float maxStress = network.calculateCapacity();

        if(stress > maxStress) {
            float neededSU = stress - maxStress;
            if(true) {//flyNetwork.canExtract(neededSU)) {
                //flyNetwork.exract(neededSU);

                for(KineticBlockEntity be : this.members.keySet()) {
                    be.updateFromNetwork(maxStress + neededSU, stress, network.getSize());
                }

                for(KineticBlockEntity be : this.sources.keySet()) {
                    be.updateFromNetwork(maxStress + neededSU, stress, network.getSize());
                }
            }
        }

    }
    @Inject(method = "remove(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;)V",at=@At("TAIL"), remap=false)
    private void remove(CallbackInfo ci) {
        NetworkFlywheels.get((KineticNetwork)(Object)this).update();
    }
}
