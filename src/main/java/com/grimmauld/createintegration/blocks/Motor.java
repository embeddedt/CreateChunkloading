package com.grimmauld.createintegration.blocks;

import com.simibubi.create.content.contraptions.base.KineticBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;

import static com.grimmauld.createintegration.tools.ModUtil.getFacingFromEntity;

public class Motor extends KineticBlock {

    public Motor() {
        super(Properties.create(Material.IRON)
                .sound(SoundType.METAL)
                .hardnessAndResistance(2.0f)
                .lightValue(0)
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(2)
        );
        setRegistryName("motor");
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new MotorTile();
    }

    @Override
    public BlockState getStateForPlacement(@Nonnull BlockItemUseContext context) {
        return this.getDefaultState().with(BlockStateProperties.FACING, getFacingFromEntity(context.getPos(), context.getPlayer()));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING, BlockStateProperties.POWERED);
    }

    @Override
    protected boolean hasStaticPart() {
        return true;
    }

    @Override
    public boolean hasShaftTowards(IWorldReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.get(BlockStateProperties.FACING);
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.get(BlockStateProperties.AXIS);
    }

}
