package com.blockgoblin31.gofdtweaks.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.logging.LogUtils;
import com.natamus.villagespawnpoint_common_forge.events.VillageSpawnEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;

@Mixin(VillageSpawnEvent.class)
public class VillageSpawnPointMixin {
    @Shadow
    static Logger logger;

    @WrapOperation(
            method = "onWorldLoad(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/storage/ServerLevelData;)Z",
            at = @At(value = "INVOKE", target = "Lcom/natamus/collective_common_forge/functions/BlockPosFunctions;getCenterNearbyVillage(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/core/BlockPos;", remap = false),
            remap = false
    )
    private static BlockPos modify(ServerLevel server, Operation<BlockPos> original) {
        RandomSource random = server.getRandom();
        BlockPos originalPos = original.call(server);
        BlockPos returnPos = originalPos;
        logger.info("[Gofd tweaks] Offsetting spawn point");
        double startDir = random.nextDouble() * (Math.PI / 4);
        for (int i = 0; i < 8; i++) {
            double dir = startDir + (Math.PI / 4 * i);
            BlockPos pos = originalPos ;
            pos = pos.offset(Mth.floor(Math.sin(dir) * 300), 0, Mth.floor(Math.cos(dir) * 300));
            boolean valid = !(server.getBiome(pos).is(BiomeTags.IS_OCEAN) || server.getBiome(pos).is(BiomeTags.IS_RIVER));
            if(valid) {
                returnPos = server.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos);
                break;
            }
        }
        //call structure generating method
        return returnPos;
    }
}
