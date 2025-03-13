package com.blockgoblin31.gofdtweaks.kinetometer;

import com.blockgoblin31.gofdtweaks.ModBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.gauge.GaugeShaper;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.levelWrappers.WrappedLevel;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.math.VoxelShaper;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.Vector3f;

import java.util.Arrays;

import static net.createmod.catnip.math.VoxelShaper.forDirectional;

public class KineticGaugeBlock extends DirectionalAxisKineticBlock implements IBE<KineticGaugeBlockEntity> {

    public KineticGaugeBlock(BlockBehaviour.Properties properties) {
        super(properties);
        make();
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        Direction face = context.getClickedFace();
        BlockPos placedOnPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
        BlockState placedOnState = world.getBlockState(placedOnPos);
        Block block = placedOnState.getBlock();
        if (block instanceof IRotate && ((IRotate)block).hasShaftTowards(world, placedOnPos, placedOnState, face)) {
            BlockState toPlace = this.defaultBlockState();
            Direction horizontalFacing = context.getHorizontalDirection();
            Direction nearestLookingDirection = context.getNearestLookingDirection();
            boolean lookPositive = nearestLookingDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE;
            if (face.getAxis() == Direction.Axis.X) {
                toPlace = (BlockState)((BlockState)toPlace.setValue(FACING, lookPositive ? Direction.NORTH : Direction.SOUTH)).setValue(AXIS_ALONG_FIRST_COORDINATE, true);
            } else if (face.getAxis() == Direction.Axis.Y) {
                toPlace = (BlockState)((BlockState)toPlace.setValue(FACING, horizontalFacing.getOpposite())).setValue(AXIS_ALONG_FIRST_COORDINATE, horizontalFacing.getAxis() == Direction.Axis.X);
            } else {
                toPlace = (BlockState)((BlockState)toPlace.setValue(FACING, lookPositive ? Direction.WEST : Direction.EAST)).setValue(AXIS_ALONG_FIRST_COORDINATE, false);
            }

            return toPlace;
        } else {
            return super.getStateForPlacement(context);
        }
    }

    protected Direction getFacingForPlacement(BlockPlaceContext context) {
        return context.getClickedFace();
    }

    protected boolean getAxisAlignmentForPlacement(BlockPlaceContext context) {
        return context.getHorizontalDirection().getAxis() != Direction.Axis.X;
    }

