package com.grimmauld.createintegration.misc;

import com.grimmauld.createintegration.Config;
import com.grimmauld.createintegration.CreateIntegration;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
//import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ChunkLoaderList implements IChunkLoaderList {
    @Nullable
    private final ServerWorld world;


    public HashMap<iVec2d, Integer> loadedchunks;
    public ArrayList<BlockPos> chunkloaderblocks;

    private boolean enabled = false;

    public ChunkLoaderList(@Nullable ServerWorld world) {
        this.world = world;
        loadedchunks = new HashMap<>();
        chunkloaderblocks=new ArrayList<>();//could be replaced with hashset
    }

    /*public static long toChunk(BlockPos pos) {
        return ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
    }*/
    /** loads chunk containing the blockpos */
    private void force(BlockPos pos) {
        forceload(pos, "add");
    }
    /** unloads chunk containing the blockpos */
    private void unforce(BlockPos pos) {
        forceload(pos, "remove");
    }
    /** loads/unloads chunk containing the blockpos */
    private void forceload(BlockPos pos, String action) {
        if (this.world == null) return;

        CommandSource source = (this.world.getServer().getCommandSource().withWorld(this.world));
        if (!Config.CHUNK_CHAT.get()) {
            source = source.withFeedbackDisabled();
        }

        @SuppressWarnings("unused")
        int ret = this.world.getServer().getCommandManager().handleCommand(source, "forceload " + action + " " + pos.getX() + " " + pos.getZ());
    }
    /** loads/unloads chunk containing the blockpos */
    private void setforceload(BlockPos pos, Boolean x) {forceload(pos,x?"add":"remove");}
    private void setforceload(iVec2d pos, Boolean x) {forceload(new BlockPos(pos.x,0,pos.y),x?"add":"remove");}


    /**addblock adds chunk containing the blockpos to loaded chunks and to chunkloaderblocks
     * use to add a normal chunkloader (not in minecart)*/
    @Override
    public void addblock(BlockPos pos) {
        if(pos==null){CreateIntegration.logger.debug("pos is null");return;}
        chunkloaderblocks.add(pos);
        iVec2d chunk=new iVec2d(pos);
        chunk.x>>=4;chunk.y>>=4;
        addchunk(chunk);
    }

    /**removeblock removes chunk containing the blockpos from loaded chunks and from chunkloaderblocks
     * use to remove a normal chunkloader (not in minecart)*/
    @Override
    public void removeblock(BlockPos pos) {
        if(pos==null){CreateIntegration.logger.debug("pos is null");return;}
        chunkloaderblocks.remove(pos);
        iVec2d chunk=new iVec2d(pos);
        chunk.x>>=4;chunk.y>>=4;
        removechunk(chunk);
    }


    /**add adds chunk containing the blockpos to loaded chunks */
    @Override
    public void add(BlockPos pos) {
        if(pos==null){CreateIntegration.logger.debug("pos is null");return;}
        iVec2d chunk=new iVec2d(pos);
        chunk.x>>=4;chunk.y>>=4;
        addchunk(chunk);
    }

    /**remove removes chunk containing the blockpos from loaded chunks */
    @Override
    public void remove(BlockPos pos) {
        if(pos==null){CreateIntegration.logger.debug("pos is null");return;}
        iVec2d chunk=new iVec2d(pos);
        chunk.x>>=4;chunk.y>>=4;
        removechunk(chunk);
    }

    /**addchunk adds chunk to loadedchunks and loads chunk if it isn't loaded*/
    @Override
    public void addchunk(iVec2d chunk){
        if(chunk==null){CreateIntegration.logger.debug("chunk is null");return;}
        if(!loadedchunks.containsKey(chunk)){//if chunk is not in loaded chunks
            loadedchunks.put(chunk,1);
            setforceload(chunk.times(16),true);
        }else{
            loadedchunks.put(chunk, loadedchunks.get(chunk)+1);}
        CreateIntegration.logger.debug(loadedchunks);
    }

    /**removechunk removes chunk containing the blockpos from loaded chunks*/
    @Override
    public void removechunk(iVec2d chunk){
        Integer i= loadedchunks.get(chunk);
        if(i==null){CreateIntegration.logger.debug("no chunk to remove");return;}
        if(chunk==null){CreateIntegration.logger.debug("chunk is null");return;}
        if(i==1){
            loadedchunks.remove(chunk);
            setforceload(chunk.times(16),false);
        }else loadedchunks.put(chunk,i-1);
        CreateIntegration.logger.debug(loadedchunks);
    }

    public void addSilent(BlockPos pos) {
    }

    @Override
    public void start() {
        //Todo(Does nothing)
        enabled = true;
    }

    /** loads all chunks wich are suposed to be loaded */
    public void reload(){
        for(iVec2d k: loadedchunks.keySet()) {
            Integer i= loadedchunks.get(k);
            if(i==null||i==0){
                loadedchunks.remove(k);
                //setforceload(k,false);
            }else setforceload(k,true);
        }
    }

    /** removes chunks with minecart with chunkloaders from loaded chunks */
    public HashSet<iVec2d> unloadminecartchukloader(){
        HashSet<iVec2d> s=new HashSet<>();

        for(MovementContext k:ChunkLoaderMovementBehaviour.chunk.keySet()){
            s.add(ChunkLoaderMovementBehaviour.chunk.get(k));
            ChunkLoaderMovementBehaviour.chunk.remove(k);
        }
        return s;
    }



    public ArrayList<Long> getChunkNumbers() {
        ArrayList<Long> chunkNumbers = new ArrayList<>();
        if (!loadedchunks.isEmpty()) {
            //Todo(Does nothing)
        }
        return chunkNumbers;
    }

    public void readFromNBT(CompoundNBT nbt){
        long[] keys=nbt.getLongArray("loadedchunksKeys");
        int[] values=nbt.getIntArray("loadedchunksValues");
        CreateIntegration.logger.debug("readnbt");
        CreateIntegration.logger.debug(keys);
        for(int i=0;i< keys.length;i++) {
            loadedchunks.put(new iVec2d(keys[i]),values[i]);
            CreateIntegration.logger.debug((new iVec2d(keys[i])).toString()+" = "+values[i]);
        }
    }
    public CompoundNBT writeToNBT(){
        CompoundNBT nbt=new CompoundNBT();
        long[] keys=new long[loadedchunks.size()];
        int[] values=new int[loadedchunks.size()];
        int i=0;
        CreateIntegration.logger.debug("writenbt");
        CreateIntegration.logger.debug(loadedchunks.keySet());
        for(iVec2d v:loadedchunks.keySet()){
            CreateIntegration.logger.debug(v);
            keys[i]=v.toLong();
            values[i]=loadedchunks.get(v);
            i++;
        }


        nbt.putLongArray("loadedchunksKeys",keys);
        nbt.putIntArray("loadedchunksValues",values);
        return nbt;
    }

    public static class Storage implements IStorage<IChunkLoaderList> {
        @Override
        public INBT writeNBT(Capability<IChunkLoaderList> capability, IChunkLoaderList instance, Direction side) {
            if (!(instance instanceof ChunkLoaderList)) return null;
            return ((ChunkLoaderList) instance).writeToNBT();
        }

        @Override
        public void readNBT(Capability<IChunkLoaderList> capability, IChunkLoaderList instance, Direction side, INBT nbt) {
            if (!(instance instanceof ChunkLoaderList) || !(nbt instanceof CompoundNBT)) return;
            ChunkLoaderList list = (ChunkLoaderList) instance;
            try {
                list.readFromNBT((CompoundNBT) nbt);
                CreateIntegration.logger.debug("Loaded Chunk Loader positions. sucsessfull");
            } finally {
                CreateIntegration.logger.debug("Loaded Chunk Loader positions.");
            }
        }

        //TODO(not implemented)
        /*@Override
        public INBT writeNBT(Capability<IChunkLoaderList> capability, IChunkLoaderList instance, Direction side) {
            if (!(instance instanceof ChunkLoaderList)) return null;
            return new LongArrayNBT(((ChunkLoaderList) instance).getChunkNumbers());
        }

        @Override
        public void readNBT(Capability<IChunkLoaderList> capability, IChunkLoaderList instance, Direction side, INBT nbt) {
            if (!(instance instanceof ChunkLoaderList) || !(nbt instanceof LongArrayNBT)) return;
            ChunkLoaderList list = (ChunkLoaderList) instance;
            try {
                for (long l : ((LongArrayNBT) nbt).getAsLongArray()) {
                    list.addSilent(BlockPos.fromLong(l));
                }
            } finally {
                CreateIntegration.logger.debug("Loaded Chunk Loader positions.");
            }
        }*/
    }
}
/** 2D int Vektor Class */ //Todo(put in seperate file)
class iVec2d{
    public int x=0;
    public int y=0;
    iVec2d(int x,int y){
        this.x=x;
        this.y=y;
    }
    iVec2d(BlockPos p){ this(p.getX(),p.getZ());}

    iVec2d(long l){//Todo(Test if this really works)
        this((int)(l>>32),(int)l);
    }

    public int hashCode() { return (this.y + this.x * 31); }
    public boolean equals(Object o){
       if(!(o instanceof iVec2d))return false;
       else {
           iVec2d other=(iVec2d)o;
           return other.x==x && other.y==y;
       }
    }

    public int compareTo(iVec2d o) {
        if(x!=o.x)return x-o.x;
        else if(y!=o.y)return y-o.y;
        return 0;
    }
    public long toLong(){//Todo(Test if this really works)
        return ((long)x)<<32 | y;
    }

    @Override
    public String toString() {
        return "("+x+"|"+y+")";
    }

    public iVec2d div(int n){return new iVec2d(x/n,y/n);}
    public iVec2d times(int n){return new iVec2d(x*n,y*n);}
    public iVec2d plus(iVec2d n){return new iVec2d(x+n.x,y+n.y);}
    public iVec2d sub(iVec2d n){return new iVec2d(x-n.x,y-n.y);}
}