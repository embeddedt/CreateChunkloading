package com.grimmauld.createintegration.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.WeightedPressurePlateBlock;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CopperPressurePlate extends WeightedPressurePlateBlock{
	public CopperPressurePlate(){
		super(15, Block.Properties.from(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE));
		setRegistryName("copper_pressure_plate");
	}

	
	
	@Override
	protected int computeRedstoneStrength(World worldIn, BlockPos pos) {
		return (int) Math.min(worldIn.getEntitiesWithinAABB(CreatureEntity.class, PRESSURE_AABB.offset(pos)).stream().filter(e -> !(e instanceof MonsterEntity)).count(),15);
	}
	

	@Override
	protected void playClickOnSound(IWorld world, BlockPos pos){
		world.playSound(null, pos, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.8F);
	}

	@Override
	protected void playClickOffSound(IWorld world, BlockPos pos){
		world.playSound(null, pos, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.7F);
	}
}