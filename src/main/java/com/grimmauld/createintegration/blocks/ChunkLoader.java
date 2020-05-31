package com.grimmauld.createintegration.blocks;

import com.grimmauld.createintegration.CreateIntegration;
import com.grimmauld.createintegration.misc.ChunkLoaderMovementBehaviour;
import com.simibubi.create.modules.contraptions.components.contraptions.IPortableBlock;
import com.simibubi.create.modules.contraptions.components.contraptions.MovementBehaviour;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ChunkLoader extends GlassBlock implements IPortableBlock {
    public static MovementBehaviour MOVEMENT = new ChunkLoaderMovementBehaviour();


    public ChunkLoader() {
        super(Properties.from(Blocks.ANDESITE));
        setRegistryName("chunk_loader");
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        // super.onBlockAdded(state, world, pos, oldState, isMoving); // needed?
        if (world.isRemote) return;
        world.getCapability(CreateIntegration.CHUNK_LOADING_CAPABILITY, null).ifPresent(cap -> cap.add(pos));
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (world.isRemote) return;
        if (!isMoving) {
            world.getCapability(CreateIntegration.CHUNK_LOADING_CAPABILITY, null).ifPresent(cap -> cap.remove(pos));
        }
    }


    @Override
    public MovementBehaviour getMovementBehaviour() {
        return MOVEMENT;
    }


    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.NORMAL;
    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ChunkLoaderTile();
    }

}
