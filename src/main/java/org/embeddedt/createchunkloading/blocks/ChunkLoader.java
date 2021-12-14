package org.embeddedt.createchunkloading.blocks;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.world.ForgeChunkManager;
import org.embeddedt.createchunkloading.CreateChunkloading;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.UUID;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class ChunkLoader extends Block {
    public ChunkLoader() {
        super(Properties.copy(Blocks.BEACON));
        setRegistryName("chunk_loader");
    }

    @Override
    public void onPlace(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);
        if (world.isClientSide) return;
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        forgeLoadChunk((ServerLevel)world, chunkX, chunkZ, true, pos, false);
    }

    @Override
    public void onRemove(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        super.onRemove(state, world, pos, newState, isMoving);
        if (world.isClientSide) return;

        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        forgeLoadChunk((ServerLevel)world, chunkX, chunkZ, false, pos, false);
    }

    public static BlockPos roundBlockPosToChunk(BlockPos pos) {
        int roundedX = pos.getX() & ~0xf;
        int roundedZ = pos.getZ() & ~0xf;
        return new BlockPos(roundedX, 0, roundedZ);
    }

    public static void forgeLoadChunk(ServerLevel world, int chunkX, int chunkZ, boolean state, Object entityUUID, boolean shouldLoadSurroundingAsWell) {
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
    public PushReaction getPistonPushReaction(@Nonnull BlockState state) {
        return PushReaction.NORMAL;
    }
}
