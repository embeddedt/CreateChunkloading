package org.embeddedt.createchunkloading.blocks;

import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;
import org.embeddedt.createchunkloading.CreateChunkloading;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ChunkLoader extends Block {
    public ChunkLoader() {
        super(Properties.from(Blocks.BEACON));
        setRegistryName("chunk_loader");
    }

    @Override
    public void onBlockAdded(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
        if (world.isRemote) return;
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        CreateChunkloading.logger.debug("ADD " + chunkX + " " + chunkZ);
        forgeLoadChunk((ServerWorld)world, pos, true);
    }

    @Override
    public void onReplaced(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (world.isRemote) return;

        forgeLoadChunk((ServerWorld)world, pos, false);
    }

    public static BlockPos roundBlockPosToChunk(BlockPos pos) {
        int roundedX = pos.getX() & ~0xf;
        int roundedZ = pos.getZ() & ~0xf;
        return new BlockPos(roundedX, 0, roundedZ);
    }

    private static void forgeLoadChunk(ServerWorld world, BlockPos pos, boolean state, boolean shouldLoadSurroundingAsWell) {
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        ForgeChunkManager.forceChunk(world, CreateChunkloading.modid, pos, chunkX, chunkZ, state, true);
        if(shouldLoadSurroundingAsWell) {
            CreateChunkloading.logger.debug((state ? "ADD" : "REMOVE") + " " + pos.toString());
            forgeLoadChunk(world, pos.add(1, 0, 0), state, false);
            forgeLoadChunk(world, pos.add(1, 0, 1), state, false);
            forgeLoadChunk(world, pos.add(1, 0, -1), state, false);
            forgeLoadChunk(world, pos.add(-1, 0, 0), state, false);
            forgeLoadChunk(world, pos.add(-1, 0, 1), state, false);
            forgeLoadChunk(world, pos.add(-1, 0, -1), state, false);
            forgeLoadChunk(world, pos.add(0, 0, -1), state, false);
            forgeLoadChunk(world, pos.add(0, 0, 1), state, false);
        }
    }

    public static void forgeLoadChunk(ServerWorld world, BlockPos pos, boolean state) {
        forgeLoadChunk(world, pos, state, true);
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
