package org.embeddedt.createchunkloading.blocks;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {

    @ObjectHolder("createchunkloading:chunk_loader")
    public static ChunkLoader CHUNK_LOADER;

    @ObjectHolder("createchunkloading:chunk_loader")
    public static TileEntityType<ChunkLoaderTile> CHUNK_LOADER_TILE;
}
