package com.grimmauld.createintegration.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class RollingMachine extends BeltMachine {
    public RollingMachine() {
        super("rolling_machine");
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new RollingMachineTile();
    }
}
