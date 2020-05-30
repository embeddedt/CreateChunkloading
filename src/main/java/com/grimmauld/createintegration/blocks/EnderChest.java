package com.grimmauld.createintegration.blocks;

import javax.annotation.Nullable;

import com.simibubi.create.foundation.block.ITE;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class EnderChest extends Block implements ITE<EnderChestTile>{
	public EnderChest() {
		super(Properties.from(Blocks.ENDER_CHEST));
		setRegistryName("ender_chest");
	}
	
	
	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            world.setBlockState(pos, state.with(ChestBlock.FACING, getFacingFromEntity(pos, entity)), 2);
        }
    }
	
	private static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
        Vec3d vec = entity.getPositionVec();
        return Direction.getFacingFromVector((float) (entity.isSneaking()?-1:1)*(vec.x - clickedBlock.getX()), 0.0f, (float) (entity.isSneaking()?-1:1)*(vec.z - clickedBlock.getZ()));
    }
	
	@Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ChestBlock.FACING);
    }
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new EnderChestTile();
	}


	@Override
	public Class<EnderChestTile> getTileEntityClass() {
		return EnderChestTile.class;
	}
}
