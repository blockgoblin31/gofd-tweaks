package com.blockgoblin31.gofdtweaks.kinetometer;

import com.blockgoblin31.gofdtweaks.ModBlocks;
import com.blockgoblin31.gofdtweaks.flywheel.NetworkFlywheels;
import com.simibubi.create.AllPackets;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.gauge.GaugeBlockEntity;
import com.simibubi.create.content.kinetics.gauge.GaugeObservedPacket;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.theme.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KineticGaugeBlockEntity extends GaugeBlockEntity {
    public AbstractComputerBehaviour computerBehaviour;
    public NetworkFlywheels.NetworkData networkData;
    public float networkStoredSU;
    static BlockPos lastSent;

    public int flywheelCount;

    public KineticGaugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.KINETIC_GAUGE_BLOCK_ENTITY.get(), pos, state);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.add(this.computerBehaviour = ComputerCraftProxy.behaviour(this));
        this.registerAwardables(behaviours, new CreateAdvancement[]{AllAdvancements.STRESSOMETER, AllAdvancements.STRESSOMETER_MAXED});
    }

    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        super.updateFromNetwork(maxStress, currentStress, networkSize);
        if (!IRotate.StressImpact.isEnabled()) {
            this.dialTarget = 0.0F;
        } else if (this.isOverStressed()) {
            this.dialTarget = 1.125F;
        } else if (maxStress == 0.0F) {
            this.dialTarget = 0.0F;
        } else {
            this.dialTarget = currentStress / maxStress;
        }

        if (this.dialTarget > 0.0F) {
            if (this.dialTarget < 0.5F) {
                this.color = Color.mixColors(65280, 16776960, this.dialTarget * 2.0F);
            } else if (this.dialTarget < 1.0F) {
                this.color = Color.mixColors(16776960, 16711680, this.dialTarget * 2.0F - 1.0F);
            } else {
                this.color = 16711680;
            }
        }

        this.sendData();
        this.setChanged();
    }

    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        if (this.getSpeed() == 0.0F) {
            this.dialTarget = 0.0F;
            this.setChanged();
        } else {
            this.updateFromNetwork(this.capacity, this.stress, this.getOrCreateNetwork().getSize());
        }
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (!IRotate.StressImpact.isEnabled()) {
            return false;
        } else {
            NetworkFlywheels.NetworkData net = NetworkFlywheels.get(getOrCreateNetwork());
            CreateLang.translate("gui.kineticometer.title", new Object[0]).style(ChatFormatting.WHITE).forGoggles(tooltip);
            CreateLang.translate("gui.kineticometer.network_storage", new Object[0]).style(ChatFormatting.GRAY).forGoggles(tooltip);
            //CreateLang.translate("gui.kineticometer.unused_us", new Object[0]).text(capacity - stress + "su").style(ChatFormatting.GRAY).forGoggles(tooltip);
            //CreateLang.translate("gui.kineticometer.flywheel_count", new Object[0]).text(String.valueOf(getPersistentData().getFloat("display_flywheel_count"))).style(ChatFormatting.GRAY).forGoggles(tooltip);
            //CreateLang.translate("gui.kineticometer.us_per_flywheel", new Object[0]).text((capacity - stress) / getPersistentData().getFloat("display_flywheel_count") + "su").style(ChatFormatting.GRAY).forGoggles(tooltip);

            //CreateLang.translate("gui.kineticometer.total_stored", new Object[0]).text(getPersistentData().getFloat("display_total_stored") + "su").style(ChatFormatting.GRAY).forGoggles(tooltip);
            CreateLang.text(TooltipHelper.makeProgressBar(3, (int) (getPersistentData().getFloat("display_total_stored") / (102400 * getPersistentData().getFloat("display_flywheel_count")) * 3))).style(ChatFormatting.GRAY).forGoggles(tooltip);
            CreateLang.translate("gui.kineticometer.remaining_storage", new Object[0]).style(ChatFormatting.GRAY).forGoggles(tooltip);
            float stored = getPersistentData().getFloat("display_total_stored");
            float cap = getPersistentData().getFloat("display_total_storage");
            if(stored != cap) CreateLang.text(stored + "su / ").text(String.valueOf(cap)).forGoggles(tooltip);
            else CreateLang.text(stored + "su").forGoggles(tooltip);
//            if(networkData != null) {
//            }
//            else CreateLang.translate("gui.kineticometer.no_network", new Object[0]).style(ChatFormatting.GRAY).forGoggles(tooltip);

            return true;
        }
    }

    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        if (clientPacket && this.worldPosition != null && this.worldPosition.equals(lastSent)) {
            lastSent = null;
        }

    }

    public float getNetworkStress() {
        return this.stress;
    }

    public float getNetworkCapacity() {
        return this.capacity;
    }

    public void onObserved() {
        this.award(AllAdvancements.STRESSOMETER);
        if (Mth.equal(this.dialTarget, 1.0F)) {
            this.award(AllAdvancements.STRESSOMETER_MAXED);
        }

    }

    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return this.computerBehaviour.isPeripheralCap(cap) ? this.computerBehaviour.getPeripheralCapability() : super.getCapability(cap, side);
    }

    public void invalidateCaps() {
        super.invalidateCaps();
        this.computerBehaviour.removePeripheral();
    }

    public void setDisplay(float totalStored, float totalStorage, Integer size) {
        getPersistentData().putFloat("display_total_stored", totalStored);
        getPersistentData().putFloat("display_total_storage", totalStorage);
        if(size != null) getPersistentData().putFloat("display_flywheel_count", size);
    }
}

