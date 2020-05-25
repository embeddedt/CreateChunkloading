package com.grimmauld.createintegration.misc;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

import com.grimmauld.createintegration.Config;

import net.minecraft.command.CommandSource;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class ChunkLoaderList implements IChunkLoaderList {
	public HashMap<BlockPos, Integer> chunkLoaders;
	@Nullable private final ServerWorld world;

    public ChunkLoaderList(@Nullable ServerWorld world) {
        this.world = world;
        chunkLoaders = new HashMap<BlockPos, Integer>();
    }

	@Override
	public void resetForBlock(BlockPos pos) {
		if(contains(pos)){
			chunkLoaders.put(pos, 5);
		}else {
			add(pos);
		}		
	}

	@Override
	public void tickDown() {
		if(!chunkLoaders.isEmpty() && chunkLoaders.keySet() != null) {
			for(BlockPos pos: chunkLoaders.keySet()) {
				if(chunkLoaders.get(pos) > -1) {  // prevent overflows
					chunkLoaders.put(pos, chunkLoaders.get(pos)-1);
				}
			}
		}
		update();
	}
	
	private void force(BlockPos pos) { forceload(pos, "add"); }
    private void unforce(BlockPos pos) { forceload(pos, "remove"); }

    private void forceload(BlockPos pos, String action) {
        if (this.world == null || this.world.getServer() == null) return;
        
        CommandSource source = (this.world.getServer().getCommandSource().withWorld(this.world));
        if(!Config.CHUNK_CHAT.get()) {
        	source = source.withFeedbackDisabled();
        }
        
        @SuppressWarnings("unused")
        int ret = this.world.getServer().getCommandManager().handleCommand(source, "forceload " + action + " " + pos.getX() + " " + pos.getZ());
    }

	@Override
	public void add(BlockPos pos) {
		chunkLoaders.put(pos, 5);
		force(pos);
		update();
	}

	@Override
	public void remove(BlockPos pos) {
		chunkLoaders.put(pos, 0);
		update();
	}
	
	private void update() {
		try {
			if(world != null && chunkLoaders != null) {
				if(!chunkLoaders.isEmpty() && chunkLoaders.keySet() != null) {
					for(BlockPos pos: chunkLoaders.keySet()) {
						if(chunkLoaders.get(pos) <= 0) {  // TODO: only check 0 ?
							chunkLoaders.remove(pos);
							if(!getChunkNumbers().contains(toChunk(pos))) {
								unforce(pos);
							}
						}
					}
				}
			}
		} catch(Exception e){
			// CreateIntegration.logger.catching(e);
		}
	}
	
	public static final long toChunk(BlockPos pos) {
        return ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
    }
	
	
	public ArrayList<Long> getChunkNumbers(){
		ArrayList<Long> chunkNumbers = new ArrayList<Long>();
		if(!chunkLoaders.isEmpty() && chunkLoaders.keySet() != null) {
			for(BlockPos pos: chunkLoaders.keySet()) {
				if(chunkLoaders.get(pos) > 0) {  // only active chunk loaders are saved
					chunkNumbers.add(toChunk(pos));
				}
			}
		}
		return chunkNumbers;
	}
	
	
	public static class Storage implements IStorage<IChunkLoaderList> {
        @Override
        public INBT writeNBT(Capability<IChunkLoaderList> capability, IChunkLoaderList instance, Direction side) {
        	if (!(instance instanceof ChunkLoaderList)) return null;
            return new LongArrayNBT(((ChunkLoaderList)instance).getChunkNumbers());
        }

        @Override
        public void readNBT(Capability<IChunkLoaderList> capability, IChunkLoaderList instance, Direction side, INBT nbt) {
            if (!(instance instanceof ChunkLoaderList) || !(nbt instanceof LongArrayNBT)) return;
            ChunkLoaderList list = (ChunkLoaderList)instance;
	            try {
	                for (long l : ((LongArrayNBT)nbt).getAsLongArray()) {
	                    list.add(BlockPos.fromLong(l));
	                }
	            } finally {
	            	list.update();
	            }
            }
        }


	@Override
	public boolean contains(BlockPos pos) {
		return chunkLoaders.containsKey(pos) && chunkLoaders.get(pos) > 0;
	}
}