package com.blockgoblin31.gofdtweaks.flywheel;


import com.blockgoblin31.gofdtweaks.kinetometer.KineticGaugeBlockEntity;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class NetworkFlywheels {
    public static HashMap<KineticNetwork, NetworkData> data = new HashMap<>();

    public static NetworkData get(KineticNetwork net) {
        if(!data.containsKey(net)) data.put(net, new NetworkData(net));
        return data.get(net);
    }

    public static class NetworkData {
        public float unusedSU = 0;
        public NetworkData(KineticNetwork network) {
            this.network = network;
        }

        public KineticNetwork network;
        public Set<FlywheelBlockEntity> flywheels = new HashSet<>();

        public void update() {
            if(network == null) return;
            float stress = network.calculateCapacity() - network.calculateStress();
            unusedSU = stress;
            float overLoadStress = 0;

            flywheels.clear();

            network.members.forEach((k,v) -> {
                if(k.getClass() == FlywheelBlockEntity.class) flywheels.add((FlywheelBlockEntity) k);
            });
            updateKineticometors(flywheels.size());


            if(overLoadStress > 0) stress = overLoadStress;
            ArrayList<FlywheelBlockEntity> flywheelsList = new ArrayList<>(flywheels);

            for(int i = flywheelsList.size() - 1; i > 0; i--) {
                if(!flywheelsList.get(i).canStoreMoreSU()) flywheelsList.remove(i);
            }
            float stressPerWheel = stress / flywheelsList.size();

            for(int i = 0; i < flywheelsList.size(); i++) {
                overLoadStress += flywheelsList.get(i).storeSU(stressPerWheel);
            }
            if(overLoadStress == 0 || flywheelsList.isEmpty()) return;
        }

        public float getTotalStored() {
            AtomicReference<Float> total = new AtomicReference<>((float) 0);
            network.members.forEach((k,v) -> {
                if(k.getClass() == FlywheelBlockEntity.class) total.updateAndGet(v1 -> new Float((float) (v1 + ((FlywheelBlockEntity) k).getStoredSU())));
            });
            return total.get();
        }

        public void updateKineticometors(Integer flywheelCount) {
            if(network != null) network.members.forEach((k,v) -> {
                if(k.getClass() == KineticGaugeBlockEntity.class) {
                    ((KineticGaugeBlockEntity) k).setDisplay(getTotalStored(), flywheelCount);
                }
            });
        }
    }
}
