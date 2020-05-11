package com.grimmauld.createintegration.blocks;

import static com.grimmauld.createintegration.blocks.ModBlocks.DYNAMO_TILE;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.grimmauld.createintegration.Config;
import com.grimmauld.createintegration.tools.CustomEnergyStorage;
import com.simibubi.create.modules.contraptions.base.KineticTileEntity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;





public class DynamoTile extends KineticTileEntity implements ITickableTileEntity{
	
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);
    	
	public DynamoTile() {
		super(DYNAMO_TILE);
	}
		


	@Override
	public void tick() {
		energy.ifPresent(e -> ((CustomEnergyStorage) e).addEnergy((int) Math.abs(Config.DYNAMO_GENERATE_MULTIPLIER.get()*getSpeed())));
		markDirty();
		sendOutPower();
		// super.tick();
	}
	
	
    private void sendOutPower() {
        energy.ifPresent(energy -> {
            AtomicInteger capacity = new AtomicInteger(energy.getEnergyStored());
            if (capacity.get() > 0) {
            	Direction direction = getBlockState().get(BlockStateProperties.FACING).getOpposite();
                TileEntity te = world.getTileEntity(pos.offset(direction));
                if (te != null) {
                    boolean doContinue = te.getCapability(CapabilityEnergy.ENERGY, direction).map(handler -> {
                                if (handler.canReceive()) {
                                    int received = handler.receiveEnergy(Math.min(capacity.get(), Config.DYNAMO_SEND.get()), false);
                                    capacity.addAndGet(-received);
                                    ((CustomEnergyStorage) energy).consumeEnergy(received);
                                    markDirty();
                                    return capacity.get() > 0;
                                } else {
                                    return true;
                                }
                            }
                    ).orElse(true);
                    if (!doContinue) {
                        return;
                    }
                }
            }
        });
    }
	
	
    @Override
    public void read(CompoundNBT tag) {
        CompoundNBT energyTag = tag.getCompound("energy");
        energy.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(energyTag));

        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        energy.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("energy", compound);
        });
        return super.write(tag);
    }
	
	
	private IEnergyStorage createEnergy() {
		return new CustomEnergyStorage(Config.DYNAMO_MAXPOWER.get(), 0);
	}
	
	
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        }
        return super.getCapability(cap, side);
    	
    }
    
    @Override
	public float calculateStressApplied() {
		return Config.DYNAMO_SU.get();
	}
}
