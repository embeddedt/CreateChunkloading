package org.embeddedt.createchunkloading.blocks;


import org.embeddedt.createchunkloading.CreateChunkloading;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class ChunkLoaderTile extends TileEntity implements ITickableTileEntity {
    public ChunkLoaderTile() {
        super(ModBlocks.CHUNK_LOADER_TILE);
        //world.getCapability(CreateIntegration.CHUNK_LOADING_CAPABILITY, null).ifPresent(cap -> cap.addchunk(pos));
    }

    @Override
    public void tick() {}
    @Override
    public void remove(){
        //TODO(remove tile entety)
        super.remove();
        world.getCapability(CreateChunkloading.CHUNK_LOADING_CAPABILITY, null).ifPresent(cap -> cap.removeblock(pos));
    }


}
