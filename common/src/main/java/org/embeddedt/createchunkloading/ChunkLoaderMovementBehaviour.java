package org.embeddedt.createchunkloading;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import net.minecraft.core.BlockPos;

import static org.embeddedt.createchunkloading.ExampleExpectPlatform.forceLoadChunk;

//import java.util.HashMap;

public class ChunkLoaderMovementBehaviour extends MovementBehaviour {

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        if (context.world.isClientSide)return;

        ChunkPos entityChunkPosition = context.contraption.entity.chunkPosition();
        pos = new BlockPos(entityChunkPosition.x, 0, entityChunkPosition.z);

        Object oldPos = context.temporaryData;

        if(!(oldPos instanceof BlockPos)) {
            if (context.data.contains("previous_chunk"))
                oldPos = BlockPos.of(context.data.getLong("previous_chunk"));
        }

        if(pos.equals(oldPos))
            return;

        /* It's critical that we unload the old position first, as some of the chunks will overlap */
        if(oldPos instanceof BlockPos) {
            BlockPos oldChunkPos = (BlockPos)oldPos;
            forceLoadChunk(
                    (ServerLevel) context.world,
                    oldChunkPos.getX(),
                    oldChunkPos.getZ(),
                    false,
                    context.contraption.entity.getUUID(),
                    true);
        }
        /* Now load the new position */
        forceLoadChunk(
                (ServerLevel) context.world,
                entityChunkPosition.x,
                entityChunkPosition.z,
                true,
                context.contraption.entity.getUUID(),
                true);


        context.temporaryData = pos;
    }

    //private void updatepos(){ }

    public static BlockPos getBlockPos(Vec3 vec) {
        return new BlockPos((int)vec.x, (int)vec.y, (int)vec.z);
    }

    @Override
    public void startMoving(MovementContext context){
        if(context.position == null)
            return; /* not much we can do */
        context.temporaryData = context.contraption.entity.blockPosition();

        ChunkPos entityChunkPosition = context.contraption.entity.chunkPosition();
        forceLoadChunk(
                (ServerLevel) context.world,
                entityChunkPosition.x,
                entityChunkPosition.z,
                true,
                context.contraption.entity.getUUID(),
                true);
    }

    @Override
    public void stopMoving(MovementContext context){
        if(context.contraption.entity == null) {
            return; /* not much we can do */
        }
        BlockPos pos = context.contraption.entity.blockPosition();
        ChunkPos entityChunkPosition = context.contraption.entity.chunkPosition();
        forceLoadChunk(
                (ServerLevel) context.world,
                entityChunkPosition.x,
                entityChunkPosition.z,
                false,
                context.contraption.entity.getUUID(),
                true);
        context.temporaryData = null;
    }

    @Override
    public void writeExtraData(MovementContext context) {
        super.writeExtraData(context);
        if(context.temporaryData instanceof BlockPos) {
            context.data.putLong("previous_chunk", ((BlockPos)context.temporaryData).asLong());
        }
    }

}
