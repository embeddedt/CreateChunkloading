package com.grimmauld.createintegration.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.WeightedPressurePlateBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BrassPressurePlate extends WeightedPressurePlateBlock {
    public BrassPressurePlate() {
        super(15, Block.Properties.from(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE));
        setRegistryName("brass_pressure_plate");
    }


    @Override
    protected int computeRedstoneStrength(World worldIn, @Nonnull BlockPos pos) {
        return Math.min(worldIn.getEntitiesWithinAABB(ItemEntity.class, PRESSURE_AABB.offset(pos)).size(), 15);
    }


    @Override
    protected void playClickOnSound(IWorld world, @Nonnull BlockPos pos) {
        world.playSound(null, pos, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.8F);
    }

    @Override
    protected void playClickOffSound(IWorld world, @Nonnull BlockPos pos) {
        world.playSound(null, pos, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.7F);
    }
}