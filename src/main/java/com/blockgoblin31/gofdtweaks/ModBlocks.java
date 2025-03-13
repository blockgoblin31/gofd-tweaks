package com.blockgoblin31.gofdtweaks;

import com.blockgoblin31.gofdtweaks.flywheel.FlywheelBlock;
import com.blockgoblin31.gofdtweaks.flywheel.FlywheelBlockEntity;
import com.blockgoblin31.gofdtweaks.flywheel.FlywheelBlockRenderer;
import com.blockgoblin31.gofdtweaks.flywheel.FlywheelBlockVisual;
import com.blockgoblin31.gofdtweaks.kinetometer.KineticGaugeBlockEntity;
import com.blockgoblin31.gofdtweaks.kinetometer.KineticGaugeBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.flywheel.FlywheelRenderer;
import com.simibubi.create.content.kinetics.flywheel.FlywheelVisual;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityVisual;
import com.simibubi.create.content.schematics.cannon.SchematicannonRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.blockgoblin31.gofdtweaks.GofdTweaks.MODID;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID).setTooltipModifierFactory((item) -> (new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)).andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    //public static final DeferredRegister BLOCK_ENTITY_RENDERER = DeferredRegister.create(ForgeRegistries.BLO)

    public static RegistryObject FLYWHEEL_BLOCK = BLOCKS.register("flywheel", () -> new FlywheelBlock(BlockBehaviour.Properties.of()));
    public static RegistryObject KINETIC_GAUGE_BLOCK = BLOCKS.register("kineticometor", () -> new KineticGaugeBlock(BlockBehaviour.Properties.of()));

    //public static final RegistryObject<BlockEntityType<FlywheelBlockEntity>> FLYWHEEL_BLOCK_ENTITY = BLOCK_ENTITY.register("flywheel", () -> BlockEntityType.Builder.of(FlywheelBlockEntity::new, (Block) FLYWHEEL_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<KineticGaugeBlockEntity>> KINETIC_GAUGE_BLOCK_ENTITY = BLOCK_ENTITY.register("kineticometor", () -> BlockEntityType.Builder.of(KineticGaugeBlockEntity::new, (Block) KINETIC_GAUGE_BLOCK.get()).build(null));

    public static final BlockEntityEntry FLYWHEEL_SOMETHING = REGISTRATE.blockEntity("flywheel", FlywheelBlockEntity::new).visual(() -> FlywheelBlockVisual::new, false).validBlocks(new NonNullSupplier[]{FLYWHEEL_BLOCK::get}).renderer(() -> (NonNullFunction<Object, Object>) o -> new FlywheelBlockRenderer((BlockEntityRendererProvider.Context) o)).register();

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ENTITY.register(bus);
    }

}
