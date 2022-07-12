package org.embeddedt.createchunkloading.fabric;

import com.simibubi.create.AllMovementBehaviours;
import org.embeddedt.createchunkloading.ChunkLoaderMovementBehaviour;
import org.embeddedt.createchunkloading.CreateChunkloading;
import org.embeddedt.createchunkloading.ExampleExpectPlatform;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.nio.file.Path;
import java.util.UUID;

public class ExampleExpectPlatformImpl {
    /**
     * This is our actual method to {@link ExampleExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static void forceLoadChunk(ServerLevel world, int chunkX, int chunkZ, boolean state, Object entityUUID, boolean shouldLoadSurroundingAsWell) {
        if(!(entityUUID instanceof UUID))
            throw new IllegalArgumentException("Fabric only supports UUIDs");
        ChunkManager manager = world.getDataStorage().computeIfAbsent(ChunkManager::load, ChunkManager::new, CreateChunkloadingFabric.MANAGER_ID);
        for(int xDelta = -1; xDelta <= 1; xDelta++) {
            for(int zDelta = -1; zDelta <= 1; zDelta++) {
                System.out.println("Forcing " + chunkX + " " + chunkZ);
                manager.forceChunk((UUID)entityUUID, new ChunkPos(chunkX + xDelta, chunkZ + zDelta), state);
                world.setChunkForced(chunkX+xDelta, chunkZ+zDelta, state);
            }
        }
    }

    public static void registerMovementBehavior() {
        AllMovementBehaviours.registerBehaviour(CreateChunkloading.CHUNK_LOADER.get(), new ChunkLoaderMovementBehaviour());
    }
}
