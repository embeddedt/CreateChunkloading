package com.grimmauld.createintegration.blocks;

import static com.grimmauld.createintegration.blocks.BeltMachine.RUNNING;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.grimmauld.createintegration.recipes.BeltMachineRecipe;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.behaviour.base.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.modules.contraptions.base.KineticTileEntity;
import com.simibubi.create.modules.contraptions.components.saw.SawTileEntity;
import com.simibubi.create.modules.contraptions.processing.ProcessingInventory;
import com.simibubi.create.modules.contraptions.relays.belt.BeltHelper;
import com.simibubi.create.modules.contraptions.relays.belt.BeltTileEntity;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class BeltMachineTile extends KineticTileEntity{
	public ProcessingInventory inventory;
	protected int recipeIndex;
	protected LazyOptional<IItemHandler> invProvider = LazyOptional.empty();
	protected boolean destroyed;

	public BeltMachineTile(TileEntityType<?> TET) {
		super(TET);
		inventory = new ProcessingInventory(this::start);
		inventory.remainingTime = -1;
		recipeIndex = 0;
		invProvider = LazyOptional.of(() -> inventory);
	}
	
	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {		
		super.addBehaviours(behaviours);
	}
	
	protected BeltTileEntity getTargetingBelt() {
		Vec3d itemMovement = getItemMovementVec();
		BlockPos targetPos = pos.add(-itemMovement.x, -itemMovement.y, -itemMovement.z);
		if (!AllBlocks.BELT.typeOf(world.getBlockState(targetPos)))
			return null;
		return BeltHelper.getSegmentTE(world, targetPos);
	}
	
	@Override
	public boolean hasFastRenderer() {
		return false;
	}
	
	@Override
	public void onSpeedChanged(float prevSpeed) {
		super.onSpeedChanged(prevSpeed);
		boolean shouldRun = Math.abs(getSpeed()) > 1 / 64f;
		boolean running = getBlockState().get(RUNNING);
		if (shouldRun != running && !destroyed)
			world.setBlockState(pos, getBlockState().with(RUNNING, shouldRun), 2 | 16);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.put("Inventory", inventory.serializeNBT());
		compound.putInt("RecipeIndex", recipeIndex);
		return super.write(compound);
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		inventory.deserializeNBT(compound.getCompound("Inventory"));
		recipeIndex = compound.getInt("RecipeIndex");
	}
	
	@Override
	public void tick() {
		super.tick();
		if (getSpeed() == 0)
			return;
		
		if (inventory.isEmpty()) {
			
			BeltTileEntity beltTE = getTargetingBelt();
			if (beltTE == null)
				return;
			BeltTileEntity controllerTE = beltTE.getControllerTE();
			if (controllerTE == null)
				return;
			
			
			controllerTE.getInventory().forEachWithin(beltTE.index, .2f , stack -> {
				inventory.insertItem(0, stack.stack, false);
				this.markDirty();
				controllerTE.markDirty();
				return Collections.emptyList();
			});
		}		
		
		if (inventory.remainingTime == -1) {
			if (!inventory.isEmpty() && !inventory.appliedRecipe)
				start(inventory.getStackInSlot(0));
			return;
		}

		float processingSpeed = MathHelper.clamp(Math.abs(getSpeed()) / 32, 1, 128);
		inventory.remainingTime -= processingSpeed;

		if (inventory.remainingTime > 0)
			spawnParticles(inventory.getStackInSlot(0));

		if (world.isRemote)
			return;

		if (inventory.remainingTime < 20 && !inventory.appliedRecipe) {
			applyRecipe();
			inventory.appliedRecipe = true;
			sendData();
			return;
		}

		Vec3d itemMovement = getItemMovementVec();
		Direction itemMovementFacing = Direction.getFacingFromVector(itemMovement.x, itemMovement.y, itemMovement.z);
		Vec3d outPos = VecHelper.getCenterOf(pos).add(itemMovement.scale(.5f).add(0.0, .5, 0.0));
		Vec3d outMotion = itemMovement.scale(.0625).add(0.0, .125, 0.0);


		if (inventory.remainingTime <= 0) {

			// Try moving items onto the belt
			BlockPos nextPos = pos.add(itemMovement.x, itemMovement.y, itemMovement.z);
			if (AllBlocks.BELT.typeOf(world.getBlockState(nextPos))) {
				TileEntity te = world.getTileEntity(nextPos);
				if (te != null && te instanceof BeltTileEntity) {
					for (int slot = 0; slot < inventory.getSlots(); slot++) {
						ItemStack stack = inventory.getStackInSlot(slot);
						if (stack.isEmpty())
							continue;

						if (((BeltTileEntity) te).tryInsertingFromSide(itemMovementFacing, stack, false))
							inventory.setStackInSlot(slot, ItemStack.EMPTY);
						else {
							inventory.remainingTime = 0;
							return;
						}
					}
					inventory.clear();
					inventory.remainingTime = -1;
					sendData();
				}
			}

			// Try moving items onto next saw/belt machine
			if (AllBlocks.SAW.typeOf(world.getBlockState(nextPos)) || world.getBlockState(nextPos).getBlock() instanceof BeltMachine) {
				TileEntity te = world.getTileEntity(nextPos);
				if (te != null) {
					if (te instanceof SawTileEntity) {
						SawTileEntity sawTileEntity = (SawTileEntity) te;
						Vec3d otherMovement = sawTileEntity.getItemMovementVec();
						if (Direction.getFacingFromVector(otherMovement.x, otherMovement.y,
								otherMovement.z) != itemMovementFacing.getOpposite()) {
							for (int slot = 0; slot < inventory.getSlots(); slot++) {
								ItemStack stack = inventory.getStackInSlot(slot);
								if (stack.isEmpty())
									continue;
	
								ProcessingInventory sawInv = sawTileEntity.inventory;
								if (sawInv.isEmpty()) {
									sawInv.insertItem(0, stack, false);
									inventory.setStackInSlot(slot, ItemStack.EMPTY);
	
								} else {
									inventory.remainingTime = 0;
									return;
								}
							}
							inventory.clear();
							inventory.remainingTime = -1;
							sendData();
						}
					}
					if (te instanceof BeltMachineTile) {
						BeltMachineTile beltMachineTile = (BeltMachineTile) te;
						Vec3d otherMovement = beltMachineTile.getItemMovementVec();
						if (Direction.getFacingFromVector(otherMovement.x, otherMovement.y, otherMovement.z) != itemMovementFacing.getOpposite()) {
							for (int slot = 0; slot < inventory.getSlots(); slot++) {
								ItemStack stack = inventory.getStackInSlot(slot);
								if (stack.isEmpty())
									continue;
	
								ProcessingInventory beltMachineInv = beltMachineTile.inventory;
								if (beltMachineInv.isEmpty()) {
									beltMachineInv.insertItem(0, stack, false);
									inventory.setStackInSlot(slot, ItemStack.EMPTY);
	
								} else {
									inventory.remainingTime = 0;
									return;
								}
							}
							inventory.clear();
							inventory.remainingTime = -1;
							sendData();
						}
					}
				}
			}

			// Eject Items
			for (int slot = 0; slot < inventory.getSlots(); slot++) {
				ItemStack stack = inventory.getStackInSlot(slot);
				if (stack.isEmpty())
					continue;
				ItemEntity entityIn = new ItemEntity(world, outPos.x, outPos.y, outPos.z, stack);
				entityIn.setMotion(outMotion);
				world.addEntity(entityIn);
			}
			inventory.clear();
			world.updateComparatorOutputLevel(pos, getBlockState().getBlock());
			inventory.remainingTime = -1;
			sendData();
			return;
		}

		return;
	}
	
	@Override
	public void remove() {
		invProvider.invalidate();
		destroyed = true;
		super.remove();
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return invProvider.cast();
		return super.getCapability(cap, side);
	}
	
	protected void spawnParticles(ItemStack stack) {
		if (stack == null || stack.isEmpty())
			return;

		IParticleData particleData = null;
		float speed = 1;
		if (stack.getItem() instanceof BlockItem)
			particleData =
				new BlockParticleData(ParticleTypes.BLOCK, ((BlockItem) stack.getItem()).getBlock().getDefaultState());
		else {
			particleData = new ItemParticleData(ParticleTypes.ITEM, stack);
			speed = .125f;
		}

		Random r = world.rand;
		Vec3d vec = getItemMovementVec();
		Vec3d pos = VecHelper.getCenterOf(this.pos);
		float offset = inventory.recipeDuration != 0 ? (float) (inventory.remainingTime) / inventory.recipeDuration : 0;
		offset -= .5f;
		world.addParticle(particleData, pos.getX() + -vec.x * offset, pos.getY() + .45f, pos.getZ() + -vec.z * offset,
				-vec.x * speed, r.nextFloat() * speed, -vec.z * speed);
	}
	
	public Vec3d getItemMovementVec() {
		boolean alongX = getBlockState().get(BlockStateProperties.FACING).getZOffset() != 0;
		int offset = getSpeed() < 0 ? -1 : 1;
		return new Vec3d(offset * (alongX ? 1 : 0), 0, offset * (alongX ? 0 : -1));
	}
	
	protected abstract void applyRecipe();
	
	abstract List<? extends IRecipe<?>> getRecipes();
	
	public void insertItem(ItemEntity entity) {
		if (!inventory.isEmpty())
			return;
		if (world.isRemote)
			return;
		inventory.clear();
		inventory.insertItem(0, entity.getItem().copy(), false);
		entity.remove();
	}
	
	public void start(ItemStack inserted) {
		if (inventory.isEmpty())
			return;
		if (world.isRemote)
			return;

		List<? extends IRecipe<?>> recipes = getRecipes();
		boolean valid = !recipes.isEmpty();
		int time = 100;

		if (recipes.isEmpty()) {
			inventory.remainingTime = inventory.recipeDuration = 10;
			inventory.appliedRecipe = false;
			sendData();
			return;
		}

		if (valid) {
			recipeIndex++;
			recipeIndex %= recipes.size();
		}

		IRecipe<?> recipe = recipes.get(recipeIndex);
		if (recipe instanceof BeltMachineRecipe) {
			time = ((BeltMachineRecipe) recipe).getProcessingDuration();
		} 

		inventory.remainingTime = time * Math.max(1, (inserted.getCount() / 5));
		inventory.recipeDuration = inventory.remainingTime;
		inventory.appliedRecipe = false;
		sendData();
	}
	
	@Override
	public abstract float calculateStressApplied();
}
