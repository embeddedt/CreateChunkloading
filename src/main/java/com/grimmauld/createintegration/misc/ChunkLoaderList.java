package com.grimmauld.createintegration.misc;

import com.grimmauld.createintegration.Config;
import com.grimmauld.createintegration.CreateIntegration;
import com.grimmauld.createintegration.blocks.ChunkLoader;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/*
public class ChunkLoaderList implements IChunkLoaderList {
    @Nullable
    private final ServerWorld world;
    public HashMap<BlockPos, Integer> chunkLoaders;
    private boolean enabled = false;

    public ChunkLoaderList(@Nullable ServerWorld world) {
        this.world = world;
        chunkLoaders = new HashMap<>();
    }

    public static long toChunk(BlockPos pos) {
        return ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
    }

    @Override
    public void resetForBlock(BlockPos pos) {
        if (contains(pos)) {
            chunkLoaders.put(pos, 5);
        } else {
            add(pos);
        }
    }

    @Override
    public void tickDown() {
        if (!chunkLoaders.isEmpty() && enabled) {
            for (BlockPos pos : chunkLoaders.keySet()) {
                if (chunkLoaders.get(pos) > -1) {  // prevent overflows
                    chunkLoaders.put(pos, chunkLoaders.get(pos) - 1);
                }
            }
        }
        update();
    }

    private void force(BlockPos pos) {
        forceload(pos, "add");
    }

    private void unforce(BlockPos pos) {
        forceload(pos, "remove");
    }

    private void forceload(BlockPos pos, String action) {
        if (this.world == null) return;

        CommandSource source = (this.world.getServer().getCommandSource().withWorld(this.world));
        if (!Config.CHUNK_CHAT.get()) {
            source = source.withFeedbackDisabled();
        }

        @SuppressWarnings("unused")
        int ret = this.world.getServer().getCommandManager().handleCommand(source, "forceload " + action + " " + pos.getX() + " " + pos.getZ());
    }

    private void setforceload(BlockPos pos, Boolean x) {forceload(pos,x?"add":"remove");}

    @Override
    public void add(BlockPos pos) {
        CreateIntegration.logger.debug(chunkLoaders);
        chunkLoaders.put(pos, 5);
        force(pos);
        update();
    }

    @Override
    public void remove(BlockPos pos) {
        CreateIntegration.logger.debug("removed");
        chunkLoaders.put(pos, 0);
        update();
    }

    public void addSilent(BlockPos pos) {
        chunkLoaders.put(pos, 5);
    }

    @Override
    public void start() {
        for (BlockPos pos : chunkLoaders.keySet()) {
            chunkLoaders.put(pos, 5);
            force(pos);
        }
        enabled = true;
    }

    private void update() {
        try {
            if (world != null && chunkLoaders != null) {
                if (!chunkLoaders.isEmpty()) {
                    for (BlockPos pos : chunkLoaders.keySet()) {
                        if (chunkLoaders.get(pos) <= 0) {  // TODO: only check 0 ?
                            chunkLoaders.remove(pos);
                            if (!getChunkNumbers().contains(toChunk(pos))) {
                                unforce(pos);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // CreateIntegration.logger.catching(e);
        }
    }

    public ArrayList<Long> getChunkNumbers() {
        ArrayList<Long> chunkNumbers = new ArrayList<>();
        if (!chunkLoaders.isEmpty()) {
            for (BlockPos pos : chunkLoaders.keySet()) {
                if (chunkLoaders.get(pos) > 0) {  // only active chunk loaders are saved
                    chunkNumbers.add(toChunk(pos));
                }
            }
        }
        return chunkNumbers;
    }

    @Override
    public boolean contains(BlockPos pos) {
        return chunkLoaders.containsKey(pos) && chunkLoaders.get(pos) > 0;
    }

    public static class Storage implements IStorage<IChunkLoaderList> {
        @Override
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
        }
    }
}*/
public class ChunkLoaderList implements IChunkLoaderList {
    @Nullable
    private final ServerWorld world;
    public HashMap<BlockPos, Integer> chunkLoaders;


    public HashMap<iVec2d, Integer> chunkloaderentety;

    private boolean enabled = false;

    public ChunkLoaderList(@Nullable ServerWorld world) {
        this.world = world;
        chunkLoaders = new HashMap<>();

        chunkloaderentety= new HashMap<>();
    }

    public static long toChunk(BlockPos pos) {
        return ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
    }

