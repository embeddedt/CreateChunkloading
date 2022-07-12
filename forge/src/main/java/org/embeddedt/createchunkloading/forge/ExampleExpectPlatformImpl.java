package org.embeddedt.createchunkloading.forge;

import com.simibubi.create.AllMovementBehaviours;
import org.embeddedt.createchunkloading.ChunkLoaderMovementBehaviour;
import org.embeddedt.createchunkloading.CreateChunkloading;
import org.embeddedt.createchunkloading.ExampleExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.UUID;

public class ExampleExpectPlatformImpl {
    /**
     * This is our actual method to {@link ExampleExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static void forceLoadChunk(ServerLevel world, int chunkX, int chunkZ, boolean state, Object entityUUID, boolean shouldLoadSurroundingAsWell) {
        if(entityUUID instanceof UUID) {
            ForgeChunkManager.forceChunk(world, CreateChunkloading.MOD_ID, (UUID)entityUUID, chunkX, chunkZ, state, false);
        } else if(entityUUID instanceof BlockPos) {
            ForgeChunkManager.forceChunk(world, CreateChunkloading.MOD_ID, (BlockPos)entityUUID, chunkX, chunkZ, state, false);
        } else
            throw new IllegalArgumentException("entityUUID must be UUID or BlockPos");
        if(shouldLoadSurroundingAsWell) {
            forceLoadChunk(world, chunkX + 1, chunkZ, state, entityUUID,false);
            forceLoadChunk(world, chunkX + 1, chunkZ + 1, state, entityUUID,false);
            forceLoadChunk(world, chunkX + 1, chunkZ - 1, state, entityUUID, false);
            forceLoadChunk(world, chunkX - 1, chunkZ, state, entityUUID, false);
            forceLoadChunk(world, chunkX - 1, chunkZ + 1, state, entityUUID, false);
            forceLoadChunk(world, chunkX - 1, chunkZ - 1, state, entityUUID,false);
            forceLoadChunk(world, chunkX, chunkZ - 1, state, entityUUID, false);
            forceLoadChunk(world, chunkX, chunkZ + 1, state, entityUUID, false);
        }
    }

    public static void registerMovementBehavior() {
        AllMovementBehaviours.registerBehaviour(CreateChunkloading.CHUNK_LOADER.get().delegate, new ChunkLoaderMovementBehaviour());
    }
}
