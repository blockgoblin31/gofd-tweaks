package com.blockgoblin31.gofdtweaks;

import com.blockgoblin31.gofdtweaks.flywheel.FlywheelBlockRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.blockgoblin31.gofdtweaks.GofdTweaks.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EventHandler {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        //event.registerBlockEntityRenderer(ModBlocks.FLYWHEEL_BLOCK_ENTITY.get(), FlywheelBlockRenderer::new);
    }
}
