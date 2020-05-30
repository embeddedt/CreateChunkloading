package com.grimmauld.createintegration.blocks;

import com.grimmauld.createintegration.CreateIntegration;
import com.simibubi.create.foundation.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.behaviour.base.SmartTileEntity;
import com.simibubi.create.foundation.behaviour.base.TileEntityBehaviour;
import com.simibubi.create.foundation.behaviour.scrollvalue.ScrollValueBehaviour;
import com.simibubi.create.modules.contraptions.components.motor.MotorTileEntity;

import net.minecraft.block.ChestBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;


public class EnderChestTile extends SmartTileEntity {
	ScrollValueBehaviour id;
	private LazyOptional<IItemHandler> handler;

	public EnderChestTile() {
		super(ModBlocks.ENDER_CHEST_TILE);
		updateItemHandler();
	}

	public void updateItemHandler(){
		System.out.println("Update handling!");
		if(world==null) return;
		world
				.getCapability(CreateIntegration.ENDER_CHEST_CAPABILITY,
						null)
				.ifPresent(worldCap ->
						setItemHandler(worldCap.getOrCreate(id.getValue())));
	}

	private void setItemHandler(LazyOptional<IItemHandler> itemHandler){
		this.handler = itemHandler;
	}

	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {


		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if(handler == null) updateItemHandler();
			if(handler != null) return handler.cast();
		}
		return super.getCapability(cap, side);
	}


	public void read(CompoundNBT tag) {
		super.read(tag);
		int v = tag.getInt("ender_id");
		id.value = v;
		id.scrollableValue = v;
		id.setValue(v);
		updateItemHandler();
	}


	@Override
	public CompoundNBT write(CompoundNBT tag) {
		tag.putInt("ender_id", id.getValue());
		return super.write(tag);
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
		CenteredSideValueBoxTransform slot =
				new CenteredSideValueBoxTransform((ender_chest, side) -> ender_chest.get(ChestBlock.FACING) == side);

		id = new ScrollValueBehaviour("Ender ID", this, slot);
		id.between(0, 256);
		id.value = 0;
		id.scrollableValue = 0;
		id.withStepFunction(MotorTileEntity::step);
		id.withCallback(this::updateItemHandler);
		behaviours.add(id);
		System.out.println("PUT SCROLL HANDLER");
	}

	private void updateItemHandler(Integer integer) {
		updateItemHandler();
	}

	
	
	
	/* public static int step(ScrollValueBehaviour.StepContext context) {
		if (context.shift)
			return 1;

		int current = context.currentValue;
		int magnitude = Math.abs(current) - (context.forward == current > 0 ? 0 : 1);
		int step = 1;

		if (magnitude >= 4)
			step *= 4;
		if (magnitude >= 32)
			step *= 4;
		if (magnitude >= 128)
			step *= 4;
		return step;
	} */

}
