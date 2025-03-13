package com.blockgoblin31.gofdtweaks.flywheel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class FlywheelBlockRenderer extends KineticBlockEntityRenderer<FlywheelBlockEntity> {
    protected void renderSafe(FlywheelBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        if(!VisualizationManager.supportsVisualization(be.getLevel())) {
            BlockState blockState = be.getBlockState();
            float speed = be.visualSpeed.getValue(partialTicks) * 3.0F / 10.0F;
            float angle = be.angle + speed * partialTicks;
            VertexConsumer vb = buffer.getBuffer(RenderType.solid());
            this.renderFlywheel(be, ms, light, blockState, angle, vb);
        }
    }

    private void renderFlywheel(FlywheelBlockEntity be, PoseStack ms, int light, BlockState blockState, float angle, VertexConsumer vb) {
        SuperByteBuffer wheel = CachedBuffers.block(blockState);
        kineticRotationTransform(wheel, be, getRotationAxisOf(be), AngleHelper.rad((double)angle), light);
        wheel.renderInto(ms, vb);
    }

    protected BlockState getRenderedBlockState(FlywheelBlockEntity be) {
        return shaft(getRotationAxisOf(be));
    }

    public FlywheelBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
//
//    protected void renderSafe(FlywheelBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
//        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
//        if(VisualizationManager.supportsVisualization(be.getLevel())) {
//            BlockState blockState = be.getBlockState();
//            float speed = be.visualSpeed.getValue(partialTicks) * 3.0F / 10.0F;
//            float angle = be.angle + speed * partialTicks;
//            VertexConsumer vb = buffer.getBuffer(RenderType.solid());
//            this.renderShaft(be, ms, light, blockState, angle, vb);
//            this.renderWheel(be, ms, light, blockState, be.wheelAngle, vb);
//        }
//    }
//
//    private void renderShaft(FlywheelBlockEntity be, PoseStack ms, int light, BlockState blockState, float angle, VertexConsumer vb) {
//        SuperByteBuffer wheel = CachedBuffers.partialFacing(AllPartialModels.SHAFT, blockState, getDirection(blockState.getValue(BlockStateProperties.AXIS)));
//        kineticRotationTransform(wheel, be, getRotationAxisOf(be), AngleHelper.rad((double)angle), light);
//        wheel.renderInto(ms, vb);
//    }
//    private void renderWheel(FlywheelBlockEntity be, PoseStack ms, int light, BlockState blockState, float angle, VertexConsumer vb) {
//        SuperByteBuffer wheel = CachedBuffers.partialFacing(AllPartialModels.FLYWHEEL, blockState, getDirection(blockState.getValue(BlockStateProperties.AXIS)));
//        kineticRotationTransform(wheel, be, getRotationAxisOf(be), AngleHelper.rad((double)angle), light);
//        wheel.renderInto(ms, vb);
//    }
//
//    protected BlockState getRenderedBlockState(FlywheelBlockEntity be) {
//        return shaft(getRotationAxisOf(be));
//    }
//    private Direction getDirection(Direction.Axis axis) {
//        switch(axis) {
//            case X -> {
//                return Direction.EAST;
//            }
//            case Y -> {
//                return Direction.UP;
//            }
//            case Z -> {
//                return Direction.SOUTH;
//            }
//        }
//        return Direction.UP;
//    }
}

