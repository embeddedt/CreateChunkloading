package com.grimmauld.createintegration.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WeightedPressurePlateBlock;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.OptionalDouble;
import java.util.Random;

public class CopperPressurePlate extends WeightedPressurePlateBlock {
    public static final IntegerProperty OXIDIZATION = IntegerProperty.create("oxidization", 0, 7);

    public CopperPressurePlate() {
        super(15, Block.Properties.from(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE));
        setRegistryName("copper_pressure_plate");
        setDefaultState(getDefaultState().with(OXIDIZATION, 0));
    }

    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder.add(OXIDIZATION));
    }

    @Override
    public boolean ticksRandomly(BlockState state) {
        return super.ticksRandomly(state) || state.get(OXIDIZATION) < 7;
    }

    @Override
    public void randomTick(BlockState state, World worldIn, BlockPos pos, Random random) {
        if (worldIn.getRandom().nextFloat() <= 1 / 32f) {
            int currentState = state.get(OXIDIZATION);
            boolean canIncrease = false;
            LinkedList<Integer> neighbors = new LinkedList<>();
            for (Direction facing : Direction.values()) {
                BlockPos neighbourPos = pos.offset(facing);
                if (!worldIn.isBlockPresent(neighbourPos))
                    continue;
                BlockState neighborState = worldIn.getBlockState(neighbourPos);
                if (neighborState.has(OXIDIZATION) && neighborState.get(OXIDIZATION) != 0) {
                    neighbors.add(neighborState.get(OXIDIZATION));
                }
                if (Block.hasSolidSide(neighborState, worldIn, neighbourPos, facing.getOpposite())) {
                    continue;
                }
                canIncrease = true;
            }
            if (canIncrease) {
                OptionalDouble average = neighbors.stream().mapToInt(v -> v).average();
                if (average.orElse(7d) >= currentState)
                    worldIn.setBlockState(pos, state.with(OXIDIZATION, Math.min(currentState + 1, 7)));
            }
        }
    }

    @Override
    public float getBlockHardness(BlockState blockState, IBlockReader worldIn, BlockPos pos) {
        return this.blockHardness - 0.2f * blockState.get(OXIDIZATION);
    }

    @Override
    protected int computeRedstoneStrength(World worldIn, BlockPos pos) {
        return (int) Math.min(worldIn.getEntitiesWithinAABB(CreatureEntity.class, PRESSURE_AABB.offset(pos)).stream().filter(e -> !(e instanceof MonsterEntity)).count(), 15);
    }


    @Override
    protected void playClickOnSound(IWorld world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.8F);
    }

    @Override
    protected void playClickOffSound(IWorld world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.7F);
    }
}