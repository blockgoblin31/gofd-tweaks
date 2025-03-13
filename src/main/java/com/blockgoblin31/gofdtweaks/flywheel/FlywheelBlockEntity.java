//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.blockgoblin31.gofdtweaks.flywheel;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class FlywheelBlockEntity extends KineticBlockEntity implements IKineticFlywheel {
    LerpedFloat visualSpeed = LerpedFloat.linear();
    float angle;
    float direction;
    float lastDirection = 0;
    public static final float mass = 512;

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
        lastDirection = getDirection(getSpeed(), lastDirection);
        angle += updateSpeed(lastDirection);
        angle %= 360;
        onTick();
    }

    @Override
    public KineticBlockEntity getKBE() {
        return this;
    }

    @Override
    public LerpedFloat getVisualSpeed() {
        return visualSpeed;
    }
}
