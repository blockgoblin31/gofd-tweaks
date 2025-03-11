package com.blockgoblin31.gofdtweaks;

import com.blockgoblin31.gofdtweaks.flywheel.FlywheelBlock;
import com.blockgoblin31.gofdtweaks.flywheel.FlywheelBlockEntity;
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

    public static RegistryObject FLYWHEEL_BLOCK = BLOCKS.register("flywheel", () -> new FlywheelBlock(BlockBehaviour.Properties.of()));

    public static final RegistryObject<BlockEntityType<FlywheelBlockEntity>> FLYWHEEL_BLOCK_ENTITY = BLOCK_ENTITY.register("flywheel", () -> BlockEntityType.Builder.of(FlywheelBlockEntity::new, (Block) FLYWHEEL_BLOCK.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ENTITY.register(bus);
    }
}
