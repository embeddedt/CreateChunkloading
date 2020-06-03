package com.grimmauld.createintegration.blocks;

import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.modules.contraptions.IWrenchable;
import com.simibubi.create.modules.logistics.block.inventories.CrateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

import static com.grimmauld.createintegration.tools.ModUtil.getFacingFromEntity;

public class EnderCrate extends Block implements ITE<EnderCrateTile>, IWrenchable {

    public EnderCrate() {
        super(Properties.from(Blocks.OBSIDIAN));
        setRegistryName("ender_crate");
    }

    @Override
    public BlockState getStateForPlacement(@Nonnull BlockItemUseContext context) {
        return this.getDefaultState().with(BlockStateProperties.FACING, getFacingFromEntity(context.getPos(), context.getPlayer()));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING, CrateBlock.DOUBLE);
    }

    @Override
    public boolean onBlockActivated(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand,
                                    @Nonnull BlockRayTraceResult result) {


        // System.out.println(player.getActiveItemStack().getItem());
		/*if(player.getActiveItemStack().getItem() instanceof WrenchItem) {
			System.out.println("wrench");
		}*/


        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof INamedContainerProvider) {
                NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
            } else {
                throw new IllegalStateException("Ender Container Provider is missing!");
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new EnderCrateTile();
    }


    @Override
    public Class<EnderCrateTile> getTileEntityClass() {
        return EnderCrateTile.class;
    }
}
