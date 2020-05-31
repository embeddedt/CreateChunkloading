package com.grimmauld.createintegration.blocks;

import com.grimmauld.createintegration.CreateIntegration;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class EnderGui extends ContainerScreen<EnderContainer> {
    private final ResourceLocation GUI = CreateIntegration.generateResourceLocation("textures/gui/ender_chest.png");


    public EnderGui(EnderContainer screenContainer, PlayerInventory inv, ITextComponent name) {
        super(screenContainer, inv, name);

    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // drawString(Minecraft.getInstance().fontRenderer, "Ender ID: "+String.valueOf(container.getEnderId()), 60, 5, 0xffffff);
        drawCenteredString(Minecraft.getInstance().fontRenderer, "Ender ID: " + container.getEnderId(), 60, 5, 0xffffff);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(relX, relY, 0, 0, this.xSize, this.ySize);
    }
}
