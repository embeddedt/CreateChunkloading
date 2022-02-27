package org.embeddedt.createchunkloading.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import org.embeddedt.createchunkloading.CreateChunkloading;

public class CreateChunkloadingFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(CreateChunkloading.CHUNK_LOADER.get(), RenderType.cutout());
    }
}
