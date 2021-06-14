package com.grimmauld.createintegration.blocks;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {

    @ObjectHolder("createintegration:chunk_loader")
    public static ChunkLoader CHUNK_LOADER;

    @ObjectHolder("createintegration:chunk_loader")
    public static TileEntityType<ChunkLoaderTile> CHUNK_LOADER_TILE;
}
