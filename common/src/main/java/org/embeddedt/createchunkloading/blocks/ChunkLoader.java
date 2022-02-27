package org.embeddedt.createchunkloading.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.PushReaction;

import javax.annotation.Nonnull;

public class ChunkLoader extends Block {
    public ChunkLoader() {
        super(Properties.copy(Blocks.BEACON).noOcclusion());
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Nonnull
    @Override
    public PushReaction getPistonPushReaction(@Nonnull BlockState state) {
        return PushReaction.NORMAL;
    }
}
