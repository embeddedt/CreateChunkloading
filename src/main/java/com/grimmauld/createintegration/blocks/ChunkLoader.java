package com.grimmauld.createintegration.blocks;

import com.grimmauld.createintegration.CreateIntegration;
import com.grimmauld.createintegration.misc.ChunkLoaderMovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.IPortableBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ChunkLoader extends GlassBlock implements IPortableBlock {
    public static MovementBehaviour MOVEMENT = new ChunkLoaderMovementBehaviour();


    public ChunkLoader() {
        super(Properties.from(Blocks.BEACON));
        setRegistryName("chunk_loader");
    }

    @Override
    public void onBlockAdded(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
        if (world.isRemote) return;
        world.getCapability(CreateIntegration.CHUNK_LOADING_CAPABILITY, null).ifPresent(cap -> cap.add(pos));
    }

    @Override
    public void onReplaced(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (world.isRemote) return;
        if (!isMoving) {
            world.getCapability(CreateIntegration.CHUNK_LOADING_CAPABILITY, null).ifPresent(cap -> cap.remove(pos));
        }
    }


    @Override
    public MovementBehaviour getMovementBehaviour() {
        return MOVEMENT;
    }


    @Nonnull
    @Override
    public PushReaction getPushReaction(@Nonnull BlockState state) {
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
