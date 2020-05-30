package com.grimmauld.createintegration.setup;

import com.grimmauld.createintegration.blocks.EnderGui;
import com.grimmauld.createintegration.blocks.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientProxy implements IProxy{
	@Override
	public World getClientWorld() {
		return Minecraft.getInstance().world;
	}

	@Override
	public void init() {
		ScreenManager.registerFactory(ModBlocks.ENDER_CONTAINER, EnderGui::new);
	}

	@Override
	public PlayerEntity getClientPlayer() {
		return Minecraft.getInstance().player;
	}
}
