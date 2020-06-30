package com.grimmauld.createintegration.blocks;


import com.grimmauld.createintegration.CreateIntegration;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class ChunkLoaderTile extends TileEntity implements ITickableTileEntity {
    public ChunkLoaderTile() {
        super(ModBlocks.CHUNK_LOADER_TILE);
        //world.getCapability(CreateIntegration.CHUNK_LOADING_CAPABILITY, null).ifPresent(cap -> cap.add(pos));
    }

    @Override
    public void tick() {}
    @Override
    public void remove(){
        super.remove();
        world.getCapability(CreateIntegration.CHUNK_LOADING_CAPABILITY, null).ifPresent(cap -> cap.remove(pos));
    }


}
