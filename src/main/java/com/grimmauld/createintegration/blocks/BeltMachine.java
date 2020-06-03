package com.grimmauld.createintegration.blocks;

import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.modules.contraptions.base.DirectionalAxisKineticBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;

import static com.grimmauld.createintegration.tools.ModUtil.getFacingFromEntity;

public abstract class BeltMachine extends DirectionalAxisKineticBlock implements ITE<BeltMachineTile> {
    public static final BooleanProperty RUNNING = BooleanProperty.create("running");

    public BeltMachine(String registryName) {
        super(Properties.from(Blocks.ANDESITE));
        setRegistryName(registryName);
        setDefaultState(getDefaultState().with(RUNNING, false));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);

    @Override
    protected boolean hasStaticPart() {
        return true;
    }

    @Nonnull
    @Override
    public PushReaction getPushReaction(@Nonnull BlockState state) {
        return PushReaction.NORMAL;
    }

    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        builder.add(RUNNING);
        super.fillStateContainer(builder);
    }

    @Override
    public Class<BeltMachineTile> getTileEntityClass() {
        return BeltMachineTile.class;
    }

    @Override
    public boolean hasShaftTowards(IWorldReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.get(BlockStateProperties.FACING) || face == state.get(BlockStateProperties.FACING).getOpposite();
    }

    @Override
    public BlockState getStateForPlacement(@Nonnull BlockItemUseContext context) {
        return this.getDefaultState().with(BlockStateProperties.FACING, getFacingFromEntity(context.getPos(), context.getPlayer()));
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.get(BlockStateProperties.FACING).getAxis();
    }

}
