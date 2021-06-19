package org.embeddedt.createchunkloading.misc;

import org.embeddedt.createchunkloading.CreateChunkloading;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import net.minecraft.util.math.BlockPos;

//import java.util.HashMap;

public class ChunkLoaderMovementBehaviour extends MovementBehaviour {

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        if (context.world.isRemote)return;

        iVec2d chunknew=new iVec2d(pos);
        chunknew.x>>=4;chunknew.y>>=4;//mact position zu einem chunk

        if (!(context.temporaryData instanceof iVec2d)){
            if(context.data.contains("chunknew")) {
                context.temporaryData = new iVec2d(context.data.getLong("chunknew"));
            }else{
                context.temporaryData=chunknew;
                //CreateIntegration.logger.debug("wtf just happend");//visitNewPosition wurde vor startMoving aufgerufen
                writeExtraData(context);
                forceload(context,chunknew,true);
            }
        }
        iVec2d chunkalt= (iVec2d) context.temporaryData;

        if(!chunknew.equals(chunkalt)){
            forceload(context,chunkalt,false);
            forceload(context,chunknew,true);
            context.temporaryData=chunknew;
        }
    }

    //private void updatepos(){ }



    @Override
    public void startMoving(MovementContext context){

        //CreateIntegration.logger.debug("start");
        //CreateIntegration.logger.debug(context.position); probably null
        /*if(context.position!=null){
            iVec2d chunkstart= new iVec2d((int)(context.position.x),(int)(context.position.z));
            chunkstart.x>>=4;chunkstart.y>>=4;
            context.temporaryData=chunkstart;
            forceload(context,chunkstart,true);
        }*/

        //CreateIntegration.logger.debug("start");
        //chunk.put(context,null);
    }

    @Override
    public void stopMoving(MovementContext context){
        iVec2d chunkalt= (iVec2d) context.temporaryData;
        forceload(context,chunkalt,false);
        //CreateIntegration.logger.debug("stop");

    }

    @Override
    public void writeExtraData(MovementContext context) {
        super.writeExtraData(context);
        if(context.temporaryData instanceof iVec2d) {
            iVec2d chunkalt = (iVec2d) context.temporaryData;
            context.data.putLong("chunknew", chunkalt.toLong());
            CreateChunkloading.logger.debug("minecatr saved");
        }else{
            CreateChunkloading.logger.debug("i don't want to write null");
        }
    }

    //loads/unloads the specified chunk
    private void _forceload(MovementContext context,iVec2d chunk,boolean state){
        if(state){
            context.world.getCapability(CreateChunkloading.CHUNK_LOADING_CAPABILITY, null).ifPresent(cap -> cap.addchunk(chunk));
        }else{
            context.world.getCapability(CreateChunkloading.CHUNK_LOADING_CAPABILITY, null).ifPresent(cap -> cap.removechunk(chunk));
        }
    }
    private void forceload(MovementContext context,iVec2d chunk,boolean state) {
        _forceload(context, chunk, state);
        /* load the surrounding 8 chunks as well so that minecart contraptions get ticked */
        _forceload(context, chunk.plus(new iVec2d(1, 0)), state);
        _forceload(context, chunk.plus(new iVec2d(1, 1)), state);
        _forceload(context, chunk.plus(new iVec2d(1, -1)), state);
        _forceload(context, chunk.plus(new iVec2d(-1, 0)), state);
        _forceload(context, chunk.plus(new iVec2d(-1, 1)), state);
        _forceload(context, chunk.plus(new iVec2d(-1, -1)), state);
        _forceload(context, chunk.plus(new iVec2d(0, -1)), state);
        _forceload(context, chunk.plus(new iVec2d(0, 1)), state);
    }
}
