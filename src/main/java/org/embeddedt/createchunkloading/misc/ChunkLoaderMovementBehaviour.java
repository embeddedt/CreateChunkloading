package org.embeddedt.createchunkloading.misc;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
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

        CreateChunkloading.logger.debug("visit new position " + pos.toString());

        Object oldPos = context.temporaryData;

        if(!(oldPos instanceof BlockPos)) {
            if(context.data.contains("previous_chunk"))
                oldPos = BlockPos.fromLong(context.data.getLong("previous_chunk"));
        }

        if(pos.equals(oldPos))
            return;

        ChunkLoader.forgeLoadChunk((ServerWorld) context.world, pos, true, context.contraption.entity.getUniqueID());
        if(oldPos instanceof BlockPos) {
            ChunkLoader.forgeLoadChunk((ServerWorld) context.world, (BlockPos) oldPos, false, context.contraption.entity.getUniqueID());
        }

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
        context.temporaryData = getBlockPos(context.position);
        CreateChunkloading.logger.debug("start moving " + context.temporaryData.toString());
        ChunkLoader.forgeLoadChunk((ServerWorld) context.world, (BlockPos)context.temporaryData,true, context.contraption.entity.getUniqueID());
    }

    @Override
    public void stopMoving(MovementContext context){
        if(context.position == null)
            return; /* not much we can do */
        CreateChunkloading.logger.debug("stop moving " + getBlockPos(context.position).toString());
        ChunkLoader.forgeLoadChunk((ServerWorld) context.world, getBlockPos(context.position), false, context.contraption.entity.getUniqueID());
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
