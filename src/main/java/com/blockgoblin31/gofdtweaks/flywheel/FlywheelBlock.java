//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.blockgoblin31.gofdtweaks.flywheel;

import com.blockgoblin31.gofdtweaks.ModBlocks;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FlywheelBlock extends RotatedPillarKineticBlock implements IBE<FlywheelBlockEntity> {
    public FlywheelBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public Class<FlywheelBlockEntity> getBlockEntityClass() {
        return FlywheelBlockEntity.class;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AllShapes.LARGE_GEAR.get((Direction.Axis)pState.getValue(AXIS));
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public BlockEntityType<? extends FlywheelBlockEntity> getBlockEntityType() {
        return (BlockEntityType) ModBlocks.FLYWHEEL_SOMETHING.get();
    }

    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == this.getRotationAxis(state);
    }

    public Direction.Axis getRotationAxis(BlockState state) {
        return (Direction.Axis)state.getValue(AXIS);
    }

    public float getParticleTargetRadius() {
        return 2.0F;
    }

    public float getParticleInitialRadius() {
        return 1.75F;
    }
}
