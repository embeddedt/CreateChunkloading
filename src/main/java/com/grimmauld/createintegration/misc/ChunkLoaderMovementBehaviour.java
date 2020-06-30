package com.grimmauld.createintegration.misc;

import com.grimmauld.createintegration.CreateIntegration;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.HashSet;

public class ChunkLoaderMovementBehaviour extends MovementBehaviour {
    //private iVec2d chunk=null;
    private HashMap<MovementContext,iVec2d> chunk=new HashMap<>();

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        if (context.world.isRemote)
            return;

        iVec2d newchunk=new iVec2d(pos).div(16);
        iVec2d chunkalt=chunk.get(context);
        CreateIntegration.logger.debug("moved alt"+chunkalt+" new" +newchunk+" "+newchunk.equals(chunkalt)+" "+context);

        if(!newchunk.equals(chunkalt)){

            if(chunkalt!=null){context.world.getCapability(CreateIntegration.CHUNK_LOADING_CAPABILITY, null).ifPresent(cap -> cap.remove(chunkalt));CreateIntegration.logger.debug("addednew");}
            context.world.getCapability(CreateIntegration.CHUNK_LOADING_CAPABILITY, null).ifPresent(cap -> cap.add(newchunk));

            chunk.put(context,newchunk);
        }
    }

    @Override
    public void tick(MovementContext context) {
        /*resetTicking++;
        if (pos != null && resetTicking % 20 == 0) {
            context.world.getCapability(CreateIntegration.CHUNK_LOADING_CAPABILITY, null).ifPresent(cap -> cap.resetForBlock(pos));
        }*/
    }

    @Override
    public void startMoving(MovementContext context){
       // this.chunk= new iVec2d((int)(context.position.x),(int)(context.position.z)).div(16);
        CreateIntegration.logger.debug("start");
        chunk.put(context,null);
    }

    @Override
    public void stopMoving(MovementContext context){
        context.world.getCapability(CreateIntegration.CHUNK_LOADING_CAPABILITY, null).ifPresent(cap -> cap.remove(chunk.get(context)));
        CreateIntegration.logger.debug("stop");
        chunk.remove(context);
    }
}
