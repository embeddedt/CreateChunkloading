package org.embeddedt.createchunkloading.misc;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;
import org.embeddedt.createchunkloading.CreateChunkloading;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import net.minecraft.util.math.BlockPos;
import org.embeddedt.createchunkloading.blocks.ChunkLoader;

//import java.util.HashMap;

public class ChunkLoaderMovementBehaviour extends MovementBehaviour {

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        if (context.world.isRemote)return;

        pos = new BlockPos(context.contraption.entity.chunkCoordX, 0, context.contraption.entity.chunkCoordZ);

        CreateChunkloading.logger.debug("visit new position " + pos.toString());

        Object oldPos = context.temporaryData;

        if(!(oldPos instanceof BlockPos)) {
            if (context.data.contains("previous_chunk"))
                oldPos = BlockPos.fromLong(context.data.getLong("previous_chunk"));
        }

        if(pos.equals(oldPos))
            return;

        /* It's critical that we unload the old position first, as some of the chunks will overlap */
        if(oldPos instanceof BlockPos) {
            BlockPos oldChunkPos = (BlockPos)oldPos;
            ChunkLoader.forgeLoadChunk(
                    (ServerWorld) context.world,
                    oldChunkPos.getX(),
                    oldChunkPos.getZ(),
                    false,
                    context.contraption.entity.getUniqueID(),
                    true);
        }
        /* Now load the new position */
        ChunkLoader.forgeLoadChunk(
                (ServerWorld) context.world,
                context.contraption.entity.chunkCoordX,
                context.contraption.entity.chunkCoordZ,
                true,
                context.contraption.entity.getUniqueID(),
                true);


        context.temporaryData = pos;
    }

    //private void updatepos(){ }

    public static BlockPos getBlockPos(Vector3d vec) {
        return new BlockPos((int)vec.x, (int)vec.y, (int)vec.z);
    }

    @Override
    public void startMoving(MovementContext context){
        if(context.position == null)
            return; /* not much we can do */
        context.temporaryData = context.contraption.entity.getBlockPos();
        CreateChunkloading.logger.debug("start moving " + context.temporaryData.toString());
        ChunkLoader.forgeLoadChunk(
                (ServerWorld) context.world,
                context.contraption.entity.chunkCoordX,
                context.contraption.entity.chunkCoordZ,
                true,
                context.contraption.entity.getUniqueID(),
                true);
    }

    @Override
    public void stopMoving(MovementContext context){
        if(context.contraption.entity == null) {
            CreateChunkloading.logger.error("Contraption entity no longer exists - a chunk is probably going to remain loaded forever!");
            return; /* not much we can do */
        }
        BlockPos pos = context.contraption.entity.getBlockPos();
        CreateChunkloading.logger.debug("stop moving " + pos.toString());
        ChunkLoader.forgeLoadChunk(
                (ServerWorld) context.world,
                context.contraption.entity.chunkCoordX,
                context.contraption.entity.chunkCoordZ,
                false,
                context.contraption.entity.getUniqueID(),
                true);
        context.temporaryData = null;
    }

    @Override
    public void writeExtraData(MovementContext context) {
        super.writeExtraData(context);
        if(context.temporaryData instanceof BlockPos) {
            context.data.putLong("previous_chunk", ((BlockPos)context.temporaryData).toLong());
            CreateChunkloading.logger.debug("previous chunk saved");
        }else{
            CreateChunkloading.logger.debug("i don't want to write null");
        }
    }

}