    public boolean shouldRenderHeadOnFace(Level world, BlockPos pos, BlockState state, Direction face) {
        if (face.getAxis().isVertical()) {
            return false;
        } else if (face == ((Direction)state.getValue(FACING)).getOpposite()) {
            return false;
        } else if (face.getAxis() == this.getRotationAxis(state)) {
            return false;
        } else if (this.getRotationAxis(state) == Direction.Axis.Y && face != state.getValue(FACING)) {
            return false;
        } else {
            return Block.shouldRenderFace(state, world, pos, face, pos.relative(face)) || world instanceof WrappedLevel;
        }
    }

    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (be != null && be instanceof KineticGaugeBlockEntity gaugeBE) {
            if (gaugeBE.dialTarget != 0.0F) {
                int color = gaugeBE.color;

                for(Direction face : Iterate.directions) {
                    if (this.shouldRenderHeadOnFace(worldIn, pos, stateIn, face)) {
                        Vector3f rgb = (new Color(color)).asVectorF();
                        Vec3 faceVec = Vec3.atLowerCornerOf(face.getNormal());
                        Direction positiveFacing = Direction.get(Direction.AxisDirection.POSITIVE, face.getAxis());
                        Vec3 positiveFaceVec = Vec3.atLowerCornerOf(positiveFacing.getNormal());
                        int particleCount = gaugeBE.dialTarget > 1.0F ? 4 : 1;
                        if (particleCount != 1 || !(rand.nextFloat() > 0.25F)) {
                            for(int i = 0; i < particleCount; ++i) {
                                Vec3 mul = VecHelper.offsetRandomly(Vec3.ZERO, rand, 0.25F).multiply((new Vec3((double)1.0F, (double)1.0F, (double)1.0F)).subtract(positiveFaceVec)).normalize().scale((double)0.3F);
                                Vec3 offset = VecHelper.getCenterOf(pos).add(faceVec.scale(0.55)).add(mul);
                                worldIn.addParticle(new DustParticleOptions(rgb, 1.0F), offset.x, offset.y, offset.z, mul.x, mul.y, mul.z);
                            }
                        }
                    }
                }

            }
        }
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return get((Direction)state.getValue(FACING), (Boolean)state.getValue(AXIS_ALONG_FIRST_COORDINATE));
    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (be instanceof KineticGaugeBlockEntity gaugeBlockEntity) {
            return Mth.ceil(Mth.clamp(gaugeBlockEntity.dialTarget * 14.0F, 0.0F, 15.0F));
        } else {
            return 0;
        }
    }

    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
        return false;
    }

    public Class<KineticGaugeBlockEntity> getBlockEntityClass() {
        return KineticGaugeBlockEntity.class;
    }

    public BlockEntityType<? extends KineticGaugeBlockEntity> getBlockEntityType() {
        return ModBlocks.KINETIC_GAUGE_BLOCK_ENTITY.get();
    }

    private VoxelShaper axisFalse;
    private VoxelShaper axisTrue;

    void make() {
        axisFalse = forDirectional(AllShapes.GAUGE_SHAPE_UP, Direction.UP);
        axisTrue = forDirectional(rotatedCopy(AllShapes.GAUGE_SHAPE_UP, new Vec3((double)0.0F, (double)90.0F, (double)0.0F)), Direction.UP);
        Arrays.asList(Direction.EAST, Direction.WEST).forEach((direction) -> {
            VoxelShape mem = axisFalse.get(direction);
            axisFalse.withShape(axisTrue.get(direction), direction);
            axisTrue.withShape(mem, direction);
        });
    }
    protected static VoxelShape rotatedCopy(VoxelShape shape, Vec3 rotation) {
        if (rotation.equals(Vec3.ZERO)) {
            return shape;
        } else {
            MutableObject<VoxelShape> result = new MutableObject(Shapes.empty());
            Vec3 center = new Vec3((double)8.0F, (double)8.0F, (double)8.0F);
            shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
                Vec3 v1 = (new Vec3(x1, y1, z1)).scale((double)16.0F).subtract(center);
                Vec3 v2 = (new Vec3(x2, y2, z2)).scale((double)16.0F).subtract(center);
                v1 = VecHelper.rotate(v1, (double)((float)rotation.x), Direction.Axis.X);
                v1 = VecHelper.rotate(v1, (double)((float)rotation.y), Direction.Axis.Y);
                v1 = VecHelper.rotate(v1, (double)((float)rotation.z), Direction.Axis.Z).add(center);
                v2 = VecHelper.rotate(v2, (double)((float)rotation.x), Direction.Axis.X);
                v2 = VecHelper.rotate(v2, (double)((float)rotation.y), Direction.Axis.Y);
                v2 = VecHelper.rotate(v2, (double)((float)rotation.z), Direction.Axis.Z).add(center);
                VoxelShape rotated = blockBox(v1, v2);
                result.setValue(Shapes.or((VoxelShape)result.getValue(), rotated));
            });
            return (VoxelShape)result.getValue();
        }
    }
    protected static VoxelShape blockBox(Vec3 v1, Vec3 v2) {
        return Block.box(Math.min(v1.x, v2.x), Math.min(v1.y, v2.y), Math.min(v1.z, v2.z), Math.max(v1.x, v2.x), Math.max(v1.y, v2.y), Math.max(v1.z, v2.z));
    }

    public VoxelShape get(Direction direction, boolean axisAlong) {
        return (axisAlong ? this.axisTrue : this.axisFalse).get(direction);
    }

}