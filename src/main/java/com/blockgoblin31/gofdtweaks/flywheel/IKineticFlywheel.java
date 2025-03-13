package com.blockgoblin31.gofdtweaks.flywheel;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.nbt.CompoundTag;

public interface IKineticFlywheel {
    KineticBlockEntity getKBE();
    String LAST_DIRECTION = "lastDirection";
    String AVAILABLE_SU = "availableSU";
    String STORED_SU = "storedSU";
    String RENDER_ANGLE = "renderAngle";
    default float getMass() {
        return 512;
    }

    default float getDirection(float speed, float def) {
        if(speed > 0) {
            return 1;
        }
        if(speed < 0) {
            return -1;
        }
        return def;
    }
    default float updateSpeed(float def) {
        KineticBlockEntity be = getKBE();
        if (be.getLevel().isClientSide) {
            float targetSpeed = be.getSpeed();
            float viewSpeed = be.getPersistentData().getFloat(STORED_SU) / getMass() * getDirection(be.getSpeed(), def);
            if(!Float.isNaN(viewSpeed)) getVisualSpeed().updateChaseTarget(viewSpeed);
            getVisualSpeed().tickChaser();
        }
        return getAngle();
    }
    default float getAngle() {
        return getVisualSpeed().getValue() * 3.0F / 10.0F;
    }

    default float storeSU(float amount) {
        KineticBlockEntity be = getKBE();
        float stored = be.getPersistentData().getFloat(STORED_SU);
        if(stored < getMaxStorage()) {
            float add = Math.min(getMaxStorage() - stored, amount);
            be.getPersistentData().putFloat(AVAILABLE_SU, add);
            return add;
        }
        else be.getPersistentData().putFloat(AVAILABLE_SU, 0);
        return 0;
    }
    default boolean canStoreMoreSU() {
        float stored = getKBE().getPersistentData().getFloat(STORED_SU);
        return stored < getMaxStorage();
    }
    default float getStoredSU() {
        return getKBE().getPersistentData().getFloat(STORED_SU);
    }
    default void onTick() {
        KineticBlockEntity be = getKBE();
        float stored = be.getPersistentData().getFloat(STORED_SU);
        float add = be.getPersistentData().getFloat(AVAILABLE_SU);

        if(stored < getMaxStorage() && add > 0 && stored + add <= getMaxStorage()) {
            be.getPersistentData().putFloat(STORED_SU, stored + add);
            NetworkFlywheels.get(be.getOrCreateNetwork()).updateKineticometors(null);
        }
        else NetworkFlywheels.get(be.getOrCreateNetwork()).update();
    }

    LerpedFloat getVisualSpeed();
    default float getMaxStorage() {
        return getMass() * Math.abs(getKBE().getSpeed());
    }
}
