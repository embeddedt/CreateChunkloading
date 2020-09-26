package com.grimmauld.createintegration.misc;

import net.minecraft.util.math.BlockPos;

public interface IChunkLoaderList {
    void removeblock(BlockPos pos);
    void addblock(BlockPos pos);

    void add(BlockPos pos);
    void remove(BlockPos pos);

    void addchunk(iVec2d chunk);
    void removechunk(iVec2d chunk);




    void start();

}