package com.grimmauld.createintegration.misc;

import net.minecraft.util.math.BlockPos;

public interface IChunkLoaderList {
    void add(BlockPos pos);
    void remove(BlockPos pos);

    void add(iVec2d chunk);
    void remove(iVec2d chunk);

    boolean contains(BlockPos pos);



    void start();

}