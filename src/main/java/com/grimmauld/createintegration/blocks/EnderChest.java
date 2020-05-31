package com.grimmauld.createintegration.blocks;

import javax.annotation.Nullable;

import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.modules.contraptions.IWrenchable;
import com.simibubi.create.modules.contraptions.WrenchItem;
import com.simibubi.create.modules.logistics.block.inventories.CrateBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EnderChest extends Block implements ITE<EnderChestTile>, IWrenchable{
	
	public EnderChest() {
		super(Properties.from(Blocks.OBSIDIAN));
		setRegistryName("ender_chest");
	}
	
	
	@Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            world.setBlockState(pos, state.with(BlockStateProperties.FACING, getFacingFromEntity(pos, entity)).with(CrateBlock.DOUBLE, false), 2);
        }
    }
	
	private static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
        Vec3d vec = entity.getPositionVec();
        return Direction.getFacingFromVector((float) (entity.isSneaking()?-1:1)*(vec.x - clickedBlock.getX()), (float) (entity.isSneaking()?-1:1)*(vec.y - clickedBlock.getY()), (float) (entity.isSneaking()?-1:1)*(vec.z - clickedBlock.getZ()));
    }
	
	@Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING, CrateBlock.DOUBLE);
    }
	
	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockRayTraceResult result) {
		
		
		// System.out.println(player.getActiveItemStack().getItem());
		/*if(player.getActiveItemStack().getItem() instanceof WrenchItem) {
			System.out.println("wrench");
		}*/
		
		
       if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof INamedContainerProvider) {
                NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
            } else {
                throw new IllegalStateException("Our named container provider is missing!");
            }
            return true;
        }
		return super.onBlockActivated(state, world, pos, player, hand, result);
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