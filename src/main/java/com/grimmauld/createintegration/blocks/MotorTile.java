package com.grimmauld.createintegration.blocks;

import static com.grimmauld.createintegration.blocks.ModBlocks.MOTOR_TILE;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.grimmauld.createintegration.Config;
import com.grimmauld.createintegration.tools.CustomEnergyStorage;
import com.simibubi.create.modules.contraptions.base.GeneratingKineticTileEntity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class MotorTile extends GeneratingKineticTileEntity {
	
	private LazyOptional<IEnergyStorage> energy = LazyOptional.of(this::createEnergy);
	private boolean active;
	

	public MotorTile() {
		super(MOTOR_TILE);
	}

	@Override
	public void tick() {
		super.tick();
		boolean activeBefore = active;
		active = false;
		energy.ifPresent(energy -> {
			AtomicInteger capacity = new AtomicInteger(energy.getEnergyStored());
				if(capacity.get() > Config.MOTOR_FE.get()) {
					active = true;
					((CustomEnergyStorage) energy).consumeEnergy(Config.MOTOR_FE.get());
					markDirty();
				} else {
					active = false;
				}
			}
		);
		
		if(active != activeBefore) {
			updateGeneratedRotation();
			markDirty();
		}
		
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
		return new CustomEnergyStorage(Config.MOTOR_CAPACITY.get(), Config.MOTOR_MAXINPUT.get());
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
	public float getGeneratedSpeed() {
		return Config.MOTOR_SPEED.get()* (active?1:0);
	}
    
    @Override
	public float calculateAddedStressCapacity() {
		return Config.MOTOR_SU.get() * (active?1:0);
	}
    
    @Override
	public float calculateStressApplied() {
		return 0;
	}

}
