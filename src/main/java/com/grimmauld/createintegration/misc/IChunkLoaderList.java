package com.grimmauld.createintegration.misc;

import net.minecraft.util.math.BlockPos;

public interface IChunkLoaderList {
    void add(BlockPos pos);

    void remove(BlockPos pos);

    boolean contains(BlockPos pos);

    void resetForBlock(BlockPos pos);

    void tickDown();
}