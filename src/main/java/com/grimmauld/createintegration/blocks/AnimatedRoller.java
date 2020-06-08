package com.grimmauld.createintegration.blocks;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.GuiGameElement;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;

public class AnimatedRoller extends AnimatedKinetics {

    @Override
    public void draw(int xOffset, int yOffset) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(xOffset, yOffset, 0);
        AllGuiTextures.JEI_SHADOW.draw(-16, 13);

        RenderSystem.translatef(0, 0, 200);
        RenderSystem.translatef(-6, 19, 0);
        RenderSystem.rotatef(-22.5f, 1, 0, 0);
        RenderSystem.rotatef(90 - 22.5f, 0, 1, 0);
        int scale = 25;

        GuiGameElement.of(shaft(Axis.X))
                .rotateBlock(-getCurrentAngle(), 0, 0)
                .scale(scale)
                .render();

        GuiGameElement.of(
                ModBlocks.ROLLING_MACHINE.getDefaultState().with(BlockStateProperties.FACING, Direction.WEST)
                        .with(RollingMachine.AXIS_ALONG_FIRST_COORDINATE, true))
                .rotateBlock(0, 0, 0)
                .scale(scale)
                .render();

        RenderSystem.popMatrix();
    }
}
