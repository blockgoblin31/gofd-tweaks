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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class FlywheelBlockEntity extends KineticBlockEntity {
    LerpedFloat visualSpeed = LerpedFloat.linear();
    float angle;

    public FlywheelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.FLYWHEEL_BLOCK_ENTITY.get(), pos, state);
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
            this.visualSpeed.updateChaseTarget(targetSpeed);
            this.visualSpeed.tickChaser();
            this.angle += this.visualSpeed.getValue() * 3.0F / 10.0F;
            this.angle %= 360.0F;
        }

    }

    float maxLoad = 1024;
    @Override
    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        super.updateFromNetwork(maxStress, currentStress, networkSize);

        KineticNetwork network = getOrCreateNetwork();
        float lastLoad = getPersistentData().getFloat("last_load_value");
        float lastBoost = getPersistentData().getFloat("last_boost");
        float availableStress = maxStress - currentStress;
        float availableStressLoad = availableStress + lastLoad;
        float neededStressBoost = availableStress + lastBoost;

        if(network != null) {
            //network.updateCapacityFor(this, neededStressBoost < 0 ? Math.min(-neededStressBoost, 1024 / speed / 4) : 0);
            //getPersistentData().putFloat("last_boost", neededStressBoost < 0 ? Math.min(-neededStressBoost, 1024 / speed / 4) : 0)

            /*float loadValue = availableStressLoad > 0 ? Math.min(availableStressLoad, 1024 / speed / 4) : 0;
            if(lastLoad != loadValue) {
                network.updateStressFor(this, loadValue);
                getPersistentData().putFloat("last_load_value", loadValue);

            }*/

            //if(availableStress == 0 && lastLoad > 0) return;
            if((availableStress < 0 && lastLoad > 0) || (availableStress > 0 && lastLoad < maxLoad) || !network.members.containsKey(this)) {
                //getPersistentData().putFloat("last_load_value", Math.max(Math.min(lastLoad + availableStress, maxLoad),0));
                getPersistentData().putFloat("last_load_value", Math.max(Math.min(lastLoad + availableStress, maxLoad),0));
                network.updateStressFor(this, Math.max(Math.min(lastLoad + availableStress, maxLoad) / speed,0));
            }

            if((availableStress < 0 && lastBoost < maxStress) || (availableStress > 0 && lastBoost > 0) || !network.sources.containsKey(this)) {
                //getPersistentData().putFloat("last_boost", Math.max(Math.min(lastBoost - availableStress, maxLoad),0));
                //network.updateCapacityFor(this, Math.max(Math.min(lastBoost - availableStress, maxLoad) / speed,0));

            }
        }
    }

    @Override
    public float calculateStressApplied() {
        //CompoundTag tag = null;
        //read(tag, true);

        //float capacity = tag.getFloat("capacity");
        //float strsess = tag.getFloat("stress");
        float availableStress = capacity - stress;
        return availableStress > 0 ? Math.min(availableStress, 1024) : 0;
    }

    @Override
    public float calculateAddedStressCapacity() {
        float availableStress = capacity - stress;
        return availableStress < 0 ? Math.min(-availableStress, 1024) : 0;
    }

    @Override
    public float getGeneratedSpeed() {
        return 33;
    }

    @Override
    public boolean isOverStressed() {
        return super.isOverStressed();
    }
}