    private void force(BlockPos pos) {
        forceload(pos, "add");
    }
    private void unforce(BlockPos pos) {
        forceload(pos, "remove");
    }

    private void forceload(BlockPos pos, String action) {
        if (this.world == null) return;

        CommandSource source = (this.world.getServer().getCommandSource().withWorld(this.world));
        if (!Config.CHUNK_CHAT.get()) {
            source = source.withFeedbackDisabled();
        }

        @SuppressWarnings("unused")
        int ret = this.world.getServer().getCommandManager().handleCommand(source, "forceload " + action + " " + pos.getX() + " " + pos.getZ());
    }

    private void setforceload(BlockPos pos, Boolean x) {forceload(pos,x?"add":"remove");}
    private void setforceload(iVec2d pos, Boolean x) {forceload(new BlockPos(pos.x,0,pos.y),x?"add":"remove");}

    public void add(BlockPos pos) {
        if(pos==null){CreateIntegration.logger.debug("pos is null");return;}
        iVec2d chunk=new iVec2d(pos).div(16);
        add(chunk);
    }
    public void remove(BlockPos pos) {
        if(pos==null){CreateIntegration.logger.debug("pos is null");return;}
        iVec2d chunk=new iVec2d(pos).div(16);
        remove(chunk);
    }
    @Override
    public void add(iVec2d chunk){
        if(chunk==null){CreateIntegration.logger.debug("chunk is null");return;}
        if(!chunkloaderentety.keySet().contains(chunk)){
            chunkloaderentety.put(chunk,1);
            setforceload(chunk.times(16),true);
        }else{chunkloaderentety.put(chunk,chunkloaderentety.get(chunk)+1);}
        CreateIntegration.logger.debug(chunkloaderentety);
    }
    @Override
    public void remove(iVec2d chunk){
        Integer i=chunkloaderentety.get(chunk);
        if(i==null){CreateIntegration.logger.debug("no chunk to remove");return;}
        if(chunk==null){CreateIntegration.logger.debug("chunk is null");return;}
        if(i==1){
            chunkloaderentety.remove(chunk);
            setforceload(chunk.times(16),false);
        }else chunkloaderentety.put(chunk,i-1);
        CreateIntegration.logger.debug(chunkloaderentety);
    }

    public void addSilent(BlockPos pos) {
        chunkLoaders.put(pos, 5);
    }

    @Override
    public void start() {
        for (BlockPos pos : chunkLoaders.keySet()) {
            chunkLoaders.put(pos, 5);
            force(pos);
        }
        enabled = true;
    }

    private void update() {
        try {
            if (world != null && chunkLoaders != null) {
                if (!chunkLoaders.isEmpty()) {
                    for (BlockPos pos : chunkLoaders.keySet()) {
                        if (chunkLoaders.get(pos) <= 0) {  // TODO: only check 0 ?
                            chunkLoaders.remove(pos);
                            if (!getChunkNumbers().contains(toChunk(pos))) {
                                unforce(pos);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // CreateIntegration.logger.catching(e);
        }
    }

    public ArrayList<Long> getChunkNumbers() {
        ArrayList<Long> chunkNumbers = new ArrayList<>();
        if (!chunkLoaders.isEmpty()) {
            for (BlockPos pos : chunkLoaders.keySet()) {
                if (chunkLoaders.get(pos) > 0) {  // only active chunk loaders are saved
                    chunkNumbers.add(toChunk(pos));
                }
            }
        }
        return chunkNumbers;
    }

    @Override
    public boolean contains(BlockPos pos) {
        return chunkLoaders.containsKey(pos) && chunkLoaders.get(pos) > 0;
    }

    public static class Storage implements IStorage<IChunkLoaderList> {
        //TODO(not implemented)
        @Override
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
        }
    }
}

class iVec2d{
    public int x=0;
    public int y=0;
    iVec2d(int x,int y){
        this.x=x;
        this.y=y;
    }
    iVec2d(BlockPos p){ this(p.getX(),p.getZ());}

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

    @Override
    public String toString() {
        return "("+x+"|"+y+")";
    }

    public iVec2d div(int n){return new iVec2d(x/n,y/n);}
    public iVec2d times(int n){return new iVec2d(x*n,y*n);}
    public iVec2d add(iVec2d n){return new iVec2d(x+n.x,y+n.y);}
    public iVec2d sub(iVec2d n){return new iVec2d(x-n.x,y-n.y);}
}