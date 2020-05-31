package com.grimmauld.createintegration.misc;

import com.grimmauld.createintegration.CreateIntegration;
import com.simibubi.create.modules.contraptions.components.contraptions.MovementBehaviour;
import com.simibubi.create.modules.contraptions.components.contraptions.MovementContext;
import net.minecraft.util.math.BlockPos;

public class ChunkLoaderMovementBehaviour extends MovementBehaviour {
    private BlockPos pos;
    private int resetTicking;


    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        if (context.world.isRemote)
            return;

        context.world.getCapability(CreateIntegration.CHUNK_LOADING_CAPABILITY, null).ifPresent(cap -> cap.add(pos));
        if (this.pos != null) {
            context.world.getCapability(CreateIntegration.CHUNK_LOADING_CAPABILITY, null).ifPresent(cap -> cap.remove(this.pos));
        }
        this.pos = pos;
    }

    @Override
    public void tick(MovementContext context) {
        resetTicking++;
        if (pos != null && resetTicking % 20 == 0) {
            context.world.getCapability(CreateIntegration.CHUNK_LOADING_CAPABILITY, null).ifPresent(cap -> cap.resetForBlock(pos));
        }
    }


}
