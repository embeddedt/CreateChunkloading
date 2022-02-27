package org.embeddedt.createchunkloading.fabric;

import org.embeddedt.createchunkloading.CreateChunkloading;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.world.level.ChunkPos;

public class CreateChunkloadingFabric implements ModInitializer {
    public static final String MANAGER_ID = "createchunkloading:manager";
    @Override
    public void onInitialize() {
        CreateChunkloading.init();
        ServerWorldEvents.LOAD.register((server, world) -> {
            ChunkManager manager = world.getDataStorage().get(ChunkManager::load, MANAGER_ID);
            if(manager != null) {
                manager.getChunks().forEach(entry -> {
                    ChunkPos pos = new ChunkPos(entry.getValue());
                    world.setChunkForced(pos.x, pos.z, true);
                });
            }
        });
    }
}
