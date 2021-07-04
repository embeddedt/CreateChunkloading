package org.embeddedt.createchunkloading.blocks;

import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;
import org.embeddedt.createchunkloading.CreateChunkloading;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ChunkLoader extends Block {
    public ChunkLoader() {
        super(Properties.from(Blocks.BEACON));
        setRegistryName("chunk_loader");
    }

    @Override
    public void onBlockAdded(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
        super.onBlockAdded(state, world, pos, oldState, isMoving);
        if (world.isRemote) return;
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        forgeLoadChunk((ServerWorld)world, chunkX, chunkZ, true, pos, false);
    }

    @Override
    public void onReplaced(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        super.onReplaced(state, world, pos, newState, isMoving);
        if (world.isRemote) return;

        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        forgeLoadChunk((ServerWorld)world, chunkX, chunkZ, false, pos, false);
    }

    public static BlockPos roundBlockPosToChunk(BlockPos pos) {
        int roundedX = pos.getX() & ~0xf;
        int roundedZ = pos.getZ() & ~0xf;
        return new BlockPos(roundedX, 0, roundedZ);
    }

    public static void forgeLoadChunk(ServerWorld world, int chunkX, int chunkZ, boolean state, Object entityUUID, boolean shouldLoadSurroundingAsWell) {
        //CreateChunkloading.logger.debug((state ? "LOAD" : "UNLOAD") + " " + chunkX + " " + chunkZ);
        if(entityUUID instanceof UUID) {
            ForgeChunkManager.forceChunk(world, CreateChunkloading.modid, (UUID)entityUUID, chunkX, chunkZ, state, true);
        } else if(entityUUID instanceof BlockPos) {
            ForgeChunkManager.forceChunk(world, CreateChunkloading.modid, (BlockPos)entityUUID, chunkX, chunkZ, state, true);
        } else
            throw new IllegalArgumentException("entityUUID must be UUID or BlockPos");
        if(shouldLoadSurroundingAsWell) {
            forgeLoadChunk(world, chunkX + 1, chunkZ, state, entityUUID,false);
            forgeLoadChunk(world, chunkX + 1, chunkZ + 1, state, entityUUID,false);
            forgeLoadChunk(world, chunkX + 1, chunkZ - 1, state, entityUUID, false);
            forgeLoadChunk(world, chunkX - 1, chunkZ, state, entityUUID, false);
            forgeLoadChunk(world, chunkX - 1, chunkZ + 1, state, entityUUID, false);
            forgeLoadChunk(world, chunkX - 1, chunkZ - 1, state, entityUUID,false);
            forgeLoadChunk(world, chunkX, chunkZ - 1, state, entityUUID, false);
            forgeLoadChunk(world, chunkX, chunkZ + 1, state, entityUUID, false);
        }
    }


    @Nonnull
    @Override
    public PushReaction getPushReaction(@Nonnull BlockState state) {
        return PushReaction.NORMAL;
    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return false;
    }
}
