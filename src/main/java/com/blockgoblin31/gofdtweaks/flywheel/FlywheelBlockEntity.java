//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.blockgoblin31.gofdtweaks.flywheel;

import com.blockgoblin31.gofdtweaks.ModBlocks;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.stringtemplate.v4.ST;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class FlywheelBlockEntity extends KineticBlockEntity {
    LerpedFloat visualSpeed = LerpedFloat.linear();
    float angle;
    float direction;
    float lastDirection = 0;

    private final String AVAILABLE_SU = "available_su";
    private final String STORED_SU = "stored_su";

    public FlywheelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate((double)2.0F);
    }

    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
    }

    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        if (clientPacket) {
            this.visualSpeed.chase((double)this.getGeneratedSpeed(), (double)0.015625F, Chaser.EXP);
        }

    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            float targetSpeed = this.getSpeed();
            float viewSpeed = getPersistentData().getFloat(STORED_SU) / maxStorage * 256 * getDirection();
            if(viewSpeed != Float.NaN) this.visualSpeed.updateChaseTarget(viewSpeed);
            this.visualSpeed.tickChaser();
            this.angle += this.visualSpeed.getValue() * 3.0F / 10.0F;
            this.angle %= 360.0F;
            direction = getDirection();
            //this.wheelAngle += maxStorage / getPersistentData().getFloat(STORED_SU) * 256 * 60 * 360 / 20;
        }
        float stored = getPersistentData().getFloat(STORED_SU);
        float add = getPersistentData().getFloat(AVAILABLE_SU);

        if(stored < maxStorage && add > 0 && stored + add <= maxStorage) {
            getPersistentData().putFloat(STORED_SU, stored + add);
            NetworkFlywheels.get(getOrCreateNetwork()).updateKineticometors(null);
        }
        else NetworkFlywheels.get(getOrCreateNetwork()).update();
    }
    float getDirection() {
        if(getSpeed() > 0) {
            lastDirection = 1;
            return 1;
        }
        if(getSpeed() < 0) {
            lastDirection = -1;
            return -1;
        }
        return lastDirection;
    }

    public final float maxStorage = 102400;
    public float storeSU(float amount) {
        float stored = getPersistentData().getFloat(STORED_SU);
        if(stored < maxStorage) {
            float add = Math.min(maxStorage - stored, amount);
            getPersistentData().putFloat(AVAILABLE_SU, add);
            return add;
        }
        else getPersistentData().putFloat(AVAILABLE_SU, 0);
        return 0;
    }
    public boolean canStoreMoreSU() {
        float stored = getPersistentData().getFloat(STORED_SU);
        return stored < maxStorage;
    }

    public float getStoredSU() {
        return getPersistentData().getFloat(STORED_SU);
    }
}
